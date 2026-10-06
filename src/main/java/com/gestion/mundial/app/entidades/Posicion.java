package com.gestion.mundial.app.entidades;
 
/** Se guarda en MongoDB como texto: "PORTERO", "DEFENSA", ... */
public enum Posicion {
    PORTERO("Portero"),
    DEFENSA("Defensa"),
    MEDIOCAMPISTA("Mediocampista"),
    DELANTERO("Delantero");
 
    private final String etiqueta;
 
    Posicion(String etiqueta) { this.etiqueta = etiqueta; }
 
    public String getEtiqueta() { return etiqueta; }
}
