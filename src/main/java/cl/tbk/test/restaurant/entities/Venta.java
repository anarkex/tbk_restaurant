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
import java.util.UUID;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

/**
 * Mantiene los datos de la Venta
 * @author manuelpinto
 */
@Entity
public class Venta implements Serializable{
    
    private static final long serialVersionUID = 1L;
    
    @Id
    private UUID id;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private List<Item> items; // items de la venta
    @Temporal(value = TemporalType.TIMESTAMP)
    private Date timestamp; // momento de la emision de esta venta
    private Integer sucursalId; // ID de la sucursal que vendió
    private String username; // persona que registró esta venta
    
    public Venta(){
        items=new ArrayList<>();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public Integer getSucursalId() {
        return sucursalId;
    }

    public void setSucursalId(Integer sucursalId) {
        this.sucursalId = sucursalId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    
    /**
     * Obtiene el monto de esta venta
     * @return 
     */
    @Transient
    public BigDecimal getMontoTotalDeVenta(){
        if(items==null || items.size()==0) return BigDecimal.ZERO;
        return items.parallelStream().map(Item::getPrecioTotal).reduce(BigDecimal.ZERO, (venta,sum)->sum.add(venta));
    }

}
