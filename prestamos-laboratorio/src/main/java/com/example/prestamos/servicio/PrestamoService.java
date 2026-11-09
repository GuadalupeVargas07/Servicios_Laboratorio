/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.prestamos.servicio;

import com.example.prestamos.modelo.Prestamo;
import com.example.prestamos.repositorio.PrestamoRepository;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import com.example.prestamos.cliente.EquipoRespuesta;
import com.example.prestamos.cliente.EquiposCliente;
import com.example.prestamos.dto.SolicitudPrestamo;
import java.time.LocalDate;



/**
 *
 * @author jorge
 */

@Service
public class PrestamoService {
    private final PrestamoRepository prestamoRepository;
    private final EquiposCliente equiposCliente;
    
    public PrestamoService(PrestamoRepository prestamoRepository, EquiposCliente equiposCliente){
        this.prestamoRepository = prestamoRepository;
        this.equiposCliente = equiposCliente;
    }
    public List<Prestamo> listar() {
        return prestamoRepository.findAll();
    }
    public Prestamo obtenerPorId(Long id){
        if (id==null || id <= 0 ){
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
            "El identificador del prestamo debe ser mayor a 0");
        }
        return prestamoRepository.findById(id)
                .orElseThrow(()-> new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "No existe el prestamo con identificador "+ id));
    }
    

public synchronized Prestamo registrar (SolicitudPrestamo solicitud){
    return crear (solicitud, false);
}
public synchronized Prestamo reservar(SolicitudPrestamo solicitud){
    return crear (solicitud,true);
}

private Prestamo crear(SolicitudPrestamo solicitud, boolean esReserva){
    if(solicitud == null
            ||solicitud.getEquipoId() == null
            ||solicitud.getEquipoId() <=0){
    throw new ResponseStatusException(
            HttpStatus.BAD_REQUEST,
            "El identificador del equipo debe ser mayor  a 0");
            }
    if  (solicitud.getMatricula()==null
            || solicitud.getMatricula().isBlank()){
        throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "La matricula es obligatoria");
    }
    
 Long equipoId = solicitud.getEquipoId();

    if (prestamoRepository.existsByEquipoIdAndEstado(equipoId, "ACTIVO")
            || prestamoRepository.existsByEquipoIdAndEstado(
                    equipoId, "RESERVADO")) {
        throw new ResponseStatusException(
                HttpStatus.CONFLICT,
                "El equipo ya tiene un prestamo activo o una reserva");
    }

    EquipoRespuesta equipo = equiposCliente.obtenerPorId(equipoId);

    if (!Boolean.TRUE.equals(equipo.getDisponible())) {
        throw new ResponseStatusException(
                HttpStatus.CONFLICT,
                "El equipo no esta disponible");
    }

    Prestamo prestamo = new Prestamo(
            equipo.getId(),
            equipo.getNombre(),
            solicitud.getMatricula().trim(),
            LocalDate.now());

    if (esReserva) {
        prestamo.reservar();
    }

    return prestamoRepository.save(prestamo);
}

public synchronized Prestamo confirmarReserva(Long id) {
    Prestamo prestamo = obtenerPorId(id);

    if (!"RESERVADO".equals(prestamo.getEstado())) {
        throw new ResponseStatusException(
                HttpStatus.CONFLICT,
                "Solo se puede confirmar una reserva pendiente");
    }

    prestamo.confirmar();
    return prestamoRepository.save(prestamo);
}
public synchronized Prestamo reservarPorNombre(String nombre) {
    EquipoRespuesta equipo = equiposCliente.obtenerPorNombre(nombre);

    if (prestamoRepository.existsByEquipoIdAndEstado(equipo.getId(), "ACTIVO")
            || prestamoRepository.existsByEquipoIdAndEstado(
                    equipo.getId(), "RESERVADO")) {

        throw new ResponseStatusException(
                HttpStatus.CONFLICT,
                "El equipo ya tiene un préstamo activo o una reserva");
    }

    if (!Boolean.TRUE.equals(equipo.getDisponible())) {
        throw new ResponseStatusException(
                HttpStatus.CONFLICT,
                "El equipo no está disponible");
    }

    Prestamo prestamo = new Prestamo(
            equipo.getId(),
            equipo.getNombre(),
            "",
            LocalDate.now());

    prestamo.reservar();
    return prestamoRepository.save(prestamo);
}
public synchronized Prestamo cancelar(Long id) {
    Prestamo prestamo = obtenerPorId(id);

    if ("CANCELADO".equals(prestamo.getEstado())) {
        throw new ResponseStatusException(
                HttpStatus.CONFLICT,
                "El prestamo ya esta cancelado");
    }

    prestamo.cancelar(LocalDate.now());
    return prestamoRepository.save(prestamo);
}

}
 


