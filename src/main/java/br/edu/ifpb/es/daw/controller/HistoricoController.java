package br.edu.ifpb.es.daw.controller;

import br.edu.ifpb.es.daw.dto.HistoricoRequestDTO;
import br.edu.ifpb.es.daw.dto.HistoricoResponseDTO;
import br.edu.ifpb.es.daw.service.HistoricoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/historico")
@CrossOrigin(origins = "*")
@Tag(name = "Histórico", description = "Endpoints para registro de visualização de conteúdo")
public class HistoricoController {

    private final HistoricoService historicoService;

    public HistoricoController(HistoricoService historicoService) {
        this.historicoService = historicoService;
    }

    @PostMapping
    @Operation(summary = "Registrar que um usuário assistiu a um conteúdo (assíncrono)")
    public ResponseEntity<Void> registrar(@Valid @RequestBody HistoricoRequestDTO dto) {
        historicoService.registrar(dto);
        return ResponseEntity.accepted().build();
    }

    @GetMapping("/usuario/{idUsuario}")
    @Operation(summary = "Listar o histórico de um usuário")
    public ResponseEntity<List<HistoricoResponseDTO>> listarHistoricoUsuario(@PathVariable Long idUsuario) {
        return ResponseEntity.ok(historicoService.buscarHistoricoPorUsuario(idUsuario));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remover um item do histórico")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        historicoService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}