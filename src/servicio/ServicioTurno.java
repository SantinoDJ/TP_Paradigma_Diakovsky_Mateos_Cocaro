package servicio;

import excepciones.DatoInvalidoException;
import excepciones.TurnoYaReservadoException;
import modelo.Turno;
import repositorio.IRepositorio;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class ServicioTurno {

    private IRepositorio<Turno> turnoRepo;
    private ServicioPaciente servicioPaciente;
    private ServicioOdontologo servicioOdonto;

    public ServicioTurno(IRepositorio<Turno> repo,
                         ServicioPaciente sPaciente,
                         ServicioOdontologo sOdonto) {

        this.turnoRepo = repo;
        this.servicioPaciente = sPaciente;
        this.servicioOdonto = sOdonto;
    }

    public void agendarTurno(Turno t) throws Exception {

        if (t == null) {
            throw new DatoInvalidoException("El turno no puede ser nulo.");
        }

        servicioPaciente.buscarPorCuil(t.getPaciente().getCuil());
        servicioOdonto.buscarPorId(t.getOdontologo().getId());

        boolean ocupado = turnoRepo.listarTodos()
                .stream()
                .anyMatch(turno ->
                        turno.getFecha().equals(t.getFecha())
                                && turno.getHora().equals(t.getHora())
                                && turno.getOdontologo().getId() == t.getOdontologo().getId()
                );

        if (ocupado) {
            throw new TurnoYaReservadoException(
                    "El odontólogo ya tiene un turno asignado en ese horario."
            );
        }

        turnoRepo.guardar(t);
        System.out.println("✅ Turno agendado con éxito.");
    }

    public List<Turno> listarTodosLosTurnos() {
        return turnoRepo.listarTodos();
    }

    public List<Turno> buscarTurnosPorFecha(LocalDate fecha) {
        return turnoRepo.listarTodos()
                .stream()
                .filter(t -> t.getFecha().equals(fecha))
                .collect(Collectors.toList());
    }

    public List<Turno> buscarTurnosPorPaciente(Long cuilPaciente) {
        return turnoRepo.listarTodos()
                .stream()
                .filter(t -> t.getPaciente().getCuil() == cuilPaciente)
                .collect(Collectors.toList());
    }

    public List<Turno> buscarTurnosPorOdontologo(Long idOdontologo) {
        return turnoRepo.listarTodos()
                .stream()
                .filter(t -> t.getOdontologo().getId() == idOdontologo)
                .collect(Collectors.toList());
    }
}