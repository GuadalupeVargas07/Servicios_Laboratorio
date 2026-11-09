/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.laboratorio.repositorio;
import com.example.laboratorio.modelo.Equipo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 *
 * @author Ducel
 */
@Repository
public interface EquipoRepository extends JpaRepository<Equipo, Long>{
    List<Equipo> findByDisponible(boolean disponible);
    List<Equipo> findByEstadoIgnoreCase(String estado);
}

