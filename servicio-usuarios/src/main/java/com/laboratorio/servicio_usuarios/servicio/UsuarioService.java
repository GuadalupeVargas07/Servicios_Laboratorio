package com.laboratorio.servicio_usuarios.servicio;
import com.laboratorio.servicio_usuarios.dto.RespuestaAutorizacion;
import com.laboratorio.servicio_usuarios.modelo.Usuario;
import com.laboratorio.servicio_usuarios.repositorio.UsuarioRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public List<Usuario> obtenerTodos() {
        return usuarioRepository.findAll();
    }

    public Optional<Usuario> obtenerPorId(Long id) {
        return usuarioRepository.findById(id);
    }

    public Optional<Usuario> obtenerPorMatricula(String matricula) {
        return usuarioRepository.findByMatricula(matricula);
    }

    public Usuario guardar(Usuario usuario) {
        if (usuario.getLimitePrestamos() == null) {
            usuario.setLimitePrestamos("DOCENTE".equalsIgnoreCase(usuario.getTipo()) ? 5 : 3);
        }
        return usuarioRepository.save(usuario);
    }

    public Optional<Usuario> actualizar(Long id, Usuario datosDetalle) {
        return usuarioRepository.findById(id).map(usuario -> {
            usuario.setNombre(datosDetalle.getNombre());
            usuario.setEmail(datosDetalle.getEmail());
            usuario.setTipo(datosDetalle.getTipo());
            if (datosDetalle.getEstado() != null) usuario.setEstado(datosDetalle.getEstado());
            if (datosDetalle.getSancionado() != null) usuario.setSancionado(datosDetalle.getSancionado());
            if (datosDetalle.getPrestamosActivos() != null) usuario.setPrestamosActivos(datosDetalle.getPrestamosActivos());
            if (datosDetalle.getLimitePrestamos() != null) usuario.setLimitePrestamos(datosDetalle.getLimitePrestamos());
            return usuarioRepository.save(usuario);
        });
    }

    public Optional<Usuario> cambiarEstado(Long id, String nuevoEstado) {
        return usuarioRepository.findById(id).map(usuario -> {
            usuario.setEstado(nuevoEstado);
            return usuarioRepository.save(usuario);
        });
    }

    public void eliminar(Long id) {
        usuarioRepository.deleteById(id);
    }

    public RespuestaAutorizacion evaluarAutorizacion(String matricula) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByMatricula(matricula);

        if (usuarioOpt.isEmpty()) {
            return new RespuestaAutorizacion(matricula, false, "El usuario no existe en el sistema.");
        }

        Usuario usuario = usuarioOpt.get();

        if (!"HABILITADO".equalsIgnoreCase(usuario.getEstado())) {
            return new RespuestaAutorizacion(matricula, false, "El usuario está DESHABILITADO.");
        }

        if (Boolean.TRUE.equals(usuario.getSancionado())) {
            return new RespuestaAutorizacion(matricula, false, "El usuario cuenta con sanciones o adeudos activos.");
        }

        if (usuario.getPrestamosActivos() >= usuario.getLimitePrestamos()) {
            return new RespuestaAutorizacion(matricula, false, "El usuario ha alcanzado el límite máximo de préstamos activos.");
        }

        return new RespuestaAutorizacion(matricula, true, "El usuario cumple con todos los requisitos para solicitar equipo.");
    }
}
