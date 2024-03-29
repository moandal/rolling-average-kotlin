package com.moandal.rollingaverage

import android.app.Activity
import android.content.Context
import androidx.appcompat.app.AlertDialog
import androidx.preference.PreferenceManager
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.pow
import kotlin.math.roundToInt

object Rad {
    const val arraySize = 100
    var dataSetNum = 1 // dataset number (1-5)
    var rollingAverage = 0.0
    var rollingNumber = 0 // number of readings to average over
    var decimalPlaces = 0 // number of decimal places for rounding of rolling average
    var numberToDisplay = 0 // number of readings in history to display
    var dataSetName = ""
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
            rollingAverage = (rollingAverage / rollingNumber * multiplier).roundToInt().toDouble()
            rollingAverage = rollingAverage / multiplier
            rollingAvs[i] = rollingAverage
        }
    }

    fun loadData(context: Context) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        rollingNumber = preferences.getString("rolling_number", "7")!!.toInt()
        decimalPlaces = preferences.getString("decimal_places", "2")!!.toInt()
        numberToDisplay = preferences.getString("number_to_display", "7")!!.toInt()
        val sp = context.getSharedPreferences("MyPrefs$dataSetNum", Context.MODE_PRIVATE)
        dataSetName = sp.getString("dataSetName","")!!
        for (i in 0 until arraySize) {
            readings[i] = sp.getString("Weight$i", "0")!!.toDouble()
            readDates[i] = convertStringToDate(sp.getString("readDates$i", "0"))
        }
    }

    fun saveData(context: Context) {
        val ddmmFormat = SimpleDateFormat("dd/MM/yyyy")
        val sp = context.getSharedPreferences("MyPrefs$dataSetNum", Context.MODE_PRIVATE)
        var editor = sp.edit()
        editor.putString("dataSetName", dataSetName)
        for (i in 0 until arraySize) {
            editor.putString("Weight$i", readings[i].toString())
            editor.putString("readDates$i", ddmmFormat.format(readDates[i]!!))
        }
        editor.apply()
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
            formattedDate = sdf.parse(dateString!!)!!
        } catch (e: ParseException) {
            formattedDate = sdf.parse("01/01/1900")!!
        }
        return formattedDate
    }

    fun validateStringToDate(dateString: String?): Date {
        val df = DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault())
        df.isLenient = false
        var formattedDate = Date()
        try {
            formattedDate = df.parse(dateString!!)!!
        } catch (e: ParseException) {
            formattedDate = df.parse("01/01/1900")!!
        }
        return formattedDate
    }

}