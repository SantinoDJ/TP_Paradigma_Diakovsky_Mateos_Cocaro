package servicio;

import excepciones.DatoInvalidoException;
import excepciones.PacienteNoEncontradoException;
import modelo.Paciente;
import repositorio.IRepositorio;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ServicioPaciente {
    private IRepositorio<Paciente> repositorio;

    public ServicioPaciente(IRepositorio<Paciente> repositorio) {
        this.repositorio = repositorio;
    }

    public void registrarPaciente(Paciente p) throws DatoInvalidoException {
        if (p == null) {
            throw new DatoInvalidoException("El paciente no puede ser nulo.");
        }

        if (p.getNombre() == null || p.getNombre().isBlank()) {
            throw new DatoInvalidoException("El nombre del paciente es obligatorio.");
        }

        if (p.getCuil() == 0){
            throw new DatoInvalidoException("El CUIL del paciente es obligatorio.");
        }

        repositorio.guardar(p);
        System.out.println("✅ Paciente registrado con éxito: " + p.getNombre());
    }

    public List<Paciente> listarTodos() {
        return repositorio.listarTodos();
    }

    public List<Paciente> listarOrdenadosPorNombre() {
        return repositorio.listarTodos()
                .stream()
                .sorted(Comparator.comparing(Paciente::getNombre))
                .collect(Collectors.toList());
    }

    public Paciente buscarPorCuil(Long cuil) throws PacienteNoEncontradoException {
        Paciente paciente = repositorio.buscarPorId(cuil);

        if (paciente == null) {
            throw new PacienteNoEncontradoException("No existe un paciente con CUIL: " + cuil);
        }

        return paciente;
    }

    public void eliminarPaciente(Long cuil) throws PacienteNoEncontradoException {
        Paciente paciente = buscarPorCuil(cuil);
        repositorio.eliminar(paciente.getCuil());
    }
}