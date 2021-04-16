package com.example.crimereporting;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class map extends FragmentActivity implements OnMapReadyCallback {

    GoogleMap map;
    private int ACCESS_LOCATION_REQUEST_CODE = 10001;
    FusedLocationProviderClient fusedLocationProviderClient;

    private String mJSONURLString = "https://thawing-depths-14843.herokuapp.com/reports";
    private RequestQueue mQueue;


    private Button list_view;
    private Button add_new;
    private Button test;

    private double lat;
    private double lon;
    private String area;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        lat=Double.valueOf(getIntent().getStringExtra("lat"));
        lon=Double.valueOf(getIntent().getStringExtra("lon"));
        area=getIntent().getStringExtra("area");

        Log.d("", "onCreate: "+lat+" "+lon);

        list_view=findViewById(R.id.list_view);
        add_new=findViewById(R.id.add_new);
        test=findViewById(R.id.test);



        test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });



        list_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent list_intent=new Intent(getApplicationContext(),list.class);
                startActivity(list_intent);
            }
        });

        add_new.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                map.super.onBackPressed();
            }
        });

        SupportMapFragment mapFragment=(SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);



        fusedLocationProviderClient= LocationServices.getFusedLocationProviderClient(this);
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {

        map=googleMap;

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){

            map.setMyLocationEnabled(true);


        } else {
          if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION)){
              ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},ACCESS_LOCATION_REQUEST_CODE);
          } else {
              ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},ACCESS_LOCATION_REQUEST_CODE);
          }
        }


        LatLng annangar=new LatLng(13.0850, 80.2101);
        LatLng place1=new LatLng(13.718572,77.813163);

        LatLng myLocation=new LatLng(lat,lon);

        MarkerOptions markerOptions1=new MarkerOptions().position(place1).title("my place1").snippet("This is my place1");
        //map.addMarker(markerOptions1);
        //map.addMarker(new MarkerOptions().position(annangar).title("anna nagar").snippet(" nothing to say"));

        map.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                //getData(map);

                //LatLng ll=new LatLng(13.0795414,80.2039435);
                //map.addMarker(new MarkerOptions().position(ll).title("Me").snippet(" nothing to say"));


                final ArrayList<LatLng> latLngs = new ArrayList<LatLng>();

                mQueue= Volley.newRequestQueue(getApplicationContext());

                JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                        Request.Method.GET,
                        mJSONURLString,
                        null,
                        new Response.Listener<JSONArray>() {
                            @Override
                            public void onResponse(JSONArray response) {

                                for(int i=0;i<response.length();i++){

                                    try {
                                        JSONObject report = response.getJSONObject(i);

                                        String crimetype = report.getString("crimetype");
                                        String description = report.getString("description");
                                        String latitude = report.getString("latitude");
                                        String longitude = report.getString("longitude");
                                        String Area = report.getString("area");


                                        LatLng place=new LatLng(Double.valueOf(latitude),Double.valueOf(longitude));
                                        latLngs.add(place);
                                        //LatLng l=new LatLng(12.9249, 80.1000);
                                        //map.addMarker(new MarkerOptions().position(l).title("Me").snippet(" nothing to say"));




                                        for(LatLng latLng:latLngs){
                                            MarkerOptions markerOptions=new MarkerOptions().position(latLng).title(crimetype).snippet(description);
                                            map.addMarker(markerOptions);
                                        }



                                        // Display the formatted json data in text view
                                        //Toast.makeText(getApplicationContext(),crimetype,Toast.LENGTH_SHORT).show();

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }


                                }

                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(getApplicationContext(),"error getting data",Toast.LENGTH_SHORT).show();
                            }
                        }
                );
                mQueue.add(jsonArrayRequest);



            }
        });

        //MarkerOptions markerOptions2=new MarkerOptions().position(myLocation).title("my place1").snippet("This is my place1");
        //map.addMarker(markerOptions2);

        CameraUpdate cameraUpdate=CameraUpdateFactory.newLatLngZoom(myLocation,17);
        map.moveCamera(cameraUpdate);

    }




    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == ACCESS_LOCATION_REQUEST_CODE){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

                if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){

                    map.setMyLocationEnabled(true);

                }

            }
            else{

            }
        }
    }


}