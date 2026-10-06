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
 
/**
 * Lógica de negocio de clubes. La usan tanto las vistas Thymeleaf
 * como la API REST, así las reglas son las mismas en ambos lados.
 */
@Service
@RequiredArgsConstructor
public class ClubServicio {
 
    public static final int MAXIMO_JUGADORES = 26;
 
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
 
    /** Clubes afiliados a una asociación. */
    public List<Club> listarPorAsociacion(String asociacionId) {
        return clubRepositorio.findAllByOrderByNombreAsc().stream()
                .filter(c -> c.getAsociacion() != null && asociacionId.equals(c.getAsociacion().getId()))
                .toList();
    }
 
    /** Clubes inscritos en una competición. */
    public List<Club> listarPorCompeticion(String competicionId) {
        return clubRepositorio.findAllByOrderByNombreAsc().stream()
                .filter(c -> c.getCompeticiones() != null && c.getCompeticiones().stream()
                        .anyMatch(comp -> comp != null && competicionId.equals(comp.getId())))
                .toList();
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
 
    // ---------- Crear (POST) ----------
 
    public Club crear(Club club, String asociacionId, List<String> competicionIds) {
        club.setId(null);                              // siempre es un alta: nunca sobrescribir otro club
        club.setNombre(club.getNombre().trim());
 
        if (clubRepositorio.existsByNombreIgnoreCase(club.getNombre())) {
            throw new ReglaNegocioException("nombre", "Ya existe un club llamado " + club.getNombre());
        }
        prepararYValidar(club, asociacionId, competicionIds);
        return clubRepositorio.save(club);
    }
 
    // ---------- Actualizar (PUT) ----------
 
    /** Reemplaza todos los datos del club, incluida su plantilla embebida. */
    public Club actualizar(String id, Club datos, String asociacionId, List<String> competicionIds) {
        buscarPorId(id);                               // 404 si el club no existe
        datos.setId(id);                               // el id lo define la URL, no el cuerpo
        datos.setNombre(datos.getNombre().trim());
 
        if (clubRepositorio.existsByNombreIgnoreCaseAndIdNot(datos.getNombre(), id)) {
            throw new ReglaNegocioException("nombre", "Ya existe otro club llamado " + datos.getNombre());
        }
        prepararYValidar(datos, asociacionId, competicionIds);
        return clubRepositorio.save(datos);            // save con id existente = actualización
    }
 
    // ---------- Eliminar (DELETE) ----------
 
    public void eliminar(String id) {
        Club club = buscarPorId(id);
        // Entrenador y jugadores están embebidos: se borran junto con el club (como un CASCADE).
        // Asociación y competiciones son referencias: NO se borran.
        clubRepositorio.delete(club);
    }
 
    // ---------- Reglas internas ----------
 
    /** Lógica común de POST y PUT: limpia la plantilla, valida dorsales y resuelve referencias. */
    private void prepararYValidar(Club club, String asociacionId, List<String> competicionIds) {
        List<Jugador> jugadores = club.getJugadores() == null ? new ArrayList<>()
                : new ArrayList<>(club.getJugadores().stream().filter(Objects::nonNull).toList());
        if (jugadores.size() > MAXIMO_JUGADORES) {
            throw new ReglaNegocioException("jugadores",
                    "Una plantilla admite máximo " + MAXIMO_JUGADORES + " jugadores");
        }
        club.setJugadores(jugadores);
        validarDorsales(jugadores);
 
        club.setAsociacion(resolverAsociacion(asociacionId));
        club.setCompeticiones(resolverCompeticiones(competicionIds));
    }
 
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
        if (ids == null || ids.isEmpty()) {
            return new ArrayList<>();
        }
        List<Competicion> encontradas = new ArrayList<>(competicionRepositorio.findAllById(ids));
        if (encontradas.size() != new HashSet<>(ids).size()) {
            throw new ReglaNegocioException("competiciones", "Alguna competición seleccionada no existe");
        }
        return encontradas;
    }
}
