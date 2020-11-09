package cl.tbk.test.restaurant.config;

import javax.servlet.Filter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Since we're working with JSON as api language we need JWT and Authorization Bearer<br/>
 * This didn't worked as expected, so I had to put a workaround in JwtAuthFilter.
 * @author manuelpinto
 */
@Configuration
@Order(2)
@EnableWebSecurity
public class VentaSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {

    @Autowired
    @Qualifier("jwtauth")
    Filter jwtAuthFilter;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable().headers().frameOptions().disable()
                .and().addFilterAfter(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeRequests().antMatchers("/tbk/restaurant/v1/ventas/**").authenticated()
                .and()
                .authorizeRequests().anyRequest().permitAll();
                
    }

}
