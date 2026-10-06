package com.gestion.mundial.app.controladores;
 
import com.gestion.mundial.app.entidades.Asociacion;
import com.gestion.mundial.app.entidades.Club;
import com.gestion.mundial.app.servicios.AsociacionServicio;
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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
 
import java.net.URI;
import java.util.List;
 
/**
 * API REST de asociaciones (CRUD completo + consulta de sus clubes).
 *
 *  GET    /api/asociaciones              listar
 *  GET    /api/asociaciones/{id}         obtener una
 *  GET    /api/asociaciones/{id}/clubes  clubes afiliados
 *  POST   /api/asociaciones              crear       -> 201
 *  PUT    /api/asociaciones/{id}         actualizar  -> 200
 *  DELETE /api/asociaciones/{id}         eliminar    -> 204 (400 si tiene clubes)
 */
@RestController
@RequestMapping("/api/asociaciones")
@RequiredArgsConstructor
public class AsociacionRestControlador {
 
    private final AsociacionServicio asociacionServicio;
    private final ClubServicio clubServicio;
 
    @GetMapping
    public List<Asociacion> listar() {
        return asociacionServicio.listar();
    }
 
    @GetMapping("/{id}")
    public Asociacion obtener(@PathVariable String id) {
        return asociacionServicio.buscarPorId(id);
    }
 
    @GetMapping("/{id}/clubes")
    public List<Club> clubes(@PathVariable String id) {
        asociacionServicio.buscarPorId(id); // 404 si no existe
        return clubServicio.listarPorAsociacion(id);
    }
 
    @PostMapping
    public ResponseEntity<Asociacion> crear(@Valid @RequestBody Asociacion asociacion) {
        Asociacion creada = asociacionServicio.crear(asociacion);
        URI ubicacion = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(creada.getId()).toUri();
        return ResponseEntity.created(ubicacion).body(creada);
    }
 
    @PutMapping("/{id}")
    public Asociacion actualizar(@PathVariable String id, @Valid @RequestBody Asociacion asociacion) {
        return asociacionServicio.actualizar(id, asociacion);
    }
 
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable String id) {
        asociacionServicio.eliminar(id);
    }
}
