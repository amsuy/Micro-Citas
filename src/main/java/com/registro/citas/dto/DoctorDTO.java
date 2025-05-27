package com.registro.citas.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DoctorDTO {
    private Long iddoctor;
    private String colegiado;
    private String nombrecompleto;
    private String especialidad;
    private LocalDate fecharegistro;
    private String direccion;
    private String centrohospitalario;
    private int edad;
    private String observacion;
    private String estado;

    public DoctorDTO(String colegiado, String nombrecompleto, String especialidad,
            String direccion, String centrohospitalario, int edad,
            String observacion) {
        this.colegiado = colegiado;
        this.nombrecompleto = nombrecompleto;
        this.especialidad = especialidad;
        this.direccion = direccion;
        this.centrohospitalario = centrohospitalario;
        this.edad = edad;
        this.observacion = observacion;
    }

}
