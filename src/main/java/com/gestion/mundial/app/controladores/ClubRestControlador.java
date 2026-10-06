package com.gestion.mundial.app.controladores;
 
import com.gestion.mundial.app.entidades.Club;
import com.gestion.mundial.app.entidades.Competicion;
import com.gestion.mundial.app.servicios.ClubServicio;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
 
import java.net.URI;
import java.util.List;
import java.util.Objects;
 
/**
 * API REST de clubes (CRUD completo).
 *
 *  GET    /api/clubes          listar (opcional ?q=texto)
 *  GET    /api/clubes/{id}     obtener uno
 *  POST   /api/clubes          crear       -> 201 Created + cabecera Location
 *  PUT    /api/clubes/{id}     actualizar  -> 200 OK
 *  DELETE /api/clubes/{id}     eliminar    -> 204 No Content
 *
 * Las relaciones se envían solo con su id:
 *  "asociacion": {"id": "..."}   y   "competiciones": [{"id": "..."}]
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
    public ResponseEntity<Club> crear(@Valid @RequestBody Club club) {
        Club creado = clubServicio.crear(club, asociacionId(club), competicionIds(club));
        URI ubicacion = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(creado.getId()).toUri();
        return ResponseEntity.created(ubicacion).body(creado);
    }
 
    @PutMapping("/{id}")
    public Club actualizar(@PathVariable String id, @Valid @RequestBody Club club) {
        return clubServicio.actualizar(id, club, asociacionId(club), competicionIds(club));
    }
 
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable String id) {
        clubServicio.eliminar(id);
    }
 
    // El JSON trae objetos con id; el servicio solo necesita los ids
    private String asociacionId(Club club) {
        return club.getAsociacion() == null ? null : club.getAsociacion().getId();
    }
 
    private List<String> competicionIds(Club club) {
        return club.getCompeticiones() == null ? List.of()
                : club.getCompeticiones().stream().filter(Objects::nonNull).map(Competicion::getId).toList();
    }
}
