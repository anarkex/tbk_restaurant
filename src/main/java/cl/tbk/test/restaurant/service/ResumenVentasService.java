/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cl.tbk.test.restaurant.service;

import cl.tbk.test.restaurant.entities.ResumenVentas;
import java.util.Date;

/**
 * Generador de resumen de ventas
 * @author manuelpinto
 */
public interface ResumenVentasService {
    /**
     * Genera un resumen de ventas para el día
     * @param date
     * @return 
     */
    ResumenVentas get(Date date);
}
