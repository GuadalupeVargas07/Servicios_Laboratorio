/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.example.prestamos.repositorio;

import com.example.prestamos.modelo.Prestamo;
import org.springframework.data.jpa.repository.JpaRepository;
/**
 *
 * @author jorge
 */
public interface PrestamoRepository extends JpaRepository<Prestamo, Long>{
    boolean existsByEquipoIdAndEstado(Long equipoId, String estado);
    
}
