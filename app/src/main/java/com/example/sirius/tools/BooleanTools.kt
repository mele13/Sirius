package com.example.sirius.tools

/**
 * Converts a string representation of a boolean value to a Boolean.
 *
 * @param value The string representation of the boolean value.
 * @return The Boolean value corresponding to the provided string representation ("1" for true, "0" for false).
 */
fun stringToBoolean(value: String): Boolean {
    return value == "1"
}

/**
 * Converts a Boolean value to an integer (0 for false, 1 for true).
 *
 * @param value The Boolean value to be converted.
 * @return The integer representation of the Boolean value (0 for false, 1 for true).
 */
fun booleanToInt(value: Boolean): Int {
    return if (value) 1 else 0
}

fun stringToInt(value: String): Int {
    if (value == "true"){
        return 1
    }
    return 0
}

fun intToBoolean(value: Int): Boolean {
    return value != 0
}