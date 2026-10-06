package com.gestion.mundial.app.controladores;
 
import com.gestion.mundial.app.entidades.Club;
import com.gestion.mundial.app.entidades.Competicion;
import com.gestion.mundial.app.entidades.Posicion;
import com.gestion.mundial.app.servicios.ClubServicio;
import com.gestion.mundial.app.servicios.ReglaNegocioException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
 
import java.util.List;
import java.util.Objects;
 
/** Vistas web de clubes: listado, alta, edición y eliminación. */
@Controller
@RequiredArgsConstructor
public class ClubControlador {
 
    private final ClubServicio clubServicio;
 
    /** Seguridad: el formulario no puede inyectar id ni referencias directamente. */
    @InitBinder("club")
    void configurarBinder(WebDataBinder binder) {
        binder.setDisallowedFields("id", "asociacion*", "competiciones*");
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true)); // "  " -> null
    }
 
    // ---------- Listado ----------
 
    @GetMapping({"/", "/clubes"})
    public String listar(@RequestParam(required = false) String q, Model model) {
        List<Club> clubes = clubServicio.listar(q);
        model.addAttribute("clubes", clubes);
        model.addAttribute("q", q);
        model.addAttribute("totalJugadores", clubServicio.contarJugadores(clubes));
        model.addAttribute("totalCompeticiones", clubServicio.listarCompeticiones().size());
        model.addAttribute("seccion", "clubes");
        return "index";
    }
 
    // ---------- Alta ----------
 
    @GetMapping("/clubes/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("club", new Club());
        cargarCatalogos(model, null, null);
        return "clubes/formulario";
    }
 
    @PostMapping("/clubes/guardar")
    public String guardar(@Valid @ModelAttribute("club") Club club,
                          BindingResult resultado,
                          @RequestParam(required = false) String asociacionId,
                          @RequestParam(required = false) List<String> competicionIds,
                          Model model,
                          RedirectAttributes redirect) {
        if (!resultado.hasErrors()) {
            try {
                Club guardado = clubServicio.crear(club, asociacionId, competicionIds);
                redirect.addFlashAttribute("exito", guardado.getNombre() + " ya está en la edición.");
                return "redirect:/clubes";
            } catch (ReglaNegocioException e) {
                registrarError(resultado, e);
            }
        }
        cargarCatalogos(model, asociacionId, competicionIds);
        return "clubes/formulario";
    }
 
    // ---------- Edición ----------
 
    @GetMapping("/clubes/{id}/editar")
    public String editar(@PathVariable String id, Model model) {
        Club club = clubServicio.buscarPorId(id);
        model.addAttribute("club", club);
        String asociacionId = club.getAsociacion() == null ? null : club.getAsociacion().getId();
        List<String> competicionIds = club.getCompeticiones() == null ? List.of()
                : club.getCompeticiones().stream().filter(Objects::nonNull).map(Competicion::getId).toList();
        cargarCatalogos(model, asociacionId, competicionIds);
        return "clubes/formulario";
    }
 
    @PostMapping("/clubes/{id}/actualizar")
    public String actualizar(@PathVariable String id,
                             @Valid @ModelAttribute("club") Club club,
                             BindingResult resultado,
                             @RequestParam(required = false) String asociacionId,
                             @RequestParam(required = false) List<String> competicionIds,
                             Model model,
                             RedirectAttributes redirect) {
        club.setId(id); // para que el formulario conserve la ruta de edición si hay errores
        if (!resultado.hasErrors()) {
            try {
                Club actualizado = clubServicio.actualizar(id, club, asociacionId, competicionIds);
                redirect.addFlashAttribute("exito", "Cambios guardados en " + actualizado.getNombre() + ".");
                return "redirect:/clubes";
            } catch (ReglaNegocioException e) {
                registrarError(resultado, e);
            }
        }
        cargarCatalogos(model, asociacionId, competicionIds);
        return "clubes/formulario";
    }
 
    // ---------- Eliminación ----------
 
    @PostMapping("/clubes/{id}/eliminar")
    public String eliminar(@PathVariable String id, RedirectAttributes redirect) {
        String nombre = clubServicio.buscarPorId(id).getNombre();
        clubServicio.eliminar(id);
        redirect.addFlashAttribute("exito", nombre + " se eliminó de la edición.");
        return "redirect:/clubes";
    }
 
    // ---------- Apoyo ----------
 
    private void cargarCatalogos(Model model, String asociacionId, List<String> competicionIds) {
        model.addAttribute("asociaciones", clubServicio.listarAsociaciones());
        model.addAttribute("competiciones", clubServicio.listarCompeticiones());
        model.addAttribute("posiciones", Posicion.values());
        model.addAttribute("asociacionId", asociacionId);
        model.addAttribute("competicionIds", competicionIds == null ? List.of() : competicionIds);
        model.addAttribute("seccion", "clubes");
    }
 
    private void registrarError(BindingResult resultado, ReglaNegocioException e) {
        if (e.getCampo() == null) {
            resultado.reject("negocio", e.getMessage());
        } else {
            resultado.rejectValue(e.getCampo(), "negocio", e.getMessage());
        }
    }
}
