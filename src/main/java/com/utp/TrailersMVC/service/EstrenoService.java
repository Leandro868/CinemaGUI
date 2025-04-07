/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.utp.TrailersMVC.service;

import com.utp.TrailersMVC.model.*;
import com.utp.TrailersMVC.repository.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.Arrays;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.List;

import org.springframework.context.event.EventListener;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.data.domain.PageRequest;

@Service
public class EstrenoService {

    @Autowired
    private EstrenoRepository estrenoRepository;

    @Autowired
    private GeneroRepository generoRepository;

    @Transactional
    public Estreno saveEstreno(Estreno estreno, Integer generoId) {
        Genero genero = generoRepository.findById(generoId)
                .orElseThrow(() -> new EntityNotFoundException("Género no encontrado con id: " + generoId));
        estreno.setGenero(genero);
        return estrenoRepository.save(estreno);
    }

    public Page<Estreno> getLatestEstreno(Pageable pageable) {
        return estrenoRepository.findAllByOrderByIdEstrenoDesc(pageable);
    }

    public Page<Estreno> getAllProximoEstreno(Pageable pageable) {
        return estrenoRepository.findAllByEstadoOrderByIdEstrenoDesc("proximoEstreno", pageable);
    }
    
    public Page<Estreno> getAllEstreno(Pageable pageable) {
        return estrenoRepository.findAllByEstadoOrderByIdEstrenoDesc("estreno", pageable);
    }

    public Page<Estreno> getAllEstrenoAdmin(Pageable pageable) {
        return estrenoRepository.findAllByOrderByIdEstrenoDesc(pageable);
    }

    public Estreno saveEstreno(Estreno estreno) {
        return estrenoRepository.save(estreno);
    }

    public Estreno findById(Integer idEstreno) {
        return estrenoRepository.findById(idEstreno).orElse(null);
    }

    public void save(Estreno estreno) {
        estrenoRepository.save(estreno);
    }

    public List<Estreno> getAllEstreno() {
        return estrenoRepository.findByOrderByIdEstrenoDesc();
    }

    public List<Estreno> obtenerProximosEstrenos() {
        return estrenoRepository.findByEstadoOrderByIdEstrenoDesc("proximoEstreno");
    }

    public void evaluarEstadosEstrenos() {
        List<String> estados = Arrays.asList("proximoEstreno", "estreno", "reEstreno");
        List<Estreno> estrenos = estrenoRepository.findByEstadoIn(estados);
        LocalDate fechaActual = LocalDate.now();

        for (Estreno estreno : estrenos) {
            if ("proximoEstreno".equals(estreno.getEstado())) {
                // Verifica si la fecha de estreno es igual a la fecha actual
                if (estreno.getFechaEstreno().isEqual(fechaActual) || estreno.getFechaEstreno().isBefore(fechaActual)) {
                    // Cambia el estado a "Estreno" si la fecha de estreno ya pasó
                    estreno.setEstado("estreno");
                } else if (estreno.getFechaEstreno().isBefore(fechaActual)) {
                    // Conservar el estado
                    continue;
                }
            } else if ("estreno".equals(estreno.getEstado())) {
                // Suma los días de estreno a la fecha de estreno
                LocalDate fechaFinEstreno = estreno.getFechaEstreno().plusDays(estreno.getDiasEnCartelera());
                if (fechaFinEstreno.isEqual(fechaActual.plusDays(1)) || fechaFinEstreno.isBefore(fechaActual)) {
                    // Cambia el estado a "estrenoPasado" si la fecha de fin de estreno es igual a la fecha actual más 1 día o si ya pasó
                    estreno.setEstado("estrenoPasado");
                }
            } else if ("reEstreno".equals(estreno.getEstado())) {
                // Suma los días de estreno a la fecha de estreno
                LocalDate fechaFinEstreno = estreno.getFechaEstreno().plusDays(estreno.getDiasEnCartelera());
                if (fechaFinEstreno.isEqual(fechaActual.plusDays(1)) || fechaFinEstreno.isBefore(fechaActual)) {
                    // Cambia el estado a "estrenoPasado" si la fecha de fin de estreno es igual a la fecha actual más 1 día o si ya pasó
                    estreno.setEstado("estrenoPasado");
                }
            }
        }
        // Guarda los cambios en la base de datos
        estrenoRepository.saveAll(estrenos);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void ejecutarEvaluacionAlInicio() {
        evaluarEstadosEstrenos();
    }
    
    public List<Estreno> buscarPorNombre(String nombre) {
        return estrenoRepository.findByTituloContainingIgnoreCase(nombre);
    }
    
    public Page<Estreno> filtrarPorGenero(String genero, Pageable pageable) {
        return estrenoRepository.findByGeneroNombre(genero, pageable);
    }
}
