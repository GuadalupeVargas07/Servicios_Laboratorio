/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.laboratorio.servicio;
import com.example.laboratorio.modelo.Equipo;
import com.example.laboratorio.repositorio.EquipoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author Ducel
 */
@Service
public class EquipoService{
    @Autowired
    private EquipoRepository equipoRepository;

    public List<Equipo> obtenerCatalogoCompleto(){
        return equipoRepository.findAll();
    }

    public Optional<Equipo> obtenerPorId(Long id){
        return equipoRepository.findById(id);
    }

    public List<Equipo> obtenerPorDisponibilidad(boolean disponible){
        return equipoRepository.findByDisponible(disponible);
    }

    public List<Equipo> obtenerPorEstado(String estado){
        return equipoRepository.findByEstadoIgnoreCase(estado);
    }

    public Equipo guardarEquipo(Equipo equipo){
        return equipoRepository.save(equipo);
    }

    public void eliminarEquipo(Long id){
        equipoRepository.deleteById(id);
    }
    
}
