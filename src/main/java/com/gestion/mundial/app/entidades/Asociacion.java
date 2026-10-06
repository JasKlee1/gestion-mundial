package com.gestion.mundial.app.entidades;
 
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
 
/** Colección propia: muchos clubes apuntan a la misma asociación (N:1). */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "asociacion")
public class Asociacion {
 
    @Id
    private String id;
 
    @NotBlank(message = "Escribe el nombre de la asociación")
    @Size(max = 80, message = "Máximo 80 caracteres")
    private String nombre;
 
    @NotBlank(message = "Escribe las siglas")
    @Size(max = 10, message = "Máximo 10 caracteres")
    @Indexed(unique = true)
    private String siglas;
 
    @NotBlank(message = "Indica el país")
    @Size(max = 40, message = "Máximo 40 caracteres")
    private String pais;
 
    @Size(max = 60, message = "Máximo 60 caracteres")
    private String presidente;
}
