/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.prestamos.controlador;

import com.example.prestamos.cliente.EquipoRespuesta;
import com.example.prestamos.cliente.EquiposCliente;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 *
 * @author jorge
 */

@RestController
@RequestMapping("/api/prestamos/equipos")
public class ConsultaEquiposController {
private final EquiposCliente equiposCliente;

public ConsultaEquiposController(EquiposCliente equiposCliente){
    this.equiposCliente = equiposCliente;
    }

@GetMapping("/{id}")
public EquipoRespuesta obtenerPorId(@PathVariable("id") Long id){
    return equiposCliente.obtenerPorId(id);
}
@PostMapping
@ResponseStatus(HttpStatus.CREATED)
public EquipoRespuesta registrar(
        @RequestBody Map<String, String> solicitud) {

    String nombre = solicitud == null
            ? null
            : solicitud.get("nombre");

    return equiposCliente.registrar(nombre);
}

}
