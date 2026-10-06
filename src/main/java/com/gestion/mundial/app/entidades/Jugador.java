package com.gestion.mundial.app.entidades;
 
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
 
/** Subdocumento embebido dentro del arreglo "jugadores" de Club. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Jugador {
 
    @NotBlank(message = "Nombre obligatorio")
    private String nombre;
 
    @NotBlank(message = "Apellido obligatorio")
    private String apellido;
 
    @NotNull(message = "Dorsal obligatorio")
    @Min(value = 1, message = "Dorsal entre 1 y 99")
    @Max(value = 99, message = "Dorsal entre 1 y 99")
    private Integer numero;
 
    @NotNull(message = "Elige la posición")
    private Posicion posicion;
}
