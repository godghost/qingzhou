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

public class MetaData {
	public final static String PREFS_NAME = "Qingzhou"; 
	public final static String USER_NAME = "Name"; 
	public final static String PROMISE_TIME = "PromiseTime"; 
	public final static String FIRST_RUN = "FirstRun"; 
	public final static String LAST_DATE = "LastDate";
	public final static String USE_PASSWORD = "UsePassword";
	public final static String PASSWORD = "Password";
	public final static String REMIND_HOUR = "RemindHour";
	public final static String REMIND_MINUTE = "RemindMinute";
	
	public static final long A_DAY_LONG_IN_MILL = 86400000;
	
	public static Boolean isFirstRun = false;
	public static Long promiseTime=System.currentTimeMillis();
	public static String userName="";
	public static Long lastDate=System.currentTimeMillis();
	public static Boolean usePassword = false;
	public static String password="";
	public static int remindHour = 22;
	public static int remindMinute = 30;
	
	public static Long magicTimer;
	
	public final static boolean DEBUG_TEST = false;
	public final static boolean DEBUG_MAKE_FAKE_DATA = false;
	
	public final static String VERSION = "V1.0@2012.11";
}
