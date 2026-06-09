package br.com.alura.screenmatchspring.service;

public interface IConverteDados {
    <T> T obterDados(String json, Class<T> classe);
}
