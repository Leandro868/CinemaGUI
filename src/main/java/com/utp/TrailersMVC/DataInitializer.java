
package com.utp.TrailersMVC;

import com.utp.TrailersMVC.model.Role;
import com.utp.TrailersMVC.model.Usuario;
import com.utp.TrailersMVC.repository.RoleRepository;
import com.utp.TrailersMVC.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Set;

@Configuration
public class DataInitializer {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Bean
    CommandLineRunner initData(RoleRepository roleRepository, UsuarioRepository usuarioRepository) {
        return args -> {
            // Inicializar roles
            initRoles(roleRepository);
            
            // Inicializar usuario admin
            initAdminUser(roleRepository, usuarioRepository);
        };
    }

    private void initRoles(RoleRepository roleRepository) {
        // Crear ROLE_ADMIN si no existe
        if (roleRepository.findByName("ROLE_ADMIN") == null) {
            Role roleAdmin = new Role();
            roleAdmin.setName("ROLE_ADMIN");
            roleRepository.save(roleAdmin);
        }

        // Crear ROLE_USER si no existe
        if (roleRepository.findByName("ROLE_MODERADOR") == null) {
            Role roleModerador = new Role();
            roleModerador.setName("ROLE_MODERADOR");
            roleRepository.save(roleModerador);
        }
    }

    private void initAdminUser(RoleRepository roleRepository, UsuarioRepository usuarioRepository) {
        // Verificar si el usuario admin ya existe
        if (usuarioRepository.findByUsername("admin") == null) {
            Usuario admin = new Usuario();
            admin.setUsername("admin");
            admin.setEmail("admin@example.com");
            admin.setPassword(passwordEncoder.encode("admin123")); // Contraseña por defecto

            // Asignar rol de admin
            Role adminRole = roleRepository.findByName("ROLE_ADMIN");
            Set<Role> roles = new HashSet<>();
            roles.add(adminRole);
            admin.setRoles(roles);

            usuarioRepository.save(admin);
        }
    }
}
