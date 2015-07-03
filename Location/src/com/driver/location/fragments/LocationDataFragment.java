package com.driver.location.fragments;

import java.text.SimpleDateFormat;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.driver.location.R;
import com.driver.location.db._Location;
import com.driver.location.db._LocationTable;

public class LocationDataFragment extends SherlockFragment{

	private static final String TAG = "LocationDataFragment" ;
	private static final int LOADER_CURSOR = 2;

	private ListView listView;
	private _LocationAdapter adapter;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.gps_location_map, container, false);
		listView = (ListView) view.findViewById(R.id.listView1);
		
		startLocationCursorLoader();
		
		return view;
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
					null, null);
		}

		@Override
		public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
			// Log.v(TAG, "CursorLoaderCallback", "onLoadFinished");
			if (data != null) {
				data.setNotificationUri(getActivity().getContentResolver(),
						_LocationTable.CONTENT_URI);
			}
			if (adapter == null) {
				int flags = CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER;
				adapter = new _LocationAdapter(getActivity(), data, flags);
				listView.setAdapter(adapter);
			} else {
				Log.v(TAG,
						"CursorLoaderCallback onLoadFinished, reuse old adapter ");
				Cursor oldCursor = adapter.swapCursor(data);
				if (listView.getAdapter() == null) {
					listView.setAdapter(adapter);
				}
				if (oldCursor != null) {
					oldCursor.close();
				}
			}
		}

		@Override
		public void onLoaderReset(Loader<Cursor> loader) {
			Log.v(TAG, "CursorLoaderCallback onLoaderReset");
			Cursor oldCursor = adapter.swapCursor(null);
			if (oldCursor != null) {
				oldCursor.close();
			}
		}
	}

	public class ViewHolder {
		TextView txt_lat;
		TextView txt_lng;
		TextView txt_date;
		TextView txt_accury;
		TextView txt_addr;
		TextView txt_createTime ;
		TextView txt_from;

	}

	public class _LocationAdapter extends CursorAdapter {

		private SimpleDateFormat format = new SimpleDateFormat(
				"yyyyMMdd HH:mm:ss SSS");

		public _LocationAdapter(Context context, Cursor c, int flags) {
			super(context, c, flags);
		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {

			LayoutInflater inflater = LayoutInflater.from(context);
			View view = inflater.inflate(R.layout.location_item, parent, false);
			ViewHolder holder = new ViewHolder();
			holder.txt_lat = (TextView) view.findViewById(R.id.txt_lat);
			holder.txt_lng = (TextView) view.findViewById(R.id.txt_lng);
			holder.txt_date = (TextView) view.findViewById(R.id.txt_date);
			holder.txt_accury = (TextView) view.findViewById(R.id.txt_accury);
			holder.txt_addr = (TextView) view.findViewById(R.id.txt_addr);
			holder.txt_from=(TextView)view.findViewById(R.id.txt_from) ;
			holder.txt_createTime = (TextView)view.findViewById(R.id.creat_time);
			view.setTag(holder);
			return view;
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			ViewHolder holder = (ViewHolder) view.getTag();
			_Location _location = _LocationTable.fromCursor(cursor);
			holder.txt_lat.setText(_location.latitude + "");
			holder.txt_lng.setText(_location.longitude + "");
			holder.txt_date.setText(format.format(_location.time));
			holder.txt_accury.setText(_location.accuracy + "");
			holder.txt_addr.setText(_location.name + "");
			holder.txt_from.setText(_location.from + "");
			holder.txt_createTime.setText(format.format(_location.creatTime));
		}
	}
}
