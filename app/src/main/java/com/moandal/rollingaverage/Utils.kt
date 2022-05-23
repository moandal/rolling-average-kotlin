package com.moandal.rollingaverage

import android.app.Activity
import android.content.Context
import androidx.appcompat.app.AlertDialog
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

object Utils {
    var arraySize = 100
    fun showMessage(title: String?, message: String?, activity: Activity?) {
        val builder = AlertDialog.Builder(
            activity!!
        )
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setPositiveButton(android.R.string.ok, null)
        builder.show()
    }

    fun convertStringToDate(dateString: String?): Date {
        val sdf = SimpleDateFormat("dd/MM/yyyy")
        sdf.isLenient = false
        var formattedDate = Date()
        try {
            formattedDate = sdf.parse(dateString)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return formattedDate
    }

    fun validateStringToDate(dateString: String?): Date {
        val df = DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault())
        df.isLenient = false
        var formattedDate = Date()
        try {
            formattedDate = df.parse(dateString)
        } catch (e: ParseException) {
            try {
                formattedDate = df.parse("01/01/1900")
            } catch (f: ParseException) {
                f.printStackTrace()
            }
        }
        return formattedDate
    }

    fun saveData(context: Context, readings: DoubleArray, readDates: Array<Date?>) {
        val ddmmFormat = SimpleDateFormat("dd/MM/yyyy")
        val sp = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val editor = sp.edit()
        for (i in 0 until arraySize) {
            editor.putString("Weight$i", java.lang.Double.toString(readings[i]))
            editor.putString("readDates$i", ddmmFormat.format(readDates[i]))
        }
        editor.commit()
    }
}