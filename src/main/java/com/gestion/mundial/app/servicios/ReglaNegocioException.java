package com.gestion.mundial.app.servicios;
 
import lombok.Getter;
 
/** Error de negocio asociado a un campo del formulario (para mostrarlo junto al input). */
@Getter
public class ReglaNegocioException extends RuntimeException {
 
    private final String campo;
 
    public ReglaNegocioException(String campo, String mensaje) {
        super(mensaje);
        this.campo = campo;
    }
}
