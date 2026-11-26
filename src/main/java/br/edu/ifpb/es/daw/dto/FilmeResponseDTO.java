package br.edu.ifpb.es.daw.dto;

import br.edu.ifpb.es.daw.entities.Filme;
import br.edu.ifpb.es.daw.entities.Genero;

import java.util.List;
import java.util.stream.Collectors;

public record FilmeResponseDTO(
        Long idConteudo,
        String titulo,
        String descricao,
        String urlFilme,
        Integer duracaoMinutos,
        List<String> generos,
        Long versao
) {
    public FilmeResponseDTO(Filme f) {
        this(
                f.getIdConteudo(),
                f.getTitulo(),
                f.getDescricao(),
                f.getUrlFilme(),
                f.getDuracao(),
                f.getGeneros() != null ? f.getGeneros().stream().map(Genero::getNome).collect(Collectors.toList()) : List.of(),
                f.getVersao()
        );
    }
}