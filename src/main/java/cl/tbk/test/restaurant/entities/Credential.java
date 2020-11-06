/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cl.tbk.test.restaurant.entities;

import java.io.Serializable;

/**
 * Una entidad para manejar la petición y respuesta del login.<br/>
 * peticion llena username y password (puede llenar otras cosas, pero serán ignoradas)<br/>
 * respuesta llena username, token y token type;
 * @author manuelpinto
 */
public class Credential implements Serializable{

    private static final long serialVersionUID = 1L;
    private String username;
    private String password;
    private String token;
    private String tokenType;
    
    public Credential(){
        
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }
    
}
