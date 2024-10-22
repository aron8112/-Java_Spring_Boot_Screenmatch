package com.aluracursos.screenmatch.principal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EjemploStreams {
  public void muestraEjemplo(){
    List<String> nombres = Arrays.asList("Brenda", "Luis", "Maria", "Eric", "Genesys");

    nombres.stream()
        .sorted()
        .filter(n -> n.startsWith("L"))
        .map(n-> n.toUpperCase())
        .forEach(
        System.out::println
    );
  }
}
