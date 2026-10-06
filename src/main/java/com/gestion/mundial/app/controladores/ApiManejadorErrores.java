package com.gestion.mundial.app.controladores;
 
import com.gestion.mundial.app.servicios.ReglaNegocioException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;
 
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
 
/**
 * Respuestas de error uniformes para la API REST (solo afecta a los @RestController).
 * Formato:
 * { "fecha": "...", "estado": 400, "mensaje": "...", "campos": { "nombre": "..." } }
 */
@RestControllerAdvice(annotations = RestController.class)
public class ApiManejadorErrores {
 
    /** Regla de negocio incumplida: nombre repetido, asociación con clubes, fechas inválidas... */
    @ExceptionHandler(ReglaNegocioException.class)
    public ResponseEntity<Map<String, Object>> reglaNegocio(ReglaNegocioException e) {
        Map<String, String> campos = new LinkedHashMap<>();
        if (e.getCampo() != null) {
            campos.put(e.getCampo(), e.getMessage());
        }
        return respuesta(HttpStatus.BAD_REQUEST, e.getMessage(), campos);
    }
 
    /** Errores de @Valid: campos vacíos, fuera de rango, etc. */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> validacion(MethodArgumentNotValidException e) {
        Map<String, String> campos = new LinkedHashMap<>();
        for (FieldError error : e.getBindingResult().getFieldErrors()) {
            campos.putIfAbsent(error.getField(), error.getDefaultMessage());
        }
        return respuesta(HttpStatus.BAD_REQUEST, "Hay datos inválidos en la petición", campos);
    }
 
    /** Recurso no encontrado (404) u otros estados lanzados desde los servicios. */
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> estado(ResponseStatusException e) {
        return respuesta(e.getStatusCode(), e.getReason(), null);
    }
 
    /** JSON mal escrito o con un valor imposible de convertir (por ejemplo, una posición que no existe). */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> jsonInvalido(HttpMessageNotReadableException e) {
        return respuesta(HttpStatus.BAD_REQUEST,
                "El cuerpo JSON no es válido. Revisa la sintaxis y los valores enviados.", null);
    }
 
    private ResponseEntity<Map<String, Object>> respuesta(HttpStatusCode estado, String mensaje,
                                                         Map<String, String> campos) {
        Map<String, Object> cuerpo = new LinkedHashMap<>();
        cuerpo.put("fecha", LocalDateTime.now().toString());
        cuerpo.put("estado", estado.value());
        cuerpo.put("mensaje", mensaje);
        if (campos != null && !campos.isEmpty()) {
            cuerpo.put("campos", campos);
        }
        return ResponseEntity.status(estado).body(cuerpo);
    }
}
