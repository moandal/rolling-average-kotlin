package com.moandal.rollingaverage

import androidx.appcompat.app.AppCompatActivity
import android.widget.EditText
import android.os.Bundle
import android.widget.LinearLayout
import android.text.InputType
import android.view.View
import java.text.DateFormat
import java.util.*

class EditActivity : AppCompatActivity() {
    var rollingAverage = 0.0
    var rollingNumber = 0
    var decimalPlaces = 0
    var numberToDisplay // number of readings in history to display
            = 0
    var arraySize = Utils.arraySize
    var readings = DoubleArray(arraySize)
    var rollingAvs = DoubleArray(arraySize)
    var readDates = arrayOfNulls<Date>(arraySize)
    var df = DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault())
    var textEdRead = arrayOfNulls<EditText>(arraySize)
    var textEdDate = arrayOfNulls<EditText>(arraySize)
    var raData: RAData? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)
        setupActionBar()
        raData = RAData(
            rollingAverage,
            rollingNumber,
            decimalPlaces,
            numberToDisplay,
            readings,
            rollingAvs,
            readDates
        )
        raData!!.loadData(this)
        rollingNumber = raData!!.rollingNumber
        decimalPlaces = raData!!.decimalPlaces
        numberToDisplay = raData!!.numberToDisplay
        readings = raData!!.readings
        readDates = raData!!.readDates
        displayData()
    }

    private fun setupActionBar() {
        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onPause() {
        super.onPause()
        Utils.saveData(this, readings, readDates)
    }

    override fun onResume() {
        super.onResume()
    }

    private fun displayData() {
        val linLayReading = findViewById<LinearLayout>(R.id.linLayReading)
        val linLayReadingparams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        val linLayDate = findViewById<LinearLayout>(R.id.linLayDate)
        val linLayDateparams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        for (i in 0 until numberToDisplay) {
            textEdRead[i] = EditText(this)
            textEdRead[i]!!.layoutParams = linLayReadingparams
            textEdRead[i]!!.setText(java.lang.Double.toString(readings[i]))
            textEdRead[i]!!.inputType =
                InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
            textEdRead[i]!!.id = i
            linLayReading.addView(textEdRead[i])
            textEdDate[i] = EditText(this)
            textEdDate[i]!!.layoutParams = linLayDateparams
            textEdDate[i]!!.setText(df.format(readDates[i]))
            textEdDate[i]!!.inputType =
                InputType.TYPE_CLASS_DATETIME or InputType.TYPE_DATETIME_VARIATION_DATE
            textEdDate[i]!!.id = i
            linLayDate.addView(textEdDate[i])
        }
    }

    // Performed when the Update button is clicked
    fun updateReadings(view: View?) {
        val linLayReading = findViewById<LinearLayout>(R.id.linLayReading)
        val linLayDate = findViewById<LinearLayout>(R.id.linLayDate)
        var textValue: String
        var editText: EditText
        var inputValue: Double
        var inputDate: Date
        val defaultDate = Utils.convertStringToDate("01/01/1900")
        rollingAverage = 0.0
        var duffDates = false
        for (i in 0 until numberToDisplay) {
            editText = linLayReading.findViewById(i)
            textValue = editText.text.toString()
            inputValue = java.lang.Double.valueOf(textValue)
            readings[i] = inputValue
            editText = linLayDate.findViewById(i)
            textValue = editText.text.toString()
            inputDate = Utils.validateStringToDate(textValue)
            if (inputDate == defaultDate) {
                duffDates = true
                editText.setText(df.format(readDates[i]))
            } else {
                readDates[i] = inputDate
            }
        }
        if (duffDates) Utils.showMessage(
            "Invalid input",
            "Invalid date(s) ignored",
            this
        ) else Utils.showMessage("Input accepted", "Data updated", this)
        val raData = RAData(
            rollingAverage,
            rollingNumber,
            decimalPlaces,
            numberToDisplay,
            readings,
            rollingAvs,
            readDates
        )
        raData.calcAvs()
        rollingAverage = raData.rollingAverage
        rollingAvs = raData.rollingAvs
        Utils.saveData(this, readings, readDates)
    }
}