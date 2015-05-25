package com.worktimetracker.prefsmanager;


import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.worktimetracker.R;

public class PreferencesManager extends PreferenceFragment
{
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.addPreferencesFromResource (R.xml.spreadsheetprefs);
    }

}
