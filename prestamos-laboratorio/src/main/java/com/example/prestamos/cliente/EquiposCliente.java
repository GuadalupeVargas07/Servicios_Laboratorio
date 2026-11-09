/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.prestamos.cliente;

import java.util.Map;
import java.net.http.HttpClient;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.server.ResponseStatusException;

/**
 *
 * @author jorge
 */

@Component
public class EquiposCliente {
    private final RestClient restClient;
    public EquiposCliente (@Value("${equipos.url}")String equiposUrl){
        HttpClient httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(3))
                .build();
        JdkClientHttpRequestFactory factory =
                new JdkClientHttpRequestFactory(httpClient);
        
        factory.setReadTimeout(Duration.ofSeconds(5));
        
        this.restClient = RestClient.builder()
                .baseUrl(equiposUrl)
                .requestFactory(factory)
                .build();
    }
    
    public EquipoRespuesta registrar(String nombre){
        if(nombre==null|| nombre.isBlank ()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                "El nombre del equipo es obligatroio");
    }
        try {
        EquipoRespuesta equipo = restClient.post()
                .uri("/api/equipos")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Map.of(
                        "nombre", nombre.trim(),
                        "estado", "DISPONIBLE",
                        "disponible", true))
                .retrieve()
                .body(EquipoRespuesta.class);

        if (equipo == null || equipo.getId() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "El servicio de equipos devolvió una respuesta incompleta");
        }

        return equipo;

    } catch (RestClientResponseException ex) {
        throw new ResponseStatusException(
                HttpStatus.BAD_GATEWAY,
                "El servicio de equipos rechazó el registro",
                ex);

    } catch (ResourceAccessException ex) {
        throw new ResponseStatusException(
                HttpStatus.SERVICE_UNAVAILABLE,
                "No fue posible comunicarse con el servicio de equipos",
                ex);

    } catch (RestClientException ex) {
        throw new ResponseStatusException(
                HttpStatus.BAD_GATEWAY,
                "No fue posible interpretar la respuesta del servicio de equipos",
                ex);
    }
        }
    public EquipoRespuesta obtenerPorNombre(String nombre) {
    if (nombre == null || nombre.isBlank()) {
        throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "El nombre del equipo es obligatorio");
    }

    try {
        EquipoRespuesta[] equipos = restClient.get()
                .uri("/api/equipos")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(EquipoRespuesta[].class);

        if (equipos == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "El servicio de equipos devolvió una respuesta vacía");
        }

        EquipoRespuesta encontrado = null;

        for (EquipoRespuesta equipo : equipos) {
            if (equipo != null && equipo.getNombre() != null
                    && nombre.trim().equalsIgnoreCase(equipo.getNombre().trim())) {

                if (encontrado != null) {
                    throw new ResponseStatusException(
                            HttpStatus.CONFLICT,
                            "Hay varios equipos con ese nombre");
                }

                encontrado = equipo;
            }
        }

        if (encontrado == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "No existe un equipo con ese nombre");
        }

        if (encontrado.getId() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "El equipo recibido no tiene identificador");
        }

        return encontrado;

    } catch (ResourceAccessException ex) {
        throw new ResponseStatusException(
                HttpStatus.SERVICE_UNAVAILABLE,
                "No fue posible comunicarse con el servicio de equipos",
                ex);

    } catch (RestClientException ex) {
        throw new ResponseStatusException(
                HttpStatus.BAD_GATEWAY,
                "No fue posible consultar el catálogo de equipos",
                ex);
    }
}

    public EquipoRespuesta obtenerPorId(Long id) {
    if (id == null || id <= 0) {
        throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "El identificador del equipo debe ser mayor a cero");
    }
    try {
    EquipoRespuesta equipo = restClient.get()
            .uri("/api/equipos/{id}", id)
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .body(EquipoRespuesta.class);
    if (equipo == null
            || !id.equals(equipo.getId())
            || equipo.getNombre() == null
            || equipo.getNombre().isBlank()
            || equipo.getDisponible() == null){
    throw new ResponseStatusException(
        HttpStatus.BAD_GATEWAY,
    "El servicio de equipos devolvió datos incompletos o inconsistentes");
}
    
    return equipo;
    } catch (RestClientResponseException ex){
    if (ex.getStatusCode().value() == 404){
        throw new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "No existe el identificador " + id,
                ex);
}
    
    throw new ResponseStatusException(
        HttpStatus.BAD_GATEWAY,
        "El servicio de equipos respondio con un error",
        ex);
    } catch(ResourceAccessException ex){
    throw new ResponseStatusException(
        HttpStatus.SERVICE_UNAVAILABLE,
        "No fue posible comunicarse con el servicio de equipos",
        ex);
    }catch (RestClientException ex){
    throw new ResponseStatusException(
        HttpStatus.BAD_GATEWAY,
        "No fue posible interpretar la respuesta del servicio de equipos",
        ex);
}
}
}
    