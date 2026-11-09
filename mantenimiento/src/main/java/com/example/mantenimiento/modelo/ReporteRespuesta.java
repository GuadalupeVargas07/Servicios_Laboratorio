/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.mantenimiento.modelo;

import java.time.OffsetDateTime;

public record ReporteRespuesta(
        int id,
        int equipoId,
        String descripcionFalla,
        String estado,
        OffsetDateTime fechaReporte){}
