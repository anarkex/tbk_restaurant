/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cl.tbk.test.restaurant.entities;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;

/**
 * Entidad que almacena los detalles de un item<br/>
 *
 * @author manuelpinto
 */
@Entity
public class Item implements Serializable{
    
    @Id
    UUID id;
    Long codigo;
    Integer cantidad;
    BigDecimal precioUnitario;
    String detalle;
    
    private static final long serialVersionUID = 1L;
    public Item(){
        id=UUID.randomUUID();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(Long codigo) {
        this.codigo = codigo;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(BigDecimal precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public String getDetalle() {
        return detalle;
    }

    public void setDetalle(String detalle) {
        this.detalle = detalle;
    }
    
    /**
     * calcula el precio total para este ítem de la boleta
     * @return cantidad * precio unitario o 0 si falta alguno de ellos
     */
    @Transient
    public BigDecimal getPrecioTotal(){
        if(precioUnitario!=null && cantidad!=null)
            return precioUnitario.multiply(new BigDecimal(cantidad));
        return BigDecimal.ZERO;
    }
}
