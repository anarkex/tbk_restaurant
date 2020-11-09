package cl.tbk.test.restaurant.service;

import cl.tbk.test.restaurant.entities.Venta;
import java.util.Collection;
import java.util.Date;

/**
 * Servicio para almacenar las venta
 * @author manuelpinto
 */
public interface VentaStorageService {
    /**
     * Guarda las venta
     * @param venta 
     */
    void store(Venta venta);
    /**
     * Obtiene las ventas para una fecha
     * @param fecha
     * @return 
     */
    Collection<Venta> getVentas(Date fecha);
}
