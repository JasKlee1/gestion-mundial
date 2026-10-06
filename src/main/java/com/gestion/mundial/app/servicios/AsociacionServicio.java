package com.gestion.mundial.app.servicios;
 
import com.gestion.mundial.app.entidades.Asociacion;
import com.gestion.mundial.app.entidades.Club;
import com.gestion.mundial.app.repositorio.AsociacionRepositorio;
import com.gestion.mundial.app.repositorio.ClubRepositorio;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
 
import java.util.HashMap;
import java.util.List;
import java.util.Map;
 
/** CRUD de asociaciones con sus reglas de integridad. */
@Service
@RequiredArgsConstructor
public class AsociacionServicio {
 
    private final AsociacionRepositorio asociacionRepositorio;
    private final ClubRepositorio clubRepositorio;
    private final ClubServicio clubServicio;
 
    public List<Asociacion> listar() {
        return asociacionRepositorio.findAllByOrderByNombreAsc();
    }
 
    public Asociacion buscarPorId(String id) {
        return asociacionRepositorio.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "No existe una asociación con id " + id));
    }
 
    /** Cantidad de clubes por asociación (clave: id de la asociación). */
    public Map<String, Long> clubesPorAsociacion() {
        Map<String, Long> conteo = new HashMap<>();
        for (Club club : clubRepositorio.findAll()) {
            if (club.getAsociacion() != null) {
                conteo.merge(club.getAsociacion().getId(), 1L, Long::sum);
            }
        }
        return conteo;
    }
 
    public Asociacion crear(Asociacion asociacion) {
        asociacion.setId(null);
        normalizar(asociacion);
        if (asociacionRepositorio.existsBySiglasIgnoreCase(asociacion.getSiglas())) {
            throw new ReglaNegocioException("siglas",
                    "Ya existe una asociación con las siglas " + asociacion.getSiglas());
        }
        return asociacionRepositorio.save(asociacion);
    }
 
    /**
     * Actualiza la asociación. Como los clubes la guardan por referencia (solo su id),
     * todos sus clubes ven los datos nuevos automáticamente.
     */
    public Asociacion actualizar(String id, Asociacion datos) {
        buscarPorId(id);
        datos.setId(id);
        normalizar(datos);
        if (asociacionRepositorio.existsBySiglasIgnoreCaseAndIdNot(datos.getSiglas(), id)) {
            throw new ReglaNegocioException("siglas",
                    "Ya existe otra asociación con las siglas " + datos.getSiglas());
        }
        return asociacionRepositorio.save(datos);
    }
 
    /** No se elimina una asociación mientras tenga clubes afiliados (integridad referencial). */
    public void eliminar(String id) {
        Asociacion asociacion = buscarPorId(id);
        int afiliados = clubServicio.listarPorAsociacion(id).size();
        if (afiliados > 0) {
            throw new ReglaNegocioException(null,
                    "No se puede eliminar " + asociacion.getSiglas() + ": tiene " + afiliados
                            + (afiliados == 1 ? " club afiliado" : " clubes afiliados")
                            + ". Cambia la asociación de esos clubes primero.");
        }
        asociacionRepositorio.delete(asociacion);
    }
 
    private void normalizar(Asociacion asociacion) {
        asociacion.setSiglas(asociacion.getSiglas().trim().toUpperCase());
        asociacion.setNombre(asociacion.getNombre().trim());
    }
}
