/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cl.tbk.test.restaurant.service.impl;

import cl.tbk.test.restaurant.entities.Venta;
import cl.tbk.test.restaurant.repository.VentaRepository;
import cl.tbk.test.restaurant.service.VentaStorageService;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author manuelpinto
 */
@Service
public class VentaStorageServiceImpl implements VentaStorageService {

    private static final Logger LOG = Logger.getLogger(VentaStorageServiceImpl.class.getName());

    @Autowired
    private VentaRepository repo;    
    
    public VentaStorageServiceImpl() {
    }
    
    @Override
    public void store(Venta venta) {
        repo.save(venta);
    }

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
