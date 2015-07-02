package com.driver.gps;

import android.net.Uri;
import android.location.Location;

interface IGPSLoggerServiceRemote {

	int loggingState();
    long startLogging();
    void pauseLogging();
    long resumeLogging();
	void stopLogging();
}