package cl.tbk.test.restaurant.exception;

/**
 * Excepcion para indicar que ocurrió un error de validación.<br/>
 * como Consumer<T>{ void accept(T); } no declara que bota nada<br/>
 * este necesita heredar de RuntimeException para lanzar el error desde
 * dentro de un Lambda
 * @author manuelpinto
 */
public class ValidationException extends RuntimeException{

    private static final long serialVersionUID = 1L;
    public ValidationException(){
        super();
    }
    
    public ValidationException(String message){
        super(message);
    }
    
    public ValidationException(String message, Throwable cause){
        super(message, cause);
    }
    
    public ValidationException(Throwable cause){
        super(cause);
    }
}
