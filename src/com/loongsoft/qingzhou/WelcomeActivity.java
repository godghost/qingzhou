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
import java.util.List;

import org.jraf.android.backport.switchwidget.Switch;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

public class WelcomeActivity extends Activity {
	private ViewPager mPager;
	private List<View> mPagerViews;
	private List<ImageView> mDots;
	
	LayoutInflater mInflater;
	
	EditText mUserName;
	EditText mPassword;
	EditText mPasswordConfirmed;
	Switch mPasswordSwitch;
	
	private final static int TOAST_LENGTH = 1000;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.welcome);
		
		InitViewPager();
		
		InitDots();
	
		mPager.setAdapter(new WelcomePagerAdapter(mPagerViews));
		mPager.setCurrentItem(0);
	}

	private void InitViewPager() {
		mPager = (ViewPager) findViewById(R.id.welcome_view_pager);
		mPagerViews = new ArrayList<View>();
		mInflater = getLayoutInflater();
		mPagerViews.add(mInflater.inflate(R.layout.welcome_intro, null));
		mPagerViews.add(mInflater.inflate(R.layout.welcome_overview, null));
		
		View welcome_promise = mInflater.inflate(R.layout.welcome_promise, null);
		
		mPagerViews.add(welcome_promise);
		
		Button quit = (Button)welcome_promise.findViewById(R.id.welcome_promise_quit);
		Button agree = (Button)welcome_promise.findViewById(R.id.welcome_promise_agree);
		
		quit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		
		agree.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (mPagerViews.size() < 4) {
					View welcome_setting = mInflater.inflate(
							R.layout.welcome_settings, null);
					mPagerViews.add(welcome_setting);

					addDot();
					
					Button finish = (Button)welcome_setting.findViewById(R.id.welcome_finish);
					final EditText name = (EditText)welcome_setting.findViewById(R.id.welcome_name);
					finish.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							//validate input
							if (TextUtils.isEmpty(mUserName.getText().toString())) {
								Toast.makeText(WelcomeActivity.this, 
										WelcomeActivity.this.getResources().getString(R.string.welcome_settings_name), 
										TOAST_LENGTH).show();
								return;
							}
							
							if (mPasswordSwitch.isChecked()) {
								if (TextUtils.isEmpty(mPassword.getText().toString()) ||
										TextUtils.isEmpty(mPasswordConfirmed.getText().toString())) {
									Toast.makeText(WelcomeActivity.this, 
										WelcomeActivity.this.getResources().getString(R.string.password_is_empty),
										TOAST_LENGTH).show();
									return;
								}
								
								if (!TextUtils.equals(mPassword.getText().toString(), 
										mPasswordConfirmed.getText().toString())) {
									Toast.makeText(WelcomeActivity.this, 
											WelcomeActivity.this.getResources().getString(R.string.password_not_equal), 
											TOAST_LENGTH).show();
									return;
								}
							}
							
							SharedPreferences pref = getSharedPreferences(MetaData.PREFS_NAME, MODE_PRIVATE);
							Editor editor = pref.edit();
							
							editor.putString(MetaData.USER_NAME, name.getText().toString());
							editor.putLong(MetaData.PROMISE_TIME, System.currentTimeMillis());
							editor.putBoolean(MetaData.FIRST_RUN, false);
							editor.putBoolean(MetaData.USE_PASSWORD, mPasswordSwitch.isChecked());
							editor.putString(MetaData.PASSWORD, mPassword.getText().toString());
							
							editor.commit();
							
							MetaData.isFirstRun = false;
							MetaData.userName = mUserName.getText().toString();
							MetaData.usePassword = mPasswordSwitch.isChecked();
							MetaData.promiseTime = System.currentTimeMillis();
							MetaData.password = mPassword.getText().toString();
							
							Intent intent = new Intent(WelcomeActivity.this, SplashActivity.class);
							startActivity(intent);
							
							WelcomeActivity.this.finish();
						}
					});
					
					mUserName = (EditText)welcome_setting.findViewById(R.id.welcome_name);
					mPasswordSwitch = (Switch)welcome_setting.findViewById(R.id.welcome_password_switch);
					mPassword = (EditText)welcome_setting.findViewById(R.id.welcome_password);
					mPasswordConfirmed = (EditText)welcome_setting.findViewById(R.id.welcome_password_confirmed);
					
					mPasswordSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {
						
						@Override
						public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
							if (isChecked) {
								mPassword.setVisibility(View.VISIBLE);
								mPasswordConfirmed.setVisibility(View.VISIBLE);
							} else {
								mPassword.setVisibility(View.GONE);
								mPasswordConfirmed.setVisibility(View.GONE);
							}
						}
					});
				}
				
				mPager.setCurrentItem(mPagerViews.size()-1,true);
			}
		});
		
		mPager.setOnPageChangeListener(new OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int index) {
				for (int i = 0; i < mDots.size(); i++) {
					mDots.get(i).setImageDrawable(
							getResources().getDrawable(R.drawable.welcome_dot_normal));
				}
				mDots.get(index).setImageDrawable(
						getResources().getDrawable(R.drawable.welcome_dot_selected));
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}
			
			@Override
			public void onPageScrollStateChanged(int arg0) {

			}
		});
	}
	
	private void InitDots() {
		mDots = new ArrayList<ImageView>();
		
		for (int i = 0; i < mPagerViews.size(); i++) {
			addDot();
		}
		
		mDots.get(0).setImageDrawable(
				getResources().getDrawable(R.drawable.welcome_dot_selected));
	}
	
	private void addDot() {
		ImageView dotImageView = new ImageView(this);
		dotImageView.setImageDrawable(getResources().getDrawable(R.drawable.welcome_dot_normal));
		
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(15, 15);
		params.setMargins(5, 5, 5, 5);
		dotImageView.setLayoutParams(params);
		
		LinearLayout welcome_dots_layout = (LinearLayout)findViewById(R.id.welcome_dots);
		welcome_dots_layout.addView(dotImageView);
		mDots.add(dotImageView);
	}

	public class WelcomePagerAdapter extends PagerAdapter {
		public List<View> mListViews;

		public WelcomePagerAdapter(List<View> mListViews) {
			this.mListViews = mListViews;
		}

		@Override
		public void destroyItem(View arg0, int arg1, Object arg2) {
			((ViewPager) arg0).removeView(mListViews.get(arg1));
		}

		@Override
		public void finishUpdate(View arg0) {
		}

		@Override
		public int getCount() {
			return mListViews.size();
		}

		@Override
		public Object instantiateItem(View arg0, int arg1) {
			((ViewPager) arg0).addView(mListViews.get(arg1), 0);
			return mListViews.get(arg1);
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == (arg1);
		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {
		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void startUpdate(View arg0) {
		}
	}
}
