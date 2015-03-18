package com.dh.kps.android.ebeat.locationreporting.javascriptinterface;

import android.content.Context;
import android.content.Intent;
import android.webkit.JavascriptInterface;

import com.dh.kps.android.ebeat.MainActivity;
import com.dh.kps.android.ebeat.locationreporting.LocationReportingService;

/**
 * Created by Pavan on 3/15/2015.
 */
public class LocationReportingJSI {
    MainActivity mContext;
    Intent serviceIntent;

    public LocationReportingJSI(MainActivity mContext){
        this.mContext = mContext;
        this.serviceIntent = new Intent(mContext, LocationReportingService.class);
    }

    @JavascriptInterface
    public void startRecordingLocation(int beatId){

        //Setting beatId as extra input to the intent
        serviceIntent.putExtra("beatId", beatId);
        mContext.startService(serviceIntent);
    }

    @JavascriptInterface
    public void stopRecordingLocation(){
        mContext.stopService(serviceIntent);
    }

}
