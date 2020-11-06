/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cl.tbk.test.restaurant.service;

import cl.tbk.test.restaurant.entities.Venta;
import java.util.Collection;
import java.util.Date;

/**
 *
 * @author manuelpinto
 */
public interface VentaStorageService {
    void store(Venta venta);
    Collection<Venta> getVentas(Date fecha);
}
