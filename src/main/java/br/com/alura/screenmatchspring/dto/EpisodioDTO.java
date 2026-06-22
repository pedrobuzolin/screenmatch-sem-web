package br.com.alura.screenmatchspring.dto;

import java.time.LocalDate;

public record EpisodioDTO(Integer temporada,
                          Integer numeroEpisodio,
                          String titulo) {
}
