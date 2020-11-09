package cl.tbk.test.restaurant.service.impl;

import cl.tbk.test.restaurant.App;
import cl.tbk.test.restaurant.entities.ResumenVentas;
import cl.tbk.test.restaurant.entities.Venta;
import cl.tbk.test.restaurant.jms.JmsProducer;
import cl.tbk.test.restaurant.service.PersistentStorageService;
import cl.tbk.test.restaurant.service.VentaStorageService;
import java.util.Calendar;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 *
 * @author manuelpinto
 */
@Service
@Qualifier("JMS+DB")
public class PersistentStorageServiceJMSImpl implements PersistentStorageService {

    @Autowired
    private VentaStorageService ventaStorage;

    @Autowired
    private JmsProducer jmsProducer;
    
    @Override
    public Venta store(Venta venta) {
        ventaStorage.store(venta);
        return venta;
    }

    @Override
    public ResumenVentas get(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month-1, day, 0, 0, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Logger.getLogger(this.getClass().getCanonicalName()).info("Enviando "+calendar.getTime()+" a "+App.C_RESUMEN_REQUEST);
        ResumenVentas resumen=jmsProducer.sendMessage(calendar.getTime());
        return resumen;
    }

}
