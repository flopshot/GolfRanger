package com.example.sean.golfranger;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

class ElevationDataParser {
    Double getGolferElevation(String elevationJsonStr) {
        try {
            JSONObject elevationJson = new JSONObject(elevationJsonStr);
            JSONArray elevationArray = elevationJson.getJSONArray("results");
            JSONObject golferJson = elevationArray.getJSONObject(0);
            return golferJson.getDouble("elevation");
        } catch (JSONException e) {
            e.printStackTrace();
            return 0.0;
        }
    }

    Double getMarkerElevation(String elevationJsonStr) {
        try {
            JSONObject elevationJson = new JSONObject(elevationJsonStr);
            JSONArray elevationArray = elevationJson.getJSONArray("results");
            JSONObject markerJson = elevationArray.getJSONObject(1);
            return markerJson.getDouble("elevation");
        } catch (JSONException e) {
            e.printStackTrace();
            return 0.0;
        }
    }

}
