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

import java.util.Date;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ListView;

public class DaysActivity extends Activity {
	ThingManager mThingManager = null;
	DaysAdapter mDaysAdapter = null;
	
	Button mDateButton = null;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_days);
        
        mThingManager = ThingManager.getThingManager(this);
        
        mDaysAdapter = new DaysAdapter(this, mThingManager);
        
        final ListView list = (ListView)findViewById(R.id.days_list);
        list.setAdapter(mDaysAdapter);
        
		// scroll list to bottom
		list.post(new Runnable() {
	        @Override
	        public void run() {
	        	list.setSelection(mDaysAdapter.getCount() - 1);
	        }
	    });
		
		list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (position < list.getCount() - 1) {
					Intent intent = new Intent(DaysActivity.this, ADayActivity.class);
					intent.putExtra("dayInMillis", mThingManager.getDayInMillsAtIndex(position));
					startActivity(intent);
					
					//activity switch animation
					overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
				}
			}
		});
		
		Button titleButton = (Button)findViewById(R.id.days_title);
		titleButton.setText(MetaData.userName+" "+getResources().getString(R.string.days_title_postfix));
		titleButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(DaysActivity.this, QingzhouPreference.class);
				startActivity(intent);
				//activity switch animation
				overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
			}
		});
		
		mDateButton = (Button)findViewById(R.id.days_date);
		mDateButton.setText(DateFormat.format(
				getResources().getString(R.string.days_date_format), MetaData.lastDate));
		
		final OnDateSetListener dateSetListener = new OnDateSetListener() {
			
			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear,
					int dayOfMonth) {
				// save index and top position
				int index = mThingManager.getDateIndex(new Date(year-1900, monthOfYear, dayOfMonth));
				index = index == -1 ? 0 : index == -2 ? list.getCount()-1 : index;
				
				MetaData.lastDate = mThingManager.getDayInMillsAtIndex(index);
				
				mDateButton.setText(DateFormat.format(
						getResources().getString(R.string.days_date_format), MetaData.lastDate));

				list.setSelection(index);
			}
		};
		
		mDateButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Date lastDate = new Date(MetaData.lastDate);
				DatePickerDialog dialog = new DatePickerDialog(
						DaysActivity.this, dateSetListener,
						lastDate.getYear() + 1900, lastDate.getMonth(),
						lastDate.getDate());
				dialog.show();
			}
		});
		
		list.setOnScrollListener(new OnScrollListener() {
			
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				if (mThingManager.getDaysCount() > 2) {
					String curDateString = mThingManager.getWholeDateStringAtIndex(firstVisibleItem+1);
					mDateButton.setText(curDateString);
				}
			}
		});
		
		ImageButton frequent = (ImageButton)findViewById(R.id.days_frequent);
		frequent.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(DaysActivity.this, FrequentActivity.class);
				startActivity(intent);
				
				//activity switch animation
				overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
			}
		});
    }
    
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
    	super.onRestoreInstanceState(savedInstanceState);
    }
    
    @Override
    public void onResume() {
    	super.onResume();
    	Button titleButton = (Button)findViewById(R.id.days_title);
		titleButton.setText(MetaData.userName+" "+getResources().getString(R.string.days_title_postfix));
		
		mDaysAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_days, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
    	case R.id.menu_settings:
			Intent intent = new Intent(DaysActivity.this, QingzhouPreference.class);
			startActivity(intent);
			//activity switch animation
			overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
			return true;
    	}
    	
    	return super.onOptionsItemSelected(item);
    }
    
    @Override
	public void onBackPressed() {
		super.onBackPressed();
	}
}
