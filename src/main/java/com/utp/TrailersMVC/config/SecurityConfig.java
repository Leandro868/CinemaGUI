package com.utp.TrailersMVC.config;
import com.utp.TrailersMVC.service.UserDetailService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UserDetailService userDetailService;
    private final CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;

    // Constructor
    public SecurityConfig(UserDetailService userDetailService, CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler) {
        this.userDetailService = userDetailService;
        this.customAuthenticationSuccessHandler = customAuthenticationSuccessHandler;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Deshabilitar CSRF (para simplificar, aunque no se recomienda para producción)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/registro","/","home","Cartelera","lista" ,"/estreno/**" ,"/error", "/reset-password", 
                        "/reset-password-form", "/update-password", "/login", "/css/**", "/js/**", "/imagenes/**", "/uploads/**","/noticias/{id}").permitAll()
                 .requestMatchers("/RegistroNoticia","/register","/tabla").hasAnyAuthority("ROLE_ADMIN", "ROLE_MODERADOR") // Acceso solo para ambos roles a esta página
                .requestMatchers("/admin/**", "/filtrar/**", "/listaPeliculas/**", "/editar/{id}", "/eliminar/{id}", "/buscarNoticia").hasAuthority("ROLE_ADMIN") // Acceso solo para ROLE_ADMIN
                .requestMatchers("/moderador/**").hasAuthority("ROLE_MODERADOR") // Acceso solo para ROLE_MODERADOR
                .anyRequest().authenticated() // Resto de las solicitudes requieren autenticación
            )
            .formLogin(form -> form
                .loginPage("/login") // Página de login personalizada
                .loginProcessingUrl("/login") // URL para procesar el login
                .usernameParameter("username") // Nombre de parámetro para el usuario
                .passwordParameter("password") // Nombre de parámetro para la contraseña
                .successHandler(customAuthenticationSuccessHandler) // Manejador de éxito de autenticación
                .failureUrl("/login?error=true") // URL de error si la autenticación falla
                .permitAll() // Permite el acceso a la página de login
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/login?logout=true") // Redirige a la página de login tras logout
                .invalidateHttpSession(true) // Invalida la sesión al cerrar sesión
                .deleteCookies("JSESSIONID") // Elimina las cookies de sesión
                .permitAll() // Permite el acceso al logout
            );

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager(); // Configuración del AuthenticationManager
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // Cifrado de contraseñas con BCrypt
    }
}
