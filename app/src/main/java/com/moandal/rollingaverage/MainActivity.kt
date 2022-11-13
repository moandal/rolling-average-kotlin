package com.moandal.rollingaverage

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.moandal.rollingaverage.Rad.arraySize
import com.moandal.rollingaverage.Rad.numberToDisplay
import com.moandal.rollingaverage.Rad.readDates
import com.moandal.rollingaverage.Rad.readings
import com.moandal.rollingaverage.Rad.rollingAverage
import com.moandal.rollingaverage.Rad.rollingAvs
import com.moandal.rollingaverage.Rad.rollingNumber
import org.w3c.dom.Text
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStream
import java.io.OutputStreamWriter
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

//Todo Keep track of more than one average
//Todo Average blood pressure
class MainActivity : AppCompatActivity() {

    private val createFile = 1
    private val loadFile = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Rad.loadData(this)
        displayData()
    }

    private fun displayData() {
        Rad.calcAvs()
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
            R.id.load_data -> {
                val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                    addCategory(Intent.CATEGORY_OPENABLE)
                    type = "text/plain"
                }

                startActivityForResult(intent, loadFile)
                return true
            }
            R.id.save_data -> {
                val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
                    addCategory(Intent.CATEGORY_OPENABLE)
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TITLE, "rolling_average.txt")
                }

                startActivityForResult(intent, createFile)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == createFile) {
            when (resultCode) {
                RESULT_OK -> if (data != null
                    && data.data != null
                ) {
                    writeInFile(data.data!!)
                }
                RESULT_CANCELED -> {}
            }
        }
        else if (requestCode == loadFile) {
            when (resultCode) {
                RESULT_OK -> if (data != null
                    && data.data != null
                ) {
                    readInFile(data.data!!)
                    displayData()
                }
                RESULT_CANCELED -> {}
            }
        }
    }

    private fun writeInFile(uri: Uri) {
        val outputStream: OutputStream?
        try {
            outputStream = contentResolver.openOutputStream(uri)
            val ddmmFormat = SimpleDateFormat("dd/MM/yyyy")
            val writer = BufferedWriter(OutputStreamWriter(outputStream))
            for (i in 0 until Rad.arraySize) {
                writer.write("${java.lang.Double.toString(readings[i])}, ${ddmmFormat.format(readDates[i])}")
                writer.newLine()
            }
            writer.flush()
            writer.close()
            Rad.showMessage("Save data", "Data saved", this)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun readInFile(uri: Uri) {
        val inputStream: InputStream?
        try {
            inputStream = contentResolver.openInputStream(uri)
            val reader = BufferedReader(InputStreamReader(inputStream))
            var currentLine: String

            for (i in 0 until arraySize) {
                currentLine = reader.readLine()
                var dataItems: List<String> = currentLine.split(",").map {it.trim()}

                readings[i] = java.lang.Double.valueOf(dataItems[0])
                readDates[i] = Rad.convertStringToDate(dataItems[1])
            }
            reader.close()
            Rad.showMessage("Load data", "Data loaded", this)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    override fun onPause() {
        super.onPause()
        Rad.saveData(this, readings, readDates)
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
        Rad.saveData(this, readings, readDates)
    }

}