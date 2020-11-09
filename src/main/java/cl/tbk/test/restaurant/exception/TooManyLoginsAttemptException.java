package cl.tbk.test.restaurant.exception;

/**
 * Execption para indicar que se están realizando demasiados intentos de login<br/>
 * En este momento sólo contea los fallidos, pero también debería contar los exitosos<br/>
 * porque cada login nuevo es un nuevo token.
 * @author manuelpinto
 */
public class TooManyLoginsAttemptException extends RestaurantException{

    private static final long serialVersionUID = 1L;
    public TooManyLoginsAttemptException(){
        super();
    }
    
    public TooManyLoginsAttemptException(String message){
        super(message);
    }
    
    public TooManyLoginsAttemptException(String message, Throwable cause){
        super(message, cause);
    }
    
    public TooManyLoginsAttemptException(Throwable cause){
        super(cause);
    }
}
