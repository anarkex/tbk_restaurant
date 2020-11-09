package cl.tbk.test.restaurant.service;

import cl.tbk.test.restaurant.entities.ResumenVentas;
import cl.tbk.test.restaurant.entities.Venta;

/**
 * Servicio de persistencia de las ventas<br/>
 * tambien genera el Resumen de las ventas desde la base (o del cache)<br/>
 * @author manuelpinto
 */
public interface RestaurantService {
    
    /**
     * Almacena una venta en la base
     * @param venta
     * @return 
     */
    Venta store(Venta venta);
    
    /**
     * Genera un resumen de ventas para el año/mes/dia
     * @param year
     * @param month
     * @param day
     * @return 
     */
    ResumenVentas get(int year, int month, int day);
}
