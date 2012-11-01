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

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SplashActivity extends Activity {
	private static final int SPLASH_DISPLAY_LENGHT = 700;
	private final static int TOAST_LENGTH = 1000;
	
	private final static int FAKE_DAY_NUM = 50;
	public static final long A_DAY_LONG_IN_MILL = 86400000;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
              
        setContentView(R.layout.activity_splash);
        
        //For Test
        if (MetaData.DEBUG_TEST && MetaData.DEBUG_MAKE_FAKE_DATA) {
        	Test.generateFakeData(this, FAKE_DAY_NUM);
        }
        
        initUserData();
        
        final Intent intent = new Intent(SplashActivity.this, DaysActivity.class);
        if (!MetaData.isFirstRun) {
        	if (MetaData.usePassword) {
        		final EditText password = (EditText)findViewById(R.id.splash_password);
        		Button ok = (Button)findViewById(R.id.splash_ok);
        		
        		password.setVisibility(View.VISIBLE);
        		ok.setVisibility(View.VISIBLE);
        		password.requestFocus();
        		
        		ok.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						
						if (!TextUtils.equals(password.getText().toString(), MetaData.password)) {
							Toast.makeText(SplashActivity.this, 
									getResources().getString(R.string.password_is_wrong), TOAST_LENGTH).show();
							return;
						}
						
		                SplashActivity.this.startActivity(intent);
		                SplashActivity.this.finish();
					}
				});
        	}
        	else {
		        new Handler().postDelayed(new Runnable(){
		            @Override
		            public void run() {
		                SplashActivity.this.startActivity(intent);
		                SplashActivity.this.finish();
		            }
		        }, SPLASH_DISPLAY_LENGHT);
        	}
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    private void initUserData() {
        SharedPreferences pref = getSharedPreferences(MetaData.PREFS_NAME, MODE_PRIVATE);
        MetaData.isFirstRun = pref.getBoolean(MetaData.FIRST_RUN, true);
        MetaData.userName = pref.getString(MetaData.USER_NAME, "");
        MetaData.promiseTime = pref.getLong(MetaData.PROMISE_TIME, System.currentTimeMillis());
        //For Test
        if (MetaData.DEBUG_TEST) {
	        MetaData.promiseTime = System.currentTimeMillis() - FAKE_DAY_NUM * A_DAY_LONG_IN_MILL;
        }
        
        MetaData.lastDate = pref.getLong(MetaData.LAST_DATE, System.currentTimeMillis());
        MetaData.usePassword = pref.getBoolean(MetaData.USE_PASSWORD, false);
        MetaData.password = pref.getString(MetaData.PASSWORD, "");
        
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(System.currentTimeMillis());
        c.set(Calendar.HOUR_OF_DAY, 22);
        c.set(Calendar.MINUTE, 30);
        c.set(Calendar.SECOND, 0);
        
        MetaData.remindHour = pref.getInt(MetaData.REMIND_HOUR, 22);
        MetaData.remindMinute = pref.getInt(MetaData.REMIND_MINUTE, 30);
        c.set(Calendar.HOUR_OF_DAY, MetaData.remindHour);
        c.set(Calendar.MINUTE, MetaData.remindMinute);
        
        if (MetaData.isFirstRun) {
        	Intent intent = new Intent(SplashActivity.this, WelcomeActivity.class);
        	startActivity(intent);
        	SplashActivity.this.finish();
        }
        
        //set reminder
        if (!MetaData.isFirstRun) { 
	        AlarmManager am= (AlarmManager) getSystemService(ALARM_SERVICE);  

	        PendingIntent pi= PendingIntent.getBroadcast(getApplicationContext(), 0, 
	        					new Intent(getApplicationContext(),ReminderReciever.class), 0);  
	   
	        Long delta = 0l;
	        if (c.getTimeInMillis() < System.currentTimeMillis() ) {
	        	delta = MetaData.A_DAY_LONG_IN_MILL;
	        }

	        am.setRepeating(AlarmManager.RTC_WAKEUP, c.getTimeInMillis()+delta, 
	        				MetaData.A_DAY_LONG_IN_MILL, pi);
        }
    }
}
