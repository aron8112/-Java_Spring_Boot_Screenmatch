package com.aluracursos.screenmatch.service;

import com.aluracursos.screenmatch.dto.EpisodioDTO;
import com.aluracursos.screenmatch.dto.SerieDTO;
import com.aluracursos.screenmatch.model.Categoria;
import com.aluracursos.screenmatch.model.Serie;
import com.aluracursos.screenmatch.repository.SerieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SerieService {
  @Autowired
  private SerieRepository repository;

  public List<SerieDTO> obtenerTodasLasSeries(){
    return convierteDatos(repository.findAll());
  }

  public List<SerieDTO> obtenerTop5() {
    return convierteDatos(repository.findTop5ByOrderByEvaluacionDesc());
  }

  public List<SerieDTO> obtenerUltimosLanzamientos(){
    return convierteDatos(repository.lanzamientosMasRecientes());
  }

  public List<SerieDTO> convierteDatos(List<Serie> serie){
    return serie.stream()
        .map(s-> new SerieDTO(s.getId(), s.getTitulo(), s.getTotalTemporadas(),
            s.getEvaluacion(), s.getPoster(),s.getGenero(),
            s.getActores(), s.getSinopsis()))
        .collect(Collectors.toList());
  }

  public SerieDTO obtenerSerie(Long id) {
    Optional<Serie> serie = repository.findById(id);

    if(serie.isPresent()){
      Serie s = serie.get();
      return new SerieDTO(s.getId(), s.getTitulo(), s.getTotalTemporadas(),
          s.getEvaluacion(), s.getPoster(),s.getGenero(),
          s.getActores(), s.getSinopsis());
    }
    return null;
  }

  public List<EpisodioDTO> obtenerTemporadas(Long id) {
    Optional<Serie> serie = repository.findById(id);

    if(serie.isPresent()){
      Serie s = serie.get();
      return s.getEpisodios().stream()
          .map( e-> new EpisodioDTO(e.getTemporada(), e.getTitulo(), e.getNumeroEpisodio()))
          .collect(Collectors.toList());
    }
    return null;
  }

  public List<EpisodioDTO> obtenerUnaTemporada(Long id, Integer num) {
//    Optional<Serie> serie = repository.findById(id);
//
//    if(serie.isPresent()){
//      Serie s = serie.get();
//      return s.getEpisodios().stream()
//          .filter(e -> Objects.equals(e.getTemporada(), num))
//          .map( e-> new EpisodioDTO(e.getTemporada(), e.getTitulo(), e.getNumeroEpisodio()))
//          .collect(Collectors.toList());
//    }
//    return null;
    return repository.obtenerEpisodiosPorTemporada(id, num).stream()
        .map( e-> new EpisodioDTO(
            e.getTemporada(),
            e.getTitulo(),
            e.getNumeroEpisodio()))
          .collect(Collectors.toList());
  }

  public List<SerieDTO> obtenerSeriePorCategoria(String nombreCategoria) {
    Categoria categoria = Categoria.fromSpanish(nombreCategoria);
    return convierteDatos(repository.findByGenero(categoria));
  }

  public List<EpisodioDTO> mejoresEpPorTemporada(Long id) {
    Optional<Serie> serie = repository.findById(id);

    if(serie.isPresent()) {
      Serie s = serie.get();
      return repository.top5Episodios(s).stream()
          .map(e-> new EpisodioDTO(
              e.getTemporada(),
              e.getTitulo(),
              e.getNumeroEpisodio()))
          .collect(Collectors.toList());
    }
    return null;
  }
}
