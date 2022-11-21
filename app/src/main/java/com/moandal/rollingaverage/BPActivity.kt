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
        BPRad.BPloadData(this)
        BPdisplayData()
    }

    override fun onPause() {
        super.onPause()
        BPRad.BPsaveData(this)
    }

    private fun BPdisplayData() {
        BPRad.BPcalcAvs()
        val df = DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault())
        var BPHist = BPRad.BPreadings1[0].toString() + "/" + BPRad.BPreadings2[0].toString()
        var BPAvg = BPRad.BProllingAvs1[0].toString() + "/" + BPRad.BProllingAvs2[0].toString()
        var BPDate = df.format(BPRad.BPreadDates[0]!!)
        val textBPHist = findViewById<TextView>(R.id.textBPHist)
        val textBPAvg = findViewById<TextView>(R.id.textBPAvg)
        val textBPDate = findViewById<TextView>(R.id.textBPDate)
        val textBPAverage = findViewById<TextView>(R.id.textBPAverage)
        for (i in 1 until BPRad.BPnumberToDisplay) {
            BPHist += """
                
                ${BPRad.BPreadings1[i]} + "/" + ${BPRad.BPreadings2[i]}
                """.trimIndent()
            BPAvg += """
                
                ${BPRad.BProllingAvs1[i]} + "/" + ${BPRad.BProllingAvs2[i]}
                """.trimIndent()
            BPDate += """
                
                ${df.format(BPRad.BPreadDates[i]!!)}
                """.trimIndent()
            if (i == BPRad.BProllingNumber - 1) {
                BPHist += "\n---"
                BPAvg += "\n---"
                BPDate += "\n---"
            }
        }
        textBPHist.text = BPHist
        textBPAvg.text = BPAvg
        textBPDate.text = BPDate
        textBPAverage.text = BPRad.BProllingAverage1.toString() + "/" + BPRad.BProllingAverage2.toString()
    }

    // Performed when the Enter button is clicked
    fun BPshowAverage(view: View) {
        val editBP1 = findViewById<View>(R.id.editBP1) as EditText
        val editBP2 = findViewById<View>(R.id.editBP2) as EditText
        val stringBP1 = editBP1.text.toString()
        val stringBP2 = editBP2.text.toString()
        val inputValueBP1 = java.lang.Double.valueOf(stringBP1)
        val inputValueBP2 = java.lang.Double.valueOf(stringBP2)
        BPRad.BProllingAverage1 = 0.0
        BPRad.BProllingAverage2 = 0.0
        var init = true
        for (i in 0 until Rad.arraySize) {
            if ((compareValues(BPRad.BPreadings1[i], 0.0) != 0) && (compareValues(BPRad.BPreadings2[i], 0.0) != 0)) {
                init = false
            }
        }
        if (init) {
            Arrays.fill(BPRad.BPreadings1, inputValueBP1)
            Arrays.fill(BPRad.BPreadings1, inputValueBP2)
            Arrays.fill(BPRad.BProllingAvs1, inputValueBP1)
            Arrays.fill(BPRad.BProllingAvs2, inputValueBP2)
            Arrays.fill(BPRad.BPreadDates, Date())
            BPRad.BProllingAverage1 = inputValueBP1
            BPRad.BProllingAverage2 = inputValueBP2
        } else {
            for (i in Rad.arraySize - 1 downTo 1) {
                BPRad.BPreadings1[i] = BPRad.BPreadings1[i - 1]
                BPRad.BPreadings2[i] = BPRad.BPreadings2[i - 1]
                BPRad.BPreadDates[i] = BPRad.BPreadDates[i - 1]
            }
            BPRad.BPreadings1[0] = inputValueBP1
            BPRad.BPreadings2[0] = inputValueBP2
            BPRad.BPreadDates[0] = Date()
        }
        BPdisplayData()
        BPRad.BPsaveData(this)
    }
}