/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.prestamos.controlador;

import com.example.prestamos.modelo.Prestamo;
import com.example.prestamos.servicio.PrestamoService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.prestamos.dto.SolicitudPrestamo;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import java.util.Map;
/**
 *
 * @author jorge
 */


@RestController
@RequestMapping("/api/prestamos")
public class PrestamoController {
    private final PrestamoService prestamoService;
    public PrestamoController(PrestamoService prestamoService){
        this.prestamoService = prestamoService;
        }
    @GetMapping
    public List <Prestamo> listar(){
        return prestamoService.listar();
    }
    @GetMapping("/{id}")
    public Prestamo obtenerPorId(@PathVariable("id")Long id){
        return prestamoService.obtenerPorId(id);
    }
@PostMapping
@ResponseStatus(HttpStatus.CREATED)
public Prestamo registrar(@RequestBody SolicitudPrestamo solicitud) {
    return prestamoService.registrar(solicitud);
}
@PostMapping ("/{id}/cancelar")
public Prestamo cancelar(@PathVariable("id") Long id){
    return prestamoService.cancelar(id);
}
@PostMapping("/reservas")
@ResponseStatus(HttpStatus.CREATED)
public Prestamo reservar(@RequestBody SolicitudPrestamo solicitud){
    return prestamoService.reservar(solicitud);
}
@PostMapping("/{id}/confirmar")
public Prestamo confirmarReserva(@PathVariable("id") Long id){
    return prestamoService.confirmarReserva(id);
}
@PostMapping("/reservas/por-nombre")
@ResponseStatus(HttpStatus.CREATED)
public Prestamo reservarPorNombre(
        @RequestBody Map<String, String> solicitud) {

    String nombre = solicitud == null
            ? null
            : solicitud.get("nombre");

    return prestamoService.reservarPorNombre(nombre);   
}
}
