/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.utp.TrailersMVC.service;

import com.utp.TrailersMVC.model.*;
import com.utp.TrailersMVC.repository.*;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class GeneroService {
    @Autowired
    private GeneroRepository generoRepository;

    public List<Genero> getAllGeneros() {
        return generoRepository.findAll();
    }
    
    // Método para buscar un género por su ID
    public Genero findById(Integer idGenero) {
        return generoRepository.findById(idGenero)
                .orElseThrow(() -> new EntityNotFoundException("Género no encontrado con ID: " + idGenero));
    }
}
