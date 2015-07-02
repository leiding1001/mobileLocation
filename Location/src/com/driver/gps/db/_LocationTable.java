package com.driver.gps.db;

import java.util.Date;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.net.UrlQuerySanitizer.ValueSanitizer;
import android.provider.BaseColumns;
import android.support.v4.database.DatabaseUtilsCompat;
import android.text.TextUtils;
import android.util.Log;

public class _LocationTable implements BaseColumns {
	public static final String TAG = "_LocationTable";
	public static final String NAME = "_Location";

	// columns
	public static final String _LOCATION_NAME_COL = "name";
	public static final String _LOCATION_LATITUDE_COL = "latitude";
	public static final String _LOCATION_LOGITUDE_COL = "longitude";
	public static final String _LOCATION_ACCURY_COL = "accury";

	public static final String _LOCATION_TIME_COL = "time";
	public static final String _LOCATION_FROM_COL = "origin" ;
	
	public static final String  CREATE_TIME_COL = "create_time" ;
	

	private static final String AUTHORITY = _LocationProvider.AUTHORITY;
	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
			+ "/" + NAME);
	public static final Uri CONTENT_ID_URI_BASE = Uri.parse("content://"
			+ AUTHORITY + "/" + NAME + "/");
	public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd."
			+ AUTHORITY + "." + NAME;
	public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd."
			+ AUTHORITY + "." + NAME;
	public static final String DEFAULT_SORT_ADDR = CREATE_TIME_COL + " DESC";

	public static final String DEFAULT_SORT_ASC = CREATE_TIME_COL + " ASC";
	
	private _LocationTable() {
	}

	public static void create(SQLiteDatabase db) {

		db.execSQL("CREATE TABLE " + NAME + "(" + _ID
				+ " INTEGER PRIMARY KEY ,"

				+ _LOCATION_NAME_COL + " TEXT  DEFALUT '',"
				+ _LOCATION_FROM_COL + " TEXT  DEFALUT '',"
				+ _LOCATION_LATITUDE_COL + " DOUBLE  ,"
				+ _LOCATION_LOGITUDE_COL + " DOUBLE  ," 
				+ _LOCATION_ACCURY_COL+ " DOUBLE  ," 
				+ _LOCATION_TIME_COL + " LONG," 
				+ CREATE_TIME_COL + " LONG "
				+ ")");
	}

	// VERSION7 later
	public static void upgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}

	private static boolean match(Uri uri) {
		return uri.toString().startsWith(CONTENT_URI.toString());
	}

	private static boolean matchOne(Uri uri) {
		return uri.toString().startsWith(CONTENT_ID_URI_BASE.toString());
	}

	public static Cursor query(SQLiteDatabase db, Uri uri, String[] projection,
			String selection, String[] selectionArgs, String sortAddr) {
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		qb.setTables(NAME);

		if (!match(uri))
			throw new IllegalArgumentException("Unknown URI " + uri);
		
		
		if (matchOne(uri)) {
			qb.appendWhere(_ID + "=?");
			selectionArgs = DatabaseUtilsCompat.appendSelectionArgs(
					selectionArgs, new String[] { uri.getLastPathSegment() });
		}
		if (TextUtils.isEmpty(sortAddr)) {
			sortAddr = DEFAULT_SORT_ADDR;
		}
		Cursor c = qb.query(db, projection, selection, selectionArgs,
				null /* group */, null /* filter */, sortAddr);
		return c;
	}

	// insert
	public static Uri insert(SQLiteDatabase db, Context context, Uri uri,
			ContentValues values) {
		Log.i(TAG, "insert " + uri.toString());
		if (!match(uri)) {
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
		long time = values.getAsLong(CREATE_TIME_COL) ;
		Cursor cursor = db.query(NAME,  null, _LOCATION_TIME_COL+">="+time, null , null, null, null);
		if(cursor==null || !cursor.moveToFirst()){
			long rowId = db.insert(NAME, null, values);
			// Log.v(TAG, "insert " + uri.toString());
			if (rowId >= 0) {
				Uri noteUri = ContentUris
						.withAppendedId(CONTENT_ID_URI_BASE, rowId);
				context.getContentResolver().notifyChange(noteUri, null);
				return noteUri;
			}
		}
		return null;
	}

	// delete
	public static int delete(SQLiteDatabase db, Context context, Uri uri,
			String where, String[] whereArgs) {
		Log.i(TAG, "delete " + uri);
		String finalWhere;
		int count;
		if (matchOne(uri)) {
			finalWhere = DatabaseUtilsCompat.concatenateWhere(_ID + " = "
					+ ContentUris.parseId(uri), where);
			count = db.delete(NAME, finalWhere, whereArgs);
		} else if (match(uri)) {
			count = db.delete(NAME, where, whereArgs);
		} else {
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
		context.getContentResolver().notifyChange(CONTENT_URI, null);
		return count;
	}

	// update
	public static int update(SQLiteDatabase db, Context context, Uri uri,
			ContentValues values, String where, String[] whereArgs) {
		Log.i(TAG, "update " + uri.toString());
		int count;
		String finalWhere;
		if (matchOne(uri)) {
			finalWhere = DatabaseUtilsCompat.concatenateWhere(_ID + " = "
					+ ContentUris.parseId(uri), where);
			count = db.update(NAME, values, finalWhere, whereArgs);
		} else if (match(uri)) {
			count = db.update(NAME, values, where, whereArgs);
		} else {
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
		context.getContentResolver().notifyChange(uri, null);
		return count;
	}

	public static ContentValues toContentValues(_Location _location) {
		ContentValues values = new ContentValues();

		values.put(_LOCATION_LATITUDE_COL, _location.latitude);
		values.put(_LOCATION_LOGITUDE_COL, _location.longitude);
		values.put(_LOCATION_ACCURY_COL, _location.accuracy);
		values.put(CREATE_TIME_COL, _location.creatTime.getTime());
		values.put(_LOCATION_FROM_COL, _location.from);
		return values;
	}

	public static _Location fromCursor(Cursor cursor) {
		_Location _location = new _Location();

		int index = cursor.getColumnIndex(_LOCATION_NAME_COL);
		if (index > 0 && !TextUtils.isEmpty(cursor.getString(index)))
			_location.name = cursor.getString(index);

		index = cursor.getColumnIndex(_LOCATION_LATITUDE_COL);
		if (index > 0)
			_location.latitude = cursor.getDouble(index);
		index = cursor.getColumnIndex(_LOCATION_LOGITUDE_COL);
		if (index > 0)
			_location.longitude = cursor.getDouble(index);

		index = cursor.getColumnIndex(_LOCATION_ACCURY_COL);
		if (index > 0)
			_location.accuracy = cursor.getDouble(index);

		index = cursor.getColumnIndex(_LOCATION_TIME_COL);
		if (index > 0)
			_location.time = new Date(cursor.getLong(index));
		
		index = cursor.getColumnIndex(CREATE_TIME_COL);
		if (index > 0)
			_location.creatTime = new Date(cursor.getLong(index));
		
		index = cursor.getColumnIndex(_LOCATION_FROM_COL);
		if (index > 0)
			_location.from = cursor.getString(index);

		return _location;
	}
}
