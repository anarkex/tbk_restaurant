package cl.tbk.test.restaurant.entities;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;

/**
 * Entidad que almacena los detalles de un item<br/>
 * y calcula sus montos
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

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 53 * hash + Objects.hashCode(this.id);
        hash = 53 * hash + Objects.hashCode(this.codigo);
        hash = 53 * hash + Objects.hashCode(this.cantidad);
        hash = 53 * hash + Objects.hashCode(this.precioUnitario);
        hash = 53 * hash + Objects.hashCode(this.detalle);
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
        final Item other = (Item) obj;
        if (!Objects.equals(this.detalle, other.detalle)) {
            return false;
        }
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        if (!Objects.equals(this.codigo, other.codigo)) {
            return false;
        }
        if (!Objects.equals(this.cantidad, other.cantidad)) {
            return false;
        }
        if (!Objects.equals(this.precioUnitario, other.precioUnitario)) {
            return false;
        }
        return true;
    }
}
