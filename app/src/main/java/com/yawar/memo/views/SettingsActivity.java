package com.yawar.memo.views;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceFragmentCompat;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.ListPreference;
import androidx.preference.Preference;

import com.yawar.memo.sessionManager.ClassSharedPreferences;
import com.yawar.memo.R;

import java.util.Locale;


public class SettingsActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener,
        Preference.SummaryProvider<androidx.preference.ListPreference> {
    ClassSharedPreferences classSharedPreferences;
    Resources resources;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.settings_activity);
        getSupportActionBar().setTitle(R.string.appearance);



        classSharedPreferences = new ClassSharedPreferences(SettingsActivity.this);



        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.block, new SettingsFragment())
                .commit();

        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null)
            supportActionBar.setDisplayHomeAsUpEnabled(true);

        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(this);
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        String darkModeString = getString(R.string.dark_mode);
        if (key != null && sharedPreferences != null)
            if (key.equals(darkModeString)) {
                final String[] darkModeValues = getResources().getStringArray(R.array.dark_mode_values);
                // The apps theme is decided depending upon the saved preferences on app startup
                String pref = PreferenceManager.getDefaultSharedPreferences(this)
                        .getString(getString(R.string.dark_mode), getString(R.string.dark_mode_def_value));
                // Comparing to see which preference is selected and applying those theme settings

                if (pref.equals(darkModeValues[0]))
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                if (pref.equals(darkModeValues[1]))
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

            }
//        if (key.equals("language")) {
//
////            Locale jaLocale1 = new Locale("en");
////            setLanguage(jaLocale1);
//            final String[] languageValues = getResources().getStringArray(R.array.language_values);
//            String pref = classSharedPreferences.getLocale();
//            if (pref.equals(languageValues[0])){
//
//
//              setLanguage("ar");
//                classSharedPreferences.setLocale("ar");
////
//            }
//            if (pref.equals(languageValues[1])){
//
//
//                setLanguage("en");
//                classSharedPreferences.setLocale("en");
////
//            }


//        }


    }
    @Override
    public CharSequence provideSummary(ListPreference preference) {
        String key = preference.getKey();
        if (key != null)
            if (key.equals(getString(R.string.dark_mode)))
                return preference.getEntry();
//            if(key.equals("language"))
//                return preference.getEntry();
        return null;
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void setLanguage(String lan) {
        Locale locale = new Locale(lan);
        Locale.setDefault(locale);
        Resources resources = this.getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        resources.updateConfiguration(config, resources.getDisplayMetrics());
        Intent intent = new Intent(SettingsActivity.this,SettingsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }
    //            @RequiresApi(api = Build.VERSION_CODES.N)
//        @Override
//    protected void attachBaseContext(Context newBase) {
//                System.out.println("attach first");
//
////                ClassSharedPreferences classSharedPreferences = new ClassSharedPreferences(SettingsActivity.this);
////        String lan = classSharedPreferences.getLocale();
//        Locale locale = new Locale("en");
//        Locale.setDefault(locale);
//
//
//        super.attachBaseContext(MyContextWrapper.wrap(newBase, locale));}

//    @Override
//    protected void attachBaseContext(Context newBase) {
//        Locale localeToSwitchTo = new Locale("en");
//        ContextWrapper localeUpdatedContext = ContextUtils.updateLocale(newBase, localeToSwitchTo);
//        super.attachBaseContext(localeUpdatedContext);
//    }

}