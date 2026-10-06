package com.gestion.mundial.app.servicios;
 
import com.gestion.mundial.app.entidades.Asociacion;
import com.gestion.mundial.app.entidades.Club;
import com.gestion.mundial.app.entidades.Competicion;
import com.gestion.mundial.app.entidades.Jugador;
import com.gestion.mundial.app.repositorio.AsociacionRepositorio;
import com.gestion.mundial.app.repositorio.ClubRepositorio;
import com.gestion.mundial.app.repositorio.CompeticionRepositorio;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
 
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
 
@Service
@RequiredArgsConstructor
public class ClubServicio {
 
    private final ClubRepositorio clubRepositorio;
    private final AsociacionRepositorio asociacionRepositorio;
    private final CompeticionRepositorio competicionRepositorio;
 
    // ---------- Consultas ----------
 
    public List<Club> listar(String filtro) {
        if (filtro == null || filtro.isBlank()) {
            return clubRepositorio.findAllByOrderByNombreAsc();
        }
        return clubRepositorio.findByNombreContainingIgnoreCaseOrderByNombreAsc(filtro.trim());
    }
 
    public Club buscarPorId(String id) {
        return clubRepositorio.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "No existe un club con id " + id));
    }
 
    public List<Asociacion> listarAsociaciones() {
        return asociacionRepositorio.findAllByOrderByNombreAsc();
    }
 
    public List<Competicion> listarCompeticiones() {
        return competicionRepositorio.findAllByOrderByFechaInicioAsc();
    }
 
    public int contarJugadores(List<Club> clubes) {
        return clubes.stream()
                .mapToInt(c -> c.getJugadores() == null ? 0 : c.getJugadores().size())
                .sum();
    }
 
    // ---------- Escritura ----------
 
    /**
     * Crea un club. Recibe los IDs de asociación y competiciones por separado
     * porque en Mongo esas relaciones son referencias a otras colecciones.
     */
    public Club guardar(Club club, String asociacionId, List<String> competicionIds) {
        club.setId(null);                              // siempre es un alta: nunca sobrescribir otro club
        club.setNombre(club.getNombre().trim());
 
        if (clubRepositorio.existsByNombreIgnoreCase(club.getNombre())) {
            throw new ReglaNegocioException("nombre", "Ya existe un club llamado " + club.getNombre());
        }
 
        // Quitar filas vacías que pudieran llegar del formulario
        List<Jugador> jugadores = club.getJugadores() == null ? new ArrayList<>()
                : new ArrayList<>(club.getJugadores().stream().filter(Objects::nonNull).toList());
        club.setJugadores(jugadores);
        validarDorsales(jugadores);
 
        club.setAsociacion(resolverAsociacion(asociacionId));
        club.setCompeticiones(resolverCompeticiones(competicionIds));
 
        return clubRepositorio.save(club);
    }
 
    public void eliminar(String id) {
        Club club = buscarPorId(id);
        // Jugadores y entrenador están embebidos: se borran junto con el club (como un CASCADE).
        // Asociación y competiciones son referencias: NO se borran.
        clubRepositorio.delete(club);
    }
 
    // ---------- Reglas internas ----------
 
    private void validarDorsales(List<Jugador> jugadores) {
        Set<Integer> usados = new HashSet<>();
        for (int i = 0; i < jugadores.size(); i++) {
            Integer dorsal = jugadores.get(i).getNumero();
            if (dorsal != null && !usados.add(dorsal)) {
                throw new ReglaNegocioException("jugadores[" + i + "].numero",
                        "El dorsal " + dorsal + " ya está asignado");
            }
        }
    }
 
    private Asociacion resolverAsociacion(String asociacionId) {
        if (asociacionId == null || asociacionId.isBlank()) {
            throw new ReglaNegocioException("asociacion", "Selecciona la asociación del club");
        }
        return asociacionRepositorio.findById(asociacionId)
                .orElseThrow(() -> new ReglaNegocioException("asociacion", "La asociación seleccionada no existe"));
    }
 
    private List<Competicion> resolverCompeticiones(List<String> ids) {
        if (ids == null || ids.isEmpty()) return new ArrayList<>();
        List<Competicion> encontradas = new ArrayList<>(competicionRepositorio.findAllById(ids));
        if (encontradas.size() != new HashSet<>(ids).size()) {
            throw new ReglaNegocioException("competiciones", "Alguna competición seleccionada no existe");
        }
        return encontradas;
    }
}
