package com.utp.TrailersMVC.controller;

import com.utp.TrailersMVC.model.Estreno;
import com.utp.TrailersMVC.model.Genero;
import com.utp.TrailersMVC.model.Usuario;
import com.utp.TrailersMVC.service.GeneroService;
import com.utp.TrailersMVC.service.EstrenoService;
import com.utp.TrailersMVC.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Controller
public class EstrenoController {

    @Autowired
    private EstrenoService estrenoService;

    @Autowired
    private GeneroService generoService;

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("estreno", new Estreno());
        model.addAttribute("generos", generoService.getAllGeneros());
        return "register";
    }

    @PostMapping("/register")
    public String registerEstreno(@ModelAttribute Estreno estreno,
            @RequestParam("imagenFile") MultipartFile imagenFile,
            @RequestParam("idGenero") Integer generoId) {

        // Obtén al usuario autenticado
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken)) {

            // Obtiene el nombre de usuario del contexto de seguridad
            String username = authentication.getName();
            Usuario usuario = usuarioService.findByUsername(username); // Busca el usuario por su nombre de usuario

            // Asigna el usuario autenticado al estreno
            estreno.setUsuario(usuario);
        } else {
            System.out.println("Usuario no autenticado. No se asignará un usuario al estreno.");
            // Si no hay usuario autenticado, puedes dejar el usuario en null o manejarlo según tu lógica
            estreno.setUsuario(null);
        }

        if (!imagenFile.isEmpty()) {
            try {
                String uploadsDir = "./uploads/";
                Path uploadPath = Paths.get(uploadsDir);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                // Obtener la fecha y hora actual y formatearla
                String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
                String originalFileName = imagenFile.getOriginalFilename();
                String fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
                String fileName = originalFileName.replace(fileExtension, "") + "_" + timestamp + fileExtension;

                Path filePath = uploadPath.resolve(fileName);
                Files.copy(imagenFile.getInputStream(), filePath);

                // Guardar la ruta de la imagen en el objeto Estreno
                estreno.setImagen("/uploads/" + fileName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        estrenoService.saveEstreno(estreno, generoId);
        return "redirect:/listaEstrenos";
    }

    // Mostrar formulario de edición
    @GetMapping("/edit/{idEstreno}")
    public String showEditForm(@PathVariable("idEstreno") Integer idEstreno, Model model) {
        Estreno estreno = estrenoService.findById(idEstreno);
        if (estreno == null) {
            return "redirect:/error"; // Redirige a una página de error si no existe
        }
        model.addAttribute("estreno", estreno);
        model.addAttribute("generos", generoService.getAllGeneros()); // Pasamos los géneros disponibles
        return "editarEstreno"; // Archivo HTML del formulario de edición
    }

    @PostMapping("/edit/{idEstreno}")
    public String updateEstreno(@PathVariable("idEstreno") Integer idEstreno,
            @ModelAttribute Estreno estreno,
            @RequestParam("imagenFile") MultipartFile imagenFile,
            @RequestParam("idGenero") Integer generoId) {

        Estreno existingEstreno = estrenoService.findById(idEstreno);
        if (existingEstreno == null) {
            return "redirect:/error"; // Redirige a una página de error si no existe
        }

        // Obtén al usuario autenticado
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken)) {

            // Obtiene el nombre de usuario del contexto de seguridad
            String username = authentication.getName();
            Usuario usuario = usuarioService.findByUsername(username); // Busca el usuario por su nombre de usuario

            // Asigna el usuario autenticado al estreno
            estreno.setUsuario(usuario);
        } else {
            System.out.println("Usuario no autenticado. No se asignará un usuario al estreno.");
            // Si no hay usuario autenticado, puedes dejar el usuario en null o manejarlo según tu lógica
            estreno.setUsuario(null);
        }

        // Actualizar campos
        existingEstreno.setTitulo(estreno.getTitulo());
        existingEstreno.setSinopsis(estreno.getSinopsis());
        existingEstreno.setDuracion(estreno.getDuracion());
        existingEstreno.setEdad(estreno.getEdad());
        if (!estreno.getIdioma().isEmpty()) {
            existingEstreno.setIdioma(estreno.getIdioma());
        }        
        existingEstreno.setFechaEstreno(estreno.getFechaEstreno());
        existingEstreno.setTrailer(estreno.getTrailer());
        existingEstreno.setEstado(estreno.getEstado());
        existingEstreno.setDiasEnCartelera(estreno.getDiasEnCartelera());
        existingEstreno.setUsuario(estreno.getUsuario());

        // Manejo de la imagen, si hay una nueva
        if (!imagenFile.isEmpty()) {
            try {
                String uploadsDir = "./uploads/";
                Path uploadPath = Paths.get(uploadsDir);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                // Obtener la fecha y hora actual y formatearla
                String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
                String originalFileName = imagenFile.getOriginalFilename();
                String fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
                String fileName = originalFileName.replace(fileExtension, "") + "_" + timestamp + fileExtension;

                Path filePath = uploadPath.resolve(fileName);
                Files.copy(imagenFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                // Guardar la ruta de la imagen en el objeto existingEstreno
                existingEstreno.setImagen("/uploads/" + fileName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Actualizar género
        Genero genero = generoService.findById(generoId);
        existingEstreno.setGenero(genero);

        // Guardar el estreno actualizado
        estrenoService.save(existingEstreno);

        return "redirect:/listaEstrenos"; // Redirige a la página principal o a una lista de estrenos
    }

    @GetMapping("/listaEstrenos")
    public String listarPeliculas(Model model,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Estreno> peliculas = estrenoService.getLatestEstreno(pageable);

        model.addAttribute("estrenos", peliculas);
        model.addAttribute("generos", generoService.getAllGeneros());
        return "listarPeliculas";
    }

    @GetMapping("/estreno/{id}")
    public String showEstrenoDetails(@PathVariable("id") Integer id, Model model) {
        Estreno estreno = estrenoService.findById(id);
        if (estreno == null) {
            return "redirect:/error"; // Redirige a una página de error si el estreno no existe
        }
        model.addAttribute("estreno", estreno);
        return "detallesEstreno"; // Nombre del archivo HTML de la página de detalles
    }

    @GetMapping("/proximos-estrenos")
    public String mostrarProximosEstrenos(Model model) {
        List<Estreno> proximosEstrenos = estrenoService.obtenerProximosEstrenos();
        model.addAttribute("proximosEstrenos", proximosEstrenos);
        return "proximosEstrenos";  // Nombre de la vista (por ejemplo, 'proximosEstrenos.html')
    }

    @GetMapping("/inicioAdmin")
    public String home(Model model, @RequestParam(defaultValue = "0") int page) {
        Page<Estreno> EstrenoPage = estrenoService.getAllEstrenoAdmin(PageRequest.of(page, 9));

        model.addAttribute("Estrenos", EstrenoPage);
        return "inicioAdmin";
    }

    @GetMapping("/buscar")
    public String buscarPorTitulo(@RequestParam("nombre") String nombre, Model model) {
        List<Estreno> resultados = estrenoService.buscarPorNombre(nombre);

        model.addAttribute("resultados", resultados);
        model.addAttribute("nombreBuscado", nombre);

        return "resultadoBusqueda";
    }

//    @GetMapping("/filtrarPorGenero")
//    public String filtrarPorGenero(
//            @RequestParam(name = "genero", required = false, defaultValue = "Todos") String genero,
//            @RequestParam(name = "page", defaultValue = "0") int page,
//            Model model) {
//        Page<Estreno> estrenos = estrenoService.filtrarPorGenero(genero, page);
//        model.addAttribute("genero", genero);
//        model.addAttribute("estrenos", estrenos.getContent());
//        model.addAttribute("currentPage", page);
//        model.addAttribute("totalPages", estrenos.getTotalPages());
//        return "filtrarPorGenero";
//    }
    @GetMapping("/filtrarPorGenero")
    public String filtrarPorGenero(Model model,
            @RequestParam(name = "genero", required = false, defaultValue = "Todos") String genero,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Estreno> peliculas = estrenoService.filtrarPorGenero(genero, pageable);

        model.addAttribute("estrenos", peliculas); // Datos de las películas
        model.addAttribute("genero", genero); // Género seleccionado        
        return "filtrarPorGenero";
    }

    @PostMapping("/listaEstrenos")
    public String evaluarEstados() {
        estrenoService.evaluarEstadosEstrenos(); // Actualiza los estados
        return "redirect:/listaEstrenos"; // Redirige a la misma página para recargarla
    }
}
