package com.gestion.mundial.app.entidades;
 
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;
 
import java.time.LocalDate;
 
/** Colección propia: relación N:N con Club, referenciada solo desde Club. */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "competicion")
public class Competicion {
 
    @Id
    private String id;
 
    @NotBlank(message = "Escribe el nombre de la competición")
    @Size(max = 60, message = "Máximo 60 caracteres")
    private String nombre;
 
    @NotNull(message = "Indica el monto del premio")
    @PositiveOrZero(message = "El premio no puede ser negativo")
    private Long montoPremio;
 
    @NotNull(message = "Indica la fecha de inicio")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fechaInicio;
 
    @NotNull(message = "Indica la fecha de fin")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fechaFin;
}
