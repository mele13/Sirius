package com.example.sirius.tools

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.example.sirius.model.Animal
import com.example.sirius.model.TypeAnimal
import com.example.sirius.viewmodel.UserViewModel
import kotlinx.coroutines.flow.firstOrNull
import java.time.Year

/**
 * Calculates the age based on the provided birth date.
 *
 * @param birthDate The birth date in the format "YYYY-MM-DD".
 * @return The calculated age.
 */
@RequiresApi(Build.VERSION_CODES.O)
fun calculateAge(item: Any): Int {
    return if (item is Animal){
        val birthYear = item.birthDate.substring(0, 4).toInt()
        val currentYear = Year.now().value

        (currentYear - birthYear)
    } else {
        0
    }
}

/**
 * Formats a numerical value with the appropriate singular or plural label.
 *
 * @param value The numerical value to be formatted.
 * @param singular The singular label to be used when the value is equal to 1.
 * @param plural The plural label to be used when the value is not equal to 1.
 * @return The formatted string.
 */
private fun formatPluralizedAge(value: Comparable<*>, singular: String, plural: String): String {
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
fun buildAnAgeText(age: Comparable<*>, birthDate: String, isShorten: Boolean = false): String {
    return when (age) {
        0 -> formatPluralizedAge(birthDate.substring(6, 7).toInt(), if (!isShorten) "month" else "(mo)", if (!isShorten) "months" else "(mo)")
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

    print("birthDate")
    print(birthDate)

    val age = calculateAge(birthDate)

    print("age")
    print(age)

    return when {
        age <= 1 -> "Puppy"
        age in 2..3 -> "Young"
        age in 4..7 -> "Adult"
        else -> "Senior"
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
        else -> Pair(0, Int.MAX_VALUE)
    }
}

/**
 * Converts a string representation to the corresponding TypeAnimal enum value.
 *
 * @param value The string representation of the TypeAnimal.
 * @return The TypeAnimal enum value corresponding to the provided string, or null if no match is found.
 */
fun stringToEnumTypeAnimal(value: String): TypeAnimal? {
    return try {
        TypeAnimal.valueOf(value)
    } catch (e: IllegalArgumentException) {
        null
    }
}

@Composable
fun checkIfAnimalIsFavorite(userId: Int?, animal: Animal, userViewModel: UserViewModel): Boolean {
    val isFavoriteState = remember { mutableStateOf(false) }

    LaunchedEffect(userId) {
        if (userId != null) {
            val likedAnimals = userViewModel.getLikedAnimals(userId).firstOrNull()
            isFavoriteState.value = likedAnimals?.any { it.id == animal.id } ?: false
        }
    }

    return isFavoriteState.value
}

fun getAdoptionText(waitingAdoption: Int): String {
    return if (waitingAdoption == 1) "Adoption" else "Pre Adoption"
}