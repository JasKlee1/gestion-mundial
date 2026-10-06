package com.gestion.mundial.app.controladores;
 
import com.gestion.mundial.app.entidades.Asociacion;
import com.gestion.mundial.app.servicios.AsociacionServicio;
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
 
/** Vistas web de asociaciones: listado, alta, edición y eliminación en una sola página. */
@Controller
@RequestMapping("/asociaciones")
@RequiredArgsConstructor
public class AsociacionControlador {
 
    private final AsociacionServicio asociacionServicio;
 
    @InitBinder("asociacion")
    void configurarBinder(WebDataBinder binder) {
        binder.setDisallowedFields("id");
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
    }
 
    @GetMapping
    public String listar(Model model) {
        model.addAttribute("asociacion", new Asociacion());
        cargarListado(model);
        return "asociaciones/lista";
    }
 
    @GetMapping("/{id}/editar")
    public String editar(@PathVariable String id, Model model) {
        model.addAttribute("asociacion", asociacionServicio.buscarPorId(id));
        cargarListado(model);
        return "asociaciones/lista";
    }
 
    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute("asociacion") Asociacion asociacion,
                          BindingResult resultado,
                          Model model,
                          RedirectAttributes redirect) {
        if (!resultado.hasErrors()) {
            try {
                Asociacion guardada = asociacionServicio.crear(asociacion);
                redirect.addFlashAttribute("exito", "Asociación " + guardada.getSiglas() + " registrada.");
                return "redirect:/asociaciones";
            } catch (ReglaNegocioException e) {
                registrarError(resultado, e);
            }
        }
        cargarListado(model);
        return "asociaciones/lista";
    }
 
    @PostMapping("/{id}/actualizar")
    public String actualizar(@PathVariable String id,
                             @Valid @ModelAttribute("asociacion") Asociacion asociacion,
                             BindingResult resultado,
                             Model model,
                             RedirectAttributes redirect) {
        asociacion.setId(id);
        if (!resultado.hasErrors()) {
            try {
                Asociacion actualizada = asociacionServicio.actualizar(id, asociacion);
                redirect.addFlashAttribute("exito", "Asociación " + actualizada.getSiglas() + " actualizada.");
                return "redirect:/asociaciones";
            } catch (ReglaNegocioException e) {
                registrarError(resultado, e);
            }
        }
        cargarListado(model);
        return "asociaciones/lista";
    }
 
    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable String id, RedirectAttributes redirect) {
        try {
            asociacionServicio.eliminar(id);
            redirect.addFlashAttribute("exito", "Asociación eliminada.");
        } catch (ReglaNegocioException e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/asociaciones";
    }
 
    private void cargarListado(Model model) {
        model.addAttribute("asociaciones", asociacionServicio.listar());
        model.addAttribute("clubesPorAsociacion", asociacionServicio.clubesPorAsociacion());
        model.addAttribute("seccion", "asociaciones");
    }
 
    private void registrarError(BindingResult resultado, ReglaNegocioException e) {
        if (e.getCampo() == null) {
            resultado.reject("negocio", e.getMessage());
        } else {
            resultado.rejectValue(e.getCampo(), "negocio", e.getMessage());
        }
    }
}
