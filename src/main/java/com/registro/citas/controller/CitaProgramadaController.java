package com.registro.citas.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.registro.citas.dto.CitaProgramadaDTO;
import com.registro.citas.dto.PacienteDTO;
import com.registro.citas.entidades.CitaProgramada;
import com.registro.citas.servicio.CitaProgramadaServicio;

@RestController
@RequestMapping("/api/citas")
@CrossOrigin(origins = "*")
public class CitaProgramadaController {

    private final CitaProgramadaServicio citaProgramadaServicio;

    public CitaProgramadaController(CitaProgramadaServicio citaProgramadaServicio) {
        this.citaProgramadaServicio = citaProgramadaServicio;
    }

    // CREAR CITA
    @PostMapping("/crear")
    public ResponseEntity<CitaProgramada> crearCita(@RequestBody CitaProgramadaDTO citaDTO) {
        CitaProgramada nuevaCita = citaProgramadaServicio.crearCita(citaDTO);
        return new ResponseEntity<>(nuevaCita, HttpStatus.CREATED);
    }

    // LISTAR TODAS
    @GetMapping("/listar")
    public ResponseEntity<List<CitaProgramada>> listarCitas() {
        return new ResponseEntity<>(citaProgramadaServicio.obtenerTodasLasCitas(), HttpStatus.OK);
    }

    // FILTROS ESTÁNDAR
    @GetMapping("/buscar/id/{id}")
    public ResponseEntity<CitaProgramada> obtenerCitaPorId(@PathVariable Integer id) {
        return citaProgramadaServicio.obtenerCitaPorId(id)
                .map(cita -> new ResponseEntity<>(cita, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/buscar/estado/{estado}")
    public ResponseEntity<List<CitaProgramada>> obtenerCitasPorEstado(@PathVariable String estado) {
        return new ResponseEntity<>(citaProgramadaServicio.obtenerCitasPorEstado(estado), HttpStatus.OK);
    }

    @GetMapping("/buscar/fecha/{fechaInicio}/{fechaFin}")
    public ResponseEntity<List<CitaProgramada>> obtenerCitasPorFecha(
            @PathVariable LocalDateTime fechaInicio,
            @PathVariable LocalDateTime fechaFin) {
        return new ResponseEntity<>(citaProgramadaServicio.obtenerCitasPorFecha(fechaInicio, fechaFin), HttpStatus.OK);
    }

    @GetMapping("/buscar/rango-fecha/{fechaInicio}/{fechaFin}")
    public ResponseEntity<List<CitaProgramada>> buscarCitasPorRangoDeFecha(
            @PathVariable String fechaInicio,
            @PathVariable String fechaFin) {
        LocalDate inicio = LocalDate.parse(fechaInicio);
        LocalDate fin = LocalDate.parse(fechaFin);
        List<CitaProgramada> citas = citaProgramadaServicio.obtenerCitasPorRangoDeFecha(inicio, fin);
        return new ResponseEntity<>(citas, HttpStatus.OK);
    }

    @GetMapping("/buscar/nit/{nit}")
    public ResponseEntity<List<CitaProgramada>> obtenerCitasPorNit(@PathVariable Long nit) {
        return new ResponseEntity<>(citaProgramadaServicio.obtenerCitasPorNit(nit), HttpStatus.OK);
    }

    @GetMapping("/buscar/colegiado/{colegiado}")
    public ResponseEntity<List<CitaProgramada>> obtenerCitasPorColegiado(@PathVariable String colegiado) {
        return new ResponseEntity<>(citaProgramadaServicio.obtenerCitasPorColegiado(colegiado), HttpStatus.OK);
    }

    @GetMapping("/buscar/motivo/{motivo}")
    public ResponseEntity<List<CitaProgramada>> obtenerCitasPorMotivo(@PathVariable String motivo) {
        return new ResponseEntity<>(citaProgramadaServicio.obtenerCitasPorMotivo(motivo), HttpStatus.OK);
    }

    @GetMapping("/buscar/nombre/{nombre}")
    public ResponseEntity<List<CitaProgramada>> obtenerCitasPorNombre(@PathVariable String nombre) {
        return new ResponseEntity<>(citaProgramadaServicio.obtenerCitasPorNombrePaciente(nombre), HttpStatus.OK);
    }

    // FILTROS AVANZADOS
    @GetMapping("/buscar/correo/{correo}")
    public ResponseEntity<List<CitaProgramada>> obtenerCitasPorCorreo(@PathVariable String correo) {
        return new ResponseEntity<>(citaProgramadaServicio.obtenerCitasPorCorreo(correo), HttpStatus.OK);
    }

    @GetMapping("/buscar/correo-estado/{correo}/{estado}")
    public ResponseEntity<List<CitaProgramada>> obtenerCitasPorCorreoYEstado(
            @PathVariable String correo,
            @PathVariable String estado) {
        return new ResponseEntity<>(citaProgramadaServicio.obtenerCitasPorCorreoYEstado(correo, estado), HttpStatus.OK);
    }

    // LISTADOS ESPECIALES
    @GetMapping("/listar/realizadas")
    public ResponseEntity<List<CitaProgramada>> listarCitasRealizadas() {
        return new ResponseEntity<>(citaProgramadaServicio.obtenerCitasRealizadas(), HttpStatus.OK);
    }

    @GetMapping("/listar/canceladas")
    public ResponseEntity<List<CitaProgramada>> listarCitasCanceladas() {
        return new ResponseEntity<>(citaProgramadaServicio.obtenerCitasCanceladas(), HttpStatus.OK);
    }

    // ACTUALIZACIÓN Y ESTADO
    @PutMapping("/actualizar/{id}")
    public ResponseEntity<CitaProgramada> actualizarCita(
            @PathVariable Integer id,
            @RequestBody CitaProgramadaDTO citaDTO) {
        CitaProgramada citaActualizada = citaProgramadaServicio.actualizarCita(id, citaDTO);
        return new ResponseEntity<>(citaActualizada, HttpStatus.OK);
    }

    @PutMapping("/finalizar/{id}")
    public ResponseEntity<CitaProgramada> finalizarCita(
            @PathVariable Integer id,
            @RequestBody CitaProgramadaDTO citaDTO) {
        CitaProgramada citaFinalizada = citaProgramadaServicio.finalizarCita(id, citaDTO);
        return new ResponseEntity<>(citaFinalizada, HttpStatus.OK);
    }

    @PutMapping("/cancelar/{id}")
    public ResponseEntity<CitaProgramada> cancelarCita(
            @PathVariable Integer id,
            @RequestBody CitaProgramadaDTO citaDTO) {
        try {
            CitaProgramada citaCancelada = citaProgramadaServicio.cancelarCita(
                    id,
                    citaDTO.getFechaFin(),
                    citaDTO.getMotivocancelacion());
            return new ResponseEntity<>(citaCancelada, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/buscar/fecha/{fecha}")
    public ResponseEntity<List<CitaProgramada>> obtenerCitasPorFechaUnica(@PathVariable String fecha) {
        LocalDateTime inicio = LocalDate.parse(fecha).atStartOfDay();
        LocalDateTime fin = inicio.plusDays(1).minusSeconds(1);
        List<CitaProgramada> citas = citaProgramadaServicio.obtenerCitasPorFecha(inicio, fin);
        return new ResponseEntity<>(citas, HttpStatus.OK);
    }

    @GetMapping("/hoy/colegiado/{colegiado}")
    public ResponseEntity<List<CitaProgramada>> citasDeHoyPorColegiado(@PathVariable String colegiado) {
        LocalDate inicio = LocalDate.now();
        LocalDate fin = inicio;
        List<CitaProgramada> citas = citaProgramadaServicio.obtenerCitasPorColegiadoYFecha(colegiado, inicio, fin);
        return new ResponseEntity<>(citas, HttpStatus.OK);
    }

    @GetMapping("/historial/colegiado/{colegiado}/{inicio}/{fin}")
    public ResponseEntity<List<CitaProgramada>> historialCitasPorColegiado(
            @PathVariable String colegiado,
            @PathVariable String inicio,
            @PathVariable String fin) {
        LocalDate fechaInicio = LocalDate.parse(inicio);
        LocalDate fechaFin = LocalDate.parse(fin);
        List<CitaProgramada> citas = citaProgramadaServicio.obtenerHistorialPorColegiado(colegiado, fechaInicio,
                fechaFin);
        return new ResponseEntity<>(citas, HttpStatus.OK);
    }

    @PutMapping("/actualizar/motivo/{id}")
    public ResponseEntity<CitaProgramada> actualizarMotivoConsulta(
            @PathVariable Integer id,
            @RequestBody CitaProgramadaDTO dto) {
        CitaProgramada cita = citaProgramadaServicio.actualizarMotivoConsulta(id, dto.getMotivoconsulta());
        return new ResponseEntity<>(cita, HttpStatus.OK);
    }

    @PutMapping("/actualizar/fecha/{id}")
    public ResponseEntity<CitaProgramada> actualizarFechaCita(
            @PathVariable Integer id,
            @RequestBody CitaProgramadaDTO dto) {
        CitaProgramada cita = citaProgramadaServicio.actualizarFechaCita(id, dto.getFechacita());
        return new ResponseEntity<>(cita, HttpStatus.OK);
    }

    @PutMapping("/actualizar/nombre-paciente/{id}")
    public ResponseEntity<CitaProgramada> actualizarNombrePaciente(
            @PathVariable Integer id,
            @RequestBody CitaProgramadaDTO dto) {
        CitaProgramada cita = citaProgramadaServicio.actualizarNombrePaciente(id, dto.getNombrecompletoPaciente());
        return new ResponseEntity<>(cita, HttpStatus.OK);
    }

    @PutMapping("/actualizar/nombre-doctor/{id}")
    public ResponseEntity<CitaProgramada> actualizarNombreDoctor(
            @PathVariable Integer id,
            @RequestBody CitaProgramadaDTO dto) {
        CitaProgramada cita = citaProgramadaServicio.actualizarNombreDoctor(id, dto.getNombrecompletoDoctor());
        return new ResponseEntity<>(cita, HttpStatus.OK);
    }

    // ENVIAR PDF AL CORREO DEL PACIENTE POR ID DE CITA (solo si está PROGRAMADA)
    @GetMapping("/enviar-pdf-cita/{idcita}")
    public ResponseEntity<String> enviarPdfCita(@PathVariable Integer idcita) {
        try {
            citaProgramadaServicio.mandarPdfCitaPorIdYEstado(idcita);
            return ResponseEntity.ok("El PDF fue enviado correctamente al correo del paciente.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/buscar/fechas/{inicio}/{fin}")
    public ResponseEntity<List<CitaProgramada>> buscarCitasPorRangoFechas(
            @PathVariable String inicio,
            @PathVariable String fin) {
        LocalDateTime fechaInicio = LocalDateTime.parse(inicio);
        LocalDateTime fechaFin = LocalDateTime.parse(fin);
        return new ResponseEntity<>(citaProgramadaServicio.obtenerCitasPorRangoFecha(fechaInicio, fechaFin),
                HttpStatus.OK);
    }

    @GetMapping("/buscar/nit-estado/{nit}/{estado}")
    public ResponseEntity<List<CitaProgramada>> obtenerCitasPorNitYEstado(
            @PathVariable Long nit,
            @PathVariable String estado) {
        return new ResponseEntity<>(citaProgramadaServicio.obtenerCitasPorNitYEstado(nit, estado), HttpStatus.OK);
    }

    @GetMapping("/paciente/buscar/nit/{nit}")
    public ResponseEntity<List<PacienteDTO>> buscarPacientesPorNitDesdeCitas(@PathVariable Long nit) {
        List<PacienteDTO> pacientes = citaProgramadaServicio.obtenerPacientesPorNit(nit);
        return new ResponseEntity<>(pacientes, HttpStatus.OK);
    }

    @PutMapping("/actualizar/nit/{id}")
    public ResponseEntity<CitaProgramada> actualizarNit(
            @PathVariable Integer id,
            @RequestBody CitaProgramadaDTO dto) {
        CitaProgramada cita = citaProgramadaServicio.actualizarNit(id, dto.getNit());
        return new ResponseEntity<>(cita, HttpStatus.OK);
    }

    @PutMapping("/actualizar/colegiado/{id}")
    public ResponseEntity<CitaProgramada> actualizarColegiado(
            @PathVariable Integer id,
            @RequestBody CitaProgramadaDTO dto) {
        CitaProgramada cita = citaProgramadaServicio.actualizarColegiado(id, dto.getColegiado());
        return new ResponseEntity<>(cita, HttpStatus.OK);
    }

}
