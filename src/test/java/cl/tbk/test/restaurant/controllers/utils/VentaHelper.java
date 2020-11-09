package cl.tbk.test.restaurant.controllers.utils;

import cl.tbk.test.restaurant.entities.Item;
import cl.tbk.test.restaurant.entities.Venta;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.apache.commons.lang.RandomStringUtils;
import org.h2.util.MathUtils;

/**
 *
 * @author manuelpinto
 */
public final class VentaHelper {
    private VentaHelper(){}
    
    public static Venta buildVenta(){
        Venta venta=new Venta();
        venta.setId(UUID.randomUUID());
        venta.setSucursalId(MathUtils.randomInt(250));
        venta.setItems(createItems());
        return venta;
    }
    
    public static List<Item> createItems(){
        List<Item> items=new ArrayList<>();
        for(int itemCount=0;itemCount<MathUtils.randomInt(9)+1;itemCount++){
            items.add(createItem());
        }
        return items;
    }
    
    public static Item createItem(){
        Item item=new Item();
        item.setId(UUID.randomUUID());
        item.setCodigo(new Long(MathUtils.randomInt(500)));
        item.setDetalle(RandomStringUtils.random(64));
        item.setPrecioUnitario(new BigDecimal(MathUtils.randomInt(5000)+1,MathContext.DECIMAL32));
        item.setCantidad(MathUtils.randomInt(15)+1);
        return item;
    }
}
