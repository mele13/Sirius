package com.example.sirius.tools

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import java.time.Year

/**
 * Calculates the age based on the provided birth date.
 *
 * @param birthDate The birth date in the format "YYYY-MM-DD".
 * @return The calculated age.
 */
@RequiresApi(Build.VERSION_CODES.O)
fun calculateAge(birthDate: String): Int {
    val birthYear = birthDate.substring(0, 4).toInt()
    val currentYear = Year.now().value

    return (currentYear - birthYear)
}

/**
 * Formats a numerical value with the appropriate singular or plural label.
 *
 * @param value The numerical value to be formatted.
 * @param singular The singular label to be used when the value is equal to 1.
 * @param plural The plural label to be used when the value is not equal to 1.
 * @return The formatted string.
 */
private fun formatPluralizedAge(value: Int, singular: String, plural: String): String {
    return if (value == 1) "$value $singular" else "$value $plural"
}

/**
 * Builds a formatted age text based on the provided age and birth date.
 *
 * @param age The age value.
 * @param birthDate The birth date in the format "YYYY-MM-DD".
 * @param isShorten Indicates whether to use shortened labels (e.g., "mo" for months).
 * @return The formatted age text.
 */
@Composable
fun buildAnAgeText(age: Int, birthDate: String, isShorten: Boolean = false): String {
    return when {
        age == 0 -> formatPluralizedAge(birthDate.substring(6, 7).toInt(), if (!isShorten) "month" else "(mo)", if (!isShorten) "months" else "(mo)")
        else -> formatPluralizedAge(age, if (!isShorten) "year" else "", if (!isShorten) "years" else "")
    }
}

/**
 * Calculates the age category based on the provided birth date.

 * @param birthDate The birth date in the format "YYYY-MM-DD".
 * @return The calculated age category.
 */
@RequiresApi(Build.VERSION_CODES.O)
fun calculateAgeCategory(birthDate: String): String {
    val age = calculateAge(birthDate)

    return when {
        age <= 1 -> "Puppy" // Menos de un año
        age in 2..3 -> "Young" // De 2 a 3 años
        age in 4..7 -> "Adult" // De 4 a 7 años
        age >= 8 -> "Senior" // Mayores de 7 años
        else -> "Not defined"
    }
}

/**
 * Maps the animal age category to its corresponding year range.

 * @param category The animal age category.
 * @return The mapped year range for the category.
 */
fun mapCategoryToYearRange(category: String): String {
    return when (category) {
        "Puppy" -> "<= 1"
        "Young" -> "2-3"
        "Adult" -> "4-7"
        "Senior" -> ">= 8"
        else -> ""
    }
}

/**
 * Retrieves the year range from the provided animal age category.

 * @param ageRange The animal age category as a string.
 * @return The corresponding year range as a pair of integers.
 */
fun getYearRangeFromCategory(ageRange: String): Pair<Int, Int> {
    return when (ageRange) {
        "<= 1" -> Pair(0, 1)
        "2-3" -> Pair(2, 3)
        "4-7" -> Pair(4, 7)
        ">= 8" -> Pair(8, Int.MAX_VALUE)
        else -> Pair(0, Int.MAX_VALUE) // Rango predeterminado si no se encuentra ninguna categoría
    }
}