package com.moandal.rollingaverage

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.text.DateFormat
import java.util.*


class BPActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bp)
        BPRad.bpLoadData(this)
        bpDisplayData()
    }

    override fun onPause() {
        super.onPause()
        BPRad.bpSaveData(this)
    }

    private fun bpDisplayData() {
        BPRad.bpCalcAvs()
        val df = DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault())
        var bpHist = BPRad.bpReadings1[0].toString() + "/" + BPRad.bpReadings2[0].toString()
        var bpAvg = BPRad.bpRollingAvs1[0].toString() + "/" + BPRad.bpRollingAvs2[0].toString()
        var bpDate = df.format(BPRad.bpReadDates[0]!!)
        val textBPHist = findViewById<TextView>(R.id.textBPHist)
        val textBPAvg = findViewById<TextView>(R.id.textBPAvg)
        val textBPDate = findViewById<TextView>(R.id.textBPDate)
        val textBPAverage = findViewById<TextView>(R.id.textBPAverage)
        for (i in 1 until BPRad.bpNumberToDisplay) {
            bpHist += """
                
                ${BPRad.bpReadings1[i]}/${BPRad.bpReadings2[i]}
                """.trimIndent()
            bpAvg += """
                
                ${BPRad.bpRollingAvs1[i]}/${BPRad.bpRollingAvs2[i]}
                """.trimIndent()
            bpDate += """
                
                ${df.format(BPRad.bpReadDates[i]!!)}
                """.trimIndent()
            if (i == BPRad.bpRollingNumber - 1) {
                bpHist += "\n---"
                bpAvg += "\n---"
                bpDate += "\n---"
            }
        }
        textBPHist.text = bpHist
        textBPAvg.text = bpAvg
        textBPDate.text = bpDate
        textBPAverage.text = BPRad.bpRollingAverage1.toString() + "/" + BPRad.bpRollingAverage2.toString()
    }

    // Performed when the Enter button is clicked
    fun bpShowAverage(view: View) {
        val editBP1 = findViewById<View>(R.id.editBP1) as EditText
        val editBP2 = findViewById<View>(R.id.editBP2) as EditText
        val stringBP1 = editBP1.text.toString()
        val stringBP2 = editBP2.text.toString()
        val inputValueBP1 = Integer.valueOf(stringBP1)
        val inputValueBP2 = Integer.valueOf(stringBP2)
        BPRad.bpRollingAverage1 = 0.0
        BPRad.bpRollingAverage2 = 0.0
        var init = true
        for (i in 0 until Rad.arraySize) {
            if ((compareValues(BPRad.bpReadings1[i], 0) != 0) || (compareValues(BPRad.bpReadings2[i], 0) != 0)) {
                init = false
            }
        }
        if (init) {
            Arrays.fill(BPRad.bpReadings1, inputValueBP1)
            Arrays.fill(BPRad.bpReadings2, inputValueBP2)
            Arrays.fill(BPRad.bpRollingAvs1, inputValueBP1.toDouble())
            Arrays.fill(BPRad.bpRollingAvs2, inputValueBP2.toDouble())
            Arrays.fill(BPRad.bpReadDates, Date())
            BPRad.bpRollingAverage1 = inputValueBP1.toDouble()
            BPRad.bpRollingAverage2 = inputValueBP2.toDouble()
        } else {
            for (i in Rad.arraySize - 1 downTo 1) {
                BPRad.bpReadings1[i] = BPRad.bpReadings1[i - 1]
                BPRad.bpReadings2[i] = BPRad.bpReadings2[i - 1]
                BPRad.bpReadDates[i] = BPRad.bpReadDates[i - 1]
            }
            BPRad.bpReadings1[0] = inputValueBP1
            BPRad.bpReadings2[0] = inputValueBP2
            BPRad.bpReadDates[0] = Date()
        }
        bpDisplayData()
        BPRad.bpSaveData(this)
    }
}