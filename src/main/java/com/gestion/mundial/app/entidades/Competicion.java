package com.gestion.mundial.app.entidades;
 
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
 
import java.time.LocalDate;
 
/** Colección propia: relación N:N con Club, referenciada solo desde Club. */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "competicion")
public class Competicion {
 
    @Id
    private String id;
 
    @NotBlank
    private String nombre;
 
    private Long montoPremio;
 
    private LocalDate fechaInicio;
 
    private LocalDate fechaFin;
}
