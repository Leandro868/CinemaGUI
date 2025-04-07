package com.utp.TrailersMVC.controller;

import com.utp.TrailersMVC.model.Noticia;
import com.utp.TrailersMVC.model.Usuario;
import com.utp.TrailersMVC.service.UsuarioService;
import com.utp.trailersMVC.service.NoticiaService;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Arrays;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class RegistroNoticiaController {

    @Autowired
    private NoticiaService noticiaService;
     @Autowired
    private UsuarioService usuarioService;

    // Lista de extensiones válidas para archivos de imagen
    private final List<String> imageExtensions = Arrays.asList("image/jpeg", "image/png", "image/gif");

    // GET: Muestra el formulario de registro de noticias
    @GetMapping("/RegistroNoticia")
    public String showRegistrationForm(Model model) {
        model.addAttribute("noticia", new Noticia());
        return "RegistroNoticia";
    }

   // POST: Registra una nueva noticia
    @PostMapping("/RegistroNoticia")
    public String registerNoticia(@ModelAttribute Noticia noticia, 
                                  @RequestParam("image") MultipartFile image, Model model) {

        
         // Obtén al usuario autenticado
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken)) {

            // Obtiene el nombre de usuario del contexto de seguridad
            String username = authentication.getName();
            Usuario usuario = usuarioService.findByUsername(username); // Busca el usuario por su nombre de usuario

            // Asigna el usuario autenticado al estreno
            noticia.setUsuario(usuario);
        } else {
            System.out.println("Usuario no autenticado. No se asignará un usuario al estreno.");
            // Si no hay usuario autenticado, puedes dejar el usuario en null o manejarlo según tu lógica
            noticia.setUsuario(null);
        }
        // Validación de la imagen
        if (!image.isEmpty()) {
            String contentType = image.getContentType();

            // Verificar si el archivo subido es de un tipo de imagen válido
            if (!imageExtensions.contains(contentType)) {
                model.addAttribute("error", "El archivo subido no es una imagen válida. Por favor, suba un archivo JPG, PNG o GIF.");
                return "RegistroNoticia";
            }

            try {
                // Definir la ruta de subida de las imágenes
                String uploadsDir = "./uploads/";
                Path uploadPath = Paths.get(uploadsDir);

                // Crear el directorio si no existe
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                // Generar un nombre único para la imagen con la marca de tiempo
                String fileName = System.currentTimeMillis() + "_" + image.getOriginalFilename();
                Path filePath = uploadPath.resolve(fileName);

                // Guardar el archivo en la ruta especificada
                Files.copy(image.getInputStream(), filePath);

                // Asignar la ruta del archivo a la entidad Noticia
                noticia.setImagen("/uploads/" + fileName);

            } catch (IOException e) {
                e.printStackTrace();
                model.addAttribute("error", "Hubo un problema al subir la imagen. Por favor, intente nuevamente.");
                return "RegistroNoticia";
            }
        }
        // Guardar la noticia en la base de datos
        noticiaService.saveNoticia(noticia);

        // Redirigir a la lista de noticias después de guardar
        return "redirect:/tabla";
    }


    // GET: Listar noticias con paginación
    @GetMapping("/lista")
    public String listarNoticias(@RequestParam(defaultValue = "0") int page, Model model) {
        Page<Noticia> noticiasPage = noticiaService.getNoticiasPaginadas(page, 10);
        model.addAttribute("noticias", noticiasPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", noticiasPage.getTotalPages());
        return "listaNoticias";
    }

    // GET: Mostrar detalles de una noticia por ID
    @GetMapping("/noticias/{id}")
    public String mostrarDetalleNoticia(@PathVariable Integer id, Model model) {
        Noticia noticia = noticiaService.obtenerNoticiaPorId(id);
        // Obtener noticias relacionadas buscando por títulos que contengan palabras clave
        List<Noticia> noticiasRelacionadas = noticiaService.obtenerNoticiasRelacionadasPorTitulo(noticia);
        model.addAttribute("noticiasRelacionadas", noticiasRelacionadas);
        model.addAttribute("noticia", noticia);
        return "detalleNoticia";
    }

    // GET: Listar todas las noticias en una tabla
    @GetMapping("/tabla")
    public String listarNoticias(Model model) {
        List<Noticia> noticias = noticiaService.obtenerTodasLasNoticias();
        model.addAttribute("noticias", noticias);
        return "tabla-noticias";
    }

    // GET: Mostrar formulario de edición de una noticia por ID
    @GetMapping("/editar/{id}")
    public String mostrarFormularioEdicion(@PathVariable("id") Integer id, Model model) {
        Noticia noticia = noticiaService.getNoticiaById(id);
        if (noticia == null) {
            return "redirect:/listaNoticias"; // Redirigir a la lista si no se encuentra la noticia
        }
        model.addAttribute("noticia", noticia);
        return "editarNoticia"; // Vista del formulario de edición
    }

    // POST: Actualizar una noticia
    @PostMapping("/editar/{id}")
    public String actualizarNoticia(@PathVariable Integer id, 
                                    @ModelAttribute Noticia noticia, 
                                    @RequestParam("image") MultipartFile image, 
                                    Model model) {
        try {
            noticiaService.actualizarNoticia(id, noticia, image);
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "editarNoticia";
        }
        return "redirect:/tabla";
    }

    // GET: Eliminar una noticia por ID
    @GetMapping("/eliminar/{id}")
    public String eliminarNoticia(@PathVariable Integer id) {
        noticiaService.eliminarNoticiaPorId(id);
        return "redirect:/tabla";
    }

    // Método de búsqueda (ruta modificada para evitar conflictos)
    @GetMapping("/buscarNoticia")
    public String buscarNoticias(@RequestParam("query") String query, Model model) {
        List<Noticia> noticiasFiltradas = noticiaService.buscarNoticiasPorTitulo(query);
        model.addAttribute("noticias", noticiasFiltradas);
        return "tabla-noticias"; // Nombre de la plantilla Thymeleaf de la lista
    }
}
