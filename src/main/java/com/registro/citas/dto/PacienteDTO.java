package com.registro.citas.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PacienteDTO {
    private Long idpaciente;
    private String nombrecompleto;
    private int edad;
    private Long nit;
    private Long cui;
    private String direccion;
    private Integer telefono;
    private LocalDate fecha;

    public PacienteDTO(Long idpaciente, String nombrecompleto, Long nit, Long cui, String direccion, Integer telefono) {
        this.idpaciente = idpaciente;
        this.nombrecompleto = nombrecompleto;
        this.nit = nit;
        this.cui = cui;
        this.direccion = direccion;
        this.telefono = telefono;
    }
    
    
}
