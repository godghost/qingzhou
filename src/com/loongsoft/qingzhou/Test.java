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

import java.util.Random;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.loongsoft.qingzhou.QingzhouProviderMetaData.ThingTableMetaData;

public class Test {
	public static final long A_DAY_LONG_IN_MILL = 86400000;
	private static int totalDays = 1095;
	private static int thingsMinNum = 1;
	private static int thingsMaxNum = 10;
	private static int thingMinLen = 2;
	private static int thingMaxLen = 20;
	private static int thingMinQuantity = 0;
	private static int thingMaxQuantity = 1000;
	
	private static String[] preThingsDes = {
		"吃饭","睡觉","打豆豆","轻舟","看书","电影","音乐","泡妞","媾仔","上网",
		"上班","上学","看电视","走路","回家","户外运动","打篮球","踢足球","乒乓球","健身",
		"学习","自修","图书馆","逛街","打电话","聊QQ","解决难题","发呆","修电脑","煮饭",
		"下馆子","搜索","下片","喝咖啡","约人","纪念日","装软件","动漫","写代码","搬砖",
	};
	
	public static void generateFakeData(Context context, int daysNum) {
		Random ran = new Random();
		RandomHan rh = new RandomHan();
		
		Long cur = System.currentTimeMillis();
		Long des = cur - A_DAY_LONG_IN_MILL * daysNum;
		Log.i("cur,des", cur+","+des);
		
		ContentResolver resolver = context.getContentResolver();
		ContentValues values = new ContentValues();
		
		//First,delete all data in original database
		int count = resolver.delete(ThingTableMetaData.CONTENT_URI,null,null);
		Log.i("Deleted", count+"");
		
		for (long i =cur; i >= des; i-=A_DAY_LONG_IN_MILL) {
			int thingsNum = ran.nextInt(thingsMaxNum-thingsMinNum) + thingsMinNum;
			for (int n = 0; n < thingsNum; n++) {
				int thingIndex = ran.nextInt(preThingsDes.length);
				int quantity = ran.nextInt(thingMaxQuantity);
				long created_date = i + n;
				
				String descString = preThingsDes[thingIndex];
				String quanString = quantity == 0? "": Integer.toString(quantity);

				values.put(ThingTableMetaData.DESCRIPTION, descString);
				values.put(ThingTableMetaData.QUANTITY, quanString);
				values.put(ThingTableMetaData.CREATED_DATE, created_date);
				values.put(ThingTableMetaData.MODIFIED_DATE, created_date);
				
				Uri insertedUri = resolver.insert(ThingTableMetaData.CONTENT_URI, values);
			}
		}
	}
	
	static class RandomHan {
	    private Random ran = new Random();
	    private final static int delta = 0x4eff - 0x4e00 + 1;
	    
	    public char getRandomHan() {
	        return (char)(0x4e00 + ran.nextInt(delta)); 
	    }
	    
	    public String getRandomHans(int len) {
	    	StringBuilder hans = new StringBuilder();
	    	
	    	for (int i=0; i<len; i++) {
	    		hans.append(getRandomHan());
	    	}
	    	
	    	return hans.toString();
	    }
	}
}
