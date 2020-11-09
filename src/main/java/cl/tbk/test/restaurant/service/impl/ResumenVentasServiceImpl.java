package cl.tbk.test.restaurant.service.impl;

import cl.tbk.test.restaurant.entities.Item;
import cl.tbk.test.restaurant.entities.ResumenVentas;
import cl.tbk.test.restaurant.entities.Venta;
import cl.tbk.test.restaurant.service.ResumenVentasService;
import cl.tbk.test.restaurant.service.VentaStorageService;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Servicio que genera el resumen de ventas
 * @author manuelpinto
 */
@Service
public class ResumenVentasServiceImpl implements ResumenVentasService{
    
    @Autowired
    VentaStorageService ventasService;
    
    public ResumenVentasServiceImpl(){}
    
    /**
     * Obtiene las ventas para el día desde la base y
     * genera el resumen de ventas para el día especificado
     * @param dia
     * @return 
     */
    @Override
    public ResumenVentas get(Date dia) {
        Calendar calendar=Calendar.getInstance();
        calendar.setTime(dia);
        calendar.set(Calendar.HOUR_OF_DAY,0);
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND,0);
        calendar.set(Calendar.MILLISECOND, 0);
        List<Venta> ventas=new ArrayList<>(ventasService.getVentas(calendar.getTime()));
        ResumenVentas rv=new ResumenVentas();
        rv.setFechaVentas(calendar.getTime());
        rv.setGeneradoTimestamp(new Date());
        // obtiene todas las ventas y suma la cantida de productos en los items que tenga cada una de ellas
        rv.setItemCount(ventas.parallelStream()
                .map(Venta::getItems)
                .flatMap(Collection::stream)
                .mapToInt(Item::getCantidad)
                .sum());
        // obtiene todas las ventas, y suma sus montos
        rv.setMontoTotal(
                ventas.parallelStream()
                        .map(Venta::getMontoTotalDeVenta)
                        .reduce(BigDecimal.ZERO, (subtotal, value)->subtotal.add(value))
        );
        rv.setVentaCount(ventas.size());
        rv.setVentas(ventas);
        return rv;
    }
    
}
