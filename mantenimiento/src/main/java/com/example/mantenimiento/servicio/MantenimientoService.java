/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.mantenimiento.servicio;

import com.example.mantenimiento.modelo.EstadoEquipoRespuesta;
import com.example.mantenimiento.modelo.SolicitudReporte;
import com.example.mantenimiento.modelo.ReporteRespuesta;
import com.example.mantenimiento.persistencia.ReporteEntity;
import com.example.mantenimiento.persistencia.ReporteRepository;


import java.util.List;
import java.time.OffsetDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class MantenimientoService {
    private final ReporteRepository reporteRepository;
    
    public MantenimientoService(ReporteRepository reporteRepository){
        this.reporteRepository = reporteRepository;   
    }
    // Aqui se registra una nueva falla en el equipo
    @Transactional
    public ReporteRespuesta reportarFalla(SolicitudReporte solicitud){
        if (solicitud == null || solicitud.equipoId() == null || solicitud.equipoId()<=0){
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El identificador del equipo debe ser mayor a 0"
            );
        }
        if (solicitud.descripcionFalla() == null || solicitud.descripcionFalla().isBlank()){
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La descripción de la falla debe de ser obligatoria y no dejar vacío"
            );
            
        }
        // Aquí te avisa cuando te pasas de explicar xd
        if(solicitud.descripcionFalla().trim().length()>500){
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La descripción no puede ser mas de 500 caracteres"
            );
        }
        List<String>estadosBloqueantes=List.of("PENDIENTE", "EN_REVISION","BLOQUEADO");
        if(!reporteRepository.findByEquipoIdAndEstadoIn(solicitud.equipoId(),estadosBloqueantes).isEmpty()){
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Este equipo ya tiene un reporte de mantenimiento activo. Te dormiste!"
            );
        }
        
        
        ReporteEntity entidad= new ReporteEntity(
                solicitud.equipoId(),
                solicitud.descripcionFalla().trim(),
                "PENDIENTE",
                OffsetDateTime.now()
        );
        
        ReporteEntity guardado=reporteRepository.save(entidad);
        return convertir(guardado);
    }
    
    //Aqui consulta si un equipo se encuentra bloqueado por mantenimiento
    @Transactional(readOnly= true)
    //Aquí esta el metodo para listar TODOS los reportes
    public List<ReporteRespuesta>listarReportes(){
        return reporteRepository.findAll().stream()
                .map(this::convertir).toList();
    }
    public EstadoEquipoRespuesta verificarEstadoEquipo(int equipoId){
        if (equipoId <=0){
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El identificador de este equipo debe ser mayor a 0"
            );
        }
        
        List<String> estadosBloqueantes = List.of("PENDIENTE", "EN_REVISION", "BLOQUEADO");
        List<ReporteEntity> reportesActivos = reporteRepository
                .findByEquipoIdAndEstadoIn(equipoId, estadosBloqueantes);
        
        boolean bloqueado = !reportesActivos.isEmpty();
        String motivo=bloqueado ? reportesActivos.get(0).getDescripcionFalla():null;
        
        return new EstadoEquipoRespuesta(equipoId, bloqueado, motivo);
    }
    // Liberamos un equipo cuando se acabe su revision o reparación
    @Transactional public ReporteRespuesta liberarEquipo(int reporteId){
        if(reporteId <=0){
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El identificador del reporte debe ser mayor a 0"
            );
        }
        ReporteEntity reporte=reporteRepository.findById(reporteId)
                .orElseThrow(()-> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Reporte de mantenimiento no encontrado"
                ));
        if ("LIBERADO".equals(reporte.getEstado())){
            return convertir(reporte);
        }
        reporte.setEstado("LIBERADO");
        ReporteEntity actualizado=reporteRepository.save(reporte);
        return convertir(actualizado);
    }
    private ReporteRespuesta convertir(ReporteEntity entidad){
        return new ReporteRespuesta(
                entidad.getId(),
                entidad.getEquipoId(),
                entidad.getDescripcionFalla(),
                entidad.getEstado(),
                entidad.getFechaReporte()
        );
    }
    


    
}
