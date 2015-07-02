package com.driver.gps;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.support.v4.app.FragmentTabHost;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.baidu.mapapi.map.MapView;
import com.driver.gps.R;
import com.driver.gps.fragments.GpsMapFragment;
import com.driver.gps.fragments.LocationDataFragment;

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
	    startGpsService(this);
	}
	
	private void addTab(FragmentTabHost tabHost, String label, int layoutId, Class<?> childFragment) {
		View tabItem = LayoutInflater.from(this).inflate(layoutId, null);
		tabHost.addTab(tabHost.newTabSpec(label).setIndicator(tabItem), childFragment, null);
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
//		acquireWakeLock();
		
	}

	@Override
	protected void onPause() {
		super.onPause();
//		releaseWakeLock();
	}
	/*
	 * start service Automatically report goe location
	 */
	public void startGpsService(Context context) {
		//
		Intent intent = new Intent(context, GpsService.class);
		startService(intent) ;
		
		
		
		
		
		
		/*PendingIntent sender = PendingIntent.getService(context, 0, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		AlarmManager alarm = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		// 1.cancel
		alarm.cancel(sender);

		// 2.start alarm and add delay time.
		alarm.setRepeating(AlarmManager.RTC_WAKEUP, 0, 5*1000, sender);*/

	}
	/*
	 * start service Automatically report goe location
	 */
	public void startAutoService(Context context) {
		//
		Intent intent = new Intent(context, BaiduLocService.class);
		PendingIntent sender = PendingIntent.getService(context, 0, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		AlarmManager alarm = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		// 1.cancel
		Log.i(TAG, "startAutoService.1.cancel auto_report_location");
		alarm.cancel(sender);

		// 2.start alarm and add delay time.
		Log.i(TAG,
				"startAutoService.2.start auto_report_location and Reportgeo");
		alarm.setRepeating(AlarmManager.RTC_WAKEUP, 0, 5*1000, sender);

	}
	
	/**
	 * cancel alarm Automatically report goe location
	 * 
	 * @param context
	 */
	public void cancelAutoService(Context context) {
		
		Intent intent = new Intent(context, BaiduLocService.class);
		PendingIntent sender = PendingIntent.getService(context, 0, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		
		if (sender != null) {
			AlarmManager alarm = (AlarmManager) context
					.getSystemService(Context.ALARM_SERVICE);
			alarm.cancel(sender);
		}
	}
	private WakeLock wakeLock = null;

	/**
	 * 获取电源锁，保持该服务在屏幕熄灭时仍然获取CPU时，保持运行
	 */
	private void acquireWakeLock() {
		if (null == wakeLock) {
			PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
			wakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, getClass()
					.getCanonicalName());
			if (null != wakeLock) {
				wakeLock.acquire();
			}
		}
	}

	// 释放设备电源锁
	private void releaseWakeLock() {
		if (null != wakeLock && wakeLock.isHeld()) {
			wakeLock.release();
			wakeLock = null;
		}
	}
}
