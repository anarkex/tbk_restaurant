package cl.tbk.test.restaurant.service;

import cl.tbk.test.restaurant.entities.Item;
import cl.tbk.test.restaurant.entities.Venta;

/**
 * Validation service for Venta and Item (user input)
 * @author manuelpinto
 */
public interface ValidatorService {
    /**
     * Validates the data for the venta, and its items
     * @param venta 
     */
    void validate(Venta venta);
    /**
     * Validates the data for a single item
     * @param item 
     */
    void validate(Item item);
}
