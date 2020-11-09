package cl.tbk.test.restaurant.security;

import cl.tbk.test.restaurant.service.AuthenticationService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Filtro encargaod de la Autorización del sistema<br/>
 * No se por que tengo la sensación de que esto no debería hacerse asi
 *
 * @author manuelpinto
 */
@Service
@Qualifier("jwtauth")
public class JWTAuthorizationFilter extends OncePerRequestFilter {

    @Autowired
    AuthenticationService auth;

    private final String HEADER = "Authorization";
    private final String PREFIX = "Bearer ";


    /**
     * Fija o quita el SecurityContext según el token que le presenten<br/>
     * No me queda 100% claro por qué este filtro no está restringido a ciertas url
     *
     * @param request
     * @param response
     * @param filterChain
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Claims claims = null;
        try {
            // Obtiene los detalles del token
            claims = getClaimFromToken(request, response);
        } catch (SignatureException | ExpiredJwtException | UnsupportedJwtException | MalformedJwtException e) {
            // do nothing,
            // si la ruta no necesita authorized, nos e va a fijar en el securititoken
            // de ahí ya es pega del websecurityconfigureAdapter
        }
        if (claims != null && claims.get("authorities") != null) {
            setUpSpringAuthentication(claims);
        } else {
            SecurityContextHolder.clearContext();
        }
        filterChain.doFilter(request, response);
    }

    /**
     * Obtiene el detalle del token
     *
     * @param request
     * @param response
     * @return
     */
    private Claims getClaimFromToken(HttpServletRequest request, HttpServletResponse response) {
        String authorization = request.getHeader(HEADER);
        if (authorization == null || !authorization.toLowerCase().startsWith(PREFIX.toLowerCase())) {
            throw new MalformedJwtException("Missing Authorization");
        }
        String jwtToken = authorization.substring(PREFIX.length());
        Claims claim = auth.getClaimsFromToken(jwtToken);
        return claim;
    }

    /**
     * Pone todo lo necesario para hacer de esta sesión una sesión autorizada
     *
     * @param claims
     */
    private void setUpSpringAuthentication(Claims claims) {
        @SuppressWarnings("unchecked")
        List<String> authorities = (List<String>) claims.get("authorities");
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(claims.getSubject(), null,
                authorities.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList()));
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

}
