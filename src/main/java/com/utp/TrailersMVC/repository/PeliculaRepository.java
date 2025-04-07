package com.utp.TrailersMVC.repository;

import com.utp.TrailersMVC.model.Pelicula;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PeliculaRepository extends JpaRepository<Pelicula, Integer> {
    
    List<Pelicula> findTop6ByOrderByCreatedAtDesc();

    Page<Pelicula> findAllByOrderByCreatedAtDesc(Pageable pageable);
}
