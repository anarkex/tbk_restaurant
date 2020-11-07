package cl.tbk.test.restaurant.controllers;

import cl.tbk.test.restaurant.entities.ResumenVentas;
import cl.tbk.test.restaurant.entities.Venta;
import cl.tbk.test.restaurant.service.PersistentStorageService;
import cl.tbk.test.restaurant.service.ValidatorService;
import java.util.Date;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Segunda parte del test, guarda venta y recupera resumen del día<br/>
 * Esta clase está detras de Authorization Bearer <i>token</i>
 * @author manuelpinto
 */
@RestController
@RequestMapping("/tbk/restaurant/v1/ventas")
public class VentaController {

    private static final Logger LOG = Logger.getLogger(VentaController.class.getName());

    @Autowired
    private PersistentStorageService persistenceService;
    
    @Autowired
    private ValidatorService validationService;
    
    public VentaController() {
    }

    /**
     * POST /ventas/add
     * recibe un request json de la forma:
     * <pre><![CDATA[
{
    "id": "19a31f12-8f76-43b2-bc4e-03df1d7c0bd1",
    "items": [
        {
            "id": "9e76c18f-f27c-4c20-9d6a-1f27da5b26f6",
            "codigo": 70003040202,
            "cantidad": 14,
            "precioUnitario": 1200,
            "detalle": "Confort Noble 6 unidades"
        },
        {
            "id": "9e76c18f-f27c-4c20-9d6a-1f27da5b26f7",
            "codigo": 70003040203,
            "cantidad": 24,
            "precioUnitario": 500,
            "detalle": "Super8"
        }
    ],
    "timestamp": "2020-11-06T04:07:55.204+00:00",
    "sucursalId": 13,
    "username": "transbank"
}
     * ]]></pre><br/>
     * Este request es un objeto Venta con objetos Item anidados
     * @param venta
     * @param request
     * @return 
     */
    @RequestMapping(value = "add", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    @ResponseBody
    public ResponseEntity<Venta> addVenta(@RequestBody Venta venta, HttpServletRequest request) {
        String loggedUserName=(String)(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        System.out.println(loggedUserName);
        venta.setUsername(loggedUserName);
        if(venta.getTimestamp()==null)
            venta.setTimestamp(new Date());
        validationService.validate(venta);
        persistenceService.store(venta); // it's scheduled to be stored later
        return ResponseEntity.ok(venta);
    }
    
    /**
     * Resumen recibe una peticion GET /ventas/resume/<i>año<</i>/<i>mes</i>/<i>dia</i><br/>
     * Genera un consolidado del día y lo entrega.<br/>
     * Este consolidado permanece en cache por 5 minutos por si alguien lo necesita
     * @param year año a consultar
     * @param month mes a consultar
     * @param day dia a consultar
     * @return consolidado de ventas del dia
     */
    @RequestMapping(value = "resume/{y}/{m}/{d}", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public ResponseEntity<ResumenVentas> resumenVentas(@PathVariable("y") Integer year, @PathVariable("m") Integer month, @PathVariable("d") Integer day){
        ResumenVentas rv=persistenceService.get(year, month, day);
        if(rv==null){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(rv);
    }
}
