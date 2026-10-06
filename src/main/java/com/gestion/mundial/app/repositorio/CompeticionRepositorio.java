package com.gestion.mundial.app.repositorio;
 
import com.gestion.mundial.app.entidades.Competicion;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
 
import java.util.List;
 
@Repository
public interface CompeticionRepositorio extends MongoRepository<Competicion, String> {
 
    List<Competicion> findAllByOrderByFechaInicioAsc();
}
