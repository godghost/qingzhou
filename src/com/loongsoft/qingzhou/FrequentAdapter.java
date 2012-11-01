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
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

public class FrequentAdapter extends BaseAdapter {
    
    private Activity mActivity;
    private static LayoutInflater inflater=null;
    
    private ArrayList<FrequentThing> mThings;

    
    public FrequentAdapter(Activity a, ArrayList<FrequentThing> things) {
    	mActivity = a;
    	mThings=things;
        inflater = (LayoutInflater)mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        return mThings.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }
    
    public View getView(int position, View convertView, ViewGroup parent) {   
    	FrequentItemViewHolder viewHolder = null;
        
        if (convertView == null ) {
        	convertView = inflater.inflate(R.layout.frequent_list_row, null);
        	viewHolder = new FrequentItemViewHolder();
        	viewHolder.text = (TextView)convertView.findViewById(R.id.frequent_text);
        	convertView.setTag(viewHolder);
        }
        
        viewHolder = (FrequentItemViewHolder)convertView.getTag();
        viewHolder.text.setText(mThings.get(position).getName() + " x " + 
        			mThings.get(position).getCount());
        
        return convertView;
    }
    
    public class FrequentItemViewHolder {
    	public TextView text = null;
    }
}

