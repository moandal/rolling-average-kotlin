package com.moandal.rollingaverage

import android.app.Activity
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
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import java.io.*
import java.text.DateFormat
import java.text.SimpleDateFormat
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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.bpmenu, menu)
        return true
    }

    //Take appropriate action depending on which Menu item is chosen
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_bp_erase -> {
                val dialogClickListener = DialogInterface.OnClickListener { dialog, which ->
                    when (which) {
                        DialogInterface.BUTTON_POSITIVE -> {
                            BPRad.bpRollingAverage1 = 0.0
                            BPRad.bpRollingAverage2 = 0.0
                            Arrays.fill(BPRad.bpReadings1, 0)
                            Arrays.fill(BPRad.bpReadings2, 0)
                            Arrays.fill(BPRad.bpRollingAvs1, 0.0)
                            Arrays.fill(BPRad.bpRollingAvs2, 0.0)
                            Arrays.fill(BPRad.bpReadDates, Date())
                            bpDisplayData()
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
            R.id.menu_bp_settings -> {
                val intentSettings = Intent(this, BPSettingsActivity::class.java)
                startActivity(intentSettings)
                return true
            }
            R.id.menu_bp_edit_data -> {
                val intentEditBP = Intent(this, EditBPActivity::class.java)
                startActivity(intentEditBP)
                return true
            }
            R.id.menu_bp_load_data -> {
                val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                    addCategory(Intent.CATEGORY_OPENABLE)
                    type = "text/plain"
                }

                getLoadBPResult.launch(intent)

                return true
            }
            R.id.menu_bp_save_data -> {
                val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
                    addCategory(Intent.CATEGORY_OPENABLE)
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TITLE, "rolling_average_bp.txt")
                }

                getSaveBPResult.launch(intent)

                return true
            }
            R.id.menu_bp_switch_data -> {
                val intentMainActivity = Intent(this, MainActivity::class.java)
                startActivity(intentMainActivity)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
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

    private val getLoadBPResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val data: Intent? = result.data
            if (result.resultCode == Activity.RESULT_OK && data != null && data.data != null) {
                bpReadInData(data.data!!)
                bpDisplayData()
            }
        }

    private fun bpReadInData(uri: Uri) {
        val inputStream: InputStream?
        val errorDate = Rad.convertStringToDate("01/01/1900")
        try {
            inputStream = contentResolver.openInputStream(uri)
            val reader = BufferedReader(InputStreamReader(inputStream))
            var currentLine: String

            for (i in 0 until Rad.arraySize) {
                currentLine = reader.readLine()
                val dataItems: List<String> = currentLine.split(",").map { it.trim() }

                try {
                    BPRad.bpReadings1[i] = dataItems[0].toInt()
                } catch (f: NumberFormatException) {
                    Rad.showMessage("Load data", "Data load failed - invalid data", this)
                    return
                }

                try {
                    BPRad.bpReadings2[i] = dataItems[1].toInt()
                } catch (f: NumberFormatException) {
                    Rad.showMessage("Load data", "Data load failed - invalid data", this)
                    return
                }

                BPRad.bpReadDates[i] = Rad.convertStringToDate(dataItems[2])
                if (BPRad.bpReadDates[i] == errorDate) {
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

    private val getSaveBPResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val data: Intent? = result.data
            if (result.resultCode == Activity.RESULT_OK && data != null && data.data != null) {
                bpWriteOutData(data.data!!)
            }
        }

    private fun bpWriteOutData(uri: Uri) {
        val outputStream: OutputStream?
        try {
            outputStream = contentResolver.openOutputStream(uri)
            val ddmmFormat = SimpleDateFormat("dd/MM/yyyy")
            val writer = BufferedWriter(OutputStreamWriter(outputStream))
            for (i in 0 until Rad.arraySize) {
                writer.write("${BPRad.bpReadings1[i]}, ${BPRad.bpReadings2[i]}, ${ddmmFormat.format(BPRad.bpReadDates[i]!!)}")
                writer.newLine()
            }
            writer.flush()
            writer.close()
            Rad.showMessage("Save data", "Data saved", this)
        } catch (e: IOException) {
            Rad.showMessage("Save data", "Data save failed - unable to save file", this)
        }
    }

}