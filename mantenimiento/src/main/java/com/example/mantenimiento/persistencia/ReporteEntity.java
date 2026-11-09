/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.mantenimiento.persistencia;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
/**
 *
 * @author ibarr
 */
@Entity
@Table(name="reportes_mantenimiento",schema="public")
public class ReporteEntity{
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name="equipo_id", nullable=false)
    private Integer equipoId;
    
    @Column(name="descripcion_falla", nullable=false, length=500)
    private  String descripcionFalla;
    
    @Column(nullable=false, length=20)
    private String estado;
    
    @Column(name="fecha_reporte", nullable=false)
    private  OffsetDateTime fechaReporte;
    
    
    public ReporteEntity(){
        
    }
   
    public ReporteEntity(Integer equipoId, String descripcionFalla, String estado, OffsetDateTime fechaReporte){
        this.equipoId = equipoId;
        this.descripcionFalla = descripcionFalla;
        this.estado = estado;
        this.fechaReporte = fechaReporte;
    }
    
    
    public Integer getId(){
        return id;
    }
    
    public Integer getEquipoId(){
        return equipoId;
    }
    
    public String getDescripcionFalla(){
        return descripcionFalla;
    }
    
    public String getEstado(){
        return estado;
    }
    
    public OffsetDateTime getFechaReporte(){
        return fechaReporte;
    }
    
    public void setEstado(String estado){
        this.estado = estado;
    }

    
}
