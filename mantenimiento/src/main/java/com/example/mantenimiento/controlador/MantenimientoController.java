/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.mantenimiento.controlador;


import  com.example.mantenimiento.modelo.EstadoEquipoRespuesta;
import com.example.mantenimiento.modelo.ReporteRespuesta;
import com.example.mantenimiento.modelo.SolicitudReporte;
import com.example.mantenimiento.servicio.MantenimientoService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/mantenimiento")
@CrossOrigin(origins={
    "http://localhost:5500",
    "http://127.0.0.1:5500"
})

public class MantenimientoController {
    private final MantenimientoService mantenimientoService;
    public MantenimientoController(MantenimientoService mantenimientoService){
        this.mantenimientoService = mantenimientoService;
    }
    
    //Aquí se registra el reporte de falla POST /api/mantenimiento/reportes
    @PostMapping("/reportes")
    @ResponseStatus(HttpStatus.CREATED)
    public ReporteRespuesta reportarFalla(@RequestBody SolicitudReporte solicitud){
        return mantenimientoService.reportarFalla(solicitud);
    }
    
    //Aquí se consulta el estado de bloqueo de un equipo /GET/api/mantenimiento/equipos/{equipoId}/estado
    @GetMapping("/equipos/{equipoId}/estado")
    public EstadoEquipoRespuesta verificarEstadoEquipo(@PathVariable("equipoId") int equipoId){
        return mantenimientoService.verificarEstadoEquipo(equipoId);
    }
    
    @GetMapping("/reportes")
    public java.util.List<ReporteRespuesta>listarReportes(){
        return mantenimientoService.listarReportes();
    }
    
    //Aquí se liberia un equipo finalizada su revisión/reparación PUT/api/mantenimiento/reportes/{reporteId}/liberación
    @PutMapping("/reportes/{reporteId}/liberacion")
    public ReporteRespuesta liberarEquipo(@PathVariable("reporteId") int reporteId){
            return mantenimientoService.liberarEquipo(reporteId);
                    }
    
    
}



