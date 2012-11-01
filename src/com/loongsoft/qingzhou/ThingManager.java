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
import java.util.Date;
import java.util.HashMap;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.text.format.DateFormat;

import com.loongsoft.qingzhou.QingzhouProviderMetaData.ThingTableMetaData;

public class ThingManager {
	public static final long A_DAY_LONG_IN_MILL = 86400000;
	public static final int MOST_FREQUENT_THINGS_COUNT = 10;
	
	Context mContext;
	Long mTodayInMills;
	HashMap<Long, ArrayList<Thing>> mThingsInDays;
	ArrayList<Thing> mTodayThings;
	ArrayList<Long> mDaysInMills;
	
	ArrayList<FrequentThing> mMostFrequentThings;
	
	static ThingManager sThingManager = null;
	
	private ThingManager(Context context) {
		mThingsInDays = new HashMap<Long, ArrayList<Thing>>();
		mDaysInMills = new ArrayList<Long>();
		mMostFrequentThings = new ArrayList<FrequentThing>();
		
		mContext = context;
		
		initThings();
	}
	
	public static ThingManager getThingManager(Context context) {
		if (sThingManager == null) {
			sThingManager = new ThingManager(context);
		}
		return sThingManager;
	}
	
	private void initThings(){
		Long todayInMills = Utils.getZeroOfTodayInMills();
		Long promiseDateInMills = Utils.getZeroOfDayInMills(MetaData.promiseTime);

		for (long dayInMills=promiseDateInMills; dayInMills < todayInMills+A_DAY_LONG_IN_MILL; 
				dayInMills+=A_DAY_LONG_IN_MILL) {
			initThingsInDay(dayInMills);
			mDaysInMills.add(dayInMills);
		}
		
		initMostFrequentThings();
	}
	
	public int getDaysCount() {
		//For add list's last item, which is '+'
		return mThingsInDays.size() + 1;
	}
	
	public ArrayList<Thing> getTodayThings() {
		return mTodayThings;
	}
	
	public Long getToday() {
		return mTodayInMills;
	}
	
	public void setToday(Long todayInMills) {
		mTodayInMills = Utils.getZeroOfDayInMills(todayInMills);
		
		//If enter a new day, then create a new entry
		if (!mThingsInDays.containsKey(mTodayInMills)) {
			Long lastDay = mDaysInMills.get(mDaysInMills.size()-1);
			
			for (long d = lastDay+A_DAY_LONG_IN_MILL; d<=mTodayInMills; d+=A_DAY_LONG_IN_MILL) {
				ArrayList<Thing> newDayThings = new ArrayList<Thing>();
				newDayThings.add(new Thing());
				mThingsInDays.put(d, newDayThings);
				mDaysInMills.add(d);
			}
		}
		
		mTodayThings = mThingsInDays.get(mTodayInMills);
	}
	
	public String getDescriptionCombined(Long todayInMills) {
		StringBuilder sb = new StringBuilder();
		
		ArrayList<Thing> todayThings = mThingsInDays.get(todayInMills);
		
		for (int i = 0; i < todayThings.size() - 1; i++) {
			if (!TextUtils.isEmpty(todayThings.get(i).getDescription())) {
				sb.append(todayThings.get(i).getDescription());
				if (i < todayThings.size() - 2) {
					sb.append(" ‧ ");
				}
			}
		}
		
		return sb.toString();
	}
	
	public String getTodayString() {
		return Integer.toString(new Date(mTodayInMills).getDate());
	}
	
	private void initThingsInDay(Long dayInMills) {
        
		String selection = ThingTableMetaData.CREATED_DATE + ">=? AND " + ThingTableMetaData.CREATED_DATE +
							"<?";
		
		long tomorrowInMills = dayInMills + A_DAY_LONG_IN_MILL;
		
		String[] selectionArgs = {
				Long.toString(dayInMills),
				Long.toString(tomorrowInMills)
		};
		
		Cursor c = mContext.getContentResolver().query(ThingTableMetaData.CONTENT_URI, QingzhouProvider.THINGS_PROJECTION, 
				selection, selectionArgs, null);
		
		if (!mThingsInDays.containsKey(dayInMills)) {
			ArrayList<Thing> thingsInDay = new ArrayList<Thing>();
			mThingsInDays.put(dayInMills, thingsInDay);
		}
		
		for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
			int idColumn = c.getColumnIndex(ThingTableMetaData._ID);
			int desColumn = c.getColumnIndex(ThingTableMetaData.DESCRIPTION);
			int quanColumn = c.getColumnIndex(ThingTableMetaData.QUANTITY);
			int createdColumn = c.getColumnIndex(ThingTableMetaData.CREATED_DATE);
			int modifiedColumn = c.getColumnIndex(ThingTableMetaData.MODIFIED_DATE);
			
			String id = c.getString(idColumn);
			String description = c.getString(desColumn);
			String quantity = c.getString(quanColumn);
			Long created = c.getLong(createdColumn);
			Long modified = c.getLong(modifiedColumn);
			
			Thing aThing = new Thing(id, description, quantity, created, modified);

			mThingsInDays.get(dayInMills).add(aThing);
		}
		c.close();
		
		//An useless thing represents '+'
		mThingsInDays.get(dayInMills).add(new Thing());
	}
	
	public void updateThing(Thing thing, boolean isNew) {
		ContentValues values = new ContentValues();
		
		values.put(ThingTableMetaData.DESCRIPTION, thing.getDescription());
		values.put(ThingTableMetaData.QUANTITY, thing.getQuantity());
		values.put(ThingTableMetaData.CREATED_DATE, thing.getCreatedDateInMills());
		values.put(ThingTableMetaData.MODIFIED_DATE, thing.getModifiedDateInMills());
		
		if (isNew) {
			Long real_today_inmills = Utils.getZeroOfTodayInMills();

			if (mTodayThings.size() > 2 && real_today_inmills!=mTodayInMills) {

				real_today_inmills = mTodayThings.get(mTodayThings.size() - 3).getCreatedDateInMills() + 1;
			} else if (real_today_inmills!=mTodayInMills) {
				real_today_inmills = mTodayInMills;
			} else {
				real_today_inmills = System.currentTimeMillis();
			}
			
			values.put(ThingTableMetaData.CREATED_DATE, real_today_inmills);
			
			Uri insertedUri = mContext.getContentResolver().insert(ThingTableMetaData.CONTENT_URI, values);
			thing.setCreatedDateInMills(real_today_inmills);
			thing.setId(insertedUri.getPathSegments().get(1));
		} else if (thing.getId() != null) {
			Uri toUpdateUri = ContentUris.withAppendedId(ThingTableMetaData.CONTENT_URI, Long.parseLong(thing.getId()));
			mContext.getContentResolver().update(toUpdateUri, values, null, null);
		}
	}
	
	public void deleteThing(Thing thing) {
		if (thing.getId() != null) {
			Uri toDeleteUri = ContentUris.withAppendedId(ThingTableMetaData.CONTENT_URI, Long.parseLong(thing.getId()));
			mContext.getContentResolver().delete(toDeleteUri, null, null);
		}
	}
	
	public String getDayDescriptionAtIndex(int index) {
		return getDescriptionCombined(mDaysInMills.get(index));
	}
	
	public String getDateStringAtIndex(int index) {
		Date date = new Date(mDaysInMills.get(index));
		return Integer.toString(date.getDate());
	}
	
	public String getWholeDateStringAtIndex(int index) {
		return DateFormat.format(mContext.getResources().getString(R.string.days_date_format), 
				mDaysInMills.get(index)).toString();
	}
	
	public Long getDayInMillsAtIndex(int index) {
		if (index >= mDaysInMills.size())
			return System.currentTimeMillis();
		return mDaysInMills.get(index);
	}
	
	//-1 means < all dates, -2 means > all dates
	public int getDateIndex(Date date) {
		Long dateInMills = Utils.getZeroOfDayInMills(date.getTime());
		
		for (int i = 0; i < mDaysInMills.size()-1; i++)
			if (dateInMills>= mDaysInMills.get(i) && dateInMills<mDaysInMills.get(i+1))
				return i;
		
		if (mDaysInMills.size() >=1  && dateInMills < mDaysInMills.get(0))
			return -1;
		
		return -2;
	}
	
	public void initMostFrequentThings() {
		String[] projection = {ThingTableMetaData.DESCRIPTION, "count(*) as " + ThingTableMetaData.COUNT};
		String selection = "(0==0) GROUP BY (" + ThingTableMetaData.DESCRIPTION+")";
		String[] selectionArgs = null;
		String sortOrder = ThingTableMetaData.COUNT + " desc";
		
		Cursor cursor = mContext.getContentResolver().query(ThingTableMetaData.CONTENT_URI, 
				projection, selection, selectionArgs, sortOrder);
		
		int count = 0;
		mMostFrequentThings.clear();
		for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
			if (count >= MOST_FREQUENT_THINGS_COUNT)
				break;
			FrequentThing thing = new FrequentThing(cursor.getString(0), cursor.getInt(1));
			mMostFrequentThings.add(thing);
			count++;
		}
		cursor.close();
	}
	
	public ArrayList<FrequentThing> getMostFrequentThings() {	
		initMostFrequentThings();
		return mMostFrequentThings;
	}
	
	public ArrayList<FrequentThing> getThingAllData(String thingDesc) {
		String[] projection = {ThingTableMetaData.QUANTITY, ThingTableMetaData.CREATED_DATE};
		String selection = ThingTableMetaData.DESCRIPTION + "=?";
		String[] selectionArgs = {thingDesc};
		
		Cursor cursor = mContext.getContentResolver().query(ThingTableMetaData.CONTENT_URI, 
				projection, selection, selectionArgs, null);
		
		ArrayList<FrequentThing> things = new ArrayList<FrequentThing>();
		for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
			FrequentThing thing = new FrequentThing(thingDesc, cursor.getInt(0), cursor.getLong(1));
			things.add(thing);
		}
		cursor.close();
		return things;
	}
}
