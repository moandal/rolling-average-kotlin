package com.moandal.rollingaverage

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.preference.EditTextPreference
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.moandal.rollingaverage.Rad.numberToDisplay
import com.moandal.rollingaverage.Rad.rollingNumber

class BPSettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings_frag, SettingsFragment())
                .commit()
        }
        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
    }

    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.bp_preference, rootKey)
            val bpRollingNumber =
                preferenceManager.findPreference<EditTextPreference>("bp_rolling_number")
            val bpNumberToDisplay =
                preferenceManager.findPreference<EditTextPreference>("bp_number_to_display")

            bpRollingNumber!!.onPreferenceChangeListener =
                Preference.OnPreferenceChangeListener { preference, newValue ->
                    val `val` = newValue.toString().toInt()
                    if (`val` < 2 || `val` > 100) {
                        Rad.showMessage(
                            "Invalid input",
                            "Value must be between 2 and 100",
                            activity
                        )
                        false
                    } else {
                        true
                    }
                }
            bpNumberToDisplay!!.onPreferenceChangeListener =
                Preference.OnPreferenceChangeListener { preference, newValue ->
                    val `val` = newValue.toString().toInt()
                    if (`val` < 1 || `val` > 100) {
                        Rad.showMessage(
                            "Invalid input",
                            "Value must be between 1 and 100",
                            activity
                        )
                        false
                    } else {
                        true
                    }
                }

        }
    }
}