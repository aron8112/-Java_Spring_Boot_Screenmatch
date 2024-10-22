package com.aluracursos.screenmatch.service;

public interface iConvierteDatos {
  <T> T obtenerDatos(String json, Class<T> clase);
}
