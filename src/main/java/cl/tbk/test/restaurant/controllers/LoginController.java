package cl.tbk.test.restaurant.controllers;

import cl.tbk.test.restaurant.entities.Credential;
import cl.tbk.test.restaurant.exception.TooManyLoginsAttemptException;
import cl.tbk.test.restaurant.exception.UnauthorizedException;
import cl.tbk.test.restaurant.service.AuthenticationService;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * This is the first part of the test.<br/>
 * I'll create a Credentials object to reply the login attempt.<br/>
 * Also there's a Hazelcast cluster recording each failed login attempt<br/>
 * 
 * @author manuelpinto
 */
@RestController
@RequestMapping("/tbk/restaurant/v1")
public class LoginController {

    private static final Logger LOG = Logger.getLogger(LoginController.class.getName());

    @Autowired
    private AuthenticationService authService;

    public LoginController() {

    }

    /**
     * <pre>Exponer endpoint de login, el cual debe aceptar un nombre y usuario y contraseña, 
     * las cuales deben ser almacenadas de manera segura, como es un ejemplo, no 
     * es necesario crear un api de creación de usuario y basta con dejar en el 
     * Readme las credenciales (nombre de usuario y contraseña) creado previamente 
     * para poder simular la llamada al login exitoso.</pre>
     * 
     * Este es el endpoint para loguear usuarios.<br/>
     * No tiene sentido pedirle un nombre, así que asumo que "debe aceptar un nombre y usuario y contraseña" en realidad dice
     * "debe aceptar un nombre <u><b>de</b></u> usuario y contraseña".<br/>
     * Si quiere ponerle un nombre está bien, pero yo lo voy a ignorar.
     * @param credentials
     * @param request
     * @return 
     */
    
    @RequestMapping(value = "login", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    @ResponseBody
    public ResponseEntity<Credential> login(@RequestBody Credential credentials, HttpServletRequest request) {
        try {
            validate(credentials);
            // we could include the remote ip in the token, just in case to try avoid session hijacking
            Credential authenticated = authService.authenticate(credentials.getUsername(), credentials.getPassword());
            ResponseEntity<Credential> response = null;
            response = ResponseEntity.ok(authenticated);
            return response;
        } catch (UnauthorizedException ex) {
            Logger.getLogger(LoginController.class.getName()).log(Level.WARNING, ex.getClass().getName()+" for "+(credentials==null?"NULL":credentials.getUsername()));
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        } catch (TooManyLoginsAttemptException ex) {
            Logger.getLogger(LoginController.class.getName()).log(Level.WARNING, ex.getClass().getName()+" for "+(credentials==null?"NULL":credentials.getUsername())+" from "+request.getRemoteAddr());
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(null);
        }
    }

    /**
     * Validate credentials<br/>
     * Just take in consideratino JSON has almost no rules and the deserialization to object
     * is based on what json has in the payload.<br/>
     * If the payload doesn't contains any username/password, then it will came null<br/>
     * The NullPointerException is awful, so we need to catch it before to send, at least, an "UnauthorizedException"
     * @param credentials 
     */
    private void validate(Credential credentials) throws UnauthorizedException{
        if(credentials==null) throw new UnauthorizedException("Credential is null");
        if(credentials.getUsername()==null) throw new UnauthorizedException("Credential Username is null");
        if(credentials.getPassword()==null) throw new UnauthorizedException("Credential Password is null");
        // ok ok, everything looks good now.
    }
}
