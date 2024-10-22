package com.aluracursos.screenmatch.model;

import com.aluracursos.screenmatch.service.ConsultaChatGPT;
import jakarta.persistence.*;

import java.util.List;
import java.util.OptionalDouble;

@Entity
@Table(name="series")
public class Serie {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column(unique = true)
  private String titulo;
  private Integer totalTemporadas;
  private Double evaluacion;
  private String poster;
  @Enumerated(EnumType.STRING)
  private Categoria genero;
  private String actores;
  private String sinopsis;
  @OneToMany(mappedBy = "serie", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
  private List<Episodio> episodios;

  //Constructor JPA
  public Serie(){}

  public Serie(DatosSerie datosSerie) {
    this.titulo = datosSerie.titulo();
    this.totalTemporadas = datosSerie.totalDeTemporadas();
    this.evaluacion = OptionalDouble.of(Double.parseDouble(datosSerie.evaluacion())).orElse(0);
    this.poster = datosSerie.poster();
    this.genero = Categoria.fromString(datosSerie.genero().split(",")[0].trim());
    this.actores = datosSerie.actores();
    this.sinopsis = datosSerie.sinopsis();
//    this.sinopsis = ConsultaChatGPT.obtenerTraduccion(datosSerie.sinopsis());
  }

  public String getTitulo() {
    return titulo;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public void setTitulo(String titulo) {
    this.titulo = titulo;
  }

  public Integer getTotalTemporadas() {
    return totalTemporadas;
  }

  public void setTotalTemporadas(Integer totalTemporadas) {
    this.totalTemporadas = totalTemporadas;
  }

  public Double getEvaluacion() {
    return evaluacion;
  }

  public void setEvaluacion(Double evaluacion) {
    this.evaluacion = evaluacion;
  }

  public String getPoster() {
    return poster;
  }

  public void setPoster(String poster) {
    this.poster = poster;
  }

  public Categoria getGenero() {
    return genero;
  }

  public void setGenero(Categoria genero) {
    this.genero = genero;
  }

  public String getActores() {
    return actores;
  }

  public void setActores(String actores) {
    this.actores = actores;
  }

  public String getSinopsis() {
    return sinopsis;
  }

  public void setSinopsis(String sinopsis) {
    this.sinopsis = sinopsis;
  }

  public List<Episodio> getEpisodios() {
    return episodios;
  }

  public void setEpisodios(List<Episodio> episodios) {
    //Agrega el id de la serie para cada episodio
    episodios.forEach(e-> e.setSerie(this));
    this.episodios = episodios;
  }

  @Override
  public String toString() {
    return
            titulo.toUpperCase() + "\n" +
                "-----------------------" + "\n" +
            "Sinopsis= " + sinopsis + ",\n"+
            "Actores= [ " + actores + " ],\n" +
            "Genero= " + genero + ",\n" +
            "Temporadas= " + totalTemporadas + ",\n" +
            "Evaluacion= " + evaluacion + ",\n" +
            "Poster= '" + poster + "',\n" +
            "Lista de Episodios= "+ episodios + "\n" ;
  }


}
