package com.dh.kps.android.ebeat;

import android.app.ActionBar;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.support.v7.app.ActionBarActivity;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

import com.dh.kps.android.ebeat.locationreporting.javascriptinterface.LocationReportingJSI;


public class MainActivity extends ActionBarActivity {

    private WebView mWebView;
    private EBEATWebChromeClient EBEATWebChromeClient;
    private FrameLayout webViewPlaceholder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Remove the title and set the xml layout
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUI();
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
            mWebView.getSettings().setBuiltInZoomControls(true);
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
            mWebView.getSettings().setDisplayZoomControls(false);

            // Appending application name to User Agent. This will help in
            // detecting whether the application is opened through browser or
            // via App.


            String androidId = Secure.getString(getContentResolver(), Secure.ANDROID_ID);

            mWebView.getSettings().setUserAgentString(
                    "EBEAT_ANDROID;" + androidId);

            // Add a new client which handles the geolocation functionality.
            EBEATWebChromeClient = new EBEATWebChromeClient();
            mWebView.setWebChromeClient(EBEATWebChromeClient);

            //Add Javascript interface to start and stop services
            mWebView.addJavascriptInterface(new LocationReportingJSI(this), "Android");




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


}
