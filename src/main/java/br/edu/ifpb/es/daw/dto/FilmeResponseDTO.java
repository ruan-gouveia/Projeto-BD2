package br.edu.ifpb.es.daw.dto;

import br.edu.ifpb.es.daw.entities.Filme;
import br.edu.ifpb.es.daw.entities.Genero;

import java.util.List;
import java.util.stream.Collectors;

import java.io.Serializable;

public record FilmeResponseDTO(
        Long idConteudo,
        String titulo,
        String descricao,
        String urlFilme,
        Integer duracaoMinutos,
        List<String> generos
) implements Serializable {
    public FilmeResponseDTO(Filme f) {
        this(
                f.getIdConteudo(),
                f.getTitulo(),
                f.getDescricao(),
                f.getUrlFilme(),
                f.getDuracao(),
                f.getGeneros() != null ? f.getGeneros().stream().map(Genero::getNome).collect(Collectors.toList()) : List.of()
        );
    }
}


