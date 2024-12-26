package dev.dmsviana.compawny.model.repository;

import dev.dmsviana.compawny.model.entity.Caregiver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CaregiverRepository extends JpaRepository<Caregiver, Long> {

    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Caregiver c WHERE c.cpf = :cpf AND c.deleted = false")
    boolean existsByCpfAndNotDeleted(@Param("cpf") String cpf);

    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Caregiver c WHERE c.email = :email AND c.deleted = false")
    boolean existsByEmailAndNotDeleted(@Param("email") String email);

    @Query("SELECT c FROM Caregiver c WHERE c.deleted = false")
    List<Caregiver> findAllActive();

    @Query("SELECT c FROM Caregiver c WHERE c.id = :id AND c.deleted = false")
    Optional<Caregiver> findByIdAndNotDeleted(@Param("id") Long id);
}