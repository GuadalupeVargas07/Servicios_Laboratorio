/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.prestamos.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
        
@Entity
@Table(name = "prestamos")
public class Prestamo{
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;

@Column(nullable = false)
private Long equipoId;

@Column(nullable = false)
private String nombreEquipo;

@Column(nullable =  false)
private String matricula;

@Column(nullable = false)
private LocalDate fechaPrestamo;

private LocalDate fechaCancelacion;

@Column(nullable = false)
private String estado;

protected Prestamo(){
}

public Prestamo (Long equipoId, String nombreEquipo, String matricula, LocalDate fechaPrestamo) {
    this.equipoId = equipoId;
    this.nombreEquipo = nombreEquipo;
    this.matricula = matricula;
    this.fechaPrestamo = fechaPrestamo;
    this.estado = "ACTIVO";
} 
public void cancelar (LocalDate fecha){
    if ("CANCELADO".equals(this.estado)){
    throw new IllegalStateException(
        "El prestamo ya esta cancelado");
    }
    this.estado = "CANCELADO";
        this.fechaCancelacion = fecha;
}

    public Long getId(){
    return id;
            }
    public Long getEquipoId(){
        return equipoId;
    } 
    public String getNombreEquipo(){
        return nombreEquipo;          
    }
    public String getMatricula(){
        return matricula;
    }
    public LocalDate getFechaPrestamo (){
        return fechaPrestamo;
    }
    public LocalDate getFechaCancelacion(){
        return fechaCancelacion;
    }
    public String getEstado(){
        return estado;
    }
public void reservar(){
    this.estado ="RESERVADO";
}    
public void confirmar(){
    this.estado="ACTIVO";
}        
}




/**
 *
 * @author JOK
 */
    

