package com.laboratorio.servicio_usuarios.modelo;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;

@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String matricula;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "tipo_usuario", nullable = false, length = 20)
    private String tipo;

    @Column(nullable = false, length = 20)
    private String estado = "HABILITADO";

    @Column(nullable = false)
    private Boolean sancionado = false;

    @Column(name = "prestamos_activos", nullable = false)
    private Integer prestamosActivos = 0;

    @Column(name = "limite_prestamos", nullable = false)
    private Integer limitePrestamos = 3;

    public Usuario() {}

    public Usuario(Long id, String matricula, String nombre, String email, String tipo, String estado, Boolean sancionado, Integer prestamosActivos, Integer limitePrestamos) {
        this.id = id;
        this.matricula = matricula;
        this.nombre = nombre;
        this.email = email;
        this.tipo = tipo;
        this.estado = estado;
        this.sancionado = sancionado;
        this.prestamosActivos = prestamosActivos;
        this.limitePrestamos = limitePrestamos;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Boolean getSancionado() {
        return sancionado;
    }

    public void setSancionado(Boolean sancionado) {
        this.sancionado = sancionado;
    }

    public Integer getPrestamosActivos() {
        return prestamosActivos;
    }

    public void setPrestamosActivos(Integer prestamosActivos) {
        this.prestamosActivos = prestamosActivos;
    }

    public Integer getLimitePrestamos() {
        return limitePrestamos;
    }

    public void setLimitePrestamos(Integer limitePrestamos) {
        this.limitePrestamos = limitePrestamos;
    }
}


