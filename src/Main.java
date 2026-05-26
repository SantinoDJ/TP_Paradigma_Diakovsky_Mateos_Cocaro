import excepciones.ClinicaException;
import modelo.*;
import repositorio.*;
import servicio.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        PacienteRepositorio repoPaciente = new PacienteRepositorio();
        OdontologoRepositorio repoOdonto = new OdontologoRepositorio();
        TurnoRepositorio repoTurno = new TurnoRepositorio();

        ServicioPaciente sPaciente = new ServicioPaciente(repoPaciente);
        ServicioOdontologo sOdonto = new ServicioOdontologo(repoOdonto);
        ServicioTurno sTurno = new ServicioTurno(repoTurno, sPaciente, sOdonto);

        try {
            sOdonto.registrarOdontologo(new Odontologo(123, "Joaquin", "Rodriguez", "MT1345"));
            sPaciente.registrarPaciente(new Paciente(1, "Juan", "Gonzalez", 111111, 2011111118L, 40, "juan@email.com", LocalDate.now(), null));
        } catch (ClinicaException e) {
            System.out.println("❌ Error al cargar datos de prueba: " + e.getMessage());
        }

        int opcion = -1;

        while (opcion != 0) {
            System.out.println("\n==============================================");
            System.out.println("        SISTEMA CLÍNICA       ");
            System.out.println("==============================================");
            System.out.println("1. GESTIÓN DE PACIENTES");
            System.out.println("2. GESTIÓN DE ODONTÓLOGOS");
            System.out.println("3. GESTIÓN DE TURNOS");
            System.out.println("0. SALIR");
            System.out.print("Seleccione una opción: ");

            opcion = sc.nextInt();
            sc.nextLine();

            switch (opcion) {
                case 1:
                    menuPacientes(sPaciente, sc);
                    break;
                case 2:
                    menuOdontologos(sOdonto, sc);
                    break;
                case 3:
                    menuTurnos(sTurno, sPaciente, sOdonto, sc);
                    break;
                case 0:
                    System.out.println("¡Gracias por usar el sistema! Chau.");
                    break;
                default:
                    System.out.println("❌ Opción inválida, probá de nuevo.");
            }
        }

        sc.close();
    }

    public static void menuPacientes(ServicioPaciente sP, Scanner sc) {
        System.out.println("\n---  SECCIÓN PACIENTES ---");
        System.out.println("1. Alta  2. Listar  3. Baja  4. Volver");
        int opP = sc.nextInt();
        sc.nextLine();

        if (opP == 1) {
            System.out.print("Nombre: ");
            String nom = sc.nextLine();

            System.out.print("Apellido: ");
            String ape = sc.nextLine();

            System.out.print("DNI: ");
            int dni = sc.nextInt();

            System.out.print("CUIL: ");
            long cuil = sc.nextLong();

            System.out.print("Edad: ");
            int edad = sc.nextInt();
            sc.nextLine();

            System.out.print("Email: ");
            String email = sc.nextLine();

            System.out.print("Nombre Obra Social: ");
            String nombreOS = sc.nextLine();

            System.out.print("Plan: ");
            String planOS = sc.nextLine();

            System.out.print("Nro Afiliado: ");
            String nroOS = sc.nextLine();

            ObraSocial osNueva = new ObraSocial(nombreOS, planOS, nroOS);

            try {
                sP.registrarPaciente(new Paciente(0, nom, ape, dni, cuil, edad, email, LocalDate.now(), null, osNueva));
                System.out.println("✅ Paciente cargado con Obra Social.");
            } catch (ClinicaException e) {
                System.out.println("❌ " + e.getMessage());
            }

        } else if (opP == 2) {
            System.out.println("\nLista de Pacientes en sistema:");
            sP.listarTodos().forEach(System.out::println);

        } else if (opP == 3) {
            System.out.print("Ingrese CUIL del paciente a borrar: ");
            long cuilElim = sc.nextLong();

            try {
                sP.eliminarPaciente(cuilElim);
                System.out.println("✅ Proceso de baja terminado.");
            } catch (ClinicaException e) {
                System.out.println("❌ " + e.getMessage());
            }
        }
    }

    public static void menuOdontologos(ServicioOdontologo sO, Scanner sc) {
        System.out.println("\n--- ️ SECCIÓN ODONTÓLOGOS ---");
        System.out.println("1. Alta  2. Listar  3. Volver");
        int op = sc.nextInt();
        sc.nextLine();

        if (op == 1) {
            System.out.print("Nombre: ");
            String nom = sc.nextLine();

            System.out.print("Apellido: ");
            String ape = sc.nextLine();

            System.out.print("Matrícula: ");
            String mat = sc.nextLine();

            try {
                sO.registrarOdontologo(new Odontologo(0, nom, ape, mat));
            } catch (ClinicaException e) {
                System.out.println("❌ " + e.getMessage());
            }

        } else if (op == 2) {
            sO.listarOdontologos().forEach(System.out::println);
        }
    }

    public static void menuTurnos(ServicioTurno sT, ServicioPaciente sP, ServicioOdontologo sO, Scanner sc) {
        System.out.println("\n---  SECCIÓN TURNOS ---");
        System.out.println("1. Agendar Turno  2. Ver todos  3. Volver");
        int opT = sc.nextInt();
        sc.nextLine();

        if (opT == 1) {
            try {
                System.out.print("Ingrese CUIL del paciente: ");
                long cuil = sc.nextLong();

                Paciente p = sP.buscarPorCuil(cuil);

                System.out.print("Ingrese ID del odontólogo: ");
                long idOdonto = sc.nextLong();

                Odontologo o = sO.buscarPorId(idOdonto);

                System.out.print("ID del Turno: ");
                int idTurno = sc.nextInt();
                sc.nextLine();

                Turno nuevoTurno = new Turno(idTurno, LocalDate.now(), LocalTime.of(10, 0), p, o);

                sT.agendarTurno(nuevoTurno);

                System.out.println("✅ Turno guardado en el sistema.");

            } catch (Exception e) {
                System.out.println("❌ " + e.getMessage());
            }

        } else if (opT == 2) {
            System.out.println("\n--- Lista de Turnos Agendados ---");

            if (sT.listarTodosLosTurnos().isEmpty()) {
                System.out.println("No hay turnos registrados todavía.");
            } else {
                sT.listarTodosLosTurnos().forEach(t -> {
                    System.out.println("Turno #" + t.getId()
                            + " | Paciente: " + t.getPaciente().getNombre()
                            + " | Odontólogo: " + t.getOdontologo().getNombre());
                });
            }
        }
    }
}