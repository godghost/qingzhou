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

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;

public class Utils {
	public static Long getZeroOfDayInMills(Long dateInMills) {
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(dateInMills);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		
		return c.getTimeInMillis();
	}
	
	public static Long getZeroOfTodayInMills() {
		return getZeroOfDayInMills(System.currentTimeMillis());
	}
	
	public static Long getZeroOfTomorrowInMills() {
		return getZeroOfDayInMills(System.currentTimeMillis())+MetaData.A_DAY_LONG_IN_MILL;
	}
	
	public static float getPixelsFromDp(Context context, int dp) {
		Resources r = context.getResources();
		float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
		return px;
	}
}
