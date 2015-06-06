package com.dh.kps.android.ebeat.locationreporting;

import android.app.Service;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.dh.kps.android.ebeat.Constants;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class PushDataToServerService extends Service {

    private static final String TAG = PushDataToServerService.class.getSimpleName();
    Thread thread = null;
    boolean serviceStopped = false;

    public PushDataToServerService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();

    }

    private void unbindService() {
        if (!serviceStopped) {
            serviceStopped = true;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbindService();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        final int beatId = intent.getIntExtra("beatId", 0);
        thread = new Thread() {
            public void run() {
                while (!serviceStopped) {
                    Log.d(TAG, "Location Data Sync Service has been started");
                    try {
                        Thread.sleep(10 * 1000);
                        if (checkNetworkConnectivity()) {
                            eBeatLocationWorker(beatId);
                        } else {
                            Log.d(TAG, "Network Connectivity is not available,\n Saving Location Offline");
                            Toast.makeText(getApplicationContext(), "Network Connectivity is not available. Saving Location Offline", Toast.LENGTH_LONG).show();
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        thread.start();
        return Service.START_STICKY;
    }

    // check network connection
    public boolean checkNetworkConnectivity() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(this.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
    }

    private void eBeatLocationWorker(final int beatId) {

        new Thread() {
            public void run() {
                String FILENAME = "com.eBEAT.offline.locationData";
                BufferedReader inStream = null;
                try {
                    inStream = new BufferedReader(new InputStreamReader(openFileInput(FILENAME)));
                    String line = "";
                    StringBuilder response = new StringBuilder();
                    while ((line = inStream.readLine()) != null) {
                        response.append(line);
                    }

                    URL url = null;

                    try {
                        //FileInputStream inStream =openFileInput()
            /*
                Get the Data from the Local File
                and Post the Data to Server
             */

                        url = new URL(Constants.EBEAT_LOCATION_REPORT_URL);
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                        connection.setDoOutput(true);
                        connection.setDoInput(true);
                        connection.setInstanceFollowRedirects(false);
                        connection.setRequestMethod("POST");
                        String inputString = "beatId=" + beatId + "&latlong=" + response.toString();
                        connection.setRequestProperty("Content-Length", "" + Integer.toString(inputString.getBytes().length));
                        connection.setUseCaches(false);

                        DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
                        wr.writeBytes(inputString);
                        wr.flush();
                        wr.close();

                        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                        String temp;
                        String resp = "";
                        while ((temp = in.readLine()) != null) {
                            resp += temp + "\n";
                        }
                        temp = null;
                        in.close();
                        System.out.println("Server response:\n'" + resp + "'");

                        connection.disconnect();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        if (inStream != null) {

                            try {
                                inStream.close();
                                deleteFile(FILENAME);
                                System.out.println("File got deleted ." + new File(FILENAME).exists());

                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                    }


                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }.start();
    }
}