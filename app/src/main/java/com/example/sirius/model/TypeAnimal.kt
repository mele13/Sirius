package com.example.sirius.model

enum class TypeAnimal(val displayName: String) {
    CAT("Cat"),
    DOG("Dog"),
    BIRD("Bird"),
    RABBIT("Rabbit"),
    OTHER("Other");

    companion object {
        fun fromDisplayName(displayName: String): TypeAnimal? {
            return values().find { it.displayName == displayName }
        }
    }
}