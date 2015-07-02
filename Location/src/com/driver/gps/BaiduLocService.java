package com.driver.gps;

import java.text.DecimalFormat;
import java.util.Date;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.driver.gps.db._Location;
import com.driver.gps.db._LocationTable;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;

public class BaiduLocService extends Service {

	private LocationClient bdClient;
	private MyLocationListener bdListener;

	public BaiduLocService() {
	}

	@Override
	public void onCreate() {
		super.onCreate();
		bdClient = new LocationClient(getApplicationContext());
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(LocationMode.Hight_Accuracy);
		option.setCoorType("bd09ll");
		option.setScanSpan(5000);
		option.setIsNeedAddress(false);
		option.setNeedDeviceDirect(false);
		option.setOpenGps(true);
		option.setAddrType("all"); // 返回地址类型
		option.disableCache(true) ;//禁止启用缓存定位
		option.setIgnoreKillProcess(true);
		bdClient.setLocOption(option);
		bdListener = new MyLocationListener();
		bdClient.registerLocationListener(bdListener);
		bdClient.start();
		System.out.println("----->onCreate");
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		System.out.println("----->onStartCommand");
		bdClient.requestLocation();
		return super.onStartCommand(intent, flags, startId);
	}
	@Override
	public void onDestroy() {
		System.out.println("----->onDestroy");
		super.onDestroy();
	}
	@Override
	public IBinder onBind(Intent intent) {
		System.out.println("----->onBind");
		return null;
	}
	public class MyLocationListener implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			int code = location.getLocType();
			// http://developer.baidu.com/map/geosdk-android-developv4.1.htm#and3.1.1
			// 61: GPS location
			// 62: 扫描整合定位依据失败。
			// 63: network error
			// 65: cached location.
			// 66: offline location. result of requestOfflineLocaiton
			// 67: offline location failed. result of requestOfflineLocaiton
			// 68: offline location, if network error occurs.
			// 161: network location
			// 162~167: server errors
			// 502:key parameter erroor
			// 505:invalid key or not exists
			// 601:key is disabled by developer
			// 602:key mcode mismatch
			// 501～700: key auth error
			if (code == 61 || code == 65|| code == 66 ||  code == 68 || code == 161) {
				if (location != null) {
					DecimalFormat format = new DecimalFormat("###.######");
					String s = format.format(location.getLatitude());
					location.setLatitude(Double.parseDouble(s));
					s = format.format(location.getLongitude());
					location.setLongitude(Double.parseDouble(s));

					if (location.getLatitude() < 3
							|| location.getLatitude() > 55
							|| location.getLongitude() < 72
							|| location.getLongitude() > 137) {
					}else{
						_Location _location = new _Location();
						_location.accuracy =location.getRadius();
						_location.latitude = location.getLatitude() ;
						_location.longitude =location.getLongitude() ; 
						if(code == 61){
							_location.from= "GPS" ;
						}else if(code == 65){
							_location.from= "Cache" ;
						}else if(code == 161){
							_location.from= "Network" ;
						}
						_location.creatTime = new Date();
						getContentResolver().insert(_LocationTable.CONTENT_URI,_LocationTable.toContentValues(_location));
						bdClient.stop();
						if (bdListener != null) {
							bdClient.unRegisterLocationListener(bdListener);
							bdListener = null;
						}
					}
				}
			}
			stopSelf() ;
		}

		@Override
		public void onReceivePoi(BDLocation arg0) {
			// TODO Auto-generated method stub
			
		}
	}

	

	

}
