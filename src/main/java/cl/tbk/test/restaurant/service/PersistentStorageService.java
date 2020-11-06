/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cl.tbk.test.restaurant.service;

import cl.tbk.test.restaurant.entities.ResumenVentas;
import cl.tbk.test.restaurant.entities.Venta;

/**
 *
 * @author manuelpinto
 */
public interface PersistentStorageService {
    Venta store(Venta venta);
    ResumenVentas get(int year, int month, int day);
}
