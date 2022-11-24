package com.moandal.rollingaverage

import android.content.Context
import androidx.preference.PreferenceManager
import com.moandal.rollingaverage.Rad.arraySize
import com.moandal.rollingaverage.Rad.convertStringToDate
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

object BPRad {
    var bpRollingAverage1 = 0.0
    var bpRollingAverage2 = 0.0
    var bpRollingNumber = 0 // number of readings to average over
    var bpNumberToDisplay = 0 // number of readings in history to display
    var bpReadings1 = IntArray(arraySize)
    var bpReadings2 = IntArray(arraySize)
    var bpRollingAvs1 = DoubleArray(arraySize)
    var bpRollingAvs2 = DoubleArray(arraySize)
    var bpReadDates = arrayOfNulls<Date>(arraySize)

    fun bpCalcAvs() {
        val startIndex = arraySize - bpRollingNumber
        for (i in startIndex downTo 0) {
            bpRollingAverage1 = 0.0
            bpRollingAverage2 = 0.0
            for (j in i until i + bpRollingNumber) {
                bpRollingAverage1 = bpRollingAverage1 + bpReadings1[j]
                bpRollingAverage2 = bpRollingAverage2 + bpReadings2[j]
            }
            bpRollingAverage1 = (bpRollingAverage1 / bpRollingNumber).roundToInt().toDouble()
            bpRollingAverage2 = (bpRollingAverage2 / bpRollingNumber).roundToInt().toDouble()
            bpRollingAvs1[i] = bpRollingAverage1
            bpRollingAvs2[i] = bpRollingAverage2
        }
    }

    fun bpLoadData(context: Context) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        bpRollingNumber = preferences.getString("BProlling_number", "7")!!.toInt()
        bpNumberToDisplay = preferences.getString("BPnumber_to_display", "7")!!.toInt()
        val sp = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        for (i in 0 until arraySize) {
            bpReadings1[i] = Integer.valueOf(sp.getString("BP1$i", "0")!!)
            bpReadings2[i] = Integer.valueOf(sp.getString("BP2$i", "0")!!)
            bpReadDates[i] = convertStringToDate(sp.getString("BPDates$i", "0"))
        }
    }

    fun bpSaveData(context: Context) {
        val ddmmFormat = SimpleDateFormat("dd/MM/yyyy")
        val sp = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val editor = sp.edit()
        for (i in 0 until arraySize) {
            editor.putString("BP1$i", bpReadings1[i].toString())
            editor.putString("BP2$i", bpReadings2[i].toString())
            editor.putString("BPDates$i", ddmmFormat.format(bpReadDates[i]!!))
        }
        editor.apply()
    }

}