package com.eldar.fit.seminarski.helper;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MyUrlConnection {

    public enum HttpMethod {
        GET, POST, HEAD, OPTIONS, TRACE
    }

    public static String charset = "UTF-8";

    public static MyApiResult request(String urlString, HttpMethod httpMethod, String postData, String contentType) {

        HttpURLConnection urlConnection = null;
        String result = null;

        try {
            URL url = new URL(urlString);
            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.setRequestProperty("Content-type", contentType);
            urlConnection.setRequestProperty("Accept", contentType);
            urlConnection.setRequestProperty("Accept-Charset", charset);
            urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0");
            urlConnection.setRequestMethod(httpMethod.toString());

            if (postData != null) {
                urlConnection.setDoOutput(true);
                byte[] outputBytes = postData.getBytes(charset);
                OutputStream ostream = urlConnection.getOutputStream();
                ostream.write(outputBytes);
                ostream.flush();
                ostream.close();
            }

            int statusCode = urlConnection.getResponseCode();

            if (statusCode == 200) {
                InputStream is = new BufferedInputStream(urlConnection.getInputStream());
                String response = convertToString(is);

                return MyApiResult.OK(response);
            } else {
                InputStream is = new BufferedInputStream(urlConnection.getErrorStream());
                String response = convertToString(is);

                return MyApiResult.Error(statusCode, response);
            }


        } catch(Exception e) {
            e.printStackTrace();
            return MyApiResult.Error(0, e.getMessage());
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
    }

    private static String convertToString(InputStream in) {
        BufferedReader reader = null;
        StringBuilder response = new StringBuilder();

        try {
            reader = new BufferedReader(new InputStreamReader(in));
            String line = "";
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        } catch(IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch(IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return response.toString();
    }

}
