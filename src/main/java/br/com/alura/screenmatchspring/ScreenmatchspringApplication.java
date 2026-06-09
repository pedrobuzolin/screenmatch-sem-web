package br.com.alura.screenmatchspring;

import br.com.alura.screenmatchspring.model.DadosEpisodio;
import br.com.alura.screenmatchspring.model.DadosSerie;
import br.com.alura.screenmatchspring.model.DadosTemporada;
import br.com.alura.screenmatchspring.service.ConsumoApi;
import br.com.alura.screenmatchspring.service.ConverterDados;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class ScreenmatchspringApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(ScreenmatchspringApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		ConsumoApi consumoApi = new ConsumoApi();
		String apiKey = "3f98226";
		String endereco = "http://www.omdbapi.com/?t=gilmore+girls&apikey="+apiKey;
		String json = consumoApi.obterDados(endereco);
		System.out.println(json);
		ConverterDados conversor = new ConverterDados();
		DadosSerie dados = conversor.obterDados(json, DadosSerie.class);
		System.out.println(dados);
		endereco = "http://www.omdbapi.com/?t=gilmore+girls&season=1&episode=2&apikey="+apiKey;
		json = consumoApi.obterDados(endereco);
		DadosEpisodio dadosEpisodio = conversor.obterDados(json, DadosEpisodio.class);
		System.out.println(dadosEpisodio);

		List<DadosTemporada> temporadas = new ArrayList<>();

		for (int i = 1; i <= dados.totalTemporadas(); i++) {
			endereco = "http://www.omdbapi.com/?t=gilmore+girls&season=" + i + "&apikey="+apiKey;
			json = consumoApi.obterDados(endereco);
			DadosTemporada dadosTemporada = conversor.obterDados(json, DadosTemporada.class);
			temporadas.add(dadosTemporada);
		}

		temporadas.forEach(System.out::println);

	}
}
