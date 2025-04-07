/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.utp.TrailersMVC.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;


import java.io.IOException;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        // Obtener el rol del usuario
        String role = authentication.getAuthorities().toString();

        // Redirigir según el rol
        if (role.contains("ROLE_ADMIN")) {
            response.sendRedirect("/admin"); // Redirige a la vista de Admin
        } else if (role.contains("ROLE_MODERADOR")) {
            response.sendRedirect("/moderador"); // Redirige a la vista de Moderador
        } else {
            response.sendRedirect("/login?error=true"); // Redirige a login si no tiene un rol adecuado
        }
    }
}
