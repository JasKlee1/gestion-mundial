package com.gestion.mundial.app.controladores;
 
import com.gestion.mundial.app.entidades.Club;
import com.gestion.mundial.app.entidades.Competicion;
import com.gestion.mundial.app.servicios.ClubServicio;
import com.gestion.mundial.app.servicios.CompeticionServicio;
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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
 
import java.net.URI;
import java.util.List;
 
/**
 * API REST de competiciones (CRUD completo + consulta de clubes inscritos).
 *
 *  GET    /api/competiciones              listar
 *  GET    /api/competiciones/{id}         obtener una
 *  GET    /api/competiciones/{id}/clubes  clubes inscritos
 *  POST   /api/competiciones              crear       -> 201
 *  PUT    /api/competiciones/{id}         actualizar  -> 200
 *  DELETE /api/competiciones/{id}         eliminar    -> 204 (se retira de los clubes)
 */
@RestController
@RequestMapping("/api/competiciones")
@RequiredArgsConstructor
public class CompeticionRestControlador {
 
    private final CompeticionServicio competicionServicio;
    private final ClubServicio clubServicio;
 
    @GetMapping
    public List<Competicion> listar() {
        return competicionServicio.listar();
    }
 
    @GetMapping("/{id}")
    public Competicion obtener(@PathVariable String id) {
        return competicionServicio.buscarPorId(id);
    }
 
    @GetMapping("/{id}/clubes")
    public List<Club> clubes(@PathVariable String id) {
        competicionServicio.buscarPorId(id); // 404 si no existe
        return clubServicio.listarPorCompeticion(id);
    }
 
    @PostMapping
    public ResponseEntity<Competicion> crear(@Valid @RequestBody Competicion competicion) {
        Competicion creada = competicionServicio.crear(competicion);
        URI ubicacion = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(creada.getId()).toUri();
        return ResponseEntity.created(ubicacion).body(creada);
    }
 
    @PutMapping("/{id}")
    public Competicion actualizar(@PathVariable String id, @Valid @RequestBody Competicion competicion) {
        return competicionServicio.actualizar(id, competicion);
    }
 
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable String id) {
        competicionServicio.eliminar(id);
    }
}
