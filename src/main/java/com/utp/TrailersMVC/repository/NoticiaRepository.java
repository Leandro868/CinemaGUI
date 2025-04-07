package com.utp.TrailersMVC.repository;

import com.utp.TrailersMVC.model.Noticia;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


public interface NoticiaRepository extends JpaRepository<Noticia, Integer> {
    List<Noticia> findTop10ByOrderByFechaDesc();
    Page<Noticia> findAllByOrderByFechaDesc(Pageable pageable);
    List<Noticia> findByTituloContainingIgnoreCase(String titulo);
    
  
}
