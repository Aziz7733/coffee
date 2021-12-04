package com.example.coffee;


import android.Manifest;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


public class UserHome extends AppCompatActivity {

    private static final String TAG = "UserHome";
    //Location Services To access the location APIs
    private GeofencingClient geofencingClient;
    private GeofenceHelper geofenceHelper;

    private float GEOFENCE_RADIUS = 100;
    private String GEOFENCE_ID = "CoffeeShop";

    private int FINE_LOCATION_ACCESS_REQUEST_CODE = 10001;
    private int BACKGROUND_LOCATION_ACCESS_REQUEST_CODE = 10002;

    //configure Audio
    AudioManager am;
    private static final String TAG_NAME = "name";
    private static final String TAG_LAT = "lat";
    private static final String TAG_LON = "lon";

    //Url get all Coffee shop and put it in ListView for chose one to add challenge to it
    private final String   getUrl = "https://mypro222.000webhostapp.com/mycode/list_coffee.php";

    private ArrayList<HashMap<String, String>> mCommentList;
    private JSONArray mComments = null;
    private ListView lv;
    private MySQLiteHelper myDb;//do with database SQLite
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home);

        // create an instance of the Geofencing client
        geofencingClient = LocationServices.getGeofencingClient(this);
        geofenceHelper = new GeofenceHelper(this);


        lv = findViewById(R.id.ListViewCoffee);
        requestQueue = Volley.newRequestQueue(this);
        mCommentList = new ArrayList<HashMap<String, String>>();


        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterview, View view, int position, long id) {
                HashMap<String, String> row = (HashMap<String, String>) adapterview.getItemAtPosition(position);

                String name = row.get(TAG_NAME);
                String lat = row.get(TAG_LAT);
                String lon = row.get(TAG_LON);

                double  latitude, longitude;

                latitude=Double.valueOf(lat);
                longitude=Double.valueOf(lon);

                LatLng latLng=new LatLng(latitude,longitude);

                if (myDb.checkFavoriteExit(name)){
                    Toast.makeText(getApplicationContext(),"This Coffee is in your  Favorites",Toast.LENGTH_SHORT).show();
                    return;
               }
                else {
                    if (myDb.insertFavorite(name, lat, lon)) {
                        Toast.makeText(getApplicationContext(), "Successfully added to favourites !", Toast.LENGTH_SHORT).show();
                        GEOFENCE_ID = name;
                        addCoffeeFavoriteToGeo(latLng);
                        return;
                    }
                }
              Toast.makeText(getApplicationContext(),"Addition failed",Toast.LENGTH_SHORT).show();
            }
        });
    }

    //do with menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.user_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_get_myCoupons:
                startActivity(new Intent(getApplicationContext(),UserCoupons.class));
                finish();
                break;
            case R.id.action_logOut:
                SharedPreferences sh=getSharedPreferences("USER_LOGIN", MODE_PRIVATE);
                SharedPreferences.Editor myEdit=sh.edit();
                myEdit.clear();
                myEdit.apply();
                startActivity(new Intent(getApplicationContext(),Login.class));
                finish();
                break;
            case R.id.action_silent:
                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !notificationManager.isNotificationPolicyAccessGranted()) {
                    Intent intent = new Intent(android.provider.Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
                    startActivity(intent);
                }
                //The user should be able to configure the application to automatically switch the
                // device to the silent mode.
                am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                 int mod=am.getRingerMode();
                if(mod!=0)
                am.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                else
                    am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                break;
            case R.id.action_get_myFavorite:
                startActivity(new Intent(getApplicationContext(),FavoriteCoffee.class));
                finish();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    //get All coffee from mysql database realtime database
    private void doLoadCoffees(){
        mCommentList.clear();
        mComments = null;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, getUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    mComments = new JSONArray(response);
                    for (int i = 0; i < mComments.length(); i++) {
                        JSONObject data = mComments.getJSONObject(i);
                        String Name = data.getString(TAG_NAME);
                        String lat = data.getString(TAG_LAT);
                        String lon = data.getString(TAG_LON);

                        HashMap<String, String> map = new HashMap<String, String>();
                        map.put("name", Name);
                        map.put("lat", lat);
                        map.put("lon", lon);

                        mCommentList.add(map);
                    }
                    updateList();
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Check Your Intern and reload this interface", Toast.LENGTH_LONG).show();
            }
        });
        requestQueue.add(stringRequest);

    }

    //add all coffee to listView
    private void updateList() {

        ListAdapter adapter = new SimpleAdapter(this, mCommentList,
                R.layout.list_viwe_style, new String[]{TAG_NAME, TAG_LAT,
                TAG_LON}, new int[]{R.id.txtCoffees, R.id.txtCoffeeLat,
                R.id.txtCoffeeLon});
        lv.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        doLoadCoffees();
        myDb=new MySQLiteHelper(this);
        enableUserLocation();

    }
    //This function calls the function that adds your favorite coffee to geofence
    public void addCoffeeFavoriteToGeo(LatLng latLng) {
        if (Build.VERSION.SDK_INT >= 29) {
            //We need background permission
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                addGeofence(latLng, GEOFENCE_RADIUS);
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
                    //We show a dialog and ask for permission
                    ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_BACKGROUND_LOCATION}, BACKGROUND_LOCATION_ACCESS_REQUEST_CODE);
                } else {
                    ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_BACKGROUND_LOCATION}, BACKGROUND_LOCATION_ACCESS_REQUEST_CODE);
                }
            }

        } else {
            addGeofence(latLng, GEOFENCE_RADIUS);
        }

    }
    //start monitoring geofence add Latlng and radius around the location
    private void addGeofence(LatLng latLng, float radius) {

        Geofence geofence = geofenceHelper.getGeofence(GEOFENCE_ID, latLng, radius,
                Geofence.GEOFENCE_TRANSITION_ENTER|Geofence.GEOFENCE_TRANSITION_EXIT|Geofence.GEOFENCE_TRANSITION_DWELL);
        GeofencingRequest geofencingRequest = geofenceHelper.getGeofencingRequest(geofence);
        PendingIntent pendingIntent = geofenceHelper.getPendingIntent();

        geofencingClient.addGeofences(geofencingRequest, pendingIntent)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "onSuccess: Geofence Added...");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        String errorMessage = geofenceHelper.getErrorString(e);
                        Log.d(TAG, "onFailure: " + errorMessage);
                    }
                });
    }

    //this method grant Permission ACCESS_FINE_LOCATION
    private void enableUserLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
        } else {
            //Ask for permission
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                //We need to show user a dialog for displaying why the permission is needed and then ask for the permission...
                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, FINE_LOCATION_ACCESS_REQUEST_CODE);
            } else {
                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, FINE_LOCATION_ACCESS_REQUEST_CODE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == FINE_LOCATION_ACCESS_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //We have the permission
            } else {
                //We do not have the permission..

            }
        }

        if (requestCode == BACKGROUND_LOCATION_ACCESS_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //We have the permission
                Toast.makeText(this, "You can add geofences...", Toast.LENGTH_SHORT).show();
            } else {
                //We do not have the permission..
                Toast.makeText(this, "Background location access is neccessary for geofences to trigger...", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        myDb.close();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        return;
    }
}
