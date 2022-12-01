package com.moandal.rollingaverage

import androidx.appcompat.app.AppCompatActivity
import android.widget.EditText
import android.os.Bundle
import android.widget.LinearLayout
import android.text.InputType
import android.view.View
import android.widget.TextView
import com.moandal.rollingaverage.BPRad.bpNumberToDisplay
import com.moandal.rollingaverage.BPRad.bpReadDates
import com.moandal.rollingaverage.BPRad.bpReadings1
import com.moandal.rollingaverage.BPRad.bpReadings2
import com.moandal.rollingaverage.BPRad.bpRollingAverage1
import com.moandal.rollingaverage.BPRad.bpRollingAverage2
import com.moandal.rollingaverage.Rad.arraySize
import java.text.DateFormat
import java.util.*

class EditBPActivity : AppCompatActivity() {
    private var df = DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault())
    private var textEdBP1 = arrayOfNulls<EditText>(arraySize)
    private var textSlash = arrayOfNulls<TextView>(arraySize)
    private var textEdBP2 = arrayOfNulls<EditText>(arraySize)
    private var textEdBPDate = arrayOfNulls<EditText>(arraySize)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bpedit)
        setupActionBar()
        BPRad.bpLoadData(this)
        displayBPData()
    }

    private fun setupActionBar() {
        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onPause() {
        super.onPause()
        BPRad.bpSaveData(this)
    }

    private fun displayBPData() {
        val linLayBPReading1 = findViewById<LinearLayout>(R.id.linLaybpreading1)
        val linLayBPReading1params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        val linLaySlash = findViewById<LinearLayout>(R.id.linLayslash)
        val linLaySlashparams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT)

        val linLayBPReading2 = findViewById<LinearLayout>(R.id.linLaybpreading2)
        val linLayBPReading2params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        val linLayBPDate = findViewById<LinearLayout>(R.id.linLaybpdate)
        val linLayDateparams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        for (i in 0 until bpNumberToDisplay) {
            textEdBP1[i] = EditText(this)
            textSlash[i] = EditText(this)
            textEdBP2[i] = EditText(this)
            textEdBPDate[i] = EditText(this)

            textEdBP1[i]!!.layoutParams = linLayBPReading1params
            textEdBP1[i]!!.setText(bpReadings1[i].toString())
            textEdBP1[i]!!.inputType = InputType.TYPE_CLASS_NUMBER
            textEdBP1[i]!!.id = i
            linLayBPReading1.addView(textEdBP1[i])

            textSlash[i]!!.layoutParams = linLaySlashparams
            textSlash[i]!!.text = "/"
            textSlash[i]!!.inputType = InputType.TYPE_NULL
            textSlash[i]!!.isEnabled = false
            textSlash[i]!!.id = i
            linLaySlash.addView(textSlash[i])

            textEdBP2[i]!!.layoutParams = linLayBPReading2params
            textEdBP2[i]!!.setText(bpReadings2[i].toString())
            textEdBP2[i]!!.inputType = InputType.TYPE_CLASS_NUMBER
            textEdBP2[i]!!.id = i
            linLayBPReading2.addView(textEdBP2[i])

            textEdBPDate[i]!!.layoutParams = linLayDateparams
            textEdBPDate[i]!!.setText(df.format(bpReadDates[i]!!))
            textEdBPDate[i]!!.inputType =
                InputType.TYPE_CLASS_DATETIME or InputType.TYPE_DATETIME_VARIATION_DATE
            textEdBPDate[i]!!.id = i
            linLayBPDate.addView(textEdBPDate[i])
        }
    }

    // Performed when the Update button is clicked
    fun updateBPReadings(view: View?) {
        val linLayBPReading1 = findViewById<LinearLayout>(R.id.linLaybpreading1)
        val linLayBPReading2 = findViewById<LinearLayout>(R.id.linLaybpreading2)
        val linLayBPDate = findViewById<LinearLayout>(R.id.linLaybpdate)
        var textValue: String
        var editText: EditText
        var inputValue: Int
        var inputDate: Date
        val defaultDate = Rad.convertStringToDate("01/01/1900")
        bpRollingAverage1 = 0
        bpRollingAverage2 = 0
        var duffDates = false
        for (i in 0 until bpNumberToDisplay) {
            editText = linLayBPReading1.findViewById(i)
            textValue = editText.text.toString()
            inputValue = textValue.toInt()
            bpReadings1[i] = inputValue

            editText = linLayBPReading2.findViewById(i)
            textValue = editText.text.toString()
            inputValue = textValue.toInt()
            bpReadings2[i] = inputValue

            editText = linLayBPDate.findViewById(i)
            textValue = editText.text.toString()
            inputDate = Rad.validateStringToDate(textValue)
            if (inputDate == defaultDate) {
                duffDates = true
                editText.setText(df.format(bpReadDates[i]!!))
            } else {
                bpReadDates[i] = inputDate
            }
        }
        if (duffDates)
            Rad.showMessage("Invalid input","Invalid date(s) ignored",this)
        else
            Rad.showMessage("Input accepted", "Data updated", this)
        BPRad.bpCalcAvs()
        BPRad.bpSaveData(this)
    }
}