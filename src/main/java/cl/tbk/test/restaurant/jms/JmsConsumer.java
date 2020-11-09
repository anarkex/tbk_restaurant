package cl.tbk.test.restaurant.jms;

import cl.tbk.test.restaurant.entities.ResumenVentas;
import cl.tbk.test.restaurant.service.ResumenVentasService;
import java.util.Date;
import javax.jms.JMSException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import org.apache.activemq.command.ActiveMQObjectMessage;
import org.apache.log4j.Logger;
import org.springframework.jms.listener.SessionAwareMessageListener;

/**
 * Consumidor de JMS
 * @author manuelpinto
 */

@Component
public class JmsConsumer implements SessionAwareMessageListener<Message> {

    
    @Autowired
    ResumenVentasService resumenVentasService;

    /**
     * Recibe la fecha para generar el resumen y devuelve el resultado
     * @param message
     * @param session
     * @throws JMSException 
     */
    @Override
    @JmsListener(destination = "${spring.activemq.topic}")
    public void onMessage(Message message, Session session) throws JMSException {
        try{
            Logger.getLogger(this.getClass().getCanonicalName()).info("Mensaje recibido "+message);
            ObjectMessage objectMessage = (ObjectMessage)message;
            Date fecha = (Date)objectMessage.getObject();
            ResumenVentas resumenVentas=resumenVentasService.get(fecha);
            ObjectMessage response=new ActiveMQObjectMessage();
            response.setJMSCorrelationID(message.getJMSCorrelationID());
            response.setObject(resumenVentas);
            MessageProducer mp=session.createProducer(message.getJMSReplyTo());
            mp.send(response);
        } catch(Exception e) {
            Logger.getLogger(this.getClass().getCanonicalName()).info("No pude procesar el mensaje");
        }

    }
    
    
}