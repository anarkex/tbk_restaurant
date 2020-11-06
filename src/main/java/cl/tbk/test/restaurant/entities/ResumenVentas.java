/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cl.tbk.test.restaurant.entities;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Entidad que entrega el reporte solicitado por /ventas/report/a/m/d <br/>
 * Esta entidad es cachada en el cluster hazelcast
 * @author manuelpinto
 */
public class ResumenVentas implements Serializable{

    private static final long serialVersionUID = 1L;
    /**
     * Ventas de este día
     */
    private List<Venta> ventas;
    /**
     * Cuándo se generó este resumen
     */
    private Date generadoTimestamp;
    /**
     * Para qué fecha se generó este resumen
     */
    private Date fechaVentas;
    /**
     * Cuantos items se vendieron
     */
    private Integer itemCount;
    /**
     * Cuantas ventas se hicieron
     */
    private Integer ventaCount;
    /**
     * Volumen de ventas
     */
    private BigDecimal montoTotal;
    
    public ResumenVentas(){
        ventas=new ArrayList<Venta>();
        montoTotal=BigDecimal.ZERO;
        itemCount=0;
        ventaCount=0;
    }

    public List<Venta> getVentas() {
        return ventas;
    }

    public void setVentas(List<Venta> ventas) {
        this.ventas = ventas;
    }

    public Date getGeneradoTimestamp() {
        return generadoTimestamp;
    }

    public void setGeneradoTimestamp(Date generadoTimestamp) {
        this.generadoTimestamp = generadoTimestamp;
    }

    public Date getFechaVentas() {
        return fechaVentas;
    }

    public void setFechaVentas(Date fechaVentas) {
        this.fechaVentas = fechaVentas;
    }

    public Integer getItemCount() {
        return itemCount;
    }

    public void setItemCount(Integer itemCount) {
        this.itemCount = itemCount;
    }

    public Integer getVentaCount() {
        return ventaCount;
    }

    public void setVentaCount(Integer ventaCount) {
        this.ventaCount = ventaCount;
    }

    public BigDecimal getMontoTotal() {
        return montoTotal;
    }

    public void setMontoTotal(BigDecimal montoTotal) {
        this.montoTotal = montoTotal;
    }
    
    
}
