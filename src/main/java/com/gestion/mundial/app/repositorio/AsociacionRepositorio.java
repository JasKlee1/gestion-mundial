package com.gestion.mundial.app.repositorio;
 
import com.gestion.mundial.app.entidades.Asociacion;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
 
import java.util.List;
 
@Repository
public interface AsociacionRepositorio extends MongoRepository<Asociacion, String> {
 
    List<Asociacion> findAllByOrderByNombreAsc();
 
    boolean existsBySiglasIgnoreCase(String siglas);
 
    // Para editar: verifica que las siglas no las use OTRA asociación
    boolean existsBySiglasIgnoreCaseAndIdNot(String siglas, String id);
}
