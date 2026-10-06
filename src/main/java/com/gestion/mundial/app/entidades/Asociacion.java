package com.gestion.mundial.app.entidades;
 
import jakarta.validation.constraints.NotBlank;
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
 
    @NotBlank
    private String nombre;
 
    @NotBlank
    @Indexed(unique = true)
    private String siglas;
 
    @NotBlank
    private String pais;
 
    private String presidente;
}
