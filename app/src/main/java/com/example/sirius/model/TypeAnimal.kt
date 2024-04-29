package com.example.sirius.model

enum class TypeAnimal {
    CAT,
    DOG,
    BIRD,
    RABBIT,
    OTHER;


    companion object {
        fun toList(): List<String> {
            val list: MutableList<String> = mutableListOf()
            for (animal in values()) {
                list.add(animal.name)
            }
            return list
        }
    }

}