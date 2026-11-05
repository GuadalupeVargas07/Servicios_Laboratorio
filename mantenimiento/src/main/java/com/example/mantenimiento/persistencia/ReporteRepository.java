/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.example.mantenimiento.persistencia;

import com.example.mantenimiento.persistencia.ReporteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReporteRepository 
    extends JpaRepository<ReporteEntity, Integer>{
    List<ReporteEntity>findByEquipoIdAndEstadoIn(int equipoId, List<String> estados);
}

