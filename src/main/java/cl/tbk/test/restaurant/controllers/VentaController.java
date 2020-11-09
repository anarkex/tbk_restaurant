package cl.tbk.test.restaurant.controllers;

import cl.tbk.test.restaurant.entities.ResumenVentas;
import cl.tbk.test.restaurant.entities.Venta;
import cl.tbk.test.restaurant.exception.ValidationException;
import java.util.Date;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import cl.tbk.test.restaurant.service.RestaurantService;

/**
 * Segunda parte del test, guarda venta y recupera resumen del día<br/>
 * Esta clase está detras de Authorization Bearer <i>token</i>
 * @author manuelpinto
 */
@RestController
@RequestMapping("/tbk/restaurant/v1/ventas")
public class VentaController {

    @Autowired
    @Qualifier("JMS+DB")
    //@Qualifier("Hazelcast+DB")
    private RestaurantService restaurantService;
    
    public VentaController() {
    }

    /**
     * Almacena una venta y sus items
     * @param venta
     * @param request
     * @return 
     */
    @RequestMapping(value = "add", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    @ResponseBody
    public ResponseEntity<Venta> addVenta(@RequestBody Venta venta, HttpServletRequest request) {
        String loggedUserName=SecurityContextHolder.getContext().getAuthentication().getName();
        // I could have set the username in the persistence layer
        // that's not its business and it shouldn't guess who was because that increases the coupling        
        venta.setUsername(loggedUserName);
        if(venta.getTimestamp()==null)
            venta.setTimestamp(new Date());
        Logger.getLogger(this.getClass().getCanonicalName()).info("Guardando la venta "+venta.getId()+" "+venta.getTimestamp());
        try{
            restaurantService.store(venta); // this validates and schedule the persistence
        } catch (ValidationException validationException){ // This is RuntimeException because stream.foreach on items
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(venta);
    }
    
    /**
     * resume recibe una peticion GET /ventas/resume/<i>año<</i>/<i>mes</i>/<i>dia</i><br/>
     * Genera un consolidado del día y lo entrega.<br/>
     * Este consolidado permanece en cache por 5 minutos por si alguien más lo necesita
     * y se invalida si alguien mete una nueva venta para ese dia.
     * @param year año a consultar
     * @param month mes a consultar
     * @param day dia a consultar
     * @return consolidado de ventas del dia
     */
    @RequestMapping(value = "resume/{y}/{m}/{d}", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public ResponseEntity<ResumenVentas> resumenVentas(@PathVariable("y") Integer year, @PathVariable("m") Integer month, @PathVariable("d") Integer day){
        Logger.getLogger(this.getClass().getCanonicalName()).info("Generando resumen para "+year+"-"+month+"-"+day);
        ResumenVentas rv=restaurantService.get(year, month, day);
        if(rv==null){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(rv);
    }
}
