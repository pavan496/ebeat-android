package com.dh.kps.android.ebeat.locationreporting;

import android.location.Location;
import android.location.LocationListener;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import com.dh.kps.android.ebeat.Constants;

/**
 * Created by Pavan on 3/15/2015.
 */
public class DeviceLocationListener implements LocationListener {

    LocationReportingService service;
    int beatId;

    public DeviceLocationListener(LocationReportingService service, int beatId) {
        this.service = service;
        this.beatId = beatId;
    }

    @Override
    public void onLocationChanged(Location location) {
        updateWithNewLocation(beatId, location);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    private void updateWithNewLocation(int beatId, Location location) {
        if (isConnected()) {
            new PostLocationTask().execute(Constants.EBEAT_LOCATION_REPORT_URL, "beatId=" + beatId + "&latlong=(" + Double.toString(location.getLatitude()) + "," + Double.toString(location.getLongitude()) + ")");
        }
    }


    public boolean isConnected() {
        ConnectivityManager connMgr = (ConnectivityManager) service.getSystemService(service.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
    }
}
