package com.moandal.rollingaverage

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import com.moandal.rollingaverage.Rad.arraySize
import com.moandal.rollingaverage.Rad.dataSetName
import com.moandal.rollingaverage.Rad.dataSetNum
import com.moandal.rollingaverage.Rad.numberToDisplay
import com.moandal.rollingaverage.Rad.readDates
import com.moandal.rollingaverage.Rad.readings
import com.moandal.rollingaverage.Rad.rollingAverage
import com.moandal.rollingaverage.Rad.rollingAvs
import com.moandal.rollingaverage.Rad.rollingNumber
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

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val preferences = PreferenceManager.getDefaultSharedPreferences(this)
        dataSetNum = preferences.getString("dataset_num", "1")!!.toInt()
        Rad.loadData(this)
        displayData()
    }

    private fun displayDataSetName() {
        val textDataSetNum = findViewById<TextView>(R.id.textDataSetNum)
        if (dataSetName == "") {
            textDataSetNum.text = "Dataset " + dataSetNum.toString()
        }
        else {
            textDataSetNum.text = dataSetName
        }
    }
    private fun displayData() {
        val buttonLeft = findViewById<Button>(R.id.buttonLeft)
        val buttonRight = findViewById<Button>(R.id.buttonRight)

        displayDataSetName()

        if (dataSetNum == 1) {
            buttonLeft.isEnabled = false
        }
        if (dataSetNum == 5) {
            buttonRight.isEnabled = false
        }

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
            R.id.edit_name -> {
                val dialog = Dialog(this)
                dialog.setContentView(R.layout.dialog_dataset_name)

                val et_datasetname = dialog.findViewById<EditText>(R.id.et_datasetname)
                et_datasetname.setText(dataSetName)

                val btn_update = dialog.findViewById(R.id.btn_update) as Button
                btn_update.setOnClickListener {
                    dataSetName = et_datasetname.text.toString()
                    displayDataSetName()
                    dialog.dismiss()
                }

                dialog.show()
                return true
            }
            R.id.load_data -> {
                val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                    addCategory(Intent.CATEGORY_OPENABLE)
                    type = "text/plain"
                }

                getLoadResult.launch(intent)
                return true
            }
            R.id.save_data -> {
                val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
                    addCategory(Intent.CATEGORY_OPENABLE)
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TITLE, "rolling_average.txt")
                }

                getSaveResult.launch(intent)
                return true
            }
            R.id.switch_data -> {
                val intentBPActivity = Intent(this, BPActivity::class.java)
                startActivity(intentBPActivity)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private val getSaveResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val data: Intent? = result.data
            if ((result.resultCode == Activity.RESULT_OK) && (data != null) && (data.data != null)) {
                writeOutData(data.data!!)
            }
        }

    private fun writeOutData(uri: Uri) {
        val outputStream: OutputStream?
        try {
            outputStream = contentResolver.openOutputStream(uri)
            val ddmmFormat = SimpleDateFormat("dd/MM/yyyy")
            val writer = BufferedWriter(OutputStreamWriter(outputStream))
            for (i in 0 until arraySize) {
                writer.write("${readings[i]}, ${ddmmFormat.format(readDates[i]!!)}")
                writer.newLine()
            }
            writer.flush()
            writer.close()
            Rad.showMessage("Save data", "Data saved", this)
        } catch (e: IOException) {
            Rad.showMessage("Save data", "Data save failed - unable to save file", this)
        }
    }

    private val getLoadResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val data: Intent? = result.data
            if ((result.resultCode == Activity.RESULT_OK) && (data != null) && (data.data != null)) {
                readInData(data.data!!)
                displayData()
            }
        }

    private fun readInData(uri: Uri) {
        val inputStream: InputStream?
        val errorDate = Rad.convertStringToDate("01/01/1900")
        try {
            inputStream = contentResolver.openInputStream(uri)
            val reader = BufferedReader(InputStreamReader(inputStream))
            var currentLine: String

            for (i in 0 until arraySize) {
                currentLine = reader.readLine()
                val dataItems: List<String> = currentLine.split(",").map { it.trim() }

                try {
                    readings[i] = dataItems[0].toDouble()
                } catch (f: NumberFormatException) {
                    Rad.showMessage("Load data", "Data load failed - invalid data", this)
                    return
                }

                readDates[i] = Rad.convertStringToDate(dataItems[1])
                if (readDates[i] == errorDate) {
                    Rad.showMessage("Load data", "Data load failed - invalid data", this)
                    return
                }
            }
            reader.close()
            Rad.showMessage("Load data", "Data loaded", this)
        } catch (e: IOException) {
            Rad.showMessage("Load data", "Data load failed - unable to load file", this)
        }
    }

    override fun onPause() {
        super.onPause()
        Rad.saveData(this)
    }

    // Called when the user clicks the Enter button
    fun showAverage(view: View) {
        val editWeight = findViewById<View>(R.id.editWeight) as EditText
        val message = editWeight.text.toString()
        val inputValue = message.toDouble()
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
        Rad.saveData(this)
    }

    private fun saveDataSetNum () {
        val preferences = PreferenceManager.getDefaultSharedPreferences(this)
        val editor = preferences.edit()
        editor.putString("dataset_num", dataSetNum.toString())
        editor.apply()
    }

    fun dataSetLeft(view: View) {
        val buttonLeft = findViewById<Button>(R.id.buttonLeft)
        val buttonRight = findViewById<Button>(R.id.buttonRight)
        Rad.saveData(this)
        dataSetNum = dataSetNum - 1
        saveDataSetNum()
        if (dataSetNum == 1) {
            buttonLeft.isEnabled = false
        }
        if (dataSetNum < 5) {
            buttonRight.isEnabled = true
        }
        Rad.loadData(this)
        displayData()
    }

    fun dataSetRight(view: View) {
        val buttonLeft = findViewById<Button>(R.id.buttonLeft)
        val buttonRight = findViewById<Button>(R.id.buttonRight)
        Rad.saveData(this)
        dataSetNum = dataSetNum + 1
        saveDataSetNum()
        if (dataSetNum > 1) {
            buttonLeft.isEnabled = true
        }
        if (dataSetNum == 5) {
            buttonRight.isEnabled = false
        }
        Rad.loadData(this)
        displayData()
    }

}