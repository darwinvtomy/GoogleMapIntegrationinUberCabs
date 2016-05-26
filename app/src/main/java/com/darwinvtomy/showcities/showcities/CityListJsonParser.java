package com.darwinvtomy.showcities.showcities;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Darwin on 09-02-2016.
 */
public class CityListJsonParser {

    JSONArray geonamesJSONarray;


    public ArrayList<geonames> parseJSON(String carJSON) {
        ArrayList<geonames> carlisted = new ArrayList<>();
        JSONObject jsonObj = null;
        try {
            jsonObj = new JSONObject(carJSON);
            geonamesJSONarray = jsonObj.getJSONArray("geonames");

            for (int i = 0; i < geonamesJSONarray.length(); i++) {
                geonames addnames = new geonames();
                JSONObject jsonObject = geonamesJSONarray.getJSONObject(i);
                addnames.setCountryId(jsonObject.optString("countryId").toString());
                addnames.setName(jsonObject.optString("name").toString());
                addnames.setLat(jsonObject.optString("lat").toString());
                addnames.setLng(jsonObject.optString("lng").toString());
                Log.e("SHOW_CITIES",jsonObject.optString("name").toString()+ " Added");
                carlisted.add(addnames);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return carlisted;
    }



}
