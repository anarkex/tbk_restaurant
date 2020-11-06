package cl.tbk.test.restaurant.exception;

/**
 * Excepcion para indicar que no tiene permiso para entrar a la app<br/>
 * o el token es inválido
 * @author manuelpinto
 */
public class UnauthorizedException extends RestaurantException{

    private static final long serialVersionUID = 1L;
    public UnauthorizedException(){
        super();
    }
    
    public UnauthorizedException(String message){
        super(message);
    }
    
    public UnauthorizedException(String message, Throwable cause){
        super(message, cause);
    }
    
    public UnauthorizedException(Throwable cause){
        super(cause);
    }
}
