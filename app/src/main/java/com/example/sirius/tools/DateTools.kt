package com.example.sirius.tools

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

const val DATE_FORMAT = "yyyy-MM-dd"

/**
 * Formats the input date string to the specified output format.
 *
 * @param inputDate The input date string to be formatted.
 * @return The formatted date string in the specified output format.
 */
fun formatDate(inputDate: String): String {
    val inputFormat = SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH)
    val outputFormat = SimpleDateFormat(DATE_FORMAT, Locale.ENGLISH)

    val date = inputFormat.parse(inputDate)

    return outputFormat.format(date)
}

fun formatDate(inputTimestamp: Long): String {
    val inputDate = Date(inputTimestamp)
    val outputFormat = SimpleDateFormat(DATE_FORMAT, Locale.ENGLISH)
    return outputFormat.format(inputDate)
}

fun parseDateStringToLong(dateString: String): Long {
    val inputFormat = SimpleDateFormat(DATE_FORMAT, Locale.getDefault())
    val date: Date = inputFormat.parse(dateString) ?: Date()
    return date.time
}