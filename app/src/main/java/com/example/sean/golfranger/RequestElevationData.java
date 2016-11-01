package com.example.sean.golfranger;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by snajera on 10/31/2016.
 */

class RequestElevationData {
    private Context cont;
    private String APPID;

    RequestElevationData(Context c) {
        this.cont = c;
        this.APPID = cont.getResources().getString(R.string.google_elevation_key);
    }

    String getElevationData(String[] cords) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        String locations = cords[0] + "," + cords[1] + "|" + cords[2] + "," + cords[3];

        // Will contain the raw JSON response as a string.
        String elevationJsonStr = null;

        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority("maps.googleapis.com")
                .appendPath("maps")
                .appendPath("api")
                .appendPath("elevation")
                .appendPath("json")
                .appendQueryParameter("key", APPID)
                .appendQueryParameter("locations", locations);

        String myUrl = builder.build().toString();

        try {
            // Construct the URL for the OpenWeatherMap query
            // Possible parameters are avaiable at OWM's forecast API page, at
            // http://openweathermap.org/API#forecast
            URL url =
                    new URL(myUrl);

            // Create the request to OpenWeatherMap, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }
            elevationJsonStr = buffer.toString();
        } catch (IOException e) {
            Log.e("RequestMovieData", "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attemping
            // to parse it.
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e("MovieFragment", "Error closing stream", e);
                }
            }
        }

        return elevationJsonStr;
    }
}
