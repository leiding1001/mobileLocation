package com.driver.gps;

import java.util.Date;
import java.util.Iterator;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;

import com.driver.gps.db._Location;
import com.driver.gps.db._LocationTable;

public class GpsService extends Service implements LocationListener{
    private LocationManager lm;
    private static final String TAG="GpsActivity";
    
   

    @Override
    public void onCreate() {
    	super.onCreate();
    	lm=(LocationManager)getSystemService(Context.LOCATION_SERVICE);
    }
   
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
    	
    	//参数1，设备：有GPS_PROVIDER和NETWORK_PROVIDER两种
        //参数2，位置信息更新周期，单位毫秒    
        //参数3，位置变化最小距离：当位置距离变化超过此值时，将更新位置信息    
        //参数4，监听    
        //备注：参数2和3，如果参数3不为0，则以参数3为准；参数3为0，则通过时间来定时更新；两者为0，则随时刷新   
        
        // 1秒更新一次，或最小位移变化超过1米更新一次；
        //注意：此处更新准确度非常低，推荐在service里面启动一个Thread，在run中sleep(10000);然后执行handler.sendMessage(),更新位置
    	acquireWakeLock() ;
    	String provider = lm.getBestProvider(getCriteria(), true);
        lm.requestLocationUpdates(provider, 1000, 0, this);
    	return super.onStartCommand(intent, flags, startId);
    }
    @Override
    public void onDestroy() {
    	releaseWakeLock() ;
    	super.onDestroy();
    }
    //位置监听
        /**
         * 位置信息变化时触发
         */
        public void onLocationChanged(Location location) {
            Log.i(TAG, "时间："+location.getTime()); 
            Log.i(TAG, "经度："+location.getLongitude()); 
            Log.i(TAG, "纬度："+location.getLatitude()); 
            Log.i(TAG, "海拔："+location.getAltitude()); 
            
            _Location _location = new _Location();
			_location.accuracy =location.getAccuracy() ;
			_location.latitude = location.getLatitude() ;
			_location.longitude =location.getLongitude() ;
			_location.from = location.getProvider() ;
			_location.creatTime = new Date();
			getContentResolver().insert(_LocationTable.CONTENT_URI,_LocationTable.toContentValues(_location));
//			lm.removeUpdates(this);
//			stopSelf() ;
        }
        
        /**
         * GPS状态变化时触发
         */
        public void onStatusChanged(String provider, int status, Bundle extras) {
            switch (status) {
            //GPS状态为可见时
            case LocationProvider.AVAILABLE:
                Log.i(TAG, "当前GPS状态为可见状态");
                break;
            //GPS状态为服务区外时
            case LocationProvider.OUT_OF_SERVICE:
                Log.i(TAG, "当前GPS状态为服务区外状态");
                break;
            //GPS状态为暂停服务时
            case LocationProvider.TEMPORARILY_UNAVAILABLE:
                Log.i(TAG, "当前GPS状态为暂停服务状态");
                break;
            }
        }
    
        /**
         * GPS开启时触发
         */
        public void onProviderEnabled(String provider) {
        }
    
        /**
         * GPS禁用时触发
         */
        public void onProviderDisabled(String provider) {
        }
    
    /**
     * 返回查询条件
     * @return
     */
    private Criteria getCriteria(){
        Criteria criteria=new Criteria();
        //设置定位精确度 Criteria.ACCURACY_COARSE比较粗略，Criteria.ACCURACY_FINE则比较精细 
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);  
        //设置是否要求速度
        criteria.setSpeedRequired(false);
        // 设置是否允许运营商收费  
        criteria.setCostAllowed(false);
        //设置是否需要方位信息
        criteria.setBearingRequired(false);
        //设置是否需要海拔信息
        criteria.setAltitudeRequired(false);
        // 设置对电源的需求  
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        return criteria;
    }


	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
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