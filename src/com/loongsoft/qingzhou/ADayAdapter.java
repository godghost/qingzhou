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
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;

public class ADayAdapter extends BaseAdapter {
    
    private Activity mActivity;
    private ArrayList<Thing> mThings;
    private static LayoutInflater mInflater=null;
    
    private static final int ITEM_VIEW_TYPE_COUNT = 3;
    private static final int MIDDLE_ITEM_VIEW_TYPE = 0;
    private static final int LAST_ITEM_VIEW_TYPE = 1;
    private static final int FIRST_ITEM_VIEW_TYPE = 2;
    
    /* ensure that this constant is greater than the maximum list size */
    private static final int DEFAULT_ID_VALUE = -1;
    private static final String EDITTEXT_TEXT_TYPE = "text";
    private static final String EDITTEXT_QUANTITY_TYPE = "quantity";
    /* used to keep the note edit text row id within the list */
    private int mTouchEditTextId = DEFAULT_ID_VALUE;
    private String mTouchEditTextType = "";
    
    private static final int DEFAULT_ACTION_VALUE = -1;
    private static final int ACTION_DELETE = 1;
    private static final int ACTION_ADD = 2;
    private static final int ACTION_UPDATE = 3;
    private int mLastAction = DEFAULT_ACTION_VALUE;
    
    private EditText mNewAddedEditText = null;
    private EditText mFocusEditText = null;
    private int mFocusCount = 0;
    
    public ADayAdapter(Activity a, ThingManager manager) {
    	mActivity = a;
    	mThings = manager.getTodayThings();
        mInflater = (LayoutInflater)mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
    
    @Override
    public int getViewTypeCount() {
    	return ITEM_VIEW_TYPE_COUNT;
    }
    
    @Override
    public int getItemViewType(int position) {
    	if (position == mThings.size()-1)
    		return LAST_ITEM_VIEW_TYPE;
    	if (position == 0) {
    		return FIRST_ITEM_VIEW_TYPE;
    	}
    	return MIDDLE_ITEM_VIEW_TYPE;
    }
    
    public View getView(int position, View convertView, ViewGroup parent) {   
    	ADayItemViewHolder viewHolder = null;
        int viewType = getItemViewType(position);
        
        if (convertView == null ) {
        	switch (viewType) {
        		case MIDDLE_ITEM_VIEW_TYPE:
        		case FIRST_ITEM_VIEW_TYPE:
        			if (viewType == FIRST_ITEM_VIEW_TYPE)
        				convertView = mInflater.inflate(R.layout.aday_list_first_row, null);
        			else
        				convertView = mInflater.inflate(R.layout.aday_list_middle_row, null);
        			viewHolder = new ADayItemViewHolder();
        			viewHolder.text = (EditText)convertView.findViewById(R.id.list_text);
        			viewHolder.quantity = (EditText)convertView.findViewById(R.id.list_quantity);
        			viewHolder.delButton = (ImageButton)convertView.findViewById(R.id.list_del);
      
        			convertView.setTag(viewHolder);

        			break;
        			
        		case LAST_ITEM_VIEW_TYPE:
        			convertView = mInflater.inflate(R.layout.aday_list_last_row, null);
        			break;	
        	}
        }
        
        if (viewType == MIDDLE_ITEM_VIEW_TYPE  || viewType == FIRST_ITEM_VIEW_TYPE) {
        	viewHolder = (ADayItemViewHolder)convertView.getTag();
        }
        
        View vi = convertView;

        if (viewType == MIDDLE_ITEM_VIEW_TYPE || viewType == FIRST_ITEM_VIEW_TYPE) {
        	final EditText text = viewHolder.text; 
        	final EditText quantity = viewHolder.quantity;
        	
        	text.setText(mThings.get(position).getDescription());
        	text.setId(position);
        	text.setTag(EDITTEXT_TEXT_TYPE);
        	
        	quantity.setText(mThings.get(position).getQuantity());
        	quantity.setId(position);
        	quantity.setTag(EDITTEXT_QUANTITY_TYPE);
        	
        	final ImageButton delButton = viewHolder.delButton;
        	delButton.setId(position);
        	
        	//we need to update adapter once we finish with editing
        	OnFocusChangeListener focusChangeListener = new OnFocusChangeListener() {
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus){
                    	delButton.setVisibility(View.INVISIBLE);
                        final int pos = v.getId();
                        String newText = ((EditText)v).getText().toString();
                        
                        String tag = (String)v.getTag();
                        if ((TextUtils.equals(tag, EDITTEXT_TEXT_TYPE) && 
                        		!TextUtils.equals(mThings.get(pos).getDescription(), newText)) ||
                        		(TextUtils.equals(tag, EDITTEXT_QUANTITY_TYPE) && 
                        				!TextUtils.equals(mThings.get(pos).getQuantity(), newText))) {
                        	
                        	//If last action is deletion, then do nothing.
                        	if (mLastAction == ACTION_DELETE) {
                        		mLastAction = DEFAULT_ACTION_VALUE;
                        		return;
                        	}
                        	
                        	Thing thing = mThings.get(pos);                    	
                        	if (TextUtils.equals(tag, EDITTEXT_TEXT_TYPE))
                        		thing.setDesciption(newText);
                        	else
                        		thing.setQuantitiy(newText);
                        	mThings.set(pos, thing);
                        	
                        	if (mChangeItemListener != null)
                        		mChangeItemListener.onChangeItem(thing);
                        }
                        if (TextUtils.equals(tag, EDITTEXT_TEXT_TYPE)) {
	                    	ADayActivity a = (ADayActivity)ADayAdapter.this.mActivity;
	                    	a.mPromptionScrollView.setVisibility(View.GONE);
                        }
                        mFocusEditText = null;
                    } else {
                    	if (TextUtils.equals((String)v.getTag(), EDITTEXT_TEXT_TYPE)) {
	                    	ADayActivity a = (ADayActivity)ADayAdapter.this.mActivity;
	                    	a.mPromptionScrollView.setVisibility(View.VISIBLE);
	                    	mFocusEditText = (EditText)v;
                    	}
                    	delButton.setVisibility(View.VISIBLE);
                    }
                }
            };

            text.setOnFocusChangeListener(focusChangeListener);
            quantity.setOnFocusChangeListener(focusChangeListener);
            
            /* if the last id is set, the edit text from this list item was pressed */
            if (mTouchEditTextId == position) {
            	if (TextUtils.equals(mTouchEditTextType, EDITTEXT_TEXT_TYPE)) {
	                /* make the edit text recive focus */
	                viewHolder.text.requestFocus();
	                /* make the edit text's cursor to appear at the end of the text */
	                viewHolder.text.setSelection(viewHolder.text.getText().length());
            	} else if (TextUtils.equals(mTouchEditTextType, EDITTEXT_QUANTITY_TYPE)) {
            		viewHolder.quantity.requestFocus();
            		viewHolder.quantity.setSelection(viewHolder.quantity.getText().length());
            	}

                /* reset the last id to default value */
                mTouchEditTextId = DEFAULT_ID_VALUE;
                mTouchEditTextType = "";
            }
            
            //hack: focus the new added edittext
            if (mLastAction == ACTION_ADD && position== mThings.size()-2) {
            	mNewAddedEditText = viewHolder.text;
            	
            	if (mFocusCount > 1) {
	            	mNewAddedEditText.post(new Runnable() {
	                    public void run() {
	                    	/* make the edit text recive focus */
	                    	mNewAddedEditText.requestFocus();
	                    }
	                });
            		mFocusCount = 0;
            		mLastAction = DEFAULT_ACTION_VALUE;
            	} else {
	            	mNewAddedEditText.post(new Runnable() {
	                    public void run() {
	                    	/* make the edit text recive focus */
	                    	mNewAddedEditText.requestFocus();
	                    	InputMethodManager mgr = (InputMethodManager)mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
	                    	mgr.showSoftInput(mNewAddedEditText, InputMethodManager.SHOW_IMPLICIT);
	                    }
	                });
	            	mFocusCount++;
            	}
            }

            /* set a touch listener on the edit text just to record the index of the edit text that was pressed */
            final int index = position;
            OnTouchListener onTouchListener = new OnTouchListener() {
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                        /* get the index of the touched list item */
                        mTouchEditTextId = index;
                        mTouchEditTextType = (String)view.getTag();
                    }
                    return false;
                }
            };
            viewHolder.text.setOnTouchListener(onTouchListener);
            viewHolder.quantity.setOnTouchListener(onTouchListener);
          	
            //Delete an item
        	delButton.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					final int pos = v.getId();
					
					if (mChangeItemListener != null) {
						mChangeItemListener.onDeleteItem(mThings.get(pos));
					}
					
					mThings.remove(pos);
					ADayAdapter.this.notifyDataSetChanged();
					
					mLastAction = ACTION_DELETE;
				}
			});
        } else if (viewType == LAST_ITEM_VIEW_TYPE) {
        	ImageButton add = (ImageButton)vi.findViewById(R.id.list_add);
        	
        	//add a item
        	add.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					mThings.add(mThings.size()-1, new Thing());
					ADayAdapter.this.notifyDataSetChanged();
					
					mTouchEditTextId = mThings.size() - 2;
					
					//New item added!
					if (mChangeItemListener != null) {
						mChangeItemListener.onAddNewItem();
					}
					
					if (mLastAction==ACTION_ADD) {
						mFocusCount = 2;
					}
					
					mLastAction = ACTION_ADD;
				}
			});
        }
        
        return vi;
    }
    
    public class ADayItemViewHolder {
    	public EditText text = null;
    	public EditText quantity = null;
    	public ImageButton delButton = null;
    }
    
    OnChangeItemListener mChangeItemListener;
    
    public interface OnChangeItemListener {
    	public void onChangeItem(Thing thing);
    	public void onDeleteItem(Thing thing);
    	public void onAddNewItem();
    }
    
    public void setOnChangeItemListener(OnChangeItemListener eventListener) {
    	mChangeItemListener = eventListener;
    }
    
    public EditText getFocusedEditText() {
    	return mFocusEditText;
    }
}

