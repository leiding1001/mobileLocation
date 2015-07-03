package com.driver.location;

import android.os.Bundle;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.baidu.mapapi.map.MapView;
import com.driver.location.fragments.GpsMapFragment;
import com.driver.location.fragments.LocationDataFragment;
import com.driver.location.gps.GPSLoggerServiceManager;

public class GpsMainActivity extends SherlockFragmentActivity {

	private static final String TAG = "GpsMapActivity";

	private FragmentTabHost tabHost ;
	
	MapView mMapView = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_activity);
		getSupportActionBar().setDisplayShowTitleEnabled(false);
	    setContentView(R.layout.main_activity);
	    tabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
	    tabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);
	    addTab(tabHost, "map", R.layout.tab_map, GpsMapFragment.class);
	    addTab(tabHost,"data", R.layout.tab_data, LocationDataFragment.class);
	}
	
	private void addTab(FragmentTabHost tabHost, String label, int layoutId, Class<?> childFragment) {
		View tabItem = LayoutInflater.from(this).inflate(layoutId, null);
		tabHost.addTab(tabHost.newTabSpec(label).setIndicator(tabItem), childFragment, null);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.options_menu, menu);
		return super.onCreateOptionsMenu(menu);// startup reporting location
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.gps_state:
			GPSLoggerServiceManager.getIntance().getLoggingState() ;
			return true;
		case R.id.gps_start:
			GPSLoggerServiceManager.getIntance().startGPSLogging() ;
			return true;
		case R.id.gps_stop:
			GPSLoggerServiceManager.getIntance().stopGPSLogging() ;
			return true;
		case R.id.gps_startUp:
			GPSLoggerServiceManager.getIntance().startup(this, null) ;
			return true;
		case R.id.gps_shutdown:
			GPSLoggerServiceManager.getIntance().shutdown(this) ;
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}
}
