package br.com.alura.screenmatchspring.service;

import br.com.alura.screenmatchspring.model.DadosTraducao;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import java.net.URLEncoder;

public class ConsultaMyMemory {
    public static String obterTraducao(String text) {
        ObjectMapper mapper = new ObjectMapper();
        ConsumoApi consumo = new ConsumoApi();
        String texto = URLEncoder.encode(text);
        String langpair = URLEncoder.encode("autodetect|pt-br");
        String url = "https://api.mymemory.translated.net/get?q=" + texto + "&langpair=" + langpair;
        String json = consumo.obterDados(url);
        DadosTraducao traducao;
        try {
            traducao = mapper.readValue(json, DadosTraducao.class);
        } catch (JacksonException e) {
            throw new RuntimeException(e);
        }

        return traducao.dadosRespostaTraducao().textoTraduzido();
    }
}
