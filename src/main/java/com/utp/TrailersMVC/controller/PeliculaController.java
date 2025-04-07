package com.utp.TrailersMVC.controller;

import com.utp.TrailersMVC.model.Estreno;
import com.utp.TrailersMVC.model.Pelicula;
import com.utp.TrailersMVC.service.EstrenoService;
import com.utp.TrailersMVC.service.GeneroService;
import com.utp.TrailersMVC.service.PeliculaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

@Controller
public class PeliculaController {

    @Autowired
    private PeliculaService peliculaService;
    
    @Autowired
    private EstrenoService estrenoService;

    @Autowired
    private GeneroService generoService;

    @GetMapping("/RegistroPeliculas")
    public String showRegistrationForm(Model model) {
        model.addAttribute("pelicula", new Pelicula());
        model.addAttribute("generos", generoService.getAllGeneros());
        return "RegistroPeliculas";
    }

    @GetMapping("/Cartelera")
    public String showCartelera(Model model, @RequestParam(defaultValue = "0") int page) {
        
        Page <Estreno> cartelera = estrenoService.getAllEstreno(PageRequest.of(page, 6));
        model.addAttribute("cartelera", cartelera);
        return "Cartelera";
    }

    @PostMapping("/RegistroPeliculas")
    public String registerPelicula(@ModelAttribute Pelicula pelicula, 
                                    @RequestParam("image") MultipartFile image,
                                    @RequestParam("generoId") Integer generoId) {
        if (image.isEmpty()) {
            System.out.println("El archivo de imagen está vacío.");
            // Manejar el error aquí, tal vez agregar un mensaje al modelo
            return "RegistroPeliculas"; // Devuelve al formulario si la imagen está vacía
        }

        try {
            String uploadsDir = "./uploads/";
            Path uploadPath = Paths.get(uploadsDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String fileName = image.getOriginalFilename();
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(image.getInputStream(), filePath);

            pelicula.setImageUrl("/uploads/" + fileName); // Aquí guardas la ruta de la imagen
        } catch (IOException e) {
            e.printStackTrace();
            // Manejo de errores, podrías agregar un mensaje al modelo aquí
            return "Cartelera"; // Devuelve al formulario si hay un error
        }

        peliculaService.savePelicula(pelicula, generoId); // Asegúrate que este método esté correctamente definido
        return "/Cartelera"; // Redirigir después de un registro exitoso
    }
}