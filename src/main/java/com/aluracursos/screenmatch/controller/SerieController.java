package com.aluracursos.screenmatch.controller;


import com.aluracursos.screenmatch.dto.EpisodioDTO;
import com.aluracursos.screenmatch.dto.SerieDTO;
import com.aluracursos.screenmatch.service.SerieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/series")
public class SerieController {
  @Autowired
  private SerieService serieService;

  @GetMapping()
  public List<SerieDTO> mostrarSeries(){
    return serieService.obtenerTodasLasSeries();
  }

  @GetMapping("/top5")
  public List<SerieDTO> obtenerTop5(){
    return serieService.obtenerTop5();
  }

  @GetMapping("/lanzamientos")
  public List<SerieDTO> ultimoslanzamientos(){
    return serieService.obtenerUltimosLanzamientos();
  }

  @GetMapping("/{id}")
  public SerieDTO obtenerSeriePorId(@PathVariable Long id){
    return serieService.obtenerSerie(id);
  }

  @GetMapping("/{id}/temporadas/todas")
  public List<EpisodioDTO> obtenerTodasTemporadas(@PathVariable Long id){
    return serieService.obtenerTemporadas(id);
  }

  @GetMapping("/{id}/temporadas/{num}")
  public List<EpisodioDTO> obtenerUnaTemporada(@PathVariable Long id, @PathVariable Integer num){
    return serieService.obtenerUnaTemporada(id, num);
  }

  @GetMapping("/categoria/{nombreCategoria}")
  public List<SerieDTO> obtenerSeriePorCategoria(@PathVariable String nombreCategoria){
    return serieService.obtenerSeriePorCategoria(nombreCategoria);
  }

  @GetMapping("/{id}/temporadas/top")
  public List<EpisodioDTO> obtenerMejoresEpPorTemporada(@PathVariable Long id){
    return serieService.mejoresEpPorTemporada(id);
  }
}
