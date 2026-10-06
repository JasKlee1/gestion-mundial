package com.gestion.mundial.app.entidades;
 
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;
 
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
 
/**
 * Documento raíz (agregado). Equivale a la tabla "clubes" de la versión JPA.
 *
 *  Relación JPA            ->  Estrategia MongoDB
 *  @OneToOne  Entrenador   ->  EMBEBIDO   (no existe sin su club)
 *  @OneToMany Jugadores    ->  EMBEBIDO   (lista acotada, máx. 26, siempre se lee con el club)
 *  @ManyToOne Asociacion   ->  REFERENCIA (la comparten muchos clubes)
 *  @ManyToMany Competicion ->  REFERENCIA (entidad independiente, unidireccional desde Club)
 */
@Data
@NoArgsConstructor
@Document(collection = "club")
public class Club {
 
    @Id
    private String id;
 
    @NotBlank(message = "Escribe el nombre del club")
    @Size(max = 60, message = "El nombre admite máximo 60 caracteres")
    @Indexed(unique = true)
    private String nombre;
 
    @NotBlank(message = "Escribe el país del club")
    private String pais;
 
    // 1:1 embebido -> se guarda como subdocumento { entrenador: {...} }
    @Valid
    @NotNull(message = "El club necesita un entrenador")
    private Entrenador entrenador = new Entrenador();
 
    // 1:N embebido -> arreglo interno { jugadores: [ {...}, {...} ] }
    @Valid
    @Size(max = 26, message = "Una plantilla mundialista admite máximo 26 jugadores")
    private List<Jugador> jugadores = new ArrayList<>();
 
    // N:1 por referencia -> guarda solo el ObjectId de la asociación
    @DocumentReference
    private Asociacion asociacion;
 
    // N:N por referencia -> guarda un arreglo de ObjectId de competiciones
    @DocumentReference
    private List<Competicion> competiciones = new ArrayList<>();
 
    /** Iniciales para el escudo de la tarjeta. No se persiste (no hay campo). */
    @JsonIgnore
    public String getIniciales() {
        if (nombre == null || nombre.isBlank()) return "?";
        return Arrays.stream(nombre.trim().split("\\s+"))
                .limit(2)
                .map(p -> p.substring(0, 1).toUpperCase())
                .collect(Collectors.joining());
    }
 
    /** Tono de color (0-359) derivado del nombre: cada club tiene su color fijo. */
    @JsonIgnore
    public int getHue() {
        return nombre == null ? 140 : Math.floorMod(nombre.hashCode(), 360);
    }
}