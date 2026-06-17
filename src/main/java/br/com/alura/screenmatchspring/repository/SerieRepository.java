package br.com.alura.screenmatchspring.repository;

import br.com.alura.screenmatchspring.model.Serie;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SerieRepository extends JpaRepository<Serie, Long> {
}
