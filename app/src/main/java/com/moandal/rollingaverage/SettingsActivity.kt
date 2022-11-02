package com.moandal.rollingaverage

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.preference.EditTextPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat

class SettingsActivity : AppCompatActivity() {
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
            setPreferencesFromResource(R.xml.root_preference, rootKey)
            val rolling_number =
                preferenceManager.findPreference<EditTextPreference>("rolling_number")
            val number_to_display =
                preferenceManager.findPreference<EditTextPreference>("number_to_display")
            val decimal_places =
                preferenceManager.findPreference<EditTextPreference>("decimal_places")
            rolling_number!!.onPreferenceChangeListener =
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
            number_to_display!!.onPreferenceChangeListener =
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
            decimal_places!!.onPreferenceChangeListener =
                Preference.OnPreferenceChangeListener { preference, newValue ->
                    val `val` = newValue.toString().toInt()
                    if (`val` < 1 || `val` > 5) {
                        Rad.showMessage(
                            "Invalid input",
                            "Value must be between 1 and 5",
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