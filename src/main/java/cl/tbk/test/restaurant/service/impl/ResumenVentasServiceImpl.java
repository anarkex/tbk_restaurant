/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
 *
 * @author manuelpinto
 */
@Service
public class ResumenVentasServiceImpl implements ResumenVentasService{
    
    @Autowired
    VentaStorageService ventasService;
    
    public ResumenVentasServiceImpl(){}
    
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
        rv.setItemCount(ventas.parallelStream()
                .map(Venta::getItems).flatMap(Collection::stream)
                .mapToInt(Item::getCantidad).sum());
        rv.setMontoTotal(ventas.parallelStream().map(Venta::getItems).flatMap(Collection::stream).map(i->i.getPrecioUnitario().multiply(new BigDecimal(i.getCantidad(),MathContext.DECIMAL64))).reduce(BigDecimal.ZERO, (subtotal, value)->subtotal.add(value)));
        rv.setVentaCount(ventas.size());
        rv.setVentas(ventas);
        return rv;
    }
    
}
