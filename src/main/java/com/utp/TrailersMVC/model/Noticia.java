package com.utp.TrailersMVC.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Entity
public class Noticia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String titulo;

    private String subtitulo; // Nuevo atributo
    
    private String imagen; // Nuevo atributo para la imagen
    
    @Lob
    private String descripcion; // Nuevo atributo para la descripción

    private LocalDateTime fecha; // Nuevo atributo para la fecha

    // Relación con Usuario
    @ManyToOne
    @JoinColumn(name = "usuario_id") // Columna que se añadirá en la tabla Noticia
    private Usuario usuario;

    // Asignar fecha actual al crear una noticia
    @PrePersist
    protected void onCreate() {
        fecha = LocalDateTime.now();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public String getSubtitulo() {
        return subtitulo;
    }

    public void setSubtitulo(String subtitulo) {
        this.subtitulo = subtitulo;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    

    // Getter para la fecha formateada
    public String getFechaFormateada() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM. yyyy 'a las' HH:mm", new Locale("es", "ES"));
        return fecha.format(formatter);
    }

    // Getter y setter de la fecha en formato LocalDateTime
    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }
}
