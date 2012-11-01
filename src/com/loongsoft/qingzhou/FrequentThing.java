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

public class FrequentThing {
	private String mName;
	private int mCount;
	private Long mDateInMills;
	
	public FrequentThing(String name, int count) {
		mName = name;
		mCount = count;
		setDateInMills(System.currentTimeMillis());
	}
	
	public FrequentThing(String name, int count, Long dateInMills) {
		mName = name;
		mCount = count;
		mDateInMills = dateInMills;
	}
	
	public String getName() {
		return mName;
	}
	
	public int getCount() {
		return mCount;
	}
	
	public void setCount(int count) {
		mCount = count;
	}
	
	public void setDateInMills(Long dayInMills) {
		mDateInMills = dayInMills;
	}

	public Long getDateInMills() {
		return mDateInMills;
	}
}
