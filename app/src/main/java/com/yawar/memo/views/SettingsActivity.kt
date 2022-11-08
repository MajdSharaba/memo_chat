package com.yawar.memo.views

import android.content.Intent
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.databinding.DataBindingUtil
import androidx.preference.ListPreference
import androidx.preference.Preference.SummaryProvider
import androidx.preference.PreferenceFragmentCompat
import com.yawar.memo.R
import com.yawar.memo.databinding.ActivityBlockedUsersBinding
import com.yawar.memo.databinding.SettingsActivityBinding
import com.yawar.memo.sessionManager.ClassSharedPreferences
import com.yawar.memo.utils.BaseApp
import java.util.*

class SettingsActivity : AppCompatActivity(), OnSharedPreferenceChangeListener,
    SummaryProvider<ListPreference> {
    lateinit var  binding: SettingsActivityBinding
    var classSharedPreferences: ClassSharedPreferences? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.settings_activity)

        supportActionBar!!.setTitle(R.string.appearance)
        classSharedPreferences = BaseApp.getInstance().classSharedPreferences
        supportFragmentManager
            .beginTransaction()
            .replace(binding.block.id, SettingsFragment())
            .commit()
        val supportActionBar = supportActionBar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        PreferenceManager.getDefaultSharedPreferences(this)
            .registerOnSharedPreferenceChangeListener(this)
    }

    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
        }
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        val darkModeString = getString(R.string.dark_mode)
        if (key == darkModeString) {
            val darkModeValues = resources.getStringArray(R.array.dark_mode_values)
            val pref = PreferenceManager.getDefaultSharedPreferences(this)
                .getString(getString(R.string.dark_mode), getString(R.string.dark_mode_def_value))
            if (pref == darkModeValues[0]) AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            if (pref == darkModeValues[1]) AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }

    }

    override fun provideSummary(preference: ListPreference): CharSequence? {
        val key = preference.key
        if (key != null) if (key == getString(R.string.dark_mode)) return preference.entry
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        PreferenceManager.getDefaultSharedPreferences(this)
            .unregisterOnSharedPreferenceChangeListener(this)
    }

    private fun setLanguage(lan: String) {
        val locale = Locale(lan)
        Locale.setDefault(locale)
        val resources = resources
        val config = resources.configuration
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
        val intent = Intent(this, SettingsActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return true
    }

}