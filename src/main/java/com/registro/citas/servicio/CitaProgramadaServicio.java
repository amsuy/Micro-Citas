package com.registro.citas.servicio;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.registro.citas.dto.CitaProgramadaDTO;
import com.registro.citas.dto.DoctorDTO;
// DTOs locales simulando los datos de los microservicios
import com.registro.citas.dto.PacienteDTO;
import com.registro.citas.entidades.CitaProgramada;
import com.registro.citas.repositorio.CitaProgramadaRepositorio;

@Service
public class CitaProgramadaServicio {

    @Autowired
    private CitaProgramadaRepositorio citaRepositorio;

    @Autowired
    private RestTemplate restTemplate;



    @Autowired
    private EmailPdfService emailPdfService;

    private final String URL_PACIENTE = "http://localhost:9090/api/paciente/";
    private final String URL_DOCTOR = "http://localhost:9191/api/doctor/";

    public CitaProgramada crearCita(CitaProgramadaDTO citaDTO) {
        PacienteDTO[] pacientes = restTemplate.getForObject(URL_PACIENTE + "buscar/nit/" + citaDTO.getNit(),
                PacienteDTO[].class);
        if (pacientes == null || pacientes.length == 0) {
            throw new RuntimeException("Paciente no encontrado.");
        }
        PacienteDTO paciente = pacientes[0]; // ✅ toma el primero

        DoctorDTO[] doctores = restTemplate.getForObject(URL_DOCTOR + "buscar/colegiado/" + citaDTO.getColegiado(),
                DoctorDTO[].class);
        if (doctores == null || doctores.length == 0) {
            throw new RuntimeException("Doctor no encontrado.");
        }
        DoctorDTO doctor = doctores[0]; // ✅ Toma el primero

        CitaProgramada cita = new CitaProgramada();
        cita.setNombrecompletoPaciente(paciente.getNombrecompleto());
        cita.setNit(paciente.getNit());
        cita.setCorreo(citaDTO.getCorreo());
        cita.setNombrecompletoDoctor(doctor.getNombrecompleto());
        cita.setColegiado(doctor.getColegiado());
        cita.setFechacita(citaDTO.getFechacita() != null ? citaDTO.getFechacita() : LocalDateTime.now());
        cita.setMotivoconsulta(citaDTO.getMotivoconsulta());
        cita.setEstado("PROGRAMADA");
        cita.setCosto(citaDTO.getCosto());
        cita.setFechaFin(citaDTO.getFechaFin());

        CitaProgramada citaGuardada = citaRepositorio.save(cita);

        try {
            String asunto = "Confirmación de Cita";
            String mensaje = "📅 *Confirmación de Cita Médica*\n\n" +
                    "Hola " + cita.getNombrecompletoPaciente() + ",\n\n" +
                    "Le confirmamos que su cita ha sido programada exitosamente.\n\n" +
                    "*📝 Detalles de la Cita:*\n" +
                    "👨‍⚕️ Doctor: " + cita.getNombrecompletoDoctor() + "\n" +
                    "🗓️ Fecha y hora: "
                    + cita.getFechacita().format(DateTimeFormatter.ofPattern("dd/MM/yyyy 'a las' HH:mm")) + "\n" +
                    "💬 Motivo de consulta: " + cita.getMotivoconsulta() + "\n" +
                    "Por favor, llegue con al menos 10 minutos de anticipación.\n\n" +
                    "Gracias por confiar en *Hospital La Bendición*.\n" +
                    "— Este es un mensaje automático, no responder directamente —";

            CitaProgramadaDTO dto = new CitaProgramadaDTO();
            dto.setIdcita(citaGuardada.getIdcita());
            dto.setNombrecompletoPaciente(citaGuardada.getNombrecompletoPaciente());
            dto.setNit(citaGuardada.getNit());
            dto.setNombrecompletoDoctor(citaGuardada.getNombrecompletoDoctor());
            dto.setColegiado(citaGuardada.getColegiado());
            dto.setFechacita(citaGuardada.getFechacita());
            dto.setMotivoconsulta(citaGuardada.getMotivoconsulta());
            dto.setCorreo(citaGuardada.getCorreo());

            byte[] pdf = PdfGeneratorService.generarPDF(dto);

            emailPdfService.enviarPdfPorCorreo(dto.getCorreo(), asunto, mensaje, pdf); // ✅ Solo esta línea

        } catch (Exception e) {
            System.err.println("⚠️ Error al enviar el correo con PDF: " + e.getMessage());
        }

        return citaGuardada;
    }

    public CitaProgramada actualizarCita(Integer id, CitaProgramadaDTO citaDTO) {
        Optional<CitaProgramada> citaExistente = citaRepositorio.findById(id);
        if (citaExistente.isPresent()) {
            CitaProgramada cita = citaExistente.get();
            cita.setMotivoconsulta(citaDTO.getMotivoconsulta());
            cita.setCosto(citaDTO.getCosto());
            return citaRepositorio.save(cita);
        } else {
            throw new RuntimeException("Cita no encontrada con ID: " + id);
        }
    }

    public CitaProgramada finalizarCita(Integer id, CitaProgramadaDTO citaDTO) {
        Optional<CitaProgramada> citaExistente = citaRepositorio.findById(id);
        if (citaExistente.isPresent()) {
            CitaProgramada cita = citaExistente.get();
            cita.setEstado("REALIZADA");
            cita.setCosto(citaDTO.getCosto()); // ✅ Aquí lo agregas
            cita.setFechaFin(citaDTO.getFechaFin() != null ? citaDTO.getFechaFin() : LocalDateTime.now());
            return citaRepositorio.save(cita);
        }
        throw new RuntimeException("Cita no encontrada con ID: " + id);
    }

    public CitaProgramada cancelarCita(Integer id, LocalDateTime fechaFin, String motivoCancelacion) {
        Optional<CitaProgramada> citaExistente = citaRepositorio.findById(id);
        if (citaExistente.isPresent()) {
            CitaProgramada cita = citaExistente.get();
            cita.setEstado("CANCELADA");
            cita.setFechaFin(fechaFin != null ? fechaFin : LocalDateTime.now());
            cita.setMotivocancelacion(motivoCancelacion);
            return citaRepositorio.save(cita);
        } else {
            throw new RuntimeException("Cita no encontrada con ID: " + id);
        }
    }

    public Optional<CitaProgramada> obtenerCitaPorId(Integer id) {
        return citaRepositorio.findById(id);
    }

    public List<CitaProgramada> obtenerTodasLasCitas() {
        return citaRepositorio.findAll();
    }

    // 🔎 FILTROS COMPLETOS

    public List<CitaProgramada> obtenerCitasPorEstado(String estado) {
        return citaRepositorio.findByEstado(estado);
    }

    public List<CitaProgramada> obtenerCitasPorFecha(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        return citaRepositorio.findByFechacitaBetween(fechaInicio, fechaFin);
    }

    public List<CitaProgramada> obtenerCitasPorNit(Long nit) {
        return citaRepositorio.findByNit(nit);
    }

    public List<CitaProgramada> obtenerCitasPorColegiado(String colegiado) {
        return citaRepositorio.findByColegiado(colegiado);
    }

    public List<CitaProgramada> obtenerCitasPorMotivo(String motivo) {
        return citaRepositorio.findByMotivoconsultaContainingIgnoreCase(motivo);
    }

    public List<CitaProgramada> obtenerCitasPorNombrePaciente(String nombre) {
        return citaRepositorio.findByNombrecompletoPacienteContainingIgnoreCase(nombre);
    }

    public List<CitaProgramada> obtenerCitasPorNombreDoctor(String nombre) {
        return citaRepositorio.findByNombrecompletoDoctorContainingIgnoreCase(nombre);
    }

    public List<CitaProgramada> obtenerCitasPorRangoDeFecha(LocalDate fechaInicio, LocalDate fechaFin) {
        return citaRepositorio.findByFechaBetween(fechaInicio, fechaFin);
    }

    public List<CitaProgramada> obtenerCitasRealizadas() {
        return citaRepositorio.findCitasRealizadas();
    }

    public List<CitaProgramada> obtenerCitasCanceladas() {
        return citaRepositorio.findCitasCanceladas();
    }

    public List<CitaProgramada> obtenerCitasPorCorreo(String correo) {
        return citaRepositorio.findByCorreo(correo);
    }

    public List<CitaProgramada> obtenerCitasPorCorreoYEstado(String correo, String estado) {
        return citaRepositorio.findByCorreoAndEstado(correo, estado);
    }

    public List<CitaProgramada> obtenerCitasPorColegiadoYFecha(String colegiado, LocalDate inicio, LocalDate fin) {
        return citaRepositorio.findByColegiadoAndFechacitaBetween(colegiado, inicio, fin);
    }

    public List<CitaProgramada> obtenerHistorialPorColegiado(String colegiado, LocalDate inicio, LocalDate fin) {
        return citaRepositorio.findByColegiadoAndFechacitaBetweenAndEstadoIn(
                colegiado, inicio, fin, List.of("REALIZADA", "CANCELADA"));
    }

    public CitaProgramada actualizarMotivoConsulta(Integer id, String motivoConsulta) {
        Optional<CitaProgramada> optional = citaRepositorio.findById(id);
        if (optional.isPresent()) {
            CitaProgramada cita = optional.get();
            cita.setMotivoconsulta(motivoConsulta);
            return citaRepositorio.save(cita);
        }
        throw new RuntimeException("Cita no encontrada con ID: " + id);
    }

    public CitaProgramada actualizarFechaCita(Integer id, LocalDateTime fechaCita) {
        Optional<CitaProgramada> optional = citaRepositorio.findById(id);
        if (optional.isPresent()) {
            CitaProgramada cita = optional.get();
            cita.setFechacita(fechaCita);
            return citaRepositorio.save(cita);
        }
        throw new RuntimeException("Cita no encontrada con ID: " + id);
    }

    public CitaProgramada actualizarNombrePaciente(Integer id, String nombrePaciente) {
        Optional<CitaProgramada> optional = citaRepositorio.findById(id);
        if (optional.isPresent()) {
            CitaProgramada cita = optional.get();
            cita.setNombrecompletoPaciente(nombrePaciente);
            return citaRepositorio.save(cita);
        }
        throw new RuntimeException("Cita no encontrada con ID: " + id);
    }

    public CitaProgramada actualizarNombreDoctor(Integer id, String nombreDoctor) {
        Optional<CitaProgramada> optional = citaRepositorio.findById(id);
        if (optional.isPresent()) {
            CitaProgramada cita = optional.get();
            cita.setNombrecompletoDoctor(nombreDoctor);
            return citaRepositorio.save(cita);
        }
        throw new RuntimeException("Cita no encontrada con ID: " + id);
    }

    public void mandarPdfCitaPorIdYEstado(Integer idcita) {
        CitaProgramada cita = citaRepositorio.findByIdcita(idcita)
                .orElseThrow(() -> new RuntimeException("No se encontró una cita con el ID: " + idcita));

        if (!"PROGRAMADA".equalsIgnoreCase(cita.getEstado())) {
            throw new RuntimeException("La cita no está en estado PROGRAMADA.");
        }

        CitaProgramadaDTO dto = new CitaProgramadaDTO();
        dto.setIdcita(cita.getIdcita());
        dto.setNombrecompletoPaciente(cita.getNombrecompletoPaciente());
        dto.setNit(cita.getNit());
        dto.setNombrecompletoDoctor(cita.getNombrecompletoDoctor());
        dto.setColegiado(cita.getColegiado());
        dto.setFechacita(cita.getFechacita());
        dto.setMotivoconsulta(cita.getMotivoconsulta());
        dto.setCorreo(cita.getCorreo());

        try {
            byte[] pdf = PdfGeneratorService.generarPDF(dto);
            String mensaje = "Estimado/a " + dto.getNombrecompletoPaciente()
                    + ", adjunto encontrará el PDF de su cita programada.";
            emailPdfService.enviarPdfPorCorreo(dto.getCorreo(), "Confirmación de Cita Médica", mensaje, pdf);
        } catch (Exception e) {
            throw new RuntimeException("Error al enviar PDF por correo: " + e.getMessage());
        }
    }

    public List<CitaProgramada> obtenerCitasPorRangoFecha(LocalDateTime inicio, LocalDateTime fin) {
        return citaRepositorio.findByFechacitaBetween(inicio, fin);
    }

    public List<CitaProgramada> obtenerCitasPorNitYEstado(Long nit, String estado) {
        return citaRepositorio.findByNitAndEstado(nit, estado);
    }

    public List<PacienteDTO> obtenerPacientesPorNit(Long nit) {
        String url = "http://localhost:9090/api/paciente/buscar/nit/" + nit;
        PacienteDTO[] pacientes = restTemplate.getForObject(url, PacienteDTO[].class);
        return List.of(pacientes);
    }

    public CitaProgramada actualizarNit(Integer id, Long nuevoNit) {
        Optional<CitaProgramada> optional = citaRepositorio.findById(id);
        if (optional.isPresent()) {
            CitaProgramada cita = optional.get();
            cita.setNit(nuevoNit);
            return citaRepositorio.save(cita);
        }
        throw new RuntimeException("Cita no encontrada con ID: " + id);
    }

    public CitaProgramada actualizarColegiado(Integer id, String nuevoColegiado) {
        Optional<CitaProgramada> optional = citaRepositorio.findById(id);
        if (optional.isPresent()) {
            CitaProgramada cita = optional.get();
            cita.setColegiado(nuevoColegiado);
            return citaRepositorio.save(cita);
        }
        throw new RuntimeException("Cita no encontrada con ID: " + id);
    }

}
