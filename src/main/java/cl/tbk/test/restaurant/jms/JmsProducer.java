/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
 *
 * @author manuelpinto
 */
@Component
public class JmsProducer {
    @Autowired
    JmsMessagingTemplate jmsTemplate;

    @Value("${spring.activemq.topic}")
    private String topic;

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
