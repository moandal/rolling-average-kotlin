package com.moandal.rollingaverage

import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.content.DialogInterface
import android.content.Intent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import java.text.DateFormat
import java.util.*

//Todo Option to export data to a file
//Todo Keep track of more than one average
//Todo Average blood pressure
class MainActivity : AppCompatActivity() {
    var rollingAverage = 0.0
    var rollingNumber = 0 // number of readings to average over
    var decimalPlaces = 0 // number of decimal places for rounding of rolling average
    var numberToDisplay = 0 // number of readings in history to display

    var arraySize = Utils.arraySize
    var readings = DoubleArray(arraySize)
    var rollingAvs = DoubleArray(arraySize)
    var readDates = arrayOfNulls<Date>(arraySize)
    var raData: RAData? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        raData = RAData(rollingAverage, rollingNumber, decimalPlaces, numberToDisplay, readings, rollingAvs, readDates)
        raData!!.loadData(this)
        rollingNumber = raData!!.rollingNumber
        decimalPlaces = raData!!.decimalPlaces
        numberToDisplay = raData!!.numberToDisplay
        readings = raData!!.readings
        readDates = raData!!.readDates
        displayData()
    }

    private fun displayData() {
        raData!!.calcAvs()
        rollingAverage = raData!!.rollingAverage
        rollingAvs = raData!!.rollingAvs
        val df = DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault())
        var hist1 = readings[0].toString()
        var hAv1 = rollingAvs[0].toString()
        var hDt1 = df.format(readDates[0]!!)
        val textHist1 = findViewById<TextView>(R.id.textHist1)
        val textHAv1 = findViewById<TextView>(R.id.textHAv1)
        val textHDt1 = findViewById<TextView>(R.id.textHDt1)
        val textAverage = findViewById<TextView>(R.id.textAverage)
        for (i in 1 until numberToDisplay) {
            hist1 += """
                
                ${readings[i]}
                """.trimIndent()
            hAv1 += """
                
                ${rollingAvs[i]}
                """.trimIndent()
            hDt1 += """
                
                ${df.format(readDates[i]!!)}
                """.trimIndent()
            if (i == rollingNumber - 1) {
                hist1 += "\n---"
                hAv1 += "\n---"
                hDt1 += "\n---"
            }
        }
        textHist1.text = hist1
        textHAv1.text = hAv1
        textHDt1.text = hDt1
        textAverage.text = rollingAverage.toString()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    //Take appropriate action depending on which Menu item is chosen
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_erase -> {
                val dialogClickListener = DialogInterface.OnClickListener { dialog, which ->
                    when (which) {
                        DialogInterface.BUTTON_POSITIVE -> {
                            rollingAverage = 0.0
                            Arrays.fill(readings, 0.0)
                            Arrays.fill(rollingAvs, 0.0)
                            Arrays.fill(readDates, Date())
                            displayData()
                        }
                        DialogInterface.BUTTON_NEGATIVE -> {}
                    }
                }
                val builder = AlertDialog.Builder(this)
                builder.setMessage("Are you sure?")
                    .setPositiveButton("Yes", dialogClickListener)
                    .setNegativeButton("No", dialogClickListener).show()
                return true
            }
            R.id.settings -> {
                val intentSettings = Intent(this, SettingsActivity::class.java)
                startActivity(intentSettings)
                return true
            }
            R.id.edit_data -> {
                val intentEdit = Intent(this, EditActivity::class.java)
                startActivity(intentEdit)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onPause() {
        super.onPause()
        Utils.saveData(this, readings, readDates)
    }

    // Called when the user clicks the Enter button
    fun showAverage(view: View) {
        val editWeight = findViewById<View>(R.id.editWeight) as EditText
        val message = editWeight.text.toString()
        val inputValue = java.lang.Double.valueOf(message)
        rollingAverage = 0.0
        var init = true
        for (i in 0 until arraySize) {
            if (compareValues(readings[i], 0.0) != 0) {
                init = false
            }
        }
        if (init) {
            Arrays.fill(readings, inputValue)
            Arrays.fill(rollingAvs, inputValue)
            Arrays.fill(readDates, Date())
            rollingAverage = inputValue
        } else {
            for (i in readings.size - 1 downTo 1) {
                readings[i] = readings[i - 1]
                readDates[i] = readDates[i - 1]
            }
            readings[0] = inputValue
            readDates[0] = Date()
        }
        displayData()
        Utils.saveData(this, readings, readDates)
    }

}