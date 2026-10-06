package com.gestion.mundial.app.controladores;
 
import com.gestion.mundial.app.entidades.Club;
import com.gestion.mundial.app.entidades.Competicion;
import com.gestion.mundial.app.servicios.ClubServicio;
import com.gestion.mundial.app.servicios.ReglaNegocioException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
 
import java.util.List;
import java.util.Map;
import java.util.Objects;
 
/**
 * API REST subyacente: misma lógica (ClubServicio) que la vista Thymeleaf.
 * POST de ejemplo:
 * { "nombre":"Millonarios", "pais":"Colombia",
 *   "entrenador":{"nombre":"Ana","apellido":"Pérez","edad":45,"nacionalidad":"Colombiana"},
 *   "jugadores":[{"nombre":"Juan","apellido":"Gómez","numero":10,"posicion":"MEDIOCAMPISTA"}],
 *   "asociacion":{"id":"<idAsociacion>"},
 *   "competiciones":[{"id":"<idCompeticion>"}] }
 */
@RestController
@RequestMapping("/api/clubes")
@RequiredArgsConstructor
public class ClubRestControlador {
 
    private final ClubServicio clubServicio;
 
    @GetMapping
    public List<Club> listar(@RequestParam(required = false) String q) {
        return clubServicio.listar(q);
    }
 
    @GetMapping("/{id}")
    public Club obtener(@PathVariable String id) {
        return clubServicio.buscarPorId(id);
    }
 
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Club crear(@Valid @RequestBody Club club) {
        String asociacionId = club.getAsociacion() == null ? null : club.getAsociacion().getId();
        List<String> competicionIds = club.getCompeticiones() == null ? List.of()
                : club.getCompeticiones().stream().filter(Objects::nonNull).map(Competicion::getId).toList();
        return clubServicio.guardar(club, asociacionId, competicionIds);
    }
 
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable String id) {
        clubServicio.eliminar(id);
    }
 
    @ExceptionHandler(ReglaNegocioException.class)
    public ResponseEntity<Map<String, String>> reglaNegocio(ReglaNegocioException e) {
        return ResponseEntity.badRequest().body(Map.of("campo", e.getCampo(), "mensaje", e.getMessage()));
    }
}
