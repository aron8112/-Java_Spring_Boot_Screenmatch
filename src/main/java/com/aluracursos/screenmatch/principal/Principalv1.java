package com.aluracursos.screenmatch.principal;

import com.aluracursos.screenmatch.model.DatosEpisodio;
import com.aluracursos.screenmatch.model.DatosSerie;
import com.aluracursos.screenmatch.model.DatosTemporada;
import com.aluracursos.screenmatch.model.Episodio;
import com.aluracursos.screenmatch.service.ConsumoAPI;
import com.aluracursos.screenmatch.service.ConvierteDatos;

import java.util.*;
import java.util.stream.Collectors;

public class Principalv1 {
  private Scanner teclado = new Scanner(System.in);
  private ConsumoAPI consumoAPI = new ConsumoAPI();
  private ConvierteDatos conversor = new ConvierteDatos();
  //Constantes
  private final String BASE_URL = "http://www.omdbapi.com/?t=";
  private final String API_KEY = "&apikey=5cf19980";

  public void muestraElMenu() {
    System.out.println("Por favor, a continuación, escribe el nombre de la serie que desee buscar");
    String nombreSerie = teclado.nextLine();
    var json = consumoAPI.obtenerDatos(BASE_URL + nombreSerie.replace(" ", "+") + API_KEY);
    //    System.out.println(json);
    var datos = conversor.obtenerDatos(json, DatosSerie.class);
    //Imprimir datos
    System.out.println(datos);

    //Buscar datos de cada una de las temporadas
    List<DatosTemporada> temporadas = new ArrayList<>();
    for (int i = 1; i <= datos.totalDeTemporadas(); i++) {
      json = consumoAPI.obtenerDatos(
          BASE_URL
              + nombreSerie.replace(" ", "+")
              + "&Season=" + i
              + API_KEY);
      var datosTemporadas = conversor.obtenerDatos(json, DatosTemporada.class);
      temporadas.add(datosTemporadas);
    }
//    temporadas.forEach(System.out::println);

    //Mostrar título de episodios para las temporadas

    //    for (int i = 0; i < datos.totalDeTemporadas(); i++) {
    //      List<DatosEpisodio> episodiosTemporadas = temporadas.get(i).episodios();
    //      for (int j = 0; j < episodiosTemporadas.size(); j++) {
    //        System.out.println(episodiosTemporadas.get(j).titulo());
    //      }
    //    }
    //Funciones lambda
//    temporadas.forEach(t -> t.episodios().forEach(e -> System.out.println(e.titulo())));

    //Convertir la información a un List<DatosEpisodio>
    List<DatosEpisodio> datosEpisodios = temporadas.stream()
        .flatMap(t -> t.episodios().stream())
        .collect(Collectors.toList());

    //Top 5 episodios
//    System.out.println("====== TOP 5 EPISODIOS =====");
//    datosEpisodios.stream()
//        .filter(e-> !e.evaluacion().equalsIgnoreCase("N/A"))
//        .peek(e-> System.out.println("Primer filtro (N/A)" + e ))
//        .sorted(Comparator.comparing(DatosEpisodio::evaluacion).reversed())
//        .peek(e-> System.out.println("Segundo filtro (sorted->evaluacion)" + e ))
//        .map(e->e.titulo().toUpperCase())
//        .peek(e-> System.out.println("Tercer filtro (uppercase)" + e ))
//        .limit(5)
//        .forEach(System.out::println);

    //Convirtiendo datos a un List<Episodio>
    List<Episodio> episodios = temporadas.stream()
        .flatMap(t -> t.episodios().stream()
            .map(d -> new Episodio(t.numero(), d)))
        .collect(Collectors.toList());

//    episodios.forEach(System.out::println);

    //Busqueda de episodios a partir de un año particular
//    System.out.println("Indica el año desde el cual deseas buscar a los episodios");
//    var fecha = teclado.nextInt();
//    teclado.nextLine();
//
//    LocalDate fechaBusqueda = LocalDate.of(fecha, 1, 1);
//
//    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
//    episodios.stream()
//        .filter(e->e.getFechaDeLanzamiento() != null && e.getFechaDeLanzamiento().isAfter(fechaBusqueda))
//        .forEach(e-> System.out.println(
//            "S" + e.getTemporada() +"E"+e.getNumeroEpisodio()+
//                ", Episodio: " + e.getTitulo() +
//                ", Fecha de lanzamiento: " + e.getFechaDeLanzamiento().format(dtf)
//        ));

    //Busqueda de episodios segun titulo parcial
//    System.out.println("====== BUSQUEDA DE EPISODIOS =====");
//    System.out.println("Inserte el titulo (total o o parcial) del episodio: \n");
//    var parcialTitulo = teclado.nextLine();
//    Optional<Episodio> busquedaEpisodio = episodios.stream()
//        .filter(e -> e.getTitulo().toUpperCase().contains(parcialTitulo.toUpperCase()))
//        .findAny();
//    if (busquedaEpisodio.isPresent()){
//      System.out.println("Episodio encontrado");
//      System.out.println(busquedaEpisodio.get());
//    } else {
//      System.out.println("Sin resultados");
//    }

    Map<Integer, Double> evaluacionesPorTemporada = episodios.stream()
        .filter(e -> e.getEvaluacion() > 0.0)
        .collect(Collectors.groupingBy(
            Episodio::getTemporada,
            Collectors.averagingDouble(Episodio::getEvaluacion)
        ));
    System.out.println(evaluacionesPorTemporada);
    DoubleSummaryStatistics est = episodios.stream()
        .filter(e -> e.getEvaluacion() > 0.0)
        .collect(Collectors.summarizingDouble(Episodio::getEvaluacion));
    System.out.println("=== Estadisticas de la serie ===");
    System.out.println("Media de evaluaciones: " + est.getAverage());
    System.out.println("Cantidad de evaluaciones: " + est.getCount());
    System.out.println("Evaluación más baja: " + est.getMin());
    System.out.println("Evaluación más alta: " + est.getMax());
  }

}
