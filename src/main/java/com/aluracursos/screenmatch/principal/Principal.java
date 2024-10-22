package com.aluracursos.screenmatch.principal;

import com.aluracursos.screenmatch.model.*;
import com.aluracursos.screenmatch.repository.SerieRepository;
import com.aluracursos.screenmatch.service.ConsumoAPI;
import com.aluracursos.screenmatch.service.ConvierteDatos;
import java.util.*;
import java.util.stream.Collectors;

public class Principal {

  //Instancias
  private Scanner teclado = new Scanner(System.in);
  private ConsumoAPI consumoAPI = new ConsumoAPI();
  private ConvierteDatos conversor = new ConvierteDatos();
  //Constantes
  private final String BASE_URL = "http://www.omdbapi.com/?t=";

  private final String API_KEY =  "&apikey="+System.getenv().get("API_KEY_OMDB");
  private List<DatosSerie> datosSeries = new ArrayList<>();
  private List<Serie> series;
  private SerieRepository repositorio;
  Optional<Serie> serieBuscada;

  public Principal(SerieRepository repository) {
    this.repositorio = repository;
  }

  public void muestraElMenu(){
    var opcion = -1;
    while(opcion != 0){
      var menu = """
                    \n
                    _______________________________________________
                    | =========================================== |
                    | ******************************************* |
                    | =============== ScreenMatch =============== |
                    | ******************************************* |
                    | =========================================== |
                    |                                             |
                    | -> Seleccione una opción para comenzar:     |
                    |                                             |
                    | 1 - Buscar series                           |
                    | 2 - Buscar episodios                        |
                    | 3 - Mostrar series buscadas                 |
                    | 4 - Buscar series por título                |
                    | 5 - Top 5 mejores series                    |
                    | 6 - Buscar series por Categoría             |
                    | 7 - Filtrar serie por cant. de temporadas   |
                    |     y evaluación                            |
                    | 8 - Buscar episodios por título             |
                    | 9 - Top 5 episodios por serie               |
                    |                                             |
                    | ------------------------------------------- |
                    | 0 - Salir                                   |
                    _______________________________________________
                    """;
      System.out.println(menu);
      opcion = teclado.nextInt();
      teclado.nextLine();

      switch (opcion) {
        case 1:
          buscarSerieWeb();
          break;
        case 2:
          buscarEpisodioPorSerie();
          break;
        case 3:
          mostrarSeriesMostradas();
          break;
        case 4:
          buscarSeriesPorTitulo();
          break;
        case 5:
          buscarTop5Series();
          break;
        case 6:
          buscarSeriesPorCategoria();
          break;
        case 7:
          filtrarSeriesPorTemporadaAndEvaluacion();
          break;
        case 8:
          buscarEpisodioPorTitulo();
          break;
        case 9:
          top5EpisodiosPorSerie();
          break;
        case 0:
          System.out.println("Cerrando la aplicación...");
          break;
        default:
          System.out.println("Opción inválida");
      }
    }
  }

  private DatosSerie getDatosSerie() {
    System.out.println("Escribe el nombre de la serie que deseas buscar");
    var nombreSerie = teclado.nextLine();
    var json = consumoAPI.obtenerDatos(BASE_URL + nombreSerie.replace(" ", "+")+"&type=series"+ API_KEY);
    System.out.println(json);
    DatosSerie datos = conversor.obtenerDatos(json, DatosSerie.class);
    return datos;
  }

  private void buscarEpisodioPorSerie() {
//    DatosSerie datosSerie = getDatosSerie();
    mostrarSeriesMostradas();
    System.out.println("Escribe el nombre de la serie de la cual quieres ver los episodios: ");
    var nombreSerie = teclado.nextLine();

    Optional<Serie> serie = series.stream()
        .filter(s-> s.getTitulo().toLowerCase().contains(nombreSerie.toLowerCase()))
        .findFirst();

    if(serie.isPresent()){
      var serieEncontrada = serie.get();
      List<DatosTemporada> temporadas = new ArrayList<>();

      for (int i = 1; i <= serieEncontrada.getTotalTemporadas(); i++) {
        var json = consumoAPI.obtenerDatos(BASE_URL + serieEncontrada.getTitulo().replace(" ", "+") + "&season=" + i + API_KEY);
        DatosTemporada datosTemporada = conversor.obtenerDatos(json, DatosTemporada.class);
        temporadas.add(datosTemporada);
      }
      temporadas.forEach(System.out::println);
      List<Episodio> episodios = temporadas.stream()
          .flatMap(d -> d.episodios().stream()
              .map(e-> new Episodio(d.numero(), e)))
          .collect(Collectors.toList());

      serieEncontrada.setEpisodios(episodios);
      repositorio.save(serieEncontrada);
    }
  }
  private void buscarSerieWeb() {
    DatosSerie datos = getDatosSerie();
    Serie serie = new Serie(datos);
    repositorio.save(serie);
//    datosSeries.add(datos);
    System.out.println(datos);
  }

  private void mostrarSeriesMostradas() {
    series = repositorio.findAll();
    series.stream().sorted(Comparator.comparing(Serie::getGenero)).forEach(System.out::println);
  }
  private void buscarSeriesPorTitulo() {
    System.out.println("Escribe el nombre de la serie: ");
    var nombreSerie = teclado.nextLine();

    serieBuscada = repositorio.findByTituloContainsIgnoreCase(nombreSerie);
    if(serieBuscada.isPresent()){
      System.out.println("Serie encontrada: \n"+serieBuscada.get());
    } else {
      System.out.println("Serie no encontrada");
    }
  }

  private void buscarTop5Series(){
    List<Serie> topSeries = repositorio.findTop5ByOrderByEvaluacionDesc();
    topSeries.forEach(s-> System.out.println("\n----------\n"+"Serie: "+s.getTitulo()+"\nEvaluación: "+s.getEvaluacion()+"\n----------"));
  }

  private void buscarSeriesPorCategoria() {
    System.out.println("| Escriba la categoría (género) de la serie que quiere buscar: ");
    var categoriaBuscada = teclado.nextLine();
    try{
      var categoria = Categoria.fromSpanish(categoriaBuscada);
      List<Serie> seriesPorCategoria = repositorio.findByGenero(categoria);
      System.out.println("Las series por la categoría  <<"+categoriaBuscada+">> son: \n");
      seriesPorCategoria.forEach(System.out::println);
    } catch (RuntimeException e) {
      var errorResponse = """
         _______________________________________________
         | No existe esa categoría en la base de datos. |
         |                                              |
         | Para resolver el problema, puede:            |
         | 1) volver a la opción 1 y buscar nuevas      |  
         |    series,                                   |
         | 2) compruebe que esté correcta la escritura. |
         |                                              |
         ________________________________________________
          """;
      System.out.println(errorResponse);
    }
  }

  private void filtrarSeriesPorTemporadaAndEvaluacion() {
    System.out.println("| Escriba la cantidad de temporadas que tiene las series buscadas: ");
    var temporadasSerie = teclado.nextLine();
    System.out.println("| Ahora la evaluación de las series buscadas: ");
    var evaluacionSerie = teclado.nextLine();

    try {
      List<Serie> series = repositorio.findByTotalTemporadasLessThanEqualAndEvaluacionGreaterThanEqual(
          Integer.parseInt(temporadasSerie),
          Double.parseDouble(evaluacionSerie));
      System.out.println("\n La busqueda por <<" + temporadasSerie + ">> cantidad de temporadas y evaluación mayor o igual a <<" + evaluacionSerie + ">>\n");
      if (series.isEmpty()) {
        throw new RuntimeException();
      }
      series.forEach(System.out::println);
    } catch (RuntimeException e) {
      var errorResponse = """
          _______________________________________________
          | No existe esa/s serie/s en la base de datos. |
          |                                              |
          | Para resolver el problema, puede:            |
          | 1) volver a la opción 1 y buscar nuevas      |
          |    series.                                   |
          |                                              |
          ________________________________________________
          """;
      System.out.println(errorResponse);
    }
  }

  private void buscarEpisodioPorTitulo(){
    System.out.println("Nombre del episodio a buscar: ");
    var nombreEpisodio = teclado.nextLine();
    List<Episodio> episodios = repositorio.episodiosPorNombre(nombreEpisodio);
    episodios.forEach(e-> System.out.printf("\nSerie: %s | Titulo completo: %s | Temporada: %s | Episodio: %s | Evaluación: %s",
        e.getSerie().getTitulo(), e.getTitulo(), e.getTemporada(), e.getNumeroEpisodio(), e.getEvaluacion()));
  }

  private void top5EpisodiosPorSerie() {
    buscarSeriesPorTitulo();
    if(serieBuscada.isPresent()) {
      Serie serie = serieBuscada.get();
      List<Episodio> topEpisodios = repositorio.top5Episodios(serie);
      topEpisodios.forEach(e-> System.out.printf("\nTitulo completo: %s | Temporada: %s | Episodio: %s | Evaluación: %s",
          e.getTitulo(), e.getTemporada(), e.getNumeroEpisodio(), e.getEvaluacion()));
    }
  }
}

