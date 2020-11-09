package cl.tbk.test.restaurant.service;

import cl.tbk.test.restaurant.entities.Credential;
import cl.tbk.test.restaurant.exception.TooManyLoginsAttemptException;
import cl.tbk.test.restaurant.exception.UnauthorizedException;
import io.jsonwebtoken.Claims;

/**
 * Servicio de Autorización para dar por válido un usuario<br/>
 * y obtener los Claims desde el token (para sacar el usuario del token)
 * @author manuelpinto
 */
public interface AuthenticationService {
    Credential authenticate(String username, String password) throws UnauthorizedException,TooManyLoginsAttemptException;
    Claims getClaimsFromToken(String token);
}
