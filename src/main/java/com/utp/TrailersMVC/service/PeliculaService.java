package com.utp.TrailersMVC.service;

import com.utp.TrailersMVC.model.Genero;
import com.utp.TrailersMVC.model.Pelicula;
import com.utp.TrailersMVC.repository.GeneroRepository;
import com.utp.TrailersMVC.repository.PeliculaRepository;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PeliculaService {

    @Autowired
    private PeliculaRepository peliculaRepository;

    @Autowired
    private GeneroRepository generoRepository;

    @Transactional
    public Pelicula savePelicula(Pelicula pelicula, Integer generoId) {
        Genero genero = generoRepository.findById(generoId)
                .orElseThrow(() -> new EntityNotFoundException("Género no encontrado"));
        pelicula.setGenero(genero);
        return peliculaRepository.save(pelicula);
    }

    public Pelicula savePelicula(Pelicula pelicula) {
        return peliculaRepository.save(pelicula);
    }

    public List<Pelicula> getLatestPeliculas() {
        return peliculaRepository.findTop6ByOrderByCreatedAtDesc();
    }

    public List<Pelicula> getAllPeliculas() {
        return peliculaRepository.findAll(); // Método que obtiene todas las películas
    }

    public List<Pelicula> getTop6Peliculas() {
        return peliculaRepository.findTop6ByOrderByCreatedAtDesc(); // Método para obtener las 6 películas más recientes
    }

    public Page<Pelicula> getAllPeliculas(PageRequest pageRequest) {
        return peliculaRepository.findAll(pageRequest); // Asegúrate de usar el PageRequest
    }
}