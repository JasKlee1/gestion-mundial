package com.gestion.mundial.app.entidades;
 
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
 
/** Subdocumento embebido en Club (sin @Document ni @Id). */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Entrenador {
 
    @NotBlank(message = "Escribe el nombre del entrenador")
    private String nombre;
 
    @NotBlank(message = "Escribe el apellido del entrenador")
    private String apellido;
 
    @NotNull(message = "Indica la edad")
    @Min(value = 18, message = "La edad mínima es 18")
    @Max(value = 90, message = "La edad máxima es 90")
    private Integer edad;
 
    @NotBlank(message = "Indica la nacionalidad")
    private String nacionalidad;
 
    @JsonIgnore
    public String getNombreCompleto() {
        return (nombre == null ? "" : nombre) + " " + (apellido == null ? "" : apellido);
    }
}
