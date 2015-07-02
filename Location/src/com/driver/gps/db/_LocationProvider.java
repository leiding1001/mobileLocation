package com.driver.gps.db;


import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

public class _LocationProvider extends ContentProvider {
	private static final String TAG = "DriverProvider";
	public static final String AUTHORITY = "com.driver.gps";
	private DatabaseHelper helper;

	private final UriMatcher mUriMatcher;
	private static final int MATCH_ALL_CAR = 1;
	private static final int MATCH_ONE_CAR = 2;

	public _LocationProvider() {
		mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		mUriMatcher.addURI(AUTHORITY, _LocationTable.NAME, MATCH_ALL_CAR);
		mUriMatcher.addURI(AUTHORITY, _LocationTable.NAME + "/#", MATCH_ONE_CAR);
	}

	@Override
	public boolean onCreate() {
		helper = new DatabaseHelper(getContext());
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sort) {
		SQLiteDatabase db = helper.getReadableDatabase();
		Log.d(TAG, "uri : " + uri);
		switch (mUriMatcher.match(uri)) {
		case MATCH_ALL_CAR:
		case MATCH_ONE_CAR:
			return _LocationTable.query(db, uri, projection, selection,
					selectionArgs, sort);
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
	}

	@Override
	public String getType(Uri uri) {
		switch (mUriMatcher.match(uri)) {
		case MATCH_ALL_CAR:
			return _LocationTable.CONTENT_TYPE;
		case MATCH_ONE_CAR:
			return _LocationTable.CONTENT_ITEM_TYPE;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		SQLiteDatabase db = helper.getReadableDatabase();
		Log.d(TAG, "uri : " + uri);
		if (mUriMatcher.match(uri) == MATCH_ALL_CAR) {
			return _LocationTable.insert(db, getContext(), uri, values);
		}else {
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
	}

	@Override
	public int delete(Uri uri, String where, String[] whereArgs) {
		// Log.v(TAG, "delete " + uri);
		SQLiteDatabase db = helper.getWritableDatabase();

		switch (mUriMatcher.match(uri)) {
		case MATCH_ALL_CAR:
		case MATCH_ONE_CAR:
			return _LocationTable.delete(db, getContext(), uri, where, whereArgs);
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
	}

	@Override
	public int update(Uri uri, ContentValues values, String where,
			String[] whereArgs) {
		SQLiteDatabase db = helper.getWritableDatabase();

		Log.v(TAG, "update " + uri);
		switch (mUriMatcher.match(uri)) {
		case MATCH_ALL_CAR:
		case MATCH_ONE_CAR:
			return _LocationTable.update(db, getContext(), uri, values, where,
					whereArgs);
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
	}

}
