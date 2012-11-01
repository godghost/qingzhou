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

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

public class DaysAdapter extends BaseAdapter {
    
    private Activity mActivity;
    private ThingManager mThingManager;
    private static LayoutInflater inflater=null;
    
    private static final int ITEM_VIEW_TYPE_COUNT = 3;
    private static final int MIDDLE_ITEM_VIEW_TYPE = 0;
    private static final int LAST_ITEM_VIEW_TYPE = 1;
    private static final int FIRST_ITEM_VIEW_TYPE = 2;
    
    public DaysAdapter(Activity a, ThingManager manager) {
    	mActivity = a;
    	mThingManager=manager;
        inflater = (LayoutInflater)mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        return mThingManager.getDaysCount();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }
    
    @Override
    public int getViewTypeCount() {
    	return ITEM_VIEW_TYPE_COUNT;
    }
    
    @Override
    public int getItemViewType(int position) {
    	if (position == mThingManager.getDaysCount()-1)
    		return LAST_ITEM_VIEW_TYPE;
    	if (position == 0) {
    		return FIRST_ITEM_VIEW_TYPE;
    	}
    	return MIDDLE_ITEM_VIEW_TYPE;
    }
    
    public View getView(int position, View convertView, ViewGroup parent) {   
    	DaysItemViewHolder viewHolder = null;
        int viewType = getItemViewType(position);
        
        if (convertView == null ) {
        	switch (viewType) {
        		case MIDDLE_ITEM_VIEW_TYPE:
        		case FIRST_ITEM_VIEW_TYPE:
        			if (viewType==FIRST_ITEM_VIEW_TYPE)
        				convertView = inflater.inflate(R.layout.days_list_first_row, null);
        			else
        				convertView = inflater.inflate(R.layout.days_list_middle_row, null);
        			viewHolder = new DaysItemViewHolder();
        			viewHolder.text = (TextView)convertView.findViewById(R.id.days_text);
        			viewHolder.day = (TextView)convertView.findViewById(R.id.days_day);
        			convertView.setTag(viewHolder);
        			
        			break;
        			
        		case LAST_ITEM_VIEW_TYPE:
        			convertView = inflater.inflate(R.layout.days_list_last_row, null);
        			break;	
        	}
        }
        
        if (viewType == MIDDLE_ITEM_VIEW_TYPE  || viewType == FIRST_ITEM_VIEW_TYPE) {
        	viewHolder = (DaysItemViewHolder)convertView.getTag();
        }
        
        View vi = convertView;

        if (viewType == MIDDLE_ITEM_VIEW_TYPE || viewType == FIRST_ITEM_VIEW_TYPE) {
        	TextView text = viewHolder.text; 
        	TextView day = viewHolder.day;
        	
        	text.setText(mThingManager.getDayDescriptionAtIndex(position));
        	day.setText(mThingManager.getDateStringAtIndex(position));
        } else if (viewType == LAST_ITEM_VIEW_TYPE) {
        	ImageButton add = (ImageButton)vi.findViewById(R.id.days_add);
        	
        	add.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(mActivity, ADayActivity.class);
					intent.putExtra("dayInMillis", System.currentTimeMillis());
					mActivity.startActivity(intent);
					//activity switch animation
					mActivity.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
				}
			});
        }
        
        return vi;
    }
    
    public class DaysItemViewHolder {
    	public TextView text = null;
    	public TextView day = null;
    }
}

