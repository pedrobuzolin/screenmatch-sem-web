package br.com.alura.screenmatchspring.principal;

import br.com.alura.screenmatchspring.model.*;
import br.com.alura.screenmatchspring.repository.SerieRepository;
import br.com.alura.screenmatchspring.service.ConsumoApi;
import br.com.alura.screenmatchspring.service.ConverterDados;

import java.util.*;
import java.util.stream.Collectors;

public class Principal {
    private Scanner leitor = new Scanner(System.in);
    private ConsumoApi consumo = new ConsumoApi();
    private ConverterDados conversor = new ConverterDados();
    private final String URL_BASE = "http://www.omdbapi.com/?t=";
    private final String OMDB_API_KEY = System.getenv("OMDB_API_KEY");
    private final String API_KEY = "&apikey=" + OMDB_API_KEY;
    private List<Serie> series = new ArrayList<>();
    private SerieRepository repositorio;
    private Optional<Serie> serieBusca;

    public Principal(SerieRepository repositorio) {
        this.repositorio = repositorio;
    }

    public void exibirMenu() {
        int opcao = -1;
        while (opcao != 0) {
            String menu = """
                    1 - Buscar Série
                    2 - Buscar episódios
                    3 - Listar series buscadas
                    4 - Buscar série por Titulo
                    5 - Buscar serie por Ator e Avaliacao
                    6 - Buscar Top 5 series
                    7 - Buscar serie por Categoria
                    8 - Buscar por máximo de temporadas e minimo de avaliacao
                    9 - Buscar por trecho do episodio
                    10 - Buscar top 5 episódios por série
                    11 - Buscar episódios a partir de uma data
                    
                    0 - Sair
                    """;

            System.out.println(menu);
            opcao = leitor.nextInt();
            leitor.nextLine();

            switch (opcao) {
                case 0:
                    System.out.println("Saindo....");
                    break;
                case 1:
                    buscarSerieWeb();
                    break;
                case 2:
                    buscarEpisodioPorSerie();
                    break;
                case 3:
                    listarSeriesBuscadas();
                    break;
                case 4:
                    buscarSeriePorTitulo();
                    break;
                case 5:
                    buscarSeriePorAtor();
                    break;
                case 6:
                    buscarTop5Series();
                    break;
                case 7:
                    buscarSeriePorCategoria();
                    break;
                case 8:
                    buscarSeriePorMaximoTemporadas();
                    break;
                case 9:
                    buscarEpisodioPorTrecho();
                    break;
                case 10:
                    topEpisodiosPorSerie();
                    break;
                case 11:
                    buscarEpisodiosDepoisDeUmaData();
                    break;
                default:
                    System.out.println("Opção inválida!");
                    break;
            }
        }
    }

    private void buscarSerieWeb() {
        DadosSerie dados = getDadosSerie();
        Serie serie = new Serie(dados);
        repositorio.save(serie);
        System.out.println(dados);
    }

    private DadosSerie getDadosSerie() {
        System.out.println("Digite o nome da serie: ");
        String nomeSerie = leitor.nextLine();
        String json = consumo.obterDados(URL_BASE + nomeSerie.replace(" ", "+") + API_KEY);
        DadosSerie dados = conversor.obterDados(json, DadosSerie.class);
        return dados;
    }

    private void buscarEpisodioPorSerie() {
        listarSeriesBuscadas();
        System.out.println("Digite o nome da serie: ");
        String nomeSerie = leitor.nextLine();
        Optional<Serie> serie = repositorio.findByTituloContainingIgnoreCase(nomeSerie);

        if (serie.isPresent()) {
            Serie serieEncontrada = serie.get();
            List<DadosTemporada> temporadas = new ArrayList<>();

            for (int i = 1; i <= serieEncontrada.getTotalTemporadas(); i++) {
                String json = consumo.obterDados(URL_BASE + serieEncontrada.getTitulo().replace(" ", "+")
                        + "&season=" + i + API_KEY);
                DadosTemporada dadosTemporada = conversor.obterDados(json, DadosTemporada.class);
                temporadas.add(dadosTemporada);
            }
            temporadas.forEach(System.out::println);

            List<Episodio> episodios = temporadas.stream()
                    .flatMap(t -> t.episodios().stream()
                            .map(e -> new Episodio(t.numero(), e)))
                    .collect(Collectors.toList());
            serieEncontrada.setEpisodios(episodios);
            repositorio.save(serieEncontrada);
        } else {
            System.out.println("Série não encontrada!");
        }
    }

    private void listarSeriesBuscadas() {
        series = repositorio.findAll();
        series.stream()
                .sorted(Comparator.comparing(Serie::getGenero))
                .forEach(System.out::println);
    }

    private void buscarSeriePorTitulo() {
        System.out.println("Digite o nome da serie: ");
        String nomeSerie = leitor.nextLine();
        serieBusca = repositorio.findByTituloContainingIgnoreCase(nomeSerie);

        if (serieBusca.isPresent()) {
            System.out.println("Dados da série: " + serieBusca.get());
        } else {
            System.out.println("Serie não encontrada!");
        }
    }

    private void buscarSeriePorAtor() {
        System.out.println("Digite o nome do ator: ");
        String nomeAtor = leitor.nextLine();
        System.out.println("Digite a avaliacao minima da série: ");
        Double avaliacaoMinima = leitor.nextDouble();
        List<Serie> seriesEncontradas = repositorio.findByAtoresContainingIgnoreCaseAndAvaliacaoGreaterThanEqual(nomeAtor, avaliacaoMinima);
        System.out.println("Series em que " + nomeAtor + " trabalhou com avaliacao minima de " + avaliacaoMinima + ": ");
        seriesEncontradas.forEach(s ->
                System.out.println("Titulo: " + s.getTitulo() + " Avaliação: " + s.getAvaliacao()));
    }

    private void buscarTop5Series() {
        List<Serie> seriesTop = repositorio.findTop5ByOrderByAvaliacaoDesc();
        seriesTop.forEach(s ->
                System.out.println("Titulo: " + s.getTitulo() + " Avaliação: " + s.getAvaliacao()));
    }

    private void buscarSeriePorCategoria() {
        System.out.println("Digite a categoria: ");
        String nomeCategoria = leitor.nextLine();
        Categoria categoria = Categoria.fromPortugues(nomeCategoria);
        List<Serie> seriesCategoria = repositorio.findByGenero(categoria);
        System.out.println("Series da categoria " + nomeCategoria);
        seriesCategoria.forEach(s -> System.out.println(s.getTitulo()));
    }

    private void  buscarSeriePorMaximoTemporadas() {
        System.out.println("Digite o máximo de temporadas: ");
        Integer maximoTemporadas = leitor.nextInt();
        System.out.println("Digite a avaliaco minima: ");
        Double avaliacaoMinima = leitor.nextDouble();
        List<Serie> seriesEncontradas = repositorio.seriesPorTemporadaEAvaliacao(maximoTemporadas, avaliacaoMinima);
        seriesEncontradas.forEach(s ->
                System.out.println("Titulo: " + s.getTitulo() + " Temporadas: " + s.getTotalTemporadas() + " Avaliacao: " + s.getAvaliacao()));
    }

    private void buscarEpisodioPorTrecho() {
        System.out.println("Digite o trecho do titulo do episodio: ");
        String nomeTrecho = leitor.nextLine();
        List<Episodio> episodiosEncontrados = repositorio.episodiosPorTrecho(nomeTrecho);
        episodiosEncontrados.forEach(e ->
                System.out.printf("Serie: %s - Temporada: %s - Episodio: %s\n",
                        e.getSerie().getTitulo(), e.getTemporada(), e.getTitulo()));
    }

    private void topEpisodiosPorSerie() {
        buscarSeriePorTitulo();

        if (serieBusca.isPresent()) {
            Serie serie = serieBusca.get();
            List<Episodio> topEpisodios = repositorio.topEpisodiosPorSerie(serie);
            topEpisodios.forEach(e ->
                    System.out.printf("Serie: %s - Temporada: %s - Episodio: %s - Avaliacao: %s\n",
                            e.getSerie().getTitulo(), e.getTemporada(), e.getTitulo(), e.getAvaliacao()));
        }
    }

    private void buscarEpisodiosDepoisDeUmaData() {
        buscarSeriePorTitulo();
        if (serieBusca.isPresent()) {
            Serie serie = serieBusca.get();
            System.out.println("Digite o ano limite de lancamento: ");
            int anoLimite = leitor.nextInt();
            leitor.nextLine();
            List<Episodio> episodiosData = repositorio.episodiosDepoisDeUmaData(serie, anoLimite);
            episodiosData.forEach(e ->
                    System.out.printf("Serie: %s - Episodio: %s - Ano Lançamento: %s\n",
                            e.getSerie().getTitulo(), e.getTitulo(), e.getDataLancamento()));
        }
    }
}
