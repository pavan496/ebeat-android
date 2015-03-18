package com.dh.kps.android.ebeat.locationreporting;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Pavan on 3/15/2015.
 */
public class PostLocationTask extends AsyncTask<String, Void, String> {
    @Override
    protected String doInBackground(String... urls) {
        URL url = null;
        String response = "";
        try {
            url = new URL(urls[0]);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setInstanceFollowRedirects(false);
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Length", "" + Integer.toString(urls[1].getBytes().length));
            connection.setUseCaches(false);

            DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
            wr.writeBytes(urls[1]);
            wr.flush();
            wr.close();

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String temp;
            while ((temp = in.readLine()) != null) {
                response += temp + "\n";
            }
            temp = null;
            in.close();
            System.out.println("Server response:\n'" + response + "'");
            connection.disconnect();
        } catch (MalformedURLException e) {
            System.out.println("Connection Failed ...");
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("IOException Connection Failed ...");
            e.printStackTrace();
        }
        return response;
    }
}
