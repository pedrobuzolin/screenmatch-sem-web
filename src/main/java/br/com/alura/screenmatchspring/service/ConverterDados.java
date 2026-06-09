package br.com.alura.screenmatchspring.service;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

public class ConverterDados implements IConverteDados {
    private ObjectMapper mapper = new ObjectMapper();

    @Override
    public <T> T obterDados(String json, Class<T> classe) {
        try {
            return mapper.readValue(json, classe);
        } catch (JacksonException e) {
            throw new RuntimeException(e);
        }

    }
}
