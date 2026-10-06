package com.gestion.mundial.app.config;
 
import com.gestion.mundial.app.entidades.Asociacion;
import com.gestion.mundial.app.entidades.Competicion;
import com.gestion.mundial.app.repositorio.AsociacionRepositorio;
import com.gestion.mundial.app.repositorio.CompeticionRepositorio;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
 
import java.time.LocalDate;
import java.util.List;
 
/** Carga catálogos de ejemplo la primera vez (solo si las colecciones están vacías). */
@Slf4j
@Component
@RequiredArgsConstructor
public class DatosIniciales implements CommandLineRunner {
 
    private final AsociacionRepositorio asociacionRepositorio;
    private final CompeticionRepositorio competicionRepositorio;
 
    @Override
    public void run(String... args) {
        if (asociacionRepositorio.count() == 0) {
            asociacionRepositorio.saveAll(List.of(
                    new Asociacion(null, "Federación Colombiana de Fútbol", "FCF", "Colombia", null),
                    new Asociacion(null, "Asociación del Fútbol Argentino", "AFA", "Argentina", null),
                    new Asociacion(null, "Confederación Brasileña de Fútbol", "CBF", "Brasil", null),
                    new Asociacion(null, "Federación Mexicana de Fútbol", "FMF", "México", null),
                    new Asociacion(null, "Real Federación Española de Fútbol", "RFEF", "España", null)
            ));
            log.info("Asociaciones de ejemplo cargadas");
        }
        if (competicionRepositorio.count() == 0) {
            // Montos y fechas de ejemplo (ajústalos a tus datos reales)
            competicionRepositorio.saveAll(List.of(
                    new Competicion(null, "Copa Mundial 2026", 50_000_000L,
                            LocalDate.of(2026, 6, 11), LocalDate.of(2026, 7, 19)),
                    new Competicion(null, "Copa Libertadores", 20_000_000L,
                            LocalDate.of(2026, 2, 3), LocalDate.of(2026, 11, 28)),
                    new Competicion(null, "Copa Sudamericana", 6_000_000L,
                            LocalDate.of(2026, 3, 3), LocalDate.of(2026, 11, 21)),
                    new Competicion(null, "Liga nacional", 1_000_000L,
                            LocalDate.of(2026, 1, 20), LocalDate.of(2026, 12, 15))
            ));
            log.info("Competiciones de ejemplo cargadas");
        }
    }
}
