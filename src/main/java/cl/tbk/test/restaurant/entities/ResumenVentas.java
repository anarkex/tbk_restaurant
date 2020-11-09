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
import java.util.Objects;

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

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + Objects.hashCode(this.ventas);
        hash = 67 * hash + Objects.hashCode(this.generadoTimestamp);
        hash = 67 * hash + Objects.hashCode(this.fechaVentas);
        hash = 67 * hash + Objects.hashCode(this.itemCount);
        hash = 67 * hash + Objects.hashCode(this.ventaCount);
        hash = 67 * hash + Objects.hashCode(this.montoTotal);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ResumenVentas other = (ResumenVentas) obj;
        if (!Objects.equals(this.ventas, other.ventas)) {
            return false;
        }
        if (!Objects.equals(this.generadoTimestamp, other.generadoTimestamp)) {
            return false;
        }
        if (!Objects.equals(this.fechaVentas, other.fechaVentas)) {
            return false;
        }
        if (!Objects.equals(this.itemCount, other.itemCount)) {
            return false;
        }
        if (!Objects.equals(this.ventaCount, other.ventaCount)) {
            return false;
        }
        if (!Objects.equals(this.montoTotal, other.montoTotal)) {
            return false;
        }
        return true;
    }

    
    
}
