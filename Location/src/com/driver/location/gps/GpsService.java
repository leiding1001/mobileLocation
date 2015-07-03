package com.driver.location.gps;

import java.util.Date;

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
import android.os.RemoteException;
import android.util.Log;

import com.driver.location.Constants;
import com.driver.location.db._Location;
import com.driver.location.db._LocationTable;

public class GpsService extends Service implements LocationListener {
	private LocationManager mLocationManager;
	private static final String TAG = "GpsService";

	private int mLoggingState = Constants.STOPPED;

	@Override
	public void onCreate() {
		super.onCreate();
		Log.i(TAG, "----------->onCreate<-------------------");
		mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
	}

	@Override
	public IBinder onBind(Intent intent) {
		Log.i(TAG, "-----------> onBind<-------------------");
		return new IGPSLoggerServiceRemote.Stub() {

			@Override
			public long startLogging() throws RemoteException {
				Log.i(TAG, "-----------> startLogging<-------------------");
				GpsService.this.startLogging();
				return 0;
			}
			@Override
			public void stopLogging() throws RemoteException {
				Log.i(TAG, "-----------> stopLogging<-------------------");
				GpsService.this.stopLogging();
			}
			@Override
			public int loggingState() throws RemoteException {
				Log.i(TAG, "-----------> loggingState<-------------------");
				return mLoggingState;
			}
		};
	}

	public synchronized void startLogging() {
		if (this.mLoggingState == Constants.STOPPED) {
			this.mLoggingState = Constants.LOGGING;
			updateWakeLock();
			 //参数1，设备：有GPS_PROVIDER和NETWORK_PROVIDER两种 //参数2，位置信息更新周期，单位毫秒
			 //参数3，位置变化最小距离：当位置距离变化超过此值时，将更新位置信息 //参数4，监听
			 //备注：参数2和3，如果参数3不为0，则以参数3为准；参数3为0，则通过时间来定时更新；两者为0，则随时刷新
			 // 1秒更新一次，或最小位移变化超过1米更新一次；
			 String provider = mLocationManager.getBestProvider(getCriteria(), true);
			 mLocationManager.requestLocationUpdates(provider, 5*1000, 0, this);
		}
	}
   public synchronized void stopLogging(){
	   if (this.mLoggingState == Constants.LOGGING) {
	      mLoggingState = Constants.STOPPED;
	      updateWakeLock();
	      mLocationManager.removeGpsStatusListener(mStatusListener);
	      mLocationManager.removeUpdates(this);
	   }
   }
	@Override
	public void onDestroy() {
		Log.i(TAG, "----------->onDestroy<-------------------");
		stopLogging() ;
		if (this.mWakeLock != null){
	         this.mWakeLock.release();
	         this.mWakeLock = null;
	    }
		super.onDestroy();
	}

	// 位置监听
	/**
	 * 位置信息变化时触发
	 */
	public void onLocationChanged(Location location) {
		Log.i(TAG, "时间：" + location.getTime());
		Log.i(TAG, "经度：" + location.getLongitude());
		Log.i(TAG, "纬度：" + location.getLatitude());
		Log.i(TAG, "海拔：" + location.getAltitude());

		_Location _location = new _Location();
		_location.accuracy = location.getAccuracy();
		_location.latitude = location.getLatitude();
		_location.longitude = location.getLongitude();
		_location.from = location.getProvider();
		_location.creatTime = new Date();
		getContentResolver().insert(_LocationTable.CONTENT_URI,
				_LocationTable.toContentValues(_location));
	}
	
	/**
	 * GPS状态变化时触发
	 */
	public void onStatusChanged(String provider, int status, Bundle extras) {
		switch (status) {
		// GPS状态为可见时
		case LocationProvider.AVAILABLE:
			Log.i(TAG, "当前GPS状态为可见状态");
			break;
		// GPS状态为服务区外时
		case LocationProvider.OUT_OF_SERVICE:
			Log.i(TAG, "当前GPS状态为服务区外状态");
			break;
		// GPS状态为暂停服务时
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
	 * 
	 * @return
	 */
	private Criteria getCriteria() {
		Criteria criteria = new Criteria();
		// 设置定位精确度 Criteria.ACCURACY_COARSE比较粗略，Criteria.ACCURACY_FINE则比较精细
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		// 设置是否要求速度
		criteria.setSpeedRequired(false);
		// 设置是否允许运营商收费
		criteria.setCostAllowed(false);
		// 设置是否需要方位信息
		criteria.setBearingRequired(false);
		// 设置是否需要海拔信息
		criteria.setAltitudeRequired(false);
		// 设置对电源的需求
		criteria.setPowerRequirement(Criteria.POWER_LOW);
		return criteria;
	}
	/**
	    * Listens to GPS status changes
	    */
	   private GpsStatus.Listener mStatusListener = new GpsStatus.Listener()
	   {
	      @Override
	      public synchronized void onGpsStatusChanged(int event)
	      {
	         switch (event)
	         {
	            case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
	                  GpsStatus status = mLocationManager.getGpsStatus(null);
	                  int  mSatellites = 0;
	                  Iterable<GpsSatellite> list = status.getSatellites();
	                  for (GpsSatellite satellite : list)
	                  {
	                     if (satellite.usedInFix())
	                     {
	                        mSatellites++;
	                     }
	                  }
	               break;
	            case GpsStatus.GPS_EVENT_STOPPED:
	               break;
	            case GpsStatus.GPS_EVENT_STARTED:
	               break;
	            default:
	               break;
	         }
	      }
	   };
	private WakeLock mWakeLock = null;

	/**
	 * 获取电源锁，保持该服务在屏幕熄灭时仍然获取CPU时，保持运行
	 */
	private void updateWakeLock() {
		if (this.mLoggingState == Constants.LOGGING) {

			PowerManager pm = (PowerManager) this
					.getSystemService(Context.POWER_SERVICE);
			if (this.mWakeLock != null && this.mWakeLock.isHeld()) {
				this.mWakeLock.release();
			}
			this.mWakeLock = null;
			this.mWakeLock = pm
					.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);
			this.mWakeLock.acquire();
		} else {
			if (this.mWakeLock != null) {
				this.mWakeLock.release();
				this.mWakeLock = null;
			}
		}
	}
}