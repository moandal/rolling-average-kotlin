package com.moandal.rollingaverage

import androidx.appcompat.app.AppCompatActivity
import android.widget.EditText
import android.os.Bundle
import android.widget.LinearLayout
import android.text.InputType
import android.view.View
import com.moandal.rollingaverage.Rad.arraySize
import com.moandal.rollingaverage.Rad.numberToDisplay
import com.moandal.rollingaverage.Rad.readDates
import com.moandal.rollingaverage.Rad.readings
import com.moandal.rollingaverage.Rad.rollingAverage
import java.text.DateFormat
import java.util.*

class EditActivity : AppCompatActivity() {
    private var df = DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault())
    private var textEdRead = arrayOfNulls<EditText>(arraySize)
    private var textEdDate = arrayOfNulls<EditText>(arraySize)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)
        setupActionBar()
        Rad.loadData(this)
        displayData()
    }

    private fun setupActionBar() {
        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onPause() {
        super.onPause()
        Rad.saveData(this)
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
            textEdRead[i]!!.setText(readings[i].toString())
            textEdRead[i]!!.inputType =
                InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
            textEdRead[i]!!.id = i
            linLayReading.addView(textEdRead[i])
            textEdDate[i] = EditText(this)
            textEdDate[i]!!.layoutParams = linLayDateparams
            textEdDate[i]!!.setText(df.format(readDates[i]!!))
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
        val defaultDate = Rad.convertStringToDate("01/01/1900")
        rollingAverage = 0.0
        var duffDates = false
        for (i in 0 until numberToDisplay) {
            editText = linLayReading.findViewById(i)
            textValue = editText.text.toString()
            inputValue = textValue.toDouble()
            readings[i] = inputValue
            editText = linLayDate.findViewById(i)
            textValue = editText.text.toString()
            inputDate = Rad.validateStringToDate(textValue)
            if (inputDate == defaultDate) {
                duffDates = true
                editText.setText(df.format(readDates[i]!!))
            } else {
                readDates[i] = inputDate
            }
        }
        if (duffDates)
            Rad.showMessage("Invalid input","Invalid date(s) ignored",this)
        else
            Rad.showMessage("Input accepted", "Data updated", this)
        Rad.calcAvs()
        Rad.saveData(this)
    }
}