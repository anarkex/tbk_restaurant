/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Filtro encargaod de la Autorización del sistema
 * @author manuelpinto
 */
@Service
@Qualifier("jwtauth")
public class JWTAuthorizationFilter extends OncePerRequestFilter {

    @Autowired
    AuthenticationService auth;
    
    private final String HEADER = "Authorization";
    private final String PREFIX = "Bearer ";
    
    private final RequestMatcher ventasUrl=new AntPathRequestMatcher("/ventas/**");

    /**
     * Ejecuta el filtro actual
     * @param request
     * @param response
     * @param filterChain
     * @throws ServletException
     * @throws IOException 
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if(!ventasUrl.matches(request)){ //Workaroaund: Couldn't' do a filter mapper that don't includes this /login path
            filterChain.doFilter(request, response);
        } else {
            try {
                // Obtiene los detalles del token
                Claims claims = getClaimFromToken(request, response);
                if(claims.get("authorities")!=null){
                    setUpSpringAuthentication(claims);
                } else {
                    SecurityContextHolder.clearContext();
                }
                filterChain.doFilter(request, response);
            } catch (SignatureException | ExpiredJwtException | UnsupportedJwtException | MalformedJwtException e) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.sendError(HttpServletResponse.SC_FORBIDDEN, e.getMessage());
                return;
            }
        }
    }

    /**
     * Obtiene el detalle del token
     * @param request
     * @param response
     * @return 
     */
    private Claims getClaimFromToken(HttpServletRequest request, HttpServletResponse response) {
        String authorization=request.getHeader(HEADER);
        if(authorization==null || !authorization.toLowerCase().startsWith(PREFIX.toLowerCase()))
            throw new MalformedJwtException("Missing Authorization");
        String jwtToken=authorization.substring(PREFIX.length());
        Claims claim=auth.getClaimsFromToken(jwtToken);
        return claim;
    }
    
    /**
     * Pone todo lo necesario para hacer de esta sesión una sesión autorizada
     * @param claims 
     */
    private void setUpSpringAuthentication(Claims claims){
        @SuppressWarnings("unchecked")
        List<String> authorities=(List<String>)claims.get("authorities");
        UsernamePasswordAuthenticationToken auth=new UsernamePasswordAuthenticationToken(claims.getSubject(), null,
        authorities.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList()));
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

}
