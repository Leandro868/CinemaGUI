package com.utp.TrailersMVC.controller;

import com.utp.TrailersMVC.model.Usuario;
import com.utp.TrailersMVC.service.UsuarioService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final JavaMailSender mailSender;

    @Autowired
    public UsuarioController(UsuarioService usuarioService, JavaMailSender mailSender) {
        this.usuarioService = usuarioService;
        this.mailSender = mailSender;
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/registro")
    public String showRegistrationForm(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "registro";
    }

    @PostMapping("/registro")
    public String registerUser(@ModelAttribute Usuario usuario,
            BindingResult result,
            RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "registro";
        }

        try {
            usuarioService.registrarUsuario(
                    usuario.getUsername(),
                    usuario.getEmail(),
                    usuario.getPassword()
            );
            redirectAttributes.addFlashAttribute("successMessage",
                    "Registro exitoso. Por favor, inicia sesión.");
            return "redirect:/login";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/register";
        }
    }

    @GetMapping("/reset-password")
    public String showResetPasswordForm() {
        return "reset-password";
    }

    @PostMapping("/reset-password")
    public String resetPassword(@RequestParam("email") String email,
            RedirectAttributes redirectAttributes) {
        String token = usuarioService.generarTokenDeRestablecimiento(email); // Obtener el token generado
        if (token != null) {
            // Crear el enlace para el restablecimiento
            String resetLink = "http://localhost:8080/reset-password-form?token=" + token;
            enviarCorreoRestablecimiento(email, resetLink);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Se ha enviado un enlace de recuperación a tu correo.");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "No se encontró ninguna cuenta con ese correo.");
        }
        return "redirect:/login";
    }

    @GetMapping("/reset-password-form")
    public String showNewPasswordForm(@RequestParam("token") String token,
            Model model,
            RedirectAttributes redirectAttributes) {
        Optional<Usuario> usuarioOptional = usuarioService.buscarPorResetToken(token);
        if (usuarioOptional.isPresent()) {
            model.addAttribute("token", token);
            return "update-password";
        } else {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "El enlace de recuperación no es válido o ha expirado.");
            return "redirect:/login";
        }
    }

    @PostMapping("/update-password")
    public String updatePassword(@RequestParam("token") String token,
            @RequestParam("password") String password,
            RedirectAttributes redirectAttributes) {
        Optional<Usuario> usuarioOptional = usuarioService.buscarPorResetToken(token);
        if (usuarioOptional.isPresent()) {
            Usuario usuario = usuarioOptional.get();
            usuarioService.actualizarPassword(usuario, password);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Tu contraseña ha sido actualizada exitosamente.");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "El enlace de recuperación no es válido o ha expirado.");
        }
        return "redirect:/login";
    }

    @GetMapping("/admin")
    public String adminPage() {
        return "admin";
    }

    @GetMapping("/moderador")
    public String moderadorPage() {
        return "moderador";
    }

    private void enviarCorreoRestablecimiento(String email, String link) {
        SimpleMailMessage mensaje = new SimpleMailMessage();
        mensaje.setTo(email);
        mensaje.setSubject("Restablecimiento de contraseña");
        mensaje.setText("Para restablecer tu contraseña, haz clic en el siguiente enlace:\n\n"
                + link + "\n\nEste enlace expirará en 24 horas.");
        mailSender.send(mensaje);
    }

//    @GetMapping("/role/{roleId}")
//    public List<Usuario> obtenerUsuariosPorRole(@PathVariable Long roleId) {
//        return usuarioService.obtenerUsuariosConRoleId(roleId);
//    }

    @GetMapping("/listaUsuarios")
    public String listarUsuarios(Model model) {
        List<Usuario> usuarios = usuarioService.obtenerTodosLosUsuarios();
        model.addAttribute("usuarios", usuarios);
        return "listaUsuarios";
    }
    
}
