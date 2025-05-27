package com.registro.citas.entidades;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "cita_programada", schema = "public")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CitaProgramada {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idcita")
    private Integer idcita;

    @Column(name = "nombrecompleto_paciente", nullable = false)
    private String nombrecompletoPaciente;

    @Column(name = "nit", nullable = false)
    private Long nit;

    @Column(name = "nombrecompleto_doctor", nullable = false)
    private String nombrecompletoDoctor;

    @Column(name = "colegiado", nullable = false)
    private String colegiado;

    @Column(name = "fechacita", nullable = false)
    private LocalDateTime fechacita;

    @Column(name = "motivoconsulta", nullable = false)
    private String motivoconsulta;

    @Column(name = "estado", nullable = false)
    private String estado = "PROGRAMADA";

    @Column(name = "costo")
    private Long costo;

    @Column(name = "fecha_fin")
    private LocalDateTime fechaFin;

    @PrePersist
    protected void onCreate() {
        this.fechacita = LocalDateTime.now();
        this.estado = "PROGRAMADA";
    }

    @Column(name = "correo") 
    private String correo;

    @Column(name = "motivocancelacion")
    private String motivocancelacion;

    

}
