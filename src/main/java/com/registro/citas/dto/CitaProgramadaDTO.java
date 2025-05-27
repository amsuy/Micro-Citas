package com.registro.citas.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CitaProgramadaDTO {
    private Long idpaciente;
    private String nombrecompletoPaciente;
    private Long nit;
    private Long iddoctor;
    private String nombrecompletoDoctor;
    private String colegiado;
    private LocalDateTime fechacita;
    private String motivoconsulta;
    private String estado = "PROGRAMADA";
    private Long costo;
    private LocalDateTime fechaFin;
    private String correo;
    private String motivocancelacion;
    private Integer Idcita;

}
