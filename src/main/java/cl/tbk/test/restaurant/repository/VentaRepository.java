package cl.tbk.test.restaurant.repository;

import cl.tbk.test.restaurant.entities.Venta;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Bridge entre la lógica y la base.
 * @author manuelpinto
 */
@Repository
public interface VentaRepository extends JpaRepository<Venta, UUID>{
    /**
     * Obtiene las ventas del período como un listado
     * @param from
     * @param to
     * @return 
     */
    @Query
    List<Venta> getVentasByTimestampBetween(Date from, Date to);
}
