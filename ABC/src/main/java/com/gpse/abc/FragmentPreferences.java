/*
 * Copyright 2009 Cedric Priscal
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */

package com.gpse.abc;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceFragment;

public class FragmentPreferences extends Activity {
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new SerialPortPreferences()).commit();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class SerialPortPreferences extends PreferenceFragment {
        private MyApplication mApplication;
        private SerialPortFinder mSerialPortFinder;
        public static final String PREF_FILE = "settings";

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            mApplication = (MyApplication) getActivity().getApplication();
            mSerialPortFinder = mApplication.mSerialPortFinder;

            addPreferencesFromResource(R.xml.serial_port_preferences);

            // Devices
            final ListPreference devices = (ListPreference) findPreference("DEVICE");
            String[] entries = mSerialPortFinder.getAllDevices();
            String[] entryValues = mSerialPortFinder.getAllDevicesPath();
            devices.setEntries(entries);
            devices.setEntryValues(entryValues);
            devices.setSummary(devices.getValue());
            devices.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    preference.setSummary((String) newValue);
                    return true;
                }
            });

            // Baud rates
            final ListPreference baudrates = (ListPreference) findPreference("BAUDRATE");
            baudrates.setSummary(baudrates.getValue());
            baudrates.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    preference.setSummary((String) newValue);
                    return true;
                }
            });
        }
    }
}