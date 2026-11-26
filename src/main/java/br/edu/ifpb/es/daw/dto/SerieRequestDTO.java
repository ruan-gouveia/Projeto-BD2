package br.edu.ifpb.es.daw.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record SerieRequestDTO(
        @NotBlank(message = "O título é obrigatório")
        String titulo,

        String descricao,

        @NotNull(message = "O ID do Administrador é obrigatório")
        Long idAdministrador,

        List<Long> generosIds,

        Long versao
) {}