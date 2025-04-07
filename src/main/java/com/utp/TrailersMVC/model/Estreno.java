package com.utp.TrailersMVC.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "Estreno")
public class Estreno {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idEstreno;

    @Column(nullable = false)
    private String titulo;

    @Column(nullable = false)
    private String autor;

    @Column(columnDefinition = "TEXT")
    private String sinopsis;

    @Column(nullable = false)
    private String duracion;

    @Column(nullable = false)
    private String edad;

    @Column(nullable = false)
    private String idioma;

    private String trailer;

    private String estado;

    private Integer diasEnCartelera;

    private String imagen;

    private LocalDate fechaEstreno;

    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "idGenero", nullable = false)
    private Genero genero;
    
    @ManyToOne
    @JoinColumn(name = "idUsuario", nullable = true)
    private Usuario usuario;

    public Integer getIdEstreno() {
        return idEstreno;
    }

    public void setIdEstreno(Integer idEstreno) {
        this.idEstreno = idEstreno;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public String getSinopsis() {
        return sinopsis;
    }

    public void setSinopsis(String sinopsis) {
        this.sinopsis = sinopsis;
    }

    public String getDuracion() {
        return duracion;
    }

    public void setDuracion(String duracion) {
        this.duracion = duracion;
    }

    public String getEdad() {
        return edad;
    }

    public void setEdad(String edad) {
        this.edad = edad;
    }

    public String getIdioma() {
        return idioma;
    }

    public void setIdioma(String idioma) {
        this.idioma = idioma;
    }

    public String getTrailer() {
        return trailer;
    }

    public void setTrailer(String trailer) {
        this.trailer = trailer;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public LocalDate getFechaEstreno() {
        return fechaEstreno;
    }

    public void setFechaEstreno(LocalDate fechaEstreno) {
        this.fechaEstreno = fechaEstreno;
    }

    public Genero getGenero() {
        return genero;
    }

    public void setGenero(Genero genero) {
        this.genero = genero;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Integer getDiasEnCartelera() {
        return diasEnCartelera;
    }

    public void setDiasEnCartelera(Integer diasEnCartelera) {
        this.diasEnCartelera = diasEnCartelera;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
}
