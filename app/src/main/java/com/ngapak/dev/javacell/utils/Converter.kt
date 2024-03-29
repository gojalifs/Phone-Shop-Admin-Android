package com.ngapak.dev.javacell.utils

import com.google.firebase.Timestamp
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object Converter {
    fun Int.toRupiah(): String {
        val currency = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
        return currency.format(this)
    }

    fun Timestamp.toWibDate(): String {
        val format = SimpleDateFormat("EEEE, d MMMM yyyy", Locale("id", "ID"))
        return format.format(toDate())
    }

    fun Timestamp.toWibTime(): String {
        val format = SimpleDateFormat("HH:mm:ss a", Locale("id", "ID"))
        return format.format(toDate())
    }

    fun Long.toTime(): String {
        val date = Date(this)
        val format = SimpleDateFormat("dd MMM yyyy", Locale.US)
        return format.format(date)
    }
}