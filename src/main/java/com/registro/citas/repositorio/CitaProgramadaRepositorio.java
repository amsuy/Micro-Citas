package com.registro.citas.repositorio;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.registro.citas.entidades.CitaProgramada;



@Repository
public interface CitaProgramadaRepositorio extends JpaRepository<CitaProgramada, Integer> {

        Optional<CitaProgramada> findByIdcita(Integer idcita);



        List<CitaProgramada> findByEstado(String estado);

        List<CitaProgramada> findByFechacitaBetween(LocalDateTime fechaInicio, LocalDateTime fechaFin);

        List<CitaProgramada> findByNit(Long nit);

        List<CitaProgramada> findByColegiado(String colegiado);

        List<CitaProgramada> findByMotivoconsultaContainingIgnoreCase(String motivoconsulta);

        List<CitaProgramada> findByNombrecompletoPacienteContainingIgnoreCase(String nombre);

        List<CitaProgramada> findByNombrecompletoDoctorContainingIgnoreCase(String nombre);

        // Consulta por rango de fechas (solo día, ignorando la hora)
        @Query("SELECT c FROM CitaProgramada c WHERE CAST(c.fechacita AS date) BETWEEN :fechaInicio AND :fechaFin")
        List<CitaProgramada> findByFechaBetween(@Param("fechaInicio") LocalDate fechaInicio,
                        @Param("fechaFin") LocalDate fechaFin);

        // Consultar citas realizadas
        @Query("SELECT c FROM CitaProgramada c WHERE c.estado = 'REALIZADA'")
        List<CitaProgramada> findCitasRealizadas();

        // Consultar citas canceladas
        @Query("SELECT c FROM CitaProgramada c WHERE c.estado = 'CANCELADA'")
        List<CitaProgramada> findCitasCanceladas();

        List<CitaProgramada> findByCorreo(String correo);

        List<CitaProgramada> findByFechacita(LocalDateTime fecha);



        List<CitaProgramada> findByCorreoAndEstado(String correo, String estado);



        @Query("SELECT c FROM CitaProgramada c WHERE c.colegiado = :colegiado AND CAST(c.fechacita AS date) BETWEEN :inicio AND :fin")
        List<CitaProgramada> findByColegiadoAndFechacitaBetween(@Param("colegiado") String colegiado,
                        @Param("inicio") LocalDate inicio,
                        @Param("fin") LocalDate fin);

  

        @Query("SELECT c FROM CitaProgramada c WHERE c.colegiado = :colegiado AND CAST(c.fechacita AS date) BETWEEN :inicio AND :fin AND c.estado IN :estados")
        List<CitaProgramada> findByColegiadoAndFechacitaBetweenAndEstadoIn(@Param("colegiado") String colegiado,
                        @Param("inicio") LocalDate inicio,
                        @Param("fin") LocalDate fin,
                        @Param("estados") List<String> estados);

        List<CitaProgramada> findByNitAndEstado(Long nit, String estado);

}
