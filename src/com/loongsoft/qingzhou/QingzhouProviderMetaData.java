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

import android.net.Uri;
import android.provider.BaseColumns;

public class QingzhouProviderMetaData {
	public static final String AUTHORITY = "com.loongsoft.provider.QingzhouProvider";
	
	public static final String DATABASE_NAME = "qingzhou.db";
	public static final int DATABASE_VERSION = 1;
	public static final String THINGS_TABLE_NAME = "things";
	
	private QingzhouProviderMetaData() {}
	
	public static final class ThingTableMetaData implements BaseColumns {
		private ThingTableMetaData() {}
		
		public static final String TABLE_NAME = "things";
		
		public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/things");
		
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.qingzhou.thing";
		
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.qingzhou.thing";
		
		public static final String DEFAULT_SORT_ORDER = "created ASC";
		
		public static final String DESCRIPTION = "description";
		
		public static final String QUANTITY = "quantity";
		
		public static final String CREATED_DATE = "created";
		
		public static final String MODIFIED_DATE = "modified";
		
		public static final String COUNT = "count_num";
	}
}
