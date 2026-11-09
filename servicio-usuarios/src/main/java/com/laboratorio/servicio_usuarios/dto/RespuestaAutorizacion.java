package com.laboratorio.servicio_usuarios.dto;

public class RespuestaAutorizacion {
    private String matricula;
    private boolean autorizado;
    private String mensaje;

    // Constructor sin argumentos
    public RespuestaAutorizacion() {}

    // Constructor con 3 argumentos que está buscando UsuarioService
    public RespuestaAutorizacion(String matricula, boolean autorizado, String mensaje) {
        this.matricula = matricula;
        this.autorizado = autorizado;
        this.mensaje = mensaje;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public boolean isAutorizado() {
        return autorizado;
    }

    public void setAutorizado(boolean autorizado) {
        this.autorizado = autorizado;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
}
