/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.laboratorio.controlador;
import com.example.laboratorio.modelo.Equipo;
import com.example.laboratorio.servicio.EquipoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 *
 * @author Ducel
 */
@RestController
@RequestMapping("/api/equipos")
@CrossOrigin(origins = "*")
public class EquipoController{
    @Autowired
    private EquipoService equipoService;

    //Catálogo
    @GetMapping
    public List<Equipo> listarCatalogo(){
        return equipoService.obtenerCatalogoCompleto();
    }

    //Buscar ID
    @GetMapping("/{id}")
    public ResponseEntity<Equipo> obtenerPorId(@PathVariable Long id){
        return equipoService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    //Equipos disponibles
    @GetMapping("/disponibles")
    public List<Equipo> listarDisponibles(){
        return equipoService.obtenerPorDisponibilidad(true);
    }

    //Filtrar por estado 
    @GetMapping("/estado/{estado}")
    public List<Equipo> listarPorEstado(@PathVariable String estado){
        return equipoService.obtenerPorEstado(estado);
    }

    // Registrar nuevo equipo
    @PostMapping
    public Equipo crearEquipo(@RequestBody Equipo equipo){
        return equipoService.guardarEquipo(equipo);
    }

    //Actualizar equipo/estado
    @PutMapping("/{id}")
    public ResponseEntity<Equipo> actualizarEquipo(@PathVariable Long id, @RequestBody Equipo equipoDetalles){
        return equipoService.obtenerPorId(id).map(equipo ->{
            equipo.setNombre(equipoDetalles.getNombre());
            equipo.setMarca(equipoDetalles.getMarca());
            equipo.setModelo(equipoDetalles.getModelo());
            equipo.setNumeroSerie(equipoDetalles.getNumeroSerie());
            equipo.setEstado(equipoDetalles.getEstado());
            equipo.setDisponible(equipoDetalles.isDisponible());
            Equipo actualizado = equipoService.guardarEquipo(equipo);
            return ResponseEntity.ok(actualizado);
        }).orElse(ResponseEntity.notFound().build());
    }    
}
