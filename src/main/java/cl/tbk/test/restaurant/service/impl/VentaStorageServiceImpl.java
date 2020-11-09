package cl.tbk.test.restaurant.service.impl;

import cl.tbk.test.restaurant.entities.Venta;
import cl.tbk.test.restaurant.repository.VentaRepository;
import cl.tbk.test.restaurant.service.VentaStorageService;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Servicio para recuperar ventas desde la base
 * @author manuelpinto
 */
@Service
public class VentaStorageServiceImpl implements VentaStorageService {

    private static final Logger LOG = Logger.getLogger(VentaStorageServiceImpl.class.getName());

    @Autowired
    private VentaRepository repo;    
    
    public VentaStorageServiceImpl() {
    }
    
    /**
     * guarda la venta en la base
     * @param venta 
     */
    @Override
    public void store(Venta venta) {
        repo.save(venta);
    }

    /**
     * Obtiene las ventas de un día desde la base.
     * @param fecha
     * @return 
     */
    @Override
    public Collection<Venta> getVentas(Date fecha) {
        Calendar calendar=Calendar.getInstance();
        calendar.setTime(fecha);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE,59);
        calendar.set(Calendar.SECOND,59);
        calendar.set(Calendar.MILLISECOND,999);
        List<Venta> ventas=repo.getVentasByTimestampBetween(fecha, calendar.getTime());
        // removing persistenceBag
        ventas.parallelStream().forEach(v->v.setItems(new ArrayList<>(v.getItems())));
        return ventas;
    }
    
}
