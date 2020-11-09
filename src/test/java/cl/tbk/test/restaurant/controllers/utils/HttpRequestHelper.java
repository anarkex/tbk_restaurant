package cl.tbk.test.restaurant.controllers.utils;

import javax.servlet.http.HttpServletRequest;
import org.springframework.mock.web.MockHttpServletRequest;

/**
 *
 * @author manuelpinto
 */
public final class HttpRequestHelper {
    private HttpRequestHelper(){}

    public static HttpServletRequest mockHttpServletRequest(){
        MockHttpServletRequest mock=new MockHttpServletRequest();
        mock.setRemoteAddr("server.test");
        return mock;
    } 
    
    public static ThreadLocal<String> getThreadLocal(){
        ThreadLocal<String> threadLocal=new ThreadLocal<>();
        return threadLocal;
    }
   
}
