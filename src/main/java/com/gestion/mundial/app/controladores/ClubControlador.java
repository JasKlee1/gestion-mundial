package com.gestion.mundial.app.controladores;
 
import com.gestion.mundial.app.entidades.Club;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
 
import java.util.List;
 
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
 
    // ---------- Creación ----------
 
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
                Club guardado = clubServicio.guardar(club, asociacionId, competicionIds);
                redirect.addFlashAttribute("exito", guardado.getNombre() + " ya está en el álbum.");
                return "redirect:/clubes";
            } catch (ReglaNegocioException e) {
                resultado.rejectValue(e.getCampo(), "negocio", e.getMessage());
            }
        }
        // Hay errores: volvemos al formulario conservando lo que el usuario eligió
        cargarCatalogos(model, asociacionId, competicionIds);
        return "clubes/formulario";
    }
 
    // ---------- Eliminación ----------
 
    @PostMapping("/clubes/{id}/eliminar")
    public String eliminar(@PathVariable String id, RedirectAttributes redirect) {
        String nombre = clubServicio.buscarPorId(id).getNombre();
        clubServicio.eliminar(id);
        redirect.addFlashAttribute("exito", nombre + " se eliminó del álbum.");
        return "redirect:/clubes";
    }
 
    private void cargarCatalogos(Model model, String asociacionId, List<String> competicionIds) {
        model.addAttribute("asociaciones", clubServicio.listarAsociaciones());
        model.addAttribute("competiciones", clubServicio.listarCompeticiones());
        model.addAttribute("posiciones", Posicion.values());
        model.addAttribute("asociacionId", asociacionId);
        model.addAttribute("competicionIds", competicionIds == null ? List.of() : competicionIds);
        model.addAttribute("seccion", "nuevo");
    }
}
