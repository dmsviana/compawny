package dev.dmsviana.compawny.model.entity.types;

public enum PetType {

    DOG("Cachorro"),
    CAT("Gato"),
    BIRD("Pássaro"),
    RABBIT("Coelho"),
    OTHER("Outro");

    public final String description;

    PetType(String description) {
        this.description = description;
    }

}