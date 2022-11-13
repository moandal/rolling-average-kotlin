package com.moandal.rollingaverage

import android.R.attr.path
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.provider.DocumentsContract
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.preference.PreferenceManager
import com.moandal.rollingaverage.Rad.arraySize
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.pow

object Rad {
    const val arraySize = 100
    var rollingAverage = 0.0
    var rollingNumber = 0 // number of readings to average over
    var decimalPlaces = 0 // number of decimal places for rounding of rolling average
    var numberToDisplay = 0 // number of readings in history to display
    var readings = DoubleArray(arraySize)
    var rollingAvs = DoubleArray(arraySize)
    var readDates = arrayOfNulls<Date>(arraySize)

    fun calcAvs() {
        val multiplier = 10.0.pow(decimalPlaces.toDouble())
        val startIndex = arraySize - rollingNumber
        for (i in startIndex downTo 0) {
            rollingAverage = 0.0
            for (j in i until i + rollingNumber) {
                rollingAverage = rollingAverage + readings[j]
            }
            rollingAverage = Math.round(rollingAverage / rollingNumber * multiplier).toDouble()
            rollingAverage = rollingAverage / multiplier
            rollingAvs[i] = rollingAverage
        }
    }

    fun loadData(context: Context) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        rollingNumber = preferences.getString("rolling_number", "7")!!.toInt()
        decimalPlaces = preferences.getString("decimal_places", "2")!!.toInt()
        numberToDisplay = preferences.getString("number_to_display", "7")!!.toInt()
        val sp = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        for (i in 0 until arraySize) {
            readings[i] = java.lang.Double.valueOf(sp.getString("Weight$i", "0")!!)
            readDates[i] = convertStringToDate(sp.getString("readDates$i", "0"))
        }
    }

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
            formattedDate = sdf.parse("01/01/1900")
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
            formattedDate = df.parse("01/01/1900")
        }
        return formattedDate
    }

    fun saveData(context: Context, readings: DoubleArray, readDates: Array<Date?>) {
        val ddmmFormat = SimpleDateFormat("dd/MM/yyyy")
        val sp = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val editor = sp.edit()
        for (i in 0 until arraySize) {
            editor.putString("Weight$i", readings[i].toString())
            editor.putString("readDates$i", ddmmFormat.format(readDates[i]!!))
        }
        editor.commit()
    }

}