package cl.tbk.test.restaurant.service.impl;

import cl.tbk.test.restaurant.entities.Item;
import cl.tbk.test.restaurant.entities.Venta;
import cl.tbk.test.restaurant.exception.ValidationException;
import cl.tbk.test.restaurant.service.ValidatorService;
import java.math.BigDecimal;
import org.springframework.stereotype.Service;

/**
 * Servicio para validar Ventas y, por consiguiente, Items.<br/>
 * No se expone el item idenpendiente porque este sistema no maneja items independientes.
 * @author manuelpinto
 */
@Service
public class ValidatorServiceImpl implements ValidatorService{
    
    /**
     * Valida una venta y sus items.<br/>
     * No calcula sobre sumas porque esos son @Transient
     * @param venta 
     */
    @Override
    public void validate(Venta venta){
        if(venta.getId()==null) throw new ValidationException("Venta ID es nulo");
        venta.getItems().parallelStream().forEach(i->validate(i));
    }
    
    @Override
    public void validate(Item item){
        if(item.getId()==null) throw new ValidationException("item id es nulo");
        if(item.getCantidad()==null || item.getCantidad()<=0) throw new ValidationException("cantidad es menor o igual a 0 o nulo");
        if(item.getCodigo()==null) throw new ValidationException("item sin código");
        if(item.getPrecioUnitario()==null || item.getPrecioUnitario().compareTo(BigDecimal.ZERO)<=0) throw new ValidationException("Precio unitario menor o igual a 0 o nulo");
    }
    
}
