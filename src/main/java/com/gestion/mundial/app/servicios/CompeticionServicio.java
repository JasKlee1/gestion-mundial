package com.gestion.mundial.app.servicios;
 
import com.gestion.mundial.app.entidades.Club;
import com.gestion.mundial.app.entidades.Competicion;
import com.gestion.mundial.app.repositorio.ClubRepositorio;
import com.gestion.mundial.app.repositorio.CompeticionRepositorio;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
 
import java.util.HashMap;
import java.util.List;
import java.util.Map;
 
/** CRUD de competiciones con sus reglas de integridad. */
@Service
@RequiredArgsConstructor
public class CompeticionServicio {
 
    private final CompeticionRepositorio competicionRepositorio;
    private final ClubRepositorio clubRepositorio;
    private final ClubServicio clubServicio;
 
    public List<Competicion> listar() {
        return competicionRepositorio.findAllByOrderByFechaInicioAsc();
    }
 
    public Competicion buscarPorId(String id) {
        return competicionRepositorio.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "No existe una competición con id " + id));
    }
 
    /** Cantidad de clubes inscritos por competición (clave: id de la competición). */
    public Map<String, Long> clubesPorCompeticion() {
        Map<String, Long> conteo = new HashMap<>();
        for (Club club : clubRepositorio.findAll()) {
            if (club.getCompeticiones() == null) continue;
            club.getCompeticiones().stream()
                    .filter(c -> c != null && c.getId() != null)
                    .forEach(c -> conteo.merge(c.getId(), 1L, Long::sum));
        }
        return conteo;
    }
 
    public Competicion crear(Competicion competicion) {
        competicion.setId(null);
        validar(competicion);
        return competicionRepositorio.save(competicion);
    }
 
    public Competicion actualizar(String id, Competicion datos) {
        buscarPorId(id);
        datos.setId(id);
        validar(datos);
        return competicionRepositorio.save(datos);
    }
 
    /**
     * Antes de borrar la competición se retira de los clubes inscritos,
     * para que ningún club quede apuntando a una competición inexistente.
     */
    public void eliminar(String id) {
        Competicion competicion = buscarPorId(id);
        for (Club club : clubServicio.listarPorCompeticion(id)) {
            club.getCompeticiones().removeIf(c -> c == null || id.equals(c.getId()));
            clubRepositorio.save(club);
        }
        competicionRepositorio.delete(competicion);
    }
 
    private void validar(Competicion competicion) {
        competicion.setNombre(competicion.getNombre().trim());
        if (competicion.getFechaFin().isBefore(competicion.getFechaInicio())) {
            throw new ReglaNegocioException("fechaFin",
                    "La fecha de fin debe ser igual o posterior a la de inicio");
        }
    }
}
