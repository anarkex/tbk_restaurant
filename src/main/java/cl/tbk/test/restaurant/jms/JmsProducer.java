package cl.tbk.test.restaurant.jms;

import cl.tbk.test.restaurant.entities.ResumenVentas;
import java.util.Date;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 * Productor de JMS
 * @author manuelpinto
 */
@Component
public class JmsProducer {
    @Autowired
    JmsMessagingTemplate jmsTemplate;

    @Value("${spring.activemq.topic}")
    private String topic;

    /**
     * Envía la fecha del reporte solicitado al consumidor y recibe el resumen de las ventas del dia
     * @param fecha
     * @return 
     */
    public ResumenVentas sendMessage(Date fecha){
        try{
            Logger.getLogger(this.getClass().getCanonicalName()).info("Attempting Send message to Topic: "+ fecha);
            ResumenVentas resumenVentas=jmsTemplate.convertSendAndReceive(topic, fecha, ResumenVentas.class);
            return resumenVentas;
        } catch(Exception e){
            Logger.getLogger(this.getClass().getCanonicalName()).error("Recieved Exception during send Message: ", e);
        }
        return null;
    }
}
