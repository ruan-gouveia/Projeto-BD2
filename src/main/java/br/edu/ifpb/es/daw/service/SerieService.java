package br.edu.ifpb.es.daw.service;

import br.edu.ifpb.es.daw.dto.SerieRequestDTO;
import br.edu.ifpb.es.daw.dto.SerieResponseDTO;
import br.edu.ifpb.es.daw.entities.Administrador;
import br.edu.ifpb.es.daw.entities.Genero;
import br.edu.ifpb.es.daw.entities.Serie;
import br.edu.ifpb.es.daw.exception.ConflitoDeEdicaoException;
import br.edu.ifpb.es.daw.exception.RecursoNaoEncontradoException;
import br.edu.ifpb.es.daw.repository.AdministradorRepository;
import br.edu.ifpb.es.daw.repository.SerieRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
public class SerieService {

    private final SerieRepository serieRepository;
    private final AdministradorRepository administradorRepository;
    private final GeneroService generoService;

    public SerieService(SerieRepository serieRepository, AdministradorRepository administradorRepository, GeneroService generoService) {
        this.serieRepository = serieRepository;
        this.administradorRepository = administradorRepository;
        this.generoService = generoService;
    }

    public Page<SerieResponseDTO> listarTodos(Pageable pageable) {
        return serieRepository.findAll(pageable)
                .map(SerieResponseDTO::new);
    }

    public SerieResponseDTO buscarPorId(Long id) {
        return serieRepository.findById(id)
                .map(SerieResponseDTO::new)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Série não encontrada"));
    }

    @Transactional
    public SerieResponseDTO salvar(SerieRequestDTO dto) {
        Administrador adm = administradorRepository.findById(dto.idAdministrador())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Administrador não encontrado!"));

        Serie serie = new Serie();
        serie.setTitulo(dto.titulo());
        serie.setDescricao(dto.descricao());
        serie.setTipo("SERIE");
        serie.setAdministrador(adm);

        if (dto.generosIds() != null && !dto.generosIds().isEmpty()) {
            List<Genero> generos = generoService.buscarEntidadesPorIds(dto.generosIds());
            serie.setGeneros(generos);
        }

        Serie salva = serieRepository.save(serie);
        return new SerieResponseDTO(salva);
    }

    @Transactional
    public void deletar(Long id) {
        if (!serieRepository.existsById(id)) {
            throw new RecursoNaoEncontradoException("Série não encontrada");
        }
        serieRepository.deleteById(id);
    }

    @Transactional
    public SerieResponseDTO atualizar(Long id, SerieRequestDTO dto) {
        Serie serie = serieRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Série não encontrada"));

        if (dto.versao() == null) {
            throw new IllegalArgumentException("O campo 'versao' é obrigatório na atualização");
        }
        if (!Objects.equals(serie.getVersao(), dto.versao())) {
            throw new ConflitoDeEdicaoException(
                    "Conflito de edição: outro administrador já atualizou este conteúdo. "
                            + "Recarregue os dados e tente novamente.");
        }

        serie.setTitulo(dto.titulo());
        serie.setDescricao(dto.descricao());

        if (dto.generosIds() != null && !dto.generosIds().isEmpty()) {
            serie.setGeneros(generoService.buscarEntidadesPorIds(dto.generosIds()));
        }

        Serie salva = serieRepository.saveAndFlush(serie);
        return new SerieResponseDTO(salva);
    }
}