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

import java.util.HashMap;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import com.loongsoft.qingzhou.QingzhouProviderMetaData.ThingTableMetaData;

public class QingzhouProvider extends ContentProvider {
	
	private static HashMap<String, String> sThingsProjectionMap;
	static {
		sThingsProjectionMap = new HashMap<String, String>();
		sThingsProjectionMap.put(ThingTableMetaData._ID, ThingTableMetaData._ID);
		
		sThingsProjectionMap.put(ThingTableMetaData.DESCRIPTION, ThingTableMetaData.DESCRIPTION);
		sThingsProjectionMap.put(ThingTableMetaData.QUANTITY, ThingTableMetaData.QUANTITY);
		
		sThingsProjectionMap.put(ThingTableMetaData.CREATED_DATE, ThingTableMetaData.CREATED_DATE);
		sThingsProjectionMap.put(ThingTableMetaData.MODIFIED_DATE, ThingTableMetaData.MODIFIED_DATE);
	}
	
	public static String[] THINGS_PROJECTION = {
		ThingTableMetaData._ID,
		ThingTableMetaData.DESCRIPTION,
		ThingTableMetaData.QUANTITY,
		ThingTableMetaData.CREATED_DATE,
		ThingTableMetaData.MODIFIED_DATE
	};
	
	private static final UriMatcher sUriMatcher;
	private static final int INCOMING_THING_COLLECTION_URI_INDICATOR = 1;
	private static final int INCOMING_SINGLE_THING_URI_INDICATOR = 2;
	static {
		sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		sUriMatcher.addURI(QingzhouProviderMetaData.AUTHORITY, "things", INCOMING_THING_COLLECTION_URI_INDICATOR);
		sUriMatcher.addURI(QingzhouProviderMetaData.AUTHORITY, "things/#", INCOMING_SINGLE_THING_URI_INDICATOR);
	}
	
	private DatabaseHelper mOpenHelper;
	
	@Override
	public boolean onCreate() {
		mOpenHelper = new DatabaseHelper(getContext());
		return true;
	}
	
	private static class DatabaseHelper extends SQLiteOpenHelper {

		public DatabaseHelper(Context context) {
			super(context, QingzhouProviderMetaData.DATABASE_NAME, null, QingzhouProviderMetaData.DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL("CREATE TABLE " + ThingTableMetaData.TABLE_NAME + " ("
					+ QingzhouProviderMetaData.ThingTableMetaData._ID
					+ " INTEGER PRIMARY KEY,"
					+ ThingTableMetaData.DESCRIPTION + " TEXT,"
					+ ThingTableMetaData.QUANTITY + " TEXT,"
					+ ThingTableMetaData.CREATED_DATE + " INTEGER,"
					+ ThingTableMetaData.MODIFIED_DATE + " INTEGER"
					+ ");");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS " + ThingTableMetaData.TABLE_NAME);
			onCreate(db);
		}
		
	}

	@Override
	public String getType(Uri uri) {
		switch (sUriMatcher.match(uri)) {
		case INCOMING_SINGLE_THING_URI_INDICATOR:
			return ThingTableMetaData.CONTENT_ITEM_TYPE;
		case INCOMING_THING_COLLECTION_URI_INDICATOR:
			return ThingTableMetaData.CONTENT_TYPE;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
	}
	
	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
			String sortOrder) {
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		
		switch (sUriMatcher.match(uri)) {
		case INCOMING_THING_COLLECTION_URI_INDICATOR:
			qb.setTables(ThingTableMetaData.TABLE_NAME);
			qb.setProjectionMap(sThingsProjectionMap);
			break;
		
		case INCOMING_SINGLE_THING_URI_INDICATOR:
			qb.setTables(ThingTableMetaData.TABLE_NAME);
			qb.setProjectionMap(sThingsProjectionMap);
			qb.appendWhere(ThingTableMetaData._ID + "=" + uri.getPathSegments().get(1));
			break;
			
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
		
		String orderBy;
		if (TextUtils.isEmpty(sortOrder)) {
			orderBy = ThingTableMetaData.DEFAULT_SORT_ORDER;
		} else {
			orderBy = sortOrder;
		}
		
		SQLiteDatabase db = mOpenHelper.getReadableDatabase();
		Cursor c = null;
		if (sortOrder != null)
			c = db.query(QingzhouProviderMetaData.THINGS_TABLE_NAME, projection, selection, selectionArgs, null, null, orderBy);
		else
			c = qb.query(db, projection, selection, selectionArgs, null, null, orderBy);
		int i = c.getCount();
		
		c.setNotificationUri(getContext().getContentResolver(), uri);		
		return c;
	}
	
	@Override
	public Uri insert(Uri uri, ContentValues values) {
		if (sUriMatcher.match(uri) != INCOMING_THING_COLLECTION_URI_INDICATOR) {
			throw new IllegalArgumentException("Unknow URI " + uri);
		}
		
		Long now = Long.valueOf(System.currentTimeMillis());
		
		String tableName = null;
		Uri contentUri = null;
		
		switch (sUriMatcher.match(uri)) {
		case INCOMING_THING_COLLECTION_URI_INDICATOR:
			tableName = ThingTableMetaData.TABLE_NAME;
			contentUri = ThingTableMetaData.CONTENT_URI;
			
			if (values.containsKey(ThingTableMetaData.CREATED_DATE) == false) {
				values.put(ThingTableMetaData.CREATED_DATE, now);
			}
			
			if (values.containsKey(ThingTableMetaData.MODIFIED_DATE) == false) {
				values.put(ThingTableMetaData.MODIFIED_DATE, now);
			}
			
			if (values.containsKey(ThingTableMetaData.DESCRIPTION) == false) {
				throw new IllegalArgumentException("Need thing description");
			}
			break;
		}
		
		
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		long rowId = db.insert(tableName, null, values);
		
		if (rowId > 0) {
			Uri insertedUri = ContentUris.withAppendedId(contentUri, rowId);
			getContext().getContentResolver().notifyChange(insertedUri, null);
			return insertedUri;
		}
		
		throw new SQLException("Failed to insert row into " + contentUri);
	}
	
	@Override
	public int update(Uri uri, ContentValues values, String where, String[] whereArgs) {
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		int count;
		switch (sUriMatcher.match(uri)) {
		case INCOMING_THING_COLLECTION_URI_INDICATOR:
			count = db.update(ThingTableMetaData.TABLE_NAME, values, where, whereArgs);
			break;
		case INCOMING_SINGLE_THING_URI_INDICATOR:
			String rowId = uri.getPathSegments().get(1);
			count = db.update(ThingTableMetaData.TABLE_NAME, values, ThingTableMetaData._ID + "=" + rowId
					+ (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs);
			break;
		default:
			throw new IllegalArgumentException("Unknow URI " + uri);
		}
		
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}
	
	@Override
	public int delete(Uri uri, String where, String[] whereArgs) {
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		int count;
		
		switch (sUriMatcher.match(uri)) {
		case INCOMING_THING_COLLECTION_URI_INDICATOR:
			count = db.delete(ThingTableMetaData.TABLE_NAME, where, whereArgs);
			break;
		case INCOMING_SINGLE_THING_URI_INDICATOR:
			String rowId = uri.getPathSegments().get(1);
			count = db.delete(ThingTableMetaData.TABLE_NAME, ThingTableMetaData._ID + "=" + rowId
					+ (!TextUtils.isEmpty(where) ? " AND (" + where + ")" : ""), whereArgs);
			break;
		default:
			throw new IllegalArgumentException("Unknow URI " + uri);
		}
		
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}
}
