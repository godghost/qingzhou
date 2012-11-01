/* 
 *  Copyright 2012 Loong H
 * 
 *  Qingzhou is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Qingzhou is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.loongsoft.qingzhou;

import java.util.Calendar;

import org.jraf.android.backport.switchwidget.SwitchPreference;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.text.TextUtils;

public class QingzhouPreference extends PreferenceActivity {
	String mPrefKeyName;
	String mPrefKeyTimer;
	String mPrefKeyPasswordSwitcher;
	String mPrefKeyPassword;
	String mPrefKeyAbout;
	
	EditTextPreference mPrefName;
	Preference mPrefTimer;
	SwitchPreference mPrefPasswordSwitcher;
	ResetPasswordPreference mPrefPassword;
	Preference mPrefAbout;
	
	SharedPreferences mPrefs;
	
	OnSharedPreferenceChangeListener mListener;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		addPreferencesFromResource(R.xml.preference);
		
		getListView().setDivider(getResources().getDrawable(R.drawable.seprateline_dark));
		setTitle(getResources().getString(R.string.title_activity_settings));
		
		mPrefKeyName = getResources().getString(R.string.pref_key_name);
		mPrefKeyTimer = getResources().getString(R.string.pref_key_timer);
		mPrefKeyPasswordSwitcher = getResources().getString(R.string.pref_key_password_switcher);
		mPrefKeyPassword = getResources().getString(R.string.pref_key_password);
		mPrefKeyAbout = getResources().getString(R.string.pref_key_about);

		mPrefName = (EditTextPreference)findPreference(mPrefKeyName);
		mPrefTimer = findPreference(mPrefKeyTimer);
		mPrefPasswordSwitcher = (SwitchPreference)findPreference(mPrefKeyPasswordSwitcher);
		mPrefPassword = (ResetPasswordPreference)findPreference(mPrefKeyPassword);
		mPrefAbout = findPreference(mPrefKeyAbout);

		mPrefs = getPreferenceScreen().getSharedPreferences();
		mListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
			public void onSharedPreferenceChanged(SharedPreferences prefs,
					String key) {
				updateSettings(key);
			}
		};

		mPrefs.registerOnSharedPreferenceChangeListener(mListener);
		
		initSettings();
	}
	
	private void initSettings() {
		SharedPreferences pref = getSharedPreferences(MetaData.PREFS_NAME, MODE_PRIVATE);
		
		mPrefName.setSummary(MetaData.userName);
		mPrefName.setDefaultValue(MetaData.userName);
		mPrefName.setText(MetaData.userName);
		mPrefTimer.setSummary(mPrefs.getString(mPrefKeyTimer, "22:30"));
		
		mPrefPasswordSwitcher.setChecked(pref.getBoolean(MetaData.USE_PASSWORD, false));
		
		if (mPrefPasswordSwitcher.isChecked()) {
			mPrefPassword.setEnabled(true);
		} else {
			mPrefPassword.setEnabled(false);
		}
		
		mPrefAbout.setOnPreferenceClickListener( new OnPreferenceClickListener() {
			
			@Override
			public boolean onPreferenceClick(Preference preference) {
				AlertDialog dialog = new  AlertDialog.Builder(QingzhouPreference.this)
					.setTitle(QingzhouPreference.this.getResources().getString(R.string.about))
					.setPositiveButton(QingzhouPreference.this.getResources().getString(android.R.string.ok), null)
					.setMessage(MetaData.VERSION + "\n" + QingzhouPreference.this.getResources().getString(R.string.about_msg))
					.create();
				dialog.show();
				return true;
			}
		});
	}
	
	private void updateSettings(String key) {
		SharedPreferences pref = getSharedPreferences(MetaData.PREFS_NAME, MODE_PRIVATE);
		Editor editor = pref.edit();
		
		mPrefName.setSummary(mPrefs.getString(mPrefKeyName, "A name"));
		
		if (TextUtils.equals(key, mPrefKeyTimer)) {
			String newValue = mPrefs.getString(mPrefKeyTimer, "22:30");
			mPrefTimer.setSummary(newValue);
			
			String[] values = newValue.split(":");
			editor.putInt(MetaData.REMIND_HOUR, Integer.parseInt(values[0]));
			editor.putInt(MetaData.REMIND_MINUTE, Integer.parseInt(values[1]));
			
	        Calendar c = Calendar.getInstance();
	        c.set(Calendar.HOUR_OF_DAY, Integer.parseInt(values[0]));
	        c.set(Calendar.MINUTE, Integer.parseInt(values[1]));
	        c.set(Calendar.SECOND, 0);
	        
	        // get the AlarmManager instance   
	        AlarmManager am= (AlarmManager) getSystemService(ALARM_SERVICE);  
	        // create a PendingIntent that will perform a broadcast  
	        PendingIntent pi= PendingIntent.getBroadcast(getApplicationContext(), 0, 
	        					new Intent(getApplicationContext(),ReminderReciever.class), 0);  
	        //change reminder
	        am.setRepeating(AlarmManager.RTC_WAKEUP, c.getTimeInMillis()+MetaData.A_DAY_LONG_IN_MILL, 
	        					MetaData.A_DAY_LONG_IN_MILL, pi);
		}
		
		if (TextUtils.equals(key, mPrefKeyPassword)) {
			String newValue = mPrefs.getString(mPrefKeyPassword, "");
			editor.putString(MetaData.PASSWORD, newValue);
		}

		MetaData.userName = mPrefs.getString(mPrefKeyName, "A name");
		MetaData.usePassword = mPrefs.getBoolean(mPrefKeyPasswordSwitcher, false);
		
		if (mPrefPasswordSwitcher.isChecked()) {
			mPrefPassword.setEnabled(true);
		} else {
			mPrefPassword.setEnabled(false);
		}
		
		editor.putString(MetaData.USER_NAME, MetaData.userName);
		editor.putBoolean(MetaData.USE_PASSWORD, MetaData.usePassword);
		editor.commit();
	}
	
	@Override
	public void finish() {
		super.finish();
		
		//activity switch animation
		overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
	}
}
