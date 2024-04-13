package com.example.sirius.tools

fun stringToBoolean(value: String): Boolean {
    return value == "1"
}

fun booleanToInt(value: Boolean): Int {
    return if (value) 1 else 0
}
