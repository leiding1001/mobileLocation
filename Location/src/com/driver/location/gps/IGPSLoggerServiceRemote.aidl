package com.driver.location.gps;

import android.net.Uri;
import android.location.Location;

interface IGPSLoggerServiceRemote {
	int loggingState();
    long startLogging();
	void stopLogging();
}