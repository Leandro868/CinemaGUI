package com.utp.TrailersMVC.service;

import com.utp.TrailersMVC.model.Role;
import com.utp.TrailersMVC.model.Usuario;
import com.utp.TrailersMVC.repository.RoleRepository;
import com.utp.TrailersMVC.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
@Transactional
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UsuarioService(UsuarioRepository usuarioRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Registro de usuario
    public Usuario registrarUsuario(String username, String email, String password) {
        // Verificar si el usuario ya existe
        if (usuarioRepository.findByUsername(username) != null) {
            throw new RuntimeException("El nombre de usuario ya está en uso");
        }
        if (usuarioRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("El email ya está registrado");
        }

        Usuario usuario = new Usuario();
        usuario.setUsername(username);
        usuario.setEmail(email);
        usuario.setPassword(passwordEncoder.encode(password));

        // Buscar o crear el rol Moderador
        Role roleModerador = roleRepository.findByName("ROLE_MODERADOR");
        if (roleModerador == null) {
            roleModerador = new Role();
            roleModerador.setName("ROLE_USER");
            roleModerador = roleRepository.save(roleModerador);
        }

        Set<Role> roles = new HashSet<>();
        roles.add(roleModerador);
        usuario.setRoles(roles);

        return usuarioRepository.save(usuario);
    }

    // Buscar usuario por email
    public Optional<Usuario> buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    // Actualizar la contraseña
    public void actualizarPassword(Usuario usuario, String nuevaPassword) {
        usuario.setPassword(passwordEncoder.encode(nuevaPassword));
        usuario.setResetToken(null); // Limpiar el token de reset
        usuarioRepository.save(usuario);
    }

    // Buscar usuario por token de reset
    public Optional<Usuario> buscarPorResetToken(String token) {
        return usuarioRepository.findByResetToken(token);
    }

    // Generar token de restablecimiento
    public String generarTokenDeRestablecimiento(String email) {
        Optional<Usuario> usuarioOptional = usuarioRepository.findByEmail(email);
        if (usuarioOptional.isPresent()) {
            Usuario usuario = usuarioOptional.get();
            String token = UUID.randomUUID().toString();
            usuario.setResetToken(token);
            usuarioRepository.save(usuario); // Guardamos el usuario con el token
            return token;
        }
        return null; // Si el usuario no existe, devolver null
    }

//    public List<Usuario> obtenerUsuariosConRoleId(Long roleId) {
//        return usuarioRepository.findUsuariosByRoleId(roleId);
//    }
    public List<Usuario> obtenerTodosLosUsuarios() {
        return usuarioRepository.findAll();
    }

    public List<Role> getAllRoles() {
        return roleRepository.findAll(); // Retorna todos los roles
    }

    public Usuario save(Usuario usuarioExistente) {
        return usuarioRepository.save(usuarioExistente);
    }

    public Optional<Usuario> findById(Long id) {
        return usuarioRepository.findById(id); // Busca al usuario por ID en el repositorio
    }

    public Usuario findByUsername(String username) {
        return usuarioRepository.findByUsername(username);
    }

}
