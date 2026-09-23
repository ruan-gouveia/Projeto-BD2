package br.edu.ifpb.es.daw.service;

import br.edu.ifpb.es.daw.dto.FilmeRequestDTO;
import br.edu.ifpb.es.daw.dto.FilmeResponseDTO;
import br.edu.ifpb.es.daw.entities.Administrador;
import br.edu.ifpb.es.daw.entities.Filme;
import br.edu.ifpb.es.daw.entities.Genero;
import br.edu.ifpb.es.daw.exception.RecursoNaoEncontradoException;
import br.edu.ifpb.es.daw.repository.AdministradorRepository;
import br.edu.ifpb.es.daw.repository.FilmeRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import br.edu.ifpb.es.daw.config.CacheNames;
import org.springframework.cache.annotation.Cacheable;


import java.util.List;

@Service
public class FilmeService {

    private final FilmeRepository filmeRepository;
    private final AdministradorRepository administradorRepository;
    private final GeneroService generoService;

    public FilmeService(FilmeRepository filmeRepository, AdministradorRepository administradorRepository, GeneroService generoService) {
        this.filmeRepository = filmeRepository;
        this.administradorRepository = administradorRepository;
        this.generoService = generoService;
    }

    public Page<FilmeResponseDTO> listarTodos(Pageable pageable) {
        return filmeRepository.findAll(pageable)
                .map(FilmeResponseDTO::new);
    }

    @Cacheable(cacheNames = CacheNames.FILMES, key = "#id")
    public FilmeResponseDTO buscarPorId(Long id) {
        return filmeRepository.findById(id)
                .map(FilmeResponseDTO::new)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Filme não encontrado!"));
    }

    @Transactional
    public FilmeResponseDTO atualizar(Long id, FilmeRequestDTO dto) {
        Filme filme = filmeRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Filme não encontrado"));

        filme.setTitulo(dto.titulo());
        filme.setDescricao(dto.descricao());
        filme.setUrlFilme(dto.urlFilme());
        filme.setDuracao(dto.duracaoMinutos());

        if (dto.generosIds() != null && !dto.generosIds().isEmpty()) {
            filme.setGeneros(generoService.buscarEntidadesPorIds(dto.generosIds()));
        }

        return new FilmeResponseDTO(filmeRepository.save(filme));
    }

    @Transactional
    public FilmeResponseDTO salvar(FilmeRequestDTO dto) {

        Administrador adm = administradorRepository.findById(dto.idAdministrador())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Administrador não encontrado!"));

        Filme filme = new Filme();
        filme.setTitulo(dto.titulo());
        filme.setDescricao(dto.descricao());
        filme.setTipo("FILME");
        filme.setUrlFilme(dto.urlFilme());
        filme.setDuracao(dto.duracaoMinutos());
        filme.setAdministrador(adm);

        if (dto.generosIds() != null && !dto.generosIds().isEmpty()) {
            List<Genero> generos = generoService.buscarEntidadesPorIds(dto.generosIds());
            filme.setGeneros(generos);
        }

        Filme filmeSalvo = filmeRepository.save(filme);
        return new FilmeResponseDTO(filmeSalvo);
    }

    @Transactional
    public void deletar(Long id) {
        if (!filmeRepository.existsById(id)) {
            throw new RecursoNaoEncontradoException("Filme não encontrado para deleção.");
        }
        filmeRepository.deleteById(id);
    }
}