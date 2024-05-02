package com.example.sirius.model

enum class TypeAnimal(val displayName: String) {
    CAT("CAT"),
    DOG("DOG"),
    BIRD("BIRD"),
    RABBIT("RABBIT"),
    OTHER("OTHER");

    companion object {
        fun fromDisplayName(displayName: String): TypeAnimal? {
            return values().find { it.displayName == displayName }
        }

        fun getAllDisplayNames(): List<String> {
            return values().map { it.displayName }
        }
    }
}