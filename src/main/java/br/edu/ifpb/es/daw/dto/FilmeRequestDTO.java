package br.edu.ifpb.es.daw.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.List;

public record FilmeRequestDTO(
        @NotBlank(message = "O título é obrigatório")
        String titulo,

        String descricao,

        @NotBlank(message = "A URL do filme é obrigatória")
        String urlFilme,

        @NotNull(message = "A duração é obrigatória")
        @Positive(message = "A duração deve ser maior que zero")
        Integer duracaoMinutos,

        @NotNull(message = "O ID do Administrador é obrigatório")
        Long idAdministrador,

        List<Long> generosIds,

        Long versao
) {}