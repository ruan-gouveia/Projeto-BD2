package br.edu.ifpb.es.daw.dto;

import br.edu.ifpb.es.daw.entities.Conteudo;
import br.edu.ifpb.es.daw.entities.Filme;
import br.edu.ifpb.es.daw.entities.Serie;

public record ConteudoResponseDTO(
        Long id,
        String titulo,
        String descricao,
        String tipo,
        Long versao
) {
    public static ConteudoResponseDTO daEntidade(Conteudo conteudo) {
        String tipoConteudo = "CONTEUDO";
        if (conteudo instanceof Filme) tipoConteudo = "FILME";
        if (conteudo instanceof Serie) tipoConteudo = "SERIE";

        return new ConteudoResponseDTO(
                conteudo.getIdConteudo(),
                conteudo.getTitulo(),
                conteudo.getDescricao(),
                tipoConteudo,
                conteudo.getVersao()
        );
    }
}