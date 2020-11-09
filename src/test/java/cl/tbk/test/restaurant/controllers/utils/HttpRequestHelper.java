/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cl.tbk.test.restaurant.controllers.utils;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
