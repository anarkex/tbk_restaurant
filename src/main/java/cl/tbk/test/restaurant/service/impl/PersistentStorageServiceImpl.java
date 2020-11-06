/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cl.tbk.test.restaurant.service.impl;

import cl.tbk.test.restaurant.App;
import cl.tbk.test.restaurant.entities.ResumenVentas;
import cl.tbk.test.restaurant.entities.Venta;
import cl.tbk.test.restaurant.service.PersistentStorageService;
import cl.tbk.test.restaurant.service.ResumenVentasService;
import cl.tbk.test.restaurant.service.ValidatorService;
import cl.tbk.test.restaurant.service.VentaStorageService;
import com.hazelcast.cluster.MembershipEvent;
import com.hazelcast.cluster.MembershipListener;
import com.hazelcast.core.HazelcastInstance;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

/**
 *
 * @author manuelpinto
 */
@Service
public class PersistentStorageServiceImpl implements PersistentStorageService {    
    
    private static final Logger LOG = Logger.getLogger(PersistentStorageServiceImpl.class.getName());

    @Autowired
    private VentaStorageService ventaStorage;
    
    @Autowired
    private ResumenVentasService resumenService;
    
    @Autowired
    private ValidatorService validatorService;
    
    @Autowired
    @Qualifier("databaseTaskExecutor")
    private TaskExecutor executor;
    
    private static Boolean running=Boolean.FALSE;
    
    private BlockingQueue<Venta> toStorage;
    private BlockingQueue<Date> resumenQuery;
    private Map<Date,ResumenVentas> resumenResults;
    private Map<String,String> params;
    
    @Autowired
    private HazelcastInstance hz;
    
    public PersistentStorageServiceImpl(){
    }
    
    @PostConstruct
    public void initialize(){
        resumenQuery=hz.getQueue(App.M_RESUMEQUERY);
        resumenResults=hz.getMap(App.M_RESUMERESULT);
        toStorage=hz.getQueue(App.Q_VENTAS_STORAGE);
        params=hz.getMap(App.M_PARAMETERS);
        chooseDb();        
        // This will monitor the database member for other to become the database
        hz.getCluster().addMembershipListener(new MembershipListener() {
            @Override
            public void memberAdded(MembershipEvent membershipEvent) {
            }

            @Override
            public void memberRemoved(MembershipEvent membershipEvent) {
                if(membershipEvent.getMember().getUuid().toString().equals(params.get("db_node"))){
                    throw new Error("We lose the database node");
                }
            }
        });
    }
    
    /**
     * This chose what node will be the database node which is the one that deals with
     * store and resume request. despite the fact that all the nodes tries to bring up the database
     */
    private void chooseDb(){
        // Choose who will be the DB
        if(!params.containsKey("db_node")){
            params.putIfAbsent("db_node", hz.getLocalEndpoint().getUuid().toString());
        }
        if(hz.getLocalEndpoint().getUuid().toString().equals(params.get("db_node"))){
            executor.execute(()->{
                try {
                    Logger log = Logger.getLogger(this.getClass().getName());
                    log.info("Starting toStorage consumer");
                    while(true){
                        Venta venta=toStorage.take();
                        log.info("take new venta: "+venta.getId().toString());
                        ventaStorage.store(venta);
                        removeResultsParaFecha(venta.getTimestamp());
                        log.info("saved venta: "+venta.getId().toString());
                    }
                } catch (InterruptedException ex) {
                    Logger.getLogger(PersistentStorageServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
            executor.execute(()->{
                try {
                    Logger log = Logger.getLogger(this.getClass().getName());                    
                    log.info("Starting resumeQuery consumer");
                    while(true){
                        Date queryDate = resumenQuery.take();
                        log.info("Dealing with date "+queryDate);
                        ResumenVentas rv=resumenService.get(queryDate);
                        resumenResults.put(queryDate,rv);
                        log.info("put result on resumenResult :"+rv.getFechaVentas());
                    }
                } catch (InterruptedException ex) {
                    Logger.getLogger(PersistentStorageServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
        }
    }
    
    /**
     * Calcula la fecha sin hora para remover el cache de esos resultados<br/>
     * Este método es necesario cuando un reporte para este día esta cachado en
     * el servidor en el momento en que se dignó terminarlo.
     * @param fecha 
     */
    private void removeResultsParaFecha(Date fecha){
        Calendar calendar=Calendar.getInstance();
        calendar.setTime(fecha);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND,0);
        calendar.set(Calendar.MILLISECOND,0);
        resumenResults.remove(calendar.getTime());
    }
    
    
    /**
     * Valida la venta que se va a almacenar y la pone en la cola de hazelcast
     * para que el nodo que se declaró Base de Datos la persista.
     * @param venta
     * @return 
     */
    @Override
    public Venta store(Venta venta) {
        // Storage queue is infinite
        try {
            validatorService.validate(venta);
            toStorage.put(venta);
            return venta;
        } catch (InterruptedException ex) {
            Logger.getLogger(PersistentStorageServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException("Interrupted when trying to save venta "+venta.getId().toString());
        }
    }

    /**
     * Get the Resumen de Ventas for the specified year, day, month to be recovered from database
     * @param year
     * @param month
     * @param day
     * @return 
     */
    @Override
    public ResumenVentas get(int year, int month, int day) {
        Calendar calendar=Calendar.getInstance(TimeZone.getDefault());
        calendar.set(year, month-1, day, 0, 0, 0); // Why in the hell calendar uses JAN=0 DEC=11
        calendar.set(Calendar.MILLISECOND, 0);
        Date date=calendar.getTime();
        try {
            if(!resumenResults.containsKey(date)) // si no tenemos el reporte de esta fecha en cache, lo solicitamos
                resumenQuery.put(date);
            try{
                int ttl=10;
                // Y nos quedamos esperando a que el resultado aparezca en el cluster.
                // tiene un time to live de 10 medios segundos. ¿Será necesario que lo parametrice?
                while(!resumenResults.containsKey(date) && ttl-->0){
                    Thread.sleep(500);
                }
            } catch (InterruptedException e){}
            // chequeamos de nuevo porque no se si salió del ciclo por timeout o porque ya llegó el item que buscaba
            if(resumenResults.containsKey(date))
                return resumenResults.get(date); // left this in the map until it expires
            else
                throw new RuntimeException("Request Timeout"); // no apareció nada en el mapa
        } catch (InterruptedException ex) {
            Logger.getLogger(PersistentStorageServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException("Interrupted while waiting for VentaResumen");
        }
    }
    
    
}
