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

public class Thing {
	private String mId=null;
	private String mDescirption="";
	private String mQuantity="";
	private Long mCreatedDateInMills=null;
	private Long mModifiedDateInMills = null;
	
	public Thing() {
		mId = null;
		mDescirption = "";
		mQuantity = "";
		mCreatedDateInMills = System.currentTimeMillis();
		mModifiedDateInMills = System.currentTimeMillis();
	}
	
	public Thing(String id, String description, String quantity, Long created, Long modified) {
		mId = id;
		mDescirption = description;
		mQuantity = quantity;
		mCreatedDateInMills = created;
		mModifiedDateInMills = modified;
	}
	
	public Thing(String id, String description, String quantity) {
		mId = id;
		mDescirption = description;
		mQuantity = quantity;
		mCreatedDateInMills = System.currentTimeMillis();
		mModifiedDateInMills = System.currentTimeMillis();
	}
	
	public Thing(String description, String quantity) {
		mDescirption = description;
		mQuantity = quantity;
		mCreatedDateInMills = System.currentTimeMillis();
		mModifiedDateInMills = System.currentTimeMillis();
	}
	
	public Thing(String description) {
		mDescirption = description;
		mQuantity = "";
		mCreatedDateInMills = System.currentTimeMillis();
		mModifiedDateInMills = System.currentTimeMillis();
	}
	
	public String getId() {
		return mId;
	}
	
	public String getDescription() {
		return mDescirption;
	}
	
	public String getQuantity() {
		return mQuantity;
	}
	
	public void setId(String id) {
		mId = id;
	}
	
	public void setDesciption(String description) {
		mDescirption = description;
	}
	
	public void setQuantitiy(String quantity) {
		mQuantity = quantity;
	}
	
	public long getCreatedDateInMills() {
		return mCreatedDateInMills;
	}
	
	public void setCreatedDateInMills(Long createdDateInMills) {
		mCreatedDateInMills = createdDateInMills;
	}
	
	public long getModifiedDateInMills() {
		return mModifiedDateInMills;
	}
}
