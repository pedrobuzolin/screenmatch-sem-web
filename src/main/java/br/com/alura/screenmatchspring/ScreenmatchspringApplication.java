package br.com.alura.screenmatchspring;

import br.com.alura.screenmatchspring.model.DadosSerie;
import br.com.alura.screenmatchspring.service.ConsumoApi;
import br.com.alura.screenmatchspring.service.ConverterDados;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

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

	}
}
