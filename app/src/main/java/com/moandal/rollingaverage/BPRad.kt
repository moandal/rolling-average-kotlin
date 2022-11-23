package com.moandal.rollingaverage

import android.app.Activity
import android.content.Context
import androidx.appcompat.app.AlertDialog
import androidx.preference.PreferenceManager
import com.moandal.rollingaverage.Rad.arraySize
import com.moandal.rollingaverage.Rad.convertStringToDate
import com.moandal.rollingaverage.Rad.numberToDisplay
import com.moandal.rollingaverage.Rad.readDates
import com.moandal.rollingaverage.Rad.readings
import com.moandal.rollingaverage.Rad.rollingAverage
import com.moandal.rollingaverage.Rad.rollingAvs
import com.moandal.rollingaverage.Rad.rollingNumber
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.pow
import kotlin.math.roundToInt

object BPRad {
    var BProllingAverage1 = 0.0
    var BProllingAverage2 = 0.0
    var BProllingNumber = 0 // number of readings to average over
    var BPnumberToDisplay = 0 // number of readings in history to display
    var BPreadings1 = DoubleArray(arraySize)
    var BPreadings2 = DoubleArray(arraySize)
    var BProllingAvs1 = DoubleArray(arraySize)
    var BProllingAvs2 = DoubleArray(arraySize)
    var BPreadDates = arrayOfNulls<Date>(arraySize)

    fun BPcalcAvs() {
        val startIndex = arraySize - BProllingNumber
        for (i in startIndex downTo 0) {
            BProllingAverage1 = 0.0
            BProllingAverage2 = 0.0
            for (j in i until i + BProllingNumber) {
                BProllingAverage1 = BProllingAverage1 + BPreadings1[j]
                BProllingAverage2 = BProllingAverage2 + BPreadings2[j]
            }
            BProllingAverage1 = (BProllingAverage1 / BProllingNumber).roundToInt().toDouble()
            BProllingAverage2 = (BProllingAverage2 / BProllingNumber).roundToInt().toDouble()
            BProllingAvs1[i] = BProllingAverage1
            BProllingAvs2[i] = BProllingAverage2
        }
    }

    fun BPloadData(context: Context) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        BProllingNumber = preferences.getString("BProlling_number", "7")!!.toInt()
        BPnumberToDisplay = preferences.getString("BPnumber_to_display", "7")!!.toInt()
        val sp = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        for (i in 0 until arraySize) {
            BPreadings1[i] = java.lang.Double.valueOf(sp.getString("BP1$i", "0")!!)
            BPreadings2[i] = java.lang.Double.valueOf(sp.getString("BP2$i", "0")!!)
            BPreadDates[i] = convertStringToDate(sp.getString("BPDates$i", "0"))
        }
    }

    fun BPsaveData(context: Context) {
        val ddmmFormat = SimpleDateFormat("dd/MM/yyyy")
        val sp = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val editor = sp.edit()
        for (i in 0 until arraySize) {
            editor.putString("BP1$i", BPreadings1[i].toString())
            editor.putString("BP2$i", BPreadings2[i].toString())
            editor.putString("BPDates$i", ddmmFormat.format(BPreadDates[i]!!))
        }
        editor.apply()
    }

}