/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.laboratorio.servicio;
import com.example.laboratorio.modelo.Equipo;
import com.example.laboratorio.repositorio.EquipoRepository;
import java.net.http.HttpClient;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author Ducel
 */
@Service
public class EquipoService {

    public record EstadoMantenimientoDTO(
            int equipoId,
            boolean bloqueado,
            String motivo) {

    }
    private final EquipoRepository equipoRepository;
    private final RestClient mantenimientoClient;
    
    public EquipoService(
            EquipoRepository equipoRepository,
            @Value("${mantenimiento.url:http://localhost:8088}") String mantenimientoUrl) {
        this.equipoRepository = equipoRepository;
        
        HttpClient httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(3))
                .build();
        JdkClientHttpRequestFactory factory = new JdkClientHttpRequestFactory(httpClient);
        factory.setReadTimeout(Duration.ofSeconds(5));
        
        this.mantenimientoClient = RestClient.builder()
                .baseUrl(mantenimientoUrl)
                .requestFactory(factory)
                .build();
    }

    public List<Equipo> obtenerCatalogoCompleto() {
        List<Equipo> equipos = equipoRepository.findAll();
        equipos.forEach(this::aplicarEstadoMantenimiento);
        return equipos;
    }

    public Optional<Equipo> obtenerPorId(Long id) {
        Optional<Equipo> equipo = equipoRepository.findById(id);
        equipo.ifPresent(this::aplicarEstadoMantenimiento);
        return equipo;
    }
    
    public List<Equipo> obtenerPorDisponibilidad(boolean disponible){
        return obtenerCatalogoCompleto().stream()
                .filter(equipo-> equipo.isDisponible()==disponible)
                .toList();
    }

    public List<Equipo> obtenerPorEstado(String estado) {
        return obtenerCatalogoCompleto().stream().filter(equipo -> equipo.getEstado() != null
                && equipo.getEstado().equalsIgnoreCase(estado))
                .toList();
    }

    public Equipo guardarEquipo(Equipo equipo) {
        return equipoRepository.save(equipo);
    }

    public void eliminarEquipo(Long id) {
        equipoRepository.deleteById(id);
    }

    private void aplicarEstadoMantenimiento(Equipo equipo) {
        if (equipo == null || equipo.getId() == null) {
            return;
        }
        try {
            EstadoMantenimientoDTO estadoMant = mantenimientoClient.get()
                    .uri("/api/mantenimiento/equipos/{id}/estado", equipo.getId())
                            .retrieve()
                            .body(EstadoMantenimientoDTO.class);
            if (estadoMant != null && estadoMant.bloqueado()) {
                equipo.setDisponible(false);
                equipo.setEstado("MANTENIMIENTO");
            }
          
        }catch(RestClientException e){
            
        }
    }
}


