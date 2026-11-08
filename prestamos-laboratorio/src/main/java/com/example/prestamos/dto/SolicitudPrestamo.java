/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.prestamos.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;


/**
 *
 * @author jorge
 */
public class SolicitudPrestamo {
    @NotNull (message = "El identificador del equipo es obligatorio")
    @Positive (message = "El identificador del equipo debe ser mayor a 0")
    private Long equipoId;
@NotBlank(message = "La matricula es obligatoria")
private String matricula;

public Long getEquipoId(){
    return equipoId;
}
public void setEquipoId(Long equipoId){
    this.equipoId =equipoId;
}   
public String getMatricula() {
    return matricula;
}
public void setMatricula(String matricula){
    this.matricula = matricula;
}
}
