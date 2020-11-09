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
    private List<Item> items=new ArrayList<Item>(); // items de la venta
    @Temporal(value = TemporalType.TIMESTAMP)
    private Date timestamp; // momento de la emision de esta venta
    private Integer sucursalId; // ID de la sucursal que vendió
    private String username; // persona que registró esta venta
    
    public Venta(){
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

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 19 * hash + Objects.hashCode(this.id);
        hash = 19 * hash + Objects.hashCode(this.items);
        hash = 19 * hash + Objects.hashCode(this.timestamp);
        hash = 19 * hash + Objects.hashCode(this.sucursalId);
        hash = 19 * hash + Objects.hashCode(this.username);
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
        final Venta other = (Venta) obj;
        if (!Objects.equals(this.username, other.username)) {
            return false;
        }
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        if (!Objects.equals(this.items, other.items)) {
            return false;
        }
        if (!Objects.equals(this.timestamp, other.timestamp)) {
            return false;
        }
        if (!Objects.equals(this.sucursalId, other.sucursalId)) {
            return false;
        }
        return true;
    }

}
