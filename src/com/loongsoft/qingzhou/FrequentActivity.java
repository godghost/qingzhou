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
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.achartengine.ChartFactory;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class FrequentActivity extends Activity {
	private FrequentAdapter mAdapter = null;
	private ArrayList<FrequentThing> mFrequentThings = null;
	private ThingManager mThingManager = null;
	
	ArrayList<Date> mDateValues = null;
	ArrayList<Double> mValues = null;
	String mThingDesc = null;
	Long mTotal = 0l;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frequent);
        
        ListView list = (ListView)findViewById(R.id.frequent_list);
        list.setDivider(getResources().getDrawable(R.drawable.seprateline_dark));
        
        mThingManager = ThingManager.getThingManager(this);
        mFrequentThings = mThingManager.getMostFrequentThings();
        
        mAdapter = new FrequentAdapter(this, mFrequentThings);

        list.setAdapter(mAdapter);
        
        list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> paretn, View view, int position,
					long id) {
				prepareDatas(position);
				Intent intent = execute(FrequentActivity.this);
				
			    //Loong's hack for achartengine
			    intent.putExtra("IsCustomAnimation", true);
			    intent.putExtra("InAnimation", R.anim.push_left_in);
			    intent.putExtra("OutAnimation", R.anim.push_left_out);
			    
				startActivity(intent);
				
				//activity switch animation
				overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
			}
		});
    }
    
    private void prepareDatas(int position) {
    	String thingDesc = mFrequentThings.get(position).getName();
    	mThingDesc = thingDesc;
    	
    	ArrayList<FrequentThing> things = mThingManager.getThingAllData(thingDesc);
    	HashMap<Long, FrequentThing> maps = new HashMap<Long, FrequentThing>();
    	
    	for (int i = 0; i < things.size(); i++) {
    		FrequentThing thing = things.get(i);
    		thing.setDateInMills(Utils.getZeroOfDayInMills(thing.getDateInMills()));
    		
    		if (!maps.containsKey(thing.getDateInMills())) {
    			maps.put(thing.getDateInMills(), thing);
    		} else {
    			maps.get(thing.getDateInMills()).setCount(
    					thing.getCount() + maps.get(thing.getDateInMills()).getCount());
    		}
    	}
    	
    	for (long d=things.get(0).getDateInMills(); d < Utils.getZeroOfTomorrowInMills();
    			d+=MetaData.A_DAY_LONG_IN_MILL) {
    		if (!maps.containsKey(d)) {
    			FrequentThing thing = new FrequentThing(thingDesc, 0, d);
    			maps.put(d, thing);
    		}
    	}
    	
    	things = new ArrayList<FrequentThing>(maps.values());
    	Collections.sort(things, new Comparator<FrequentThing>() {
            @Override
            public int compare(FrequentThing t1, FrequentThing t2) {
                return t1.getDateInMills().compareTo(t2.getDateInMills());
            }
        });
    	
    	mDateValues = new ArrayList<Date>();
    	mValues = new ArrayList<Double>();
    	mTotal = 0l;
    	for (int i = 0; i < things.size(); i++) {
    		FrequentThing thing = things.get(i);
    		mDateValues.add(new Date(thing.getDateInMills()));
    		mValues.add((double)thing.getCount());
    		mTotal += thing.getCount();
    	}
    }

	private Intent execute(Context context) {
		String title = getResources().getString(R.string.chart_summary);
		title = title.replace("{start}", 
				DateFormat.format(
						getResources().getString(R.string.chart_date_format),
						mDateValues.get(0)));
		title = title.replace("{end}", 
				DateFormat.format(
						getResources().getString(R.string.chart_date_format),
						mDateValues.get(mDateValues.size()-1)));
		title = title.replace("{total}", Long.toString(mTotal));
		String[] titles = new String[] { title };
		List<ArrayList<Date>> dates = new ArrayList<ArrayList<Date>>();
		List<ArrayList<Double>> values = new ArrayList<ArrayList<Double>>();

		dates.add(mDateValues);

		values.add(mValues);
		int[] colors = new int[] { getResources().getColor(R.color.graph_dot_color) };
		PointStyle[] styles = new PointStyle[] { PointStyle.CIRCLE };
		XYMultipleSeriesRenderer renderer = buildRenderer(colors, styles);
		setChartSettings(renderer, mThingDesc, "", "",
				mDateValues.get(0).getTime(),
				mDateValues.get(mDateValues.size()-1).getTime(),
				Collections.min(mValues),
				Collections.max(mValues),
				Color.GRAY, Color.WHITE);
		renderer.setYLabels(10);
		renderer.setXLabels(10);
		renderer.setMarginsColor(getResources().getColor(R.color.frequent_background));

		renderer.setPanEnabled(true, false);

		return ChartFactory.getTimeChartIntent(context,
				buildDateDataset(titles, dates, values), renderer,
				getResources().getString(R.string.aday_date_format));
	}

	private XYMultipleSeriesRenderer buildRenderer(int[] colors,
			PointStyle[] styles) {
		XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
		setRenderer(renderer, colors, styles);
		return renderer;
	}

	private void setRenderer(XYMultipleSeriesRenderer renderer, int[] colors,
			PointStyle[] styles) {
		renderer.setAxisTitleTextSize(
				getResources().getDimensionPixelSize(R.dimen.title_text_size));
		renderer.setChartTitleTextSize((int)Utils.getPixelsFromDp(this, 22));
		renderer.setLabelsTextSize(15);
		renderer.setLegendTextSize(
				getResources().getDimensionPixelSize(R.dimen.chart_legend_sise));
		renderer.setPointSize(5f);
		renderer.setMargins(new int[] { 110, 15, 20, 0 });
		renderer.setChartTitleMargin((int)Utils.getPixelsFromDp(this, 10));
		
		int length = colors.length;
		int color_below = getResources().getColor(R.color.graph_dot_fill_color);
		renderer.setBackgroundColor(getResources().getColor(R.color.frequent_background));
		renderer.setApplyBackgroundColor(true);
		for (int i = 0; i < length; i++) {
			XYSeriesRenderer r = new XYSeriesRenderer();
			r.setColor(colors[i]);
			r.setPointStyle(styles[i]);
			r.setFillPoints(true);
			r.setFillBelowLineColor(color_below);
			r.setFillBelowLine(true);
			renderer.addSeriesRenderer(r);
		}
	}

	private void setChartSettings(XYMultipleSeriesRenderer renderer,
			String title, String xTitle, String yTitle, double xMin,
			double xMax, double yMin, double yMax, int axesColor,
			int labelsColor) {
		renderer.setChartTitle(title);
		renderer.setXTitle(xTitle);
		renderer.setYTitle(yTitle);
		renderer.setXAxisMin(xMin);
		renderer.setXAxisMax(xMax);
		renderer.setYAxisMin(yMin);
		renderer.setYAxisMax(yMax);
		renderer.setAxesColor(axesColor);
		renderer.setLabelsColor(labelsColor);
	}
	
	private XYMultipleSeriesDataset buildDateDataset(String[] titles,
			List<ArrayList<Date>> xValues, List<ArrayList<Double>> yValues) {
		XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
		int length = titles.length;
		for (int i = 0; i < length; i++) {
			TimeSeries series = new TimeSeries(titles[i]);
			ArrayList<Date> xV = xValues.get(i);
			ArrayList<Double> yV = yValues.get(i);
			int seriesLength = xV.size();
			for (int k = 0; k < seriesLength; k++) {
				series.add(xV.get(k), yV.get(k));
			}
			dataset.addSeries(series);
		}
		return dataset;
	}
	
	@Override
	public void finish() {
		super.finish();
		//activity switch animation
		overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
	}
}
