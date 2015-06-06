package com.dh.kps.android.ebeat;

import android.app.ActionBar;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

import com.dh.kps.android.ebeat.locationreporting.javascriptinterface.LocationReportingJSI;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class MainActivity extends ActionBarActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private WebView mWebView;
    private EBEATWebChromeClient EBEATWebChromeClient;
    private FrameLayout webViewPlaceholder;
    private static String TAG = MainActivity.class.getSimpleName();

    private static int DURATION = 10;
    private Location lastLocation;

    private FileOutputStream fos = null;
    private String FILENAME = "com.eBEAT.offline.locationData";

    @Override
    protected void onDestroy() {
        super.onDestroy();
        locationReportingJSI.getGoogleApiClient().disconnect();
    }

    private LocationReportingJSI locationReportingJSI;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Remove the title and set the xml layout
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUI();
        try {
            fos = openFileOutput(FILENAME, Context.MODE_APPEND);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void initUI() {
// Retrieve UI elements
        webViewPlaceholder = ((FrameLayout) findViewById(R.id.webViewPlaceHolder));

        if (mWebView == null) {
            // Get the web view in the layout
            mWebView = new WebView(this);
            mWebView.setLayoutParams(new ViewGroup.LayoutParams(
                    ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT));
            mWebView.getSettings().setSupportZoom(true);
            mWebView.getSettings().setBuiltInZoomControls(false);
            mWebView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
            mWebView.setScrollbarFadingEnabled(true);
            mWebView.getSettings().setLoadsImagesAutomatically(true);

            // Change the settings of the web view to enable javascript and
            // geolocation
            mWebView.getSettings().setJavaScriptEnabled(true);
            mWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(
                    true);
            mWebView.getSettings().setGeolocationEnabled(true);
            //Remove the default WebView zoom controls.
            //mWebView.getSettings().setDisplayZoomControls(false);

            // Appending application name to User Agent. This will help in
            // detecting whether the application is opened through browser or
            // via App.


            String androidId = Secure.getString(getContentResolver(), Secure.ANDROID_ID);


            Log.i("WebViewActivity", "UA: " + mWebView.getSettings().getUserAgentString());
            mWebView.getSettings().setUserAgentString(
                    "EBEAT_ANDROID;" + androidId);

            // Add a new client which handles the geolocation functionality.
            EBEATWebChromeClient = new EBEATWebChromeClient();
            mWebView.setWebChromeClient(new WebChromeClient());

            //Add Javascript interface to start and stop services
            locationReportingJSI = new LocationReportingJSI(this);
            mWebView.addJavascriptInterface(locationReportingJSI, "Android");

            // Load the page into the web view
            mWebView.loadUrl(Constants.EBEAT_BASE_URL);

            // In case any links are clicked in the app, setting the webview
            // client
            // will make sure that all the links open in the webview itself.
            mWebView.setWebViewClient(new WebViewClient());


        }
        webViewPlaceholder.addView(mWebView);
    }

    /**
     * On back press event. This overridden method invokes the back
     * functionality of the website in the webview instead of the app
     */
    @Override
    public void onBackPressed() {
        if (mWebView.canGoBack())
            mWebView.goBack();
        else
            super.onBackPressed();
    }

    /**
     * Overriding default save instance state to save instance state of webview
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // Save the state of the WebView
        mWebView.saveState(outState);
    }

    /**
     * Overriding default restore instance state to restore the saved instance
     * state of webview
     */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // Restore the state of the WebView
        mWebView.restoreState(savedInstanceState);
    }


    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {

        // Assign the new location
        lastLocation = location;

        Log.d(TAG, "Location Changed ");

        // Displaying the new location on UI
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        try {

            if (!new File(FILENAME).exists()) {
                fos = openFileOutput(FILENAME, Context.MODE_APPEND);
            }

            fos.write((Double.toString(lastLocation.getLatitude()) + "," + Double.toString(lastLocation.getLongitude()) + "," + sdf.format(new Date()) + ";").getBytes());
            // fos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                //fos.flush();
                //fos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    public void flushFile() {
        try {
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}