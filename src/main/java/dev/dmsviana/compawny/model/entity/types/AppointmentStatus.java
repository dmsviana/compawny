package dev.dmsviana.compawny.model.entity.types;

import lombok.Getter;

import java.util.Set;

public enum AppointmentStatus {
    SCHEDULED(Set.of("Agendado", "IN_PROGRESS", "CANCELLED")),
    IN_PROGRESS(Set.of("Em Andamento", "COMPLETED", "CANCELLED")),
    COMPLETED(Set.of("Concluído")),
    CANCELLED(Set.of("Cancelado"));

    private final Set<String> allowedTransitions;
    @Getter
    private final String description;

    AppointmentStatus(Set<String> allowedTransitions) {
        this.allowedTransitions = allowedTransitions;
        this.description = allowedTransitions.iterator().next(); // Primeiro elemento é a descrição em PT-BR
    }

    public boolean canTransitionTo(AppointmentStatus newStatus) {
        return allowedTransitions.contains(newStatus.name());
    }

}