package com.gestion.mundial.app.controladores;
 
import com.gestion.mundial.app.entidades.Competicion;
import com.gestion.mundial.app.servicios.CompeticionServicio;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
 
/** Vistas web de competiciones: listado, alta, edición y eliminación en una sola página. */
@Controller
@RequestMapping("/competiciones")
@RequiredArgsConstructor
public class CompeticionControlador {
 
    private final CompeticionServicio competicionServicio;
 
    @InitBinder("competicion")
    void configurarBinder(WebDataBinder binder) {
        binder.setDisallowedFields("id");
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
    }
 
    @GetMapping
    public String listar(Model model) {
        model.addAttribute("competicion", new Competicion());
        cargarListado(model);
        return "competiciones/lista";
    }
 
    @GetMapping("/{id}/editar")
    public String editar(@PathVariable String id, Model model) {
        model.addAttribute("competicion", competicionServicio.buscarPorId(id));
        cargarListado(model);
        return "competiciones/lista";
    }
 
    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute("competicion") Competicion competicion,
                          BindingResult resultado,
                          Model model,
                          RedirectAttributes redirect) {
        if (!resultado.hasErrors()) {
            try {
                Competicion guardada = competicionServicio.crear(competicion);
                redirect.addFlashAttribute("exito", guardada.getNombre() + " registrada.");
                return "redirect:/competiciones";
            } catch (ReglaNegocioException e) {
                resultado.rejectValue(e.getCampo(), "negocio", e.getMessage());
            }
        }
        cargarListado(model);
        return "competiciones/lista";
    }
 
    @PostMapping("/{id}/actualizar")
    public String actualizar(@PathVariable String id,
                             @Valid @ModelAttribute("competicion") Competicion competicion,
                             BindingResult resultado,
                             Model model,
                             RedirectAttributes redirect) {
        competicion.setId(id);
        if (!resultado.hasErrors()) {
            try {
                Competicion actualizada = competicionServicio.actualizar(id, competicion);
                redirect.addFlashAttribute("exito", actualizada.getNombre() + " actualizada.");
                return "redirect:/competiciones";
            } catch (ReglaNegocioException e) {
                resultado.rejectValue(e.getCampo(), "negocio", e.getMessage());
            }
        }
        cargarListado(model);
        return "competiciones/lista";
    }
 
    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable String id, RedirectAttributes redirect) {
        competicionServicio.eliminar(id);
        redirect.addFlashAttribute("exito", "Competición eliminada y retirada de los clubes inscritos.");
        return "redirect:/competiciones";
    }
 
    private void cargarListado(Model model) {
        model.addAttribute("competiciones", competicionServicio.listar());
        model.addAttribute("clubesPorCompeticion", competicionServicio.clubesPorCompeticion());
        model.addAttribute("seccion", "competiciones");
    }
}
