package com.aluracursos.screenmatch.service;

import java.util.*;
import com.theokanning.openai.completion.CompletionRequest;
import com.theokanning.openai.service.OpenAiService;

public class ConsultaChatGPT {
  public static String obtenerTraduccion(String texto) {
    OpenAiService service = new OpenAiService(System.getenv().get("API_KEY_OPENAI"));

    CompletionRequest req = CompletionRequest.builder()
        .model("gpt-3.5-turbo-instruct")
        .prompt("traduce a español el siguiente texto: " + texto)
        .maxTokens(1000)
        .temperature(0.7)
        .build();

    var respuesta = service.createCompletion(req);
    return respuesta.getChoices().get(0).getText();
  }
}