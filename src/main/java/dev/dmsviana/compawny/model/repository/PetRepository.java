package dev.dmsviana.compawny.model.repository;

import dev.dmsviana.compawny.model.entity.Pet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PetRepository extends JpaRepository<Pet, Long> {


    @Query("SELECT p FROM Pet p WHERE p.id = :id AND p.deleted = false")
    Optional<Pet> findByIdAndNotDeleted(@Param("id") Long id);

    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM Pet p " +
            "WHERE p.registrationNumber = :registrationNumber AND p.deleted = false")
    boolean existsByRegistrationNumber(@Param("registrationNumber") String registrationNumber);
}