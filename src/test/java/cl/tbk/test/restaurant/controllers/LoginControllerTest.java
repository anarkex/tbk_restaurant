package cl.tbk.test.restaurant.controllers;

import cl.tbk.test.restaurant.controllers.utils.HttpRequestHelper;
import cl.tbk.test.restaurant.entities.Credential;
import cl.tbk.test.restaurant.service.AuthenticationService;
import io.jsonwebtoken.Claims;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import static org.junit.jupiter.api.Assertions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

/**
 * Tests the login controller endpoint
 * @author manuelpinto
 */
@SpringBootTest
public class LoginControllerTest {
    
    @Value("${auth.username}")
    String loginUsername;
    
    @Value("${auth.password}")
    String loginPassword;
    
    @Autowired
    private LoginController loginController;
    
    @Autowired
    private AuthenticationService authService;
    
    public LoginControllerTest() {
    }
    
    @BeforeAll
    public static void setUpClass() {
        Logger.getLogger(LoginControllerTest.class.getCanonicalName()).info("LoginControllerTest started");
    }
    
    @AfterAll
    public static void tearDownClass() {
        Logger.getLogger(LoginControllerTest.class.getCanonicalName()).info("LoginControllerTest finished");
    }
    
    @BeforeEach
    public void setUp() {
    }
    
    @AfterEach
    public void tearDown() {
    }
    
    /**
     * Test of login method, of class LoginController.
     */
    @org.junit.jupiter.api.Test
    public void testEmptyCredentialsLogin() {
        Credential credentials = null;
        LoginController instance = new LoginController();
        ResponseEntity<Credential> result = instance.login(credentials, HttpRequestHelper.mockHttpServletRequest());
        assert(result.getStatusCodeValue()==401);
    }

    @org.junit.jupiter.api.Test
    public void testOkCredentialsLogin() {
        Credential credentials = new Credential();
        credentials.setUsername(loginUsername);
        credentials.setPassword(loginPassword);
        ResponseEntity<Credential> result = loginController.login(credentials, HttpRequestHelper.mockHttpServletRequest());
        assert(result.getStatusCodeValue()==200);
        assert(result.getBody().getToken()!=null && result.getBody().getToken().length()>0);
        // getTokenAuthenticator
        try{
            Claims claim=authService.getClaimsFromToken(result.getBody().getToken());
            String usernameFromToken=claim.getSubject();
            assert(loginUsername.equals(usernameFromToken));
        } catch (Exception une){
            fail("username from token failed: "+une.getClass().getName(), une);
        }
    }
    
    @org.junit.jupiter.api.Test
    public void tooManyFailedLogins() {
        String username="a"+loginUsername;
        String password="b"+loginPassword;
        System.out.println("login");
        Credential credentials = new Credential();
        credentials.setUsername(username);
        credentials.setPassword(password);
        HttpServletRequest httpServletRequest=HttpRequestHelper.mockHttpServletRequest();
        loginController.login(credentials, httpServletRequest); // 0
        loginController.login(credentials, httpServletRequest); // 1
        loginController.login(credentials, httpServletRequest); // 2
        loginController.login(credentials, httpServletRequest); // 3
        loginController.login(credentials, httpServletRequest); // 4
        loginController.login(credentials, httpServletRequest); // 5
        ResponseEntity<Credential> result = loginController.login(credentials, httpServletRequest); // 6
        assert(result.getStatusCodeValue()==429);
    }
    
}
