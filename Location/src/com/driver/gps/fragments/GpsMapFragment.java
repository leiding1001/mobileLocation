package com.driver.gps.fragments;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.app.SherlockFragment;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.baidu.mapapi.utils.CoordinateConverter.CoordType;
import com.driver.gps.R;
import com.driver.gps.R.id;
import com.driver.gps.R.layout;
import com.driver.gps.db._Location;
import com.driver.gps.db._LocationTable;

public class GpsMapFragment extends SherlockFragment {

	private static final String TAG = "GpsMapActivity";

	private static final int LOADER_CURSOR = 3;

	private boolean isFirst = true ;
	
	MapView mMapView = null;
    BaiduMap mBaiduMap = null ;
    
    
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	};
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.gps_map, container, false);
		// 获取地图控件引用
		mMapView = (MapView) view.findViewById(R.id.bmapView);
		mBaiduMap = mMapView.getMap() ;
		
		
		return view;
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		// 获取地图控件引用
		
		startLocationCursorLoader();
	}
	@Override
	public void onDestroy() {
		super.onDestroy();
		// 在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
		mMapView.onDestroy();
		
	}

	@Override
	public void onResume() {
		super.onResume();
		isFirst = true ;
		// 在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
		/*mMapView.onResume();*/
	}

	@Override
	public void onPause() {
		super.onPause();
		// 在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
		/*mMapView.onPause();*/
	}
	
	public void startLocationCursorLoader() {
		CursorLoaderCallback cursorLoaderCallback = new CursorLoaderCallback();
		LoaderManager lm = getLoaderManager();
		if (lm.getLoader(LOADER_CURSOR) == null) {
			lm.initLoader(LOADER_CURSOR, null, cursorLoaderCallback);
		} else {
			lm.restartLoader(LOADER_CURSOR, null, cursorLoaderCallback);
		}
	}

	protected class CursorLoaderCallback implements
			LoaderManager.LoaderCallbacks<Cursor> {
		@Override
		public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
			Log.v(TAG, "CursorLoaderCallback onCreateLoader");
			Uri baseUri = _LocationTable.CONTENT_URI;
			return new CursorLoader(getActivity(), baseUri, null, null,
					null, _LocationTable.DEFAULT_SORT_ASC);
		}

		@Override
		public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
			// Log.v(TAG, "CursorLoaderCallback", "onLoadFinished");
			if (data != null) {
				data.setNotificationUri(getActivity().getContentResolver(),
						_LocationTable.CONTENT_URI);
				mBaiduMap.clear() ;
				
				List<LatLng> points = new ArrayList<LatLng>();
				
				
				while(data.moveToNext()){
					_Location _location = _LocationTable.fromCursor(data) ;
					CoordinateConverter converter  = new CoordinateConverter();  
					converter.from(CoordType.GPS);  
					// sourceLatLng待转换坐标  
					converter.coord(new LatLng(_location.latitude, _location.longitude));  
					LatLng desLatLng = converter.convert();
					points.add(desLatLng);
				}
				if(points.size()>=2){
					OverlayOptions ooPolyline = new PolylineOptions().width(5)
							.color(0xAAFF0000).points(points);
					mBaiduMap.addOverlay(ooPolyline);
					if(isFirst){
						isFirst = false ;
						//定义地图状态
						MapStatus mMapStatus = new MapStatus.Builder()
						.target(points.get(points.size()-1))
						.zoom(18)
						.build();
						//定义MapStatusUpdate对象，以便描述地图状态将要发生的变化
						MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
						//改变地图状态
						mBaiduMap.setMapStatus(mMapStatusUpdate); 
					}
				}
				
			}
		}
		@Override
		public void onLoaderReset(Loader<Cursor> loader) {}
	}
}
