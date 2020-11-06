package cl.tbk.test.restaurant.exception;

/**
 * Excepcion específica de este sistema
 * @author manuelpinto
 */
public class RestaurantException extends Exception{

    private static final long serialVersionUID = 1L;
    public RestaurantException(){
        super();
    }
    
    public RestaurantException(String message){
        super(message);
    }
    
    public RestaurantException(String message, Throwable cause){
        super(message, cause);
    }
    
    public RestaurantException(Throwable cause){
        super(cause);
    }
}
