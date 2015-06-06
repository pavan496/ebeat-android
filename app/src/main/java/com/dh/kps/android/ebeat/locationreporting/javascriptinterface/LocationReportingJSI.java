package com.dh.kps.android.ebeat.locationreporting.javascriptinterface;

import android.content.Intent;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import com.dh.kps.android.ebeat.MainActivity;
import com.dh.kps.android.ebeat.locationreporting.PushDataToServerService;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

/**
 * Created by Pavan on 3/15/2015.
 */
public class LocationReportingJSI {
    MainActivity mContext;
    Intent serviceIntent;

    private boolean mRequestingLocationUpdates = false;
    private GoogleApiClient mGoogleApiClient;
    private static String TAG = LocationReportingJSI.class.getSimpleName();
    private LocationRequest mLocationRequest;
    private static int UPDATE_INTERVAL = 5000;
    private static int FASTEST_INTERVAL = 5000;


    public LocationReportingJSI(MainActivity mContext){
        this.mContext = mContext;
        this.serviceIntent = new Intent(mContext, PushDataToServerService.class);

        if (checkPlayService()) {
            Log.d("Activity ", "Build the Google API Client");
            //Build the Google API Client
            buildGoogleApiClient();
            createLocationRequest();

            if (mGoogleApiClient != null) {
                Log.d("Activity", "Connect to Google Play Service");
                mGoogleApiClient.connect();
            }

        } else {

            System.out.print("Google Play Service doesn't seem to be available");
        }
    }

    @JavascriptInterface
    public void startRecordingLocation(int beatId){


        serviceIntent.putExtra("beatId", beatId);
        togglePeriodicLocationUpdates();
    }

    @JavascriptInterface
    public void stopRecordingLocation(){
        togglePeriodicLocationUpdates();
    }

    protected void startLocationUpdates() {


        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, mContext);

        //Setting beatId as extra input to the intent

        mContext.startService(serviceIntent);
    }

    private void togglePeriodicLocationUpdates() {
        if (!mRequestingLocationUpdates) {
            // Changing the button text
            mRequestingLocationUpdates = true;

            // Starting the location updates
            startLocationUpdates();

            Log.d(TAG, "Periodic location updates started!");

        } else {


            mRequestingLocationUpdates = false;

            // Stopping the location updates
            stopLocationUpdates();

            Log.d(TAG, "Periodic location updates stopped!");
        }
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, mContext);


        mContext.flushFile();

        mContext.stopService(serviceIntent);

    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                .addConnectionCallbacks(mContext)
                .addOnConnectionFailedListener(mContext)
                .addApi(LocationServices.API)
                .build();
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        // mLocationRequest.setSmallestDisplacement(DURATION);
    }

    private boolean checkPlayService() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(mContext);

        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, mContext, 1000).show();
            } else {
                Toast.makeText(mContext.getApplicationContext(), "This device is not supported .", Toast.LENGTH_LONG).show();
                mContext.finish();
            }
            return false;
        }
        return true;
    }

    public GoogleApiClient getGoogleApiClient() {
        return mGoogleApiClient;
    }
}
