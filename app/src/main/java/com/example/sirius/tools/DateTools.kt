package com.example.sirius.tools

import java.text.SimpleDateFormat
import java.util.Locale


fun formatDate(inputDate: String): String {
    // Formato de la fecha de entrada
    val inputFormat = SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH)
    // Formato de la fecha de salida
    val outputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)

    // Parsear la fecha de entrada
    val date = inputFormat.parse(inputDate)

    // Formatear la fecha a un String en el formato deseado
    return outputFormat.format(date)
}