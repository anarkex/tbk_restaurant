/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cl.tbk.test.restaurant.service.impl;

import cl.tbk.test.restaurant.App;
import cl.tbk.test.restaurant.entities.Credential;
import cl.tbk.test.restaurant.exception.RestaurantException;
import cl.tbk.test.restaurant.exception.TooManyLoginsAttemptException;
import cl.tbk.test.restaurant.exception.UnauthorizedException;
import cl.tbk.test.restaurant.service.AuthenticationService;
import com.hazelcast.core.HazelcastInstance;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Service;

/**
 * Servicio preocupado de la autorizacion<br/>
 * Este servicio debería encargarse de crear usuarios y manejar 
 * @author manuelpinto
 */
@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    @Value("${auth.username}")
    private String defaultUsername;

    @Value("${auth.password}")
    private String defaultPassword;

    @Value("${auth.tokenType}")
    private String tokenType;

    @Value("${auth.tokenIdName")
    private String tokenIdName;

    /**
     * Cuanto le vamos a poner al token JWT de duración
     */
    @Value("${auth.timeToLive}")
    private Long jwtExpirationTime;

    /**
     * Max login attempts inside a threshold defined in hazelcast (map entry
     * idle ttl)
     */
    @Value("${auth.maxLoginAttempts}")
    private Integer maxLoginAttempts;    
    
    @Value("${auth.keyBits}")
    private Integer keyBits;

    private Key key = null;

    @Autowired
    private HazelcastInstance hz;
    
    /**
     * Mantiene el conteo de intentos de login fallidos
     */
    Map<String, Integer> loginAttempts;
    /**
     * Mantiene parámetros básicos del cluster (quien es la base de datos y otras cosas)
     */
    Map<String, String> parameters;

    // Let's make this clusterizable
    @PostConstruct
    private void initializeHazelcast() {
        // This is my new toy, I'll try to use this wherever I can
        loginAttempts = hz.getMap(App.M_LOGIN_ATTEMPTS); // intentos de login fallido
        parameters = hz.getMap(App.M_PARAMETERS); // parámetros dinámicos
        if (!parameters.containsKey(App.P_SECRET_KEY)) { // esto genera la clave de 64 bytes (512 bits) para encriptar los JWT. TIENE que ser distribuido para que los otros nodos sepan como se encriptó el mio y puedan validar
            String key = RandomStringUtils.random((int)(5+(keyBits*1.0)/8.0)); // just need 512bits = 
            parameters.putIfAbsent(App.P_SECRET_KEY, key);
        }
        try {
            key = Keys.hmacShaKeyFor(parameters.get(App.P_SECRET_KEY).getBytes()); // key para firmar el JWT
        } catch ( Exception ex) {
            Logger.getLogger(AuthenticationServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
            throw new Error("JWT Encryption initialization failed");
        }

    }

    /**
     * Authenticate user/password against the default user/password<br/>
     * This creates the token that the user will present to the services<br/>
     *
     * @param username
     * @param password
     * @return Credential object with username/token/token-type
     * @throws RestaurantException if unauthorized or too many logins attempt
     */
    @Override
    public Credential authenticate(String username, String password) throws UnauthorizedException, TooManyLoginsAttemptException {
        checkLoginAttempts(username);
        if (username.equals(defaultUsername) && password.equals(defaultPassword)) {
            String token = getJWTToken(username);
            Credential credential = new Credential();
            credential.setUsername(username);
            credential.setToken(token.toString());
            credential.setTokenType(tokenType);
            loginAttempts.remove(username); // Successful login will reset attempt count
            return credential;
        }
        throw new UnauthorizedException();
    }

    /**
     * Security measure in case of password guessing<br/>
     * Since I would like to see this in a hazelcast cloud we need to ask other nodes about login attempt count
     *
     * @param username
     * @throws TooManyLoginsAttemptException
     */
    private void checkLoginAttempts(String username) throws TooManyLoginsAttemptException {
        if (loginAttempts.containsKey(username)) {
            Integer attempts = loginAttempts.get(username);
            if (attempts >= maxLoginAttempts) {
                throw new TooManyLoginsAttemptException();
            }
            attempts++;
            loginAttempts.put(username, attempts);
        } else {
            loginAttempts.put(username, new Integer(1));
        }
    }

    /**
     * Get Claims from JWToken<br/>
     * We assume that if this is well-signed then it was authenticated by us
     * @param token
     * @return 
     */
    @Override
    public Claims getClaimsFromToken(String token) {
        Jws<Claims> jwsClaims = Jwts.parserBuilder().
                setSigningKey(key)
                .build()
                .parseClaimsJws(token);
        return jwsClaims.getBody();
    }
    
    /**
     * Creates a JWTToken<br/>
     * we rely on the signature to validate token authenticity<br/>
     * OF COURSE we should use a stronger signature algorithm in prod<br/>
     * You can control the expiration time from application.properties auth.timeToLive
     *
     * @param username
     * @return
     */
    private String getJWTToken(String username) {
        List<GrantedAuthority> grantedAuthorities = AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_USER");
        String token = Jwts
                .builder()
                .setId(tokenIdName)
                .setSubject(username)
                .claim("authorities",
                        grantedAuthorities.stream()
                                .map(GrantedAuthority::getAuthority)
                                .collect(Collectors.toList()))
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + (jwtExpirationTime * 1000)))
                .signWith(key).compact();
        return token;
    }

}
