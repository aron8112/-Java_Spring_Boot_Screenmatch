package com.aluracursos.screenmatch.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DatosSerie(
    @JsonAlias("Title") String titulo,
    @JsonAlias("Genre") String genero,
    @JsonAlias("totalSeasons") Integer totalDeTemporadas,
    @JsonAlias("imdbRating") String evaluacion,
    @JsonAlias("Actors") String actores,
    @JsonAlias("Poster") String poster,
    @JsonAlias("Plot") String sinopsis){
}
