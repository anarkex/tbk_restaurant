/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cl.tbk.test.restaurant.service;

import cl.tbk.test.restaurant.entities.Credential;
import cl.tbk.test.restaurant.exception.TooManyLoginsAttemptException;
import cl.tbk.test.restaurant.exception.UnauthorizedException;
import io.jsonwebtoken.Claims;

/**
 *
 * @author manuelpinto
 */
public interface AuthenticationService {
    Credential authenticate(String username, String password) throws UnauthorizedException,TooManyLoginsAttemptException;
    Credential getFromToken(String token) throws UnauthorizedException;
    Claims getClaimsFromToken(String token);
}
