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

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;

import com.loongsoft.qingzhou.ADayAdapter.OnChangeItemListener;

public class ADayActivity extends Activity {
	ThingManager mThingManager = null;
	ADayAdapter mADayAdapter = null;
	
	public HorizontalScrollView mPromptionScrollView = null;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aday);
        
        Intent intent = getIntent();
        Long dayInMillis = intent.getLongExtra("dayInMillis", System.currentTimeMillis());
        
        TextView todayTextView = (TextView)findViewById(R.id.aday_date);
        todayTextView.setText(android.text.format.DateFormat.format(
        		getResources().getString(R.string.aday_date_format), dayInMillis));
        
        mThingManager = ThingManager.getThingManager(this);
        mThingManager.setToday(dayInMillis);
        
        mADayAdapter = new ADayAdapter(this, mThingManager);
        
        final ListView list = (ListView)findViewById(R.id.aday_list);
        list.setAdapter(mADayAdapter);
        
        mADayAdapter.setOnChangeItemListener(new OnChangeItemListener() {
			
			@Override
			public void onChangeItem(Thing thing) {
				boolean isThingNew = false;
				if (thing.getId() == null) {
					isThingNew = true;
				}
				mThingManager.updateThing(thing, isThingNew);
			}
            
	      //focus EditText when add new item
			@Override
			public void onAddNewItem() {
				mPromptionScrollView.setVisibility(View.VISIBLE);
				// scroll list to bottom
				list.post(new Runnable() {
			        @Override
			        public void run() {
			        	list.setSelection(mADayAdapter.getCount() - 1);
			        }
			    });
			}

			@Override
			public void onDeleteItem(Thing thing) {
				mThingManager.deleteThing(thing);
			}
		});
		
        ImageButton backButton = (ImageButton)findViewById(R.id.aday_back);
        backButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ADayActivity.this, DaysActivity.class);
				startActivity(intent);
				
				//activity switch animation
				overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
			}
		});
        
        //scroll to bottom
		list.post(new Runnable() {
	        @Override
	        public void run() {
	        	list.setSelection(mADayAdapter.getCount() - 1);
	        }
	    });
		
		initPromption();
    }
    
    private void initPromption() {
    	ArrayList<FrequentThing> promptions = mThingManager.getMostFrequentThings();
    	LinearLayout layout = (LinearLayout)findViewById(R.id.linearlayout_promption);
    	mPromptionScrollView = (HorizontalScrollView)findViewById(R.id.scrollview_promotion);
    	LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    	
    	layout.removeAllViewsInLayout();
    	for (int i = 0; i < promptions.size(); i++) {
    		Button promptionButton = new Button(this);
    		promptionButton.setLayoutParams(params);
    		promptionButton.setText(promptions.get(i).getName());
    		promptionButton.setTextColor(getResources().getColor(R.color.text));
    		promptionButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_selector));
    		promptionButton.setTag(i);
    		
    		promptionButton.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Button b = (Button)v;
					String insertedStr = b.getText().toString();
					EditText text = mADayAdapter.getFocusedEditText();
					if (text != null) {
						text.setText(insertedStr);
						text.setSelection(insertedStr.length());
					}
				}
			});
    		
    		layout.addView(promptionButton);
    	}
    }
    
    @Override
    public void onPause() {
    	super.onPause();
    }
    
    @Override
    public void onResume() {
    	super.onResume();
    	mADayAdapter.notifyDataSetChanged();
    	initPromption();
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
			//activity switch animation
			overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
        }
        return super.onKeyDown(keyCode, event);
    }
    
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
    }
}
