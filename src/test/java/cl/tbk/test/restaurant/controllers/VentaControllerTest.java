package cl.tbk.test.restaurant.controllers;

import cl.tbk.test.restaurant.controllers.utils.HttpRequestHelper;
import cl.tbk.test.restaurant.controllers.utils.VentaHelper;
import cl.tbk.test.restaurant.entities.ResumenVentas;
import cl.tbk.test.restaurant.entities.Venta;
import cl.tbk.test.restaurant.repository.VentaRepository;
import com.hazelcast.logging.Logger;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;


/**
 * Tests the venta controller endpoint
 * @author manuelpinto
 */
@SpringBootTest
public class VentaControllerTest {
    
    @Value("${auth.username}")
    String loginUsername;
    
    @Value("${auth.password}")
    String loginPassword;
    
    @Autowired
    private VentaController ventaController;
    
    @Autowired
    private VentaRepository ventaRepository;
    
    public VentaControllerTest() {
    }
    
    @BeforeAll
    public static void setUpClass() {
        java.util.logging.Logger.getLogger(VentaControllerTest.class.getCanonicalName()).info("VentaControllerTest started");
    }
    
    @AfterAll
    public static void tearDownClass() {
        java.util.logging.Logger.getLogger(VentaControllerTest.class.getCanonicalName()).info("VentaControllerTest finished");
    }
    
    @BeforeEach
    public void setUp() {
    }
    
    @AfterEach
    public void tearDown() {
    }

    @Test
    @WithMockUser(username = "transbank_test")
    public void testAddVentaAndGetResumen(){
        Venta venta=VentaHelper.buildVenta();
        ventaController.addVenta(venta, HttpRequestHelper.mockHttpServletRequest());
        try{Thread.sleep(500);} catch (Exception e){} // wait until venta is saved.
        Calendar calendar=Calendar.getInstance();
        calendar.setTime(venta.getTimestamp());
        int y=calendar.get(Calendar.YEAR);
        int m=calendar.get(Calendar.MONTH)+1;
        int d=calendar.get(Calendar.DATE);
        try{
            ResumenVentas rv=ventaController.resumenVentas(y, m, d).getBody();
            assert(rv.getVentas().parallelStream().filter(v->v.getId().equals(venta.getId())).findFirst().isPresent());
        } finally {
            Logger.getLogger(this.getClass()).info("Borrando venta de prueba");
            ventaRepository.delete(venta);
        }
    }
    
    @Test
    @WithMockUser(username = "transbank_test")
    public void testAddVentasYValidaMontos(){
        List<Venta> ventas=new ArrayList<>(); // set de pruebas
        Calendar calendar=Calendar.getInstance();
        for(int diaOffset=0;diaOffset<5; diaOffset++){
            for(int ventaCount=0;ventaCount<20;ventaCount++){
                Venta venta=VentaHelper.buildVenta();
                venta.setTimestamp(calendar.getTime());
                ventas.add(venta);
            }
            calendar.add(Calendar.DATE, 1);
        }
        // Guardando las ventas
        // Es parte de las pruebas ver si lo guarda usando el endpoint
        for(Venta vta:ventas){
            ventaController.addVenta(vta, HttpRequestHelper.mockHttpServletRequest());
        }
        try{
            // recuperando las ventas de los 5 dias
            List<ResumenVentas> resumenes=new ArrayList<>();
            calendar.setTime(new Date());
            for(int diaOffset=0;diaOffset<5; diaOffset++){
                int y=calendar.get(Calendar.YEAR);
                int m=calendar.get(Calendar.MONTH)+1;
                int d=calendar.get(Calendar.DATE); 
                // solicitando resumen de ventas para el dia
                Logger.getLogger(this.getClass()).info("CALENDAR: "+calendar.getTime()+" dias/"+diaOffset);
                ResumenVentas rv=ventaController.resumenVentas(y, m, d).getBody();
                resumenes.add(rv); // agregándolo para examinar si los resúmenes son diferentes
                assert(rv.getVentaCount()==rv.getVentas().size()); // chequeando cuenta de ventas
                // chequeando monto de ventas informado
                assert(rv.getMontoTotal().equals(rv.getVentas().parallelStream().map(vta->vta.getMontoTotalDeVenta()).reduce(BigDecimal.ZERO, BigDecimal::add)));
                // chequeando sumatoria de precios
                rv.getVentas().parallelStream().forEach(vta->{
                    // chequeando precio total items
                    vta.getItems().forEach(itm->{
                        assert itm.getPrecioUnitario().multiply(new BigDecimal(itm.getCantidad())).equals(itm.getPrecioTotal());
                    });
                    // chequeando precio total de la venta
                    assert vta.getMontoTotalDeVenta().equals(vta.getItems().parallelStream().map(itm->itm.getPrecioTotal()).reduce(BigDecimal.ZERO,BigDecimal::add));
                });
                // chequeando monto total del resumen
                assert rv.getVentas().parallelStream().map(vta->vta.getMontoTotalDeVenta()).reduce(BigDecimal.ZERO,BigDecimal::add).equals(rv.getMontoTotal());
                calendar.add(Calendar.DATE, 1); // Agrego un dia al calendario para pedir el otro set de ventas
            }
        } finally {
            Logger.getLogger(this.getClass()).info("Borrando ventas de prueba");
            ventaRepository.deleteAll(ventas); // limpiando la base de las ventas que metí
        }
    }
    
    @Test
    @WithMockUser(username = "transbank_test")
    public void testResumenMismaFecha(){
        Venta venta=VentaHelper.buildVenta();
        venta.setTimestamp(null);
        ventaController.addVenta(venta, HttpRequestHelper.mockHttpServletRequest());
        try{
            Date fechaDummy=new Date();
            Calendar calendar=Calendar.getInstance();
            calendar.setTime(venta.getTimestamp());
            int y=calendar.get(Calendar.YEAR);
            int m=calendar.get(Calendar.MONTH)+1;
            int d=calendar.get(Calendar.DATE);
            Set<ResumenVentas> resumenes=new HashSet<>();
            for(int resumenCount=0;resumenCount<10;resumenCount++){
                ResumenVentas rv=ventaController.resumenVentas(y, m, d).getBody();
                rv.setGeneradoTimestamp(fechaDummy);
                resumenes.add(rv);
            }
            assert(resumenes.size()==1);
        }finally{
            Logger.getLogger(this.getClass()).info("Borrando venta de prueba");
            ventaRepository.delete(venta);
        }
        
    }
    
    @Test
    @WithMockUser(username = "transbank_test")
    public void testResumenFechaSinVentas(){
        ResumenVentas rv=ventaController.resumenVentas(1950, 2, 10).getBody();
        assert rv.getVentaCount()==0;
        assert rv.getItemCount()==0;
        assert BigDecimal.ZERO.equals(rv.getMontoTotal());
        assert rv.getVentas().isEmpty();
    }
    
}
