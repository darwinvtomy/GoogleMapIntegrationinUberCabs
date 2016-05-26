package com.darwinvtomy.showcities.showcities;

import android.app.Dialog;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final int ERROR_DIALOG_REQUEST = 9001;
    private static final double
            SEATTLE_LAT = 47.60621,
            SEATTLE_LNG = -122.33207,
            SYDNEY_LAT = -33.867487,
            SYDNEY_LNG = 151.20699,
            NEWYORK_LAT = 40.714353,
            NEWYORK_LNG = -74.005973;
    GoogleMap mMap;
    private GoogleApiClient mLocationClient;
    private Marker marker;
    ArrayList<geonames> geonameslist = new ArrayList<>();
    String check_result;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (servicesOK()) {
            setContentView(R.layout.activity_map);

            if (initMap()) {
//                gotoLocation(SEATTLE_LAT, SEATTLE_LNG, 15);

                mLocationClient = new GoogleApiClient.Builder(this)
                        .addApi(LocationServices.API)
                        .addConnectionCallbacks(this)
                        .addOnConnectionFailedListener(this)
                        .build();

                mLocationClient.connect();
                mMap.setMyLocationEnabled(true);
            } else {
                Toast.makeText(this, "Map not connected!", Toast.LENGTH_SHORT).show();
            }

        } else {
            setContentView(R.layout.activity_main);
        }

        mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {

            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                Log.i("SHOW_CITIES",cameraPosition.target.latitude+"");
                Log.i("SHOW_CITIES",cameraPosition.target.longitude+"");
                String apiURL = "http://api.geonames.org/findNearbyJSON?lat=" + cameraPosition.target.latitude + "&lng=" + cameraPosition.target.longitude + "&username=akhiljayaram&radius=300";
                updateTheDisplay(apiURL);

                // Your custom code here

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //Add menu handling code
/*        switch (id) {
            case R.id.mapTypeNone:
                mMap.setMapType(GoogleMap.MAP_TYPE_NONE);
                break;
            case R.id.mapTypeNormal:
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                break;
            case R.id.mapTypeSatellite:
                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                break;
            case R.id.mapTypeTerrain:
                mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                break;
            case R.id.mapTypeHybrid:
                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                break;
        }*/


        return super.onOptionsItemSelected(item);
    }

    public boolean servicesOK() {

        int isAvailable = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

        if (isAvailable == ConnectionResult.SUCCESS) {
            return true;
        } else if (GooglePlayServicesUtil.isUserRecoverableError(isAvailable)) {
            Dialog dialog =
                    GooglePlayServicesUtil.getErrorDialog(isAvailable, this, ERROR_DIALOG_REQUEST);
            dialog.show();
        } else {
            Toast.makeText(this, "Can't connect to mapping service", Toast.LENGTH_SHORT).show();
        }

        return false;
    }

    private boolean initMap() {
        if (mMap == null) {
            SupportMapFragment mapFragment =
                    (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            mMap = mapFragment.getMap();

            if (mMap != null) {
                mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                    @Override
                    public View getInfoWindow(Marker marker) {
                        return null;
                    }

                    @Override
                    public View getInfoContents(Marker marker) {
                        View v = getLayoutInflater().inflate(R.layout.info_window, null);
                        TextView tvLocality = (TextView) v.findViewById(R.id.tvLocality);
                        TextView tvLat = (TextView) v.findViewById(R.id.tvLat);
                        TextView tvLng = (TextView) v.findViewById(R.id.tvLng);
                        TextView tvSnippet = (TextView) v.findViewById(R.id.tvSnippet);
                        TextView dist = (TextView) v.findViewById(R.id.distance);

                        LatLng latLng = marker.getPosition();
                        tvLocality.setText(marker.getTitle());
                        tvLat.setText("Latitude: " + latLng.latitude);
                        tvLng.setText("Longitude: " + latLng.longitude);
                        tvSnippet.setText(marker.getSnippet());

                        return v;
                    }
                });

                mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                    @Override
                    public void onMapLongClick(LatLng latLng) {
                        Geocoder gc = new Geocoder(MainActivity.this);
                        List<Address> list = null;

                        try {
                            list = gc.getFromLocation(latLng.latitude, latLng.longitude, 1);
                        } catch (IOException e) {
                            e.printStackTrace();
                            return;
                        }

                        Address add = list.get(0);
                        MainActivity.this.addMarker(add, latLng.latitude, latLng.longitude);
                    }
                });

                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        String msg = marker.getTitle() + " (" +
                                marker.getPosition().latitude + ", " +
                                marker.getPosition().longitude + ")";
                        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                        return false;
                    }
                });

                mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
                    @Override
                    public void onMarkerDragStart(Marker marker) {
                    }

                    @Override
                    public void onMarkerDrag(Marker marker) {
                    }

                    @Override
                    public void onMarkerDragEnd(Marker marker) {
                        Geocoder gc = new Geocoder(MainActivity.this);
                        List<Address> list = null;
                        LatLng ll = marker.getPosition();
                        try {
                            list = gc.getFromLocation(ll.latitude, ll.longitude, 1);
                        } catch (IOException e) {
                            e.printStackTrace();
                            return;
                        }

                        Address add = list.get(0);
                        marker.setTitle(add.getLocality());
                        marker.setSnippet(add.getCountryName());
                        marker.showInfoWindow();
                    }
                });

            }

        }
        return (mMap != null);
    }

    private void gotoLocation(double lat, double lng, float zoom) {
        LatLng latLng = new LatLng(lat, lng);
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(latLng, zoom);
        mMap.moveCamera(update);
    }

    private void hideSoftKeyboard(View v) {
        InputMethodManager imm =
                (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

/*    public void geoLocate(View v) throws IOException {

        hideSoftKeyboard(v);

        TextView tv = (TextView) findViewById(R.id.editText1);
        String searchString = tv.getText().toString();

        Geocoder gc = new Geocoder(this);
        List<Address> list = gc.getFromLocationName(searchString, 1);

        if (list.size() > 0) {
            Address add = list.get(0);
            double lat = add.getLatitude();
            double lng = add.getLongitude();
            gotoLocation(lat, lng, 15);

            if (marker != null) {
                marker.remove();
            }

            addMarker(add, lat, lng);
        }

    }*/

    private void addMarker(Address add, double lat, double lng) {
        MarkerOptions options = new MarkerOptions()
                .title(add.getLocality())
                .position(new LatLng(lat, lng))
                .draggable(true)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.city_marker));

        String country = add.getCountryName();
        if (country.length() > 0) {
            options.snippet(country);
        }

        marker = mMap.addMarker(options);
    }

    public void showCurrentLocation(MenuItem item) {
        Location currentLocation = LocationServices.FusedLocationApi
                .getLastLocation(mLocationClient);
        if (currentLocation == null) {
            Toast.makeText(this, "Couldn't connect!", Toast.LENGTH_SHORT).show();
        } else {
            LatLng latLng = new LatLng(
                    currentLocation.getLatitude(),
                    currentLocation.getLongitude()
            );
            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(
                    latLng, 15
            );
            mMap.animateCamera(update);

        }

    }

    @Override
    public void onConnected(Bundle bundle) {
        Toast.makeText(this, "Ready to map!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    public void showTheCities(MenuItem item) {
        if(!check_result.isEmpty()){
            Intent intent = new Intent(MainActivity.this,ShowTheCityList.class);
            startActivity(intent);
        }

    }

/*    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        Log.i("SHOW_CITIES",cameraPosition.target.latitude+"");
        Log.i("SHOW_CITIES",cameraPosition.target.longitude+"");
        String apiURL = "http://api.geonames.org/findNearbyJSON?lat=" + cameraPosition.target.latitude + "&lng=" + cameraPosition.target.longitude + "&username=akhiljayaram&radius=300";
        updateTheDisplay(apiURL);

    }*/

    private void updateTheDisplay(String url) {

        requestData(url);
    }

    private void requestData(String uri) {
        MyTask task = new MyTask();
        task.execute(uri);
    }

    private class MyTask extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {


        }

        @Override
        protected String doInBackground(String... params) {

            String content = HttpManager.getData(params[0]);
            return content;
            //   return content;
        }

        @Override
        protected void onPostExecute(String result) {
         check_result = result;
            Log.i("SHOW_CITIES", "ON POST EXECUTE" + result);
            CityListJsonParser parser = new CityListJsonParser();
           geonameslist =  parser.parseJSON(result);
            GlobalClass.setthePlacelist(geonameslist);

            for (int i =0;i<geonameslist.size();i++){
                Log.i("SHOW_CITIES", "Name "+geonameslist.get(i).getName());
                String CITY_NAME = geonameslist.get(i).getName();
                double latitude = Double.parseDouble(geonameslist.get(i).getLat());
                double longitude = Double.parseDouble(geonameslist.get(i).getLng());

                addCustomMarker(CITY_NAME,latitude,longitude);
            }

        }

        @Override
        protected void onProgressUpdate(String... values) {
//			updateDisplay(values[0]);
        }

    }

    private void addCustomMarker(String Cityname, double lat, double lng) {
        MarkerOptions options = new MarkerOptions()
                .title(Cityname)
                .position(new LatLng(lat, lng))
                .draggable(false)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.city_marker));

  /*      String country = add.getCountryName();
        if (country.length() > 0) {
            options.snippet(country);
        }*/

        marker = mMap.addMarker(options);
    }
}
