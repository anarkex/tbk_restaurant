/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cl.tbk.test.restaurant.service.impl;

import cl.tbk.test.restaurant.entities.Item;
import cl.tbk.test.restaurant.entities.Venta;
import cl.tbk.test.restaurant.service.ValidatorService;
import java.math.BigDecimal;
import org.springframework.stereotype.Service;

/**
 *
 * @author manuelpinto
 */
@Service
public class ValidatorServiceImpl implements ValidatorService{
    
    @Override
    public void validate(Venta venta) {
        if(venta.getId()==null) throw new RuntimeException("Venta ID es nulo");
        venta.getItems().parallelStream().forEach(i->validate(i));
    }
    
    private void validate(Item item){
        if(item.getId()==null) throw new RuntimeException("item id es nulo");
        if(item.getCantidad()==null || item.getCantidad()<=0) throw new RuntimeException("cantidad es menor o igual a 0 o nulo");
        if(item.getCodigo()==null) throw new RuntimeException("item sin código");
        if(item.getPrecioUnitario()==null || item.getPrecioUnitario().compareTo(BigDecimal.ZERO)<=0) throw new RuntimeException("Precio unitario menor o igual a 0 o nulo");
    }
    
}
