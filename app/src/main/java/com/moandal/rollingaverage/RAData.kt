package com.moandal.rollingaverage

import android.content.Context
import androidx.preference.PreferenceManager
import java.util.*

class RAData internal constructor(
    var rollingAverage: Double, // number of readings to average over
    var rollingNumber: Int, // number of decimal places for rounding of rolling average
    var decimalPlaces: Int, // number of readings in history to display
    var numberToDisplay: Int,
    var readings: DoubleArray,
    var rollingAvs: DoubleArray,
    var readDates: Array<Date?>
) {

    private val arraySize = Utils.arraySize

    fun calcAvs() {
        val multiplier = Math.pow(10.0, decimalPlaces.toDouble())
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
            readDates[i] = Utils.convertStringToDate(sp.getString("readDates$i", "0"))
        }
    }
}