/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.utp.TrailersMVC.repository;

import com.utp.TrailersMVC.model.Estreno;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface EstrenoRepository extends JpaRepository<Estreno, Integer> {

    List<Estreno> findTop6ByOrderByIdEstrenoDesc();

    List<Estreno> findByOrderByIdEstrenoDesc();

    Page<Estreno> findAllByOrderByIdEstrenoDesc(Pageable pageable);

    Page<Estreno> findAllByEstadoOrderByIdEstrenoDesc(String estado, Pageable pageable);

    List<Estreno> findByEstadoOrderByIdEstrenoDesc(String estado);

    @Query("SELECT e FROM Estreno e WHERE e.estado IN :estados ORDER BY e.idEstreno DESC")
    List<Estreno> findByEstadoIn(@Param("estados") List<String> estados);

    @Query("SELECT e FROM Estreno e WHERE e.titulo LIKE %:nombre% ORDER BY e.idEstreno DESC")
    List<Estreno> findByTituloContainingIgnoreCase(@Param("nombre") String nombre);

    @Query("SELECT e FROM Estreno e JOIN e.genero g WHERE g.nombre = :nombreGenero ORDER BY e.idEstreno DESC")
    Page<Estreno> findByGeneroNombre(@Param("nombreGenero") String nombreGenero, Pageable pageable);

}
