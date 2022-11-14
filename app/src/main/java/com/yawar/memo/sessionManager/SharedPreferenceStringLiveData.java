package com.yawar.memo.sessionManager;

import android.content.SharedPreferences;

public class SharedPreferenceStringLiveData extends SharedPreferenceLiveData<Integer>{

    public SharedPreferenceStringLiveData(SharedPreferences prefs, String key, Integer defValue) {
        super(prefs, key, defValue);
    }

    @Override
    Integer getValueFromPreferences(String key, Integer defValue) {
        return sharedPrefs.getInt(key, defValue);
    }

}
