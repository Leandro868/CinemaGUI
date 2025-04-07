
package com.utp.TrailersMVC.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "Genero")
public class Genero {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idGenero;

    @Column(nullable = false)
    private String nombre;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @OneToMany(mappedBy = "genero")
    private List<Estreno> estreno;

    public Integer getIdGenero() {
        return idGenero;
    }

    public void setIdGenero(Integer idGenero) {
        this.idGenero = idGenero;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public List<Estreno> getEstrenos() {
        return estreno;
    }

    public void setEstrenos(List<Estreno> estrenos) {
        this.estreno = estrenos;
    }
}
