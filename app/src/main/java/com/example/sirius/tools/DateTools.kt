package com.example.sirius.tools

import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Formats the input date string to the specified output format.
 *
 * @param inputDate The input date string to be formatted.
 * @return The formatted date string in the specified output format.
 */
fun formatDate(inputDate: String): String {
    val inputFormat = SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH)
    val outputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)

    val date = inputFormat.parse(inputDate)

    return outputFormat.format(date)
}