package com.dh.kps.android.ebeat.locationreporting;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.LocationManager;
import android.os.IBinder;

public class LocationReportingService extends Service {
    LocationManager locationManager;
    DeviceLocationListener locationListener;
    private boolean result = true;

    public LocationReportingService() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        //criteria.setPowerRequirement(Criteria.POWER_HIGH);
        // String provider=LocationManager.GPS_PROVIDER;
        String provider = locationManager.getBestProvider(criteria, true);
        //String provider=locationManager.GPS_PROVIDER;

        locationListener = new DeviceLocationListener(this, intent.getIntExtra("beatId",0));

        locationManager.requestLocationUpdates(provider, 60000, 1000,
                locationListener);

        return START_STICKY_COMPATIBILITY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        locationManager.removeUpdates(locationListener);
        result = false;
    }
}
