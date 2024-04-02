package com.example.sirius.tools

/**
 * Checks if the given email string is in a valid format.
 *
 * @param email The email string to be validated.
 * @return `true` if the email is valid, `false` otherwise.
 */
fun isEmailValid(email: String): Boolean {
    val emailRegex = Regex("^[A-Za-z](.*)(@)(.+)(\\.)(.+)")
    return emailRegex.matches(email)
}

/**
 * Checks if the given password string meets the required format criteria.
 *
 * @param password The password string to be validated.
 * @return `true` if the password is valid, `false` otherwise.
 */
fun isPasswordValid(password: String): Boolean {
    val passwordRegex = Regex("^(?=.*[A-Z])(?=.*[.,\\-_!@#\$%^&*()])(.{6,})\$")
    return passwordRegex.matches(password)
}
