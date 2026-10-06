package com.gestion.mundial.app.repositorio;
 
import com.gestion.mundial.app.entidades.Club;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
 
import java.util.List;
 
@Repository
public interface ClubRepositorio extends MongoRepository<Club, String> {
 
    List<Club> findAllByOrderByNombreAsc();
 
    // Búsqueda parcial sin distinguir mayúsculas: { nombre: /texto/i }
    List<Club> findByNombreContainingIgnoreCaseOrderByNombreAsc(String nombre);
 
    boolean existsByNombreIgnoreCase(String nombre);
 
    // Para editar: verifica que el nombre no lo use OTRO club
    boolean existsByNombreIgnoreCaseAndIdNot(String nombre, String id);
}
