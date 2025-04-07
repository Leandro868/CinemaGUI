package com.utp.trailersMVC.service;

import com.utp.TrailersMVC.model.Noticia;
import com.utp.TrailersMVC.repository.NoticiaRepository;
import jakarta.persistence.EntityNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.*;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.web.multipart.MultipartFile;

@Service
public class NoticiaService {
    
    @Autowired
    private NoticiaRepository noticiaRepository;
     
    public Noticia saveNoticia(Noticia noticia) {
        return noticiaRepository.save(noticia);
    }

    // Obtener las últimas 10 noticias
    public List<Noticia> getUltimasNoticias() {
        return noticiaRepository.findTop10ByOrderByFechaDesc();
    }

    // Obtener noticias paginadas en orden descendente
    public Page<Noticia> getNoticiasPaginadas(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("fecha").descending());
        return noticiaRepository.findAllByOrderByFechaDesc(pageable);
    }
    public List<Noticia> obtenerTodasLasNoticias() {
        return noticiaRepository.findAll(); // Método que obtiene todas las noticias
    }
    // Obtener noticia por ID
    public Noticia obtenerNoticiaPorId(Integer id) {
        return noticiaRepository.findById(id)
                .orElseThrow();
    }
     public Noticia getNoticiaById(Integer id) {
    return noticiaRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Película no encontrada con id: " + id));
}
    
     public Noticia actualizarNoticia(Integer id, Noticia updatedNoticia, MultipartFile image) {
    Noticia existingNoticia = getNoticiaById(id);

    // Actualizar los campos de la noticia
    existingNoticia.setTitulo(updatedNoticia.getTitulo());
    existingNoticia.setSubtitulo(updatedNoticia.getSubtitulo());
    existingNoticia.setDescripcion(updatedNoticia.getDescripcion());

    // Verificar si hay una imagen nueva para actualizar
    if (!image.isEmpty()) {

        try {
            // Ruta de subida de imágenes
            String uploadsDir = "./uploads/";
            Path uploadPath = Paths.get(uploadsDir);

            // Crear el directorio si no existe
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Generar un nombre único para la imagen con marca de tiempo
            String fileName = System.currentTimeMillis() + "_" + image.getOriginalFilename();
            Path filePath = uploadPath.resolve(fileName);

            // Guardar el archivo en la ruta especificada
            Files.copy(image.getInputStream(), filePath);

            // Asignar la nueva ruta de la imagen
            existingNoticia.setImagen("/uploads/" + fileName);

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Hubo un problema al subir la imagen. Por favor, intente nuevamente.");
        }
    }

    // Guardar los cambios en la base de datos
    return noticiaRepository.save(existingNoticia);
}

    public void eliminarNoticiaPorId(Integer id) {
    noticiaRepository.deleteById(id);
    }
    
    public List<Noticia> buscarNoticiasPorTitulo(String query) {
    return noticiaRepository.findByTituloContainingIgnoreCase(query);
    }
  // Método para obtener noticias relacionadas por título
    public List<Noticia> obtenerNoticiasRelacionadasPorTitulo(Noticia noticia) {
    // Dividir el título en palabras clave, pero primero eliminar palabras comunes
    String[] palabrasClave = filtrarPalabrasComunes(noticia.getTitulo().split(" "));

    // Crear un conjunto para evitar duplicados
    Set<Noticia> noticiasRelacionadasSet = new HashSet<>();

    // Buscar noticias que contengan las palabras clave (solo relevantes)
    for (String palabra : palabrasClave) {
        // Buscar en títulos que contengan las palabras clave relevantes
        noticiasRelacionadasSet.addAll(noticiaRepository.findByTituloContainingIgnoreCase(palabra));
    }

    // Convertir el Set a lista y ordenarlas por fecha descendente
    List<Noticia> noticiasRelacionadas = new ArrayList<>(noticiasRelacionadasSet);
    noticiasRelacionadas.sort(Comparator.comparing(Noticia::getFecha).reversed());

    // Eliminar la noticia actual de la lista de noticias relacionadas
    noticiasRelacionadas.removeIf(n -> n.getId().equals(noticia.getId()));

    // Limitar a las primeras 4 noticias más relevantes
    return noticiasRelacionadas.stream().limit(3).collect(Collectors.toList());
}

    // Método para filtrar palabras comunes que no aportan valor en la búsqueda de relaciones
    private String[] filtrarPalabrasComunes(String[] palabras) {
        // Lista de palabras comunes que no deberían considerarse como clave para relacionar noticias
        Set<String> palabrasComunes = new HashSet<>(Arrays.asList(
            "de", "la", "el", "en", "a", "que", "y", "por", "con", "para", "sobre", "como", "un", "una"
        ));

        return Arrays.stream(palabras)
                     .filter(p -> !palabrasComunes.contains(p.toLowerCase())) // Filtrar palabras comunes
                     .toArray(String[]::new); // Retornar el array filtrado
    }
}
