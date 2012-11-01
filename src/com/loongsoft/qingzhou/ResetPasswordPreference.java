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

import android.app.AlertDialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.preference.DialogPreference;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ResetPasswordPreference extends DialogPreference {
	View mResetPasswordView = null;
	EditText mPassword = null;
	EditText mPasswordConfirmed = null;
	
    public ResetPasswordPreference(Context ctxt) {
        this(ctxt, null);
    }

    public ResetPasswordPreference(Context ctxt, AttributeSet attrs) {
        this(ctxt, attrs, 0);
    }

    public ResetPasswordPreference(Context ctxt, AttributeSet attrs, int defStyle) {
        super(ctxt, attrs, defStyle);

        setPositiveButtonText(getContext().getResources().getString(android.R.string.yes));
        setNegativeButtonText(getContext().getResources().getString(android.R.string.cancel));
        setPersistent(true);
    }

    @Override
    protected View onCreateDialogView() {
        LayoutInflater inflater = new LayoutInflater(getContext()) {
			@Override
			public LayoutInflater cloneInContext(Context newContext) {
				return null;
			}
		};
		
		mResetPasswordView = inflater.inflate(R.layout.preference_reset_password, null);
		mPassword = (EditText)mResetPasswordView.findViewById(R.id.reset_password);
		mPasswordConfirmed = (EditText)mResetPasswordView.findViewById(R.id.reset_password_confirmed);
		
        return mResetPasswordView;
    }

    @Override
    protected void onBindDialogView(View v) {
        super.onBindDialogView(v);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
    	super.onDialogClosed(positiveResult);
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return(a.getString(index));
    }

    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {

    }
    
    @Override 
    protected void showDialog(Bundle state) {
    	super.showDialog(state);
    	
    	Button pos = ((AlertDialog) getDialog()).getButton(AlertDialog.BUTTON_POSITIVE);
    	pos.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (validate()) {
		        	//save the changed result
		            if (callChangeListener(mPassword.getText().toString())) {
		                persistString(mPassword.getText().toString());
		            }
					getDialog().dismiss();
				}
			}
		});
    }
    
    public boolean validate() {
    	String p1 = mPassword.getText().toString();
    	String p2 = mPasswordConfirmed.getText().toString();
    	if (TextUtils.isEmpty(p1) || TextUtils.isEmpty(p2)) {
    		Toast.makeText(getContext(), 
    				getContext().getResources().getString(R.string.password_is_empty), 
    				2000).show();
    		return false;
    	}
    	
    	if (!TextUtils.equals(p1, p2)) {
    		Toast.makeText(getContext(), 
    				getContext().getResources().getString(R.string.password_not_equal), 
    				2000).show();
    		return false;
    	}
    	
    	return true;
    }
}