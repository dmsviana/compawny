package dev.dmsviana.compawny.model.repository;

import dev.dmsviana.compawny.model.entity.Appointment;
import dev.dmsviana.compawny.model.entity.types.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    @Query("SELECT a FROM Appointment a WHERE a.id = :id AND a.deleted = false")
    Optional<Appointment> findByIdAndNotDeleted(@Param("id") Long id);

    @Query("""
        SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END FROM Appointment a
        WHERE a.caregiver.id = :caregiverId
        AND a.status NOT IN ('COMPLETED', 'CANCELLED')
        AND a.deleted = false
        AND (
            (:startTime BETWEEN a.startTime AND DATEADD(SECOND, a.durationInMinutes * 60, a.startTime))
            OR
            (:endTime BETWEEN a.startTime AND DATEADD(SECOND, a.durationInMinutes * 60, a.startTime))
            OR
            (a.startTime BETWEEN :startTime AND :endTime)
        )
    """)
    boolean hasScheduleConflict(
            @Param("caregiverId") Long caregiverId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );

    @Query("""
        SELECT a FROM Appointment a
        WHERE a.caregiver.id = :caregiverId
        AND a.status = :status
        AND a.startTime >= :startDate
        AND a.deleted = false
        ORDER BY a.startTime ASC
    """)
    List<Appointment> findByCaregiverAndStatusAndStartDateAfter(
            @Param("caregiverId") Long caregiverId,
            @Param("status") AppointmentStatus status,
            @Param("startDate") LocalDateTime startDate
    );

    @Query("""
        SELECT a FROM Appointment a
        WHERE a.pet.id = :petId
        AND a.status = :status
        AND a.startTime >= :startDate
        AND a.deleted = false
        ORDER BY a.startTime ASC
    """)
    List<Appointment> findByPetAndStatusAndStartDateAfter(
            @Param("petId") Long petId,
            @Param("status") AppointmentStatus status,
            @Param("startDate") LocalDateTime startDate
    );
}