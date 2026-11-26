package br.edu.ifpb.es.daw.dto;

import br.edu.ifpb.es.daw.entities.Genero;
import br.edu.ifpb.es.daw.entities.Serie;

import java.util.List;
import java.util.stream.Collectors;

public record SerieResponseDTO(
        Long idConteudo,
        String titulo,
        String descricao,
        List<String> generos,
        Long versao
) {
    public SerieResponseDTO(Serie s) {
        this(
                s.getIdConteudo(),
                s.getTitulo(),
                s.getDescricao(),
                s.getGeneros() != null ? s.getGeneros().stream().map(Genero::getNome).collect(Collectors.toList()) : List.of(),
                s.getVersao()
        );
    }
}