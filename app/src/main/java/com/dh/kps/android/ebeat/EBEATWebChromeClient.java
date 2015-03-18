package com.dh.kps.android.ebeat;

import android.webkit.GeolocationPermissions;
import android.webkit.WebChromeClient;

/**
 * Created by Pavan on 2/10/2015.
 */
public class EBEATWebChromeClient extends WebChromeClient {
    @Override
    public void onGeolocationPermissionsShowPrompt(String origin,
                                                   GeolocationPermissions.Callback callback) {
        // Overriding the default functionality
        super.onGeolocationPermissionsShowPrompt(origin, callback);
        callback.invoke(origin, true, false);
    }
}
