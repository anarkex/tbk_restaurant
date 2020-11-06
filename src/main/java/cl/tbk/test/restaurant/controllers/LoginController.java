/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cl.tbk.test.restaurant.controllers;

import cl.tbk.test.restaurant.entities.Credential;
import cl.tbk.test.restaurant.exception.TooManyLoginsAttemptException;
import cl.tbk.test.restaurant.exception.UnauthorizedException;
import cl.tbk.test.restaurant.service.AuthenticationService;
import java.util.HashMap;
import java.util.Map;
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
     * /login recive un JSON con:
     * { "username": "<Usuario>", "password": "<Password>" }
     * 
     * @param credentials
     * @param request
     * @return 
     */
    
    @RequestMapping(value = "/login", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    @ResponseBody
    public ResponseEntity<Credential> login(@RequestBody Credential credentials, HttpServletRequest request) {
        try {
            // we could include the remote ip in the token, just in case to try avoid session hijacking
            Credential authenticated = authService.authenticate(credentials.getUsername(), credentials.getPassword());
            ResponseEntity<Credential> response = null;
            response = ResponseEntity.ok(authenticated);
            return response;
        } catch (UnauthorizedException ex) {
            Logger.getLogger(LoginController.class.getName()).log(Level.SEVERE, null, ex);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        } catch (TooManyLoginsAttemptException ex) {
            Logger.getLogger(LoginController.class.getName()).log(Level.SEVERE, null, ex);
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(null);
        }
    }

}