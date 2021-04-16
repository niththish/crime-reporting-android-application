package com.example.crimereporting;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.os.Looper;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

public class report extends Fragment {

    private EditText alert_type,alert_desc;
    private Button alert_btn;
    FusedLocationProviderClient fusedLocationProviderClient;
    Geocoder geocoder;

    private String mJSONURLString = "https://thawing-depths-14843.herokuapp.com/reports";
    private RequestQueue mQueue;


    private String crimeType;
    private String description;
    private String latitude;
    private String longitude;
    private String area;


    public report() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_report, container, false);

        alert_type=view.findViewById(R.id.type_text);
        alert_desc=view.findViewById(R.id.desc_text);
        alert_btn=view.findViewById(R.id.alert_btn);

        fusedLocationProviderClient= LocationServices.getFusedLocationProviderClient(getContext());
        geocoder=new Geocoder(getContext());

        alert_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if(ActivityCompat.checkSelfPermission(
                        getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(getContext(),Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){

                    getLocation();


                    crimeType= alert_type.getText().toString();
                    description=alert_desc.getText().toString();

                    if(crimeType.length()<2){
                        Toast.makeText(getContext(), "enter crime type", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        if(description.length()==0){
                            description="no information provided";
                        }
                        //Toast.makeText(getContext(),crimeType+" "+description,Toast.LENGTH_LONG).show();

                        postData();
                    }

                }
                else{
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},100);
                }



            }
        });
        return view;
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==100 && grantResults.length>0 && (grantResults[0]+ grantResults[1])== PackageManager.PERMISSION_GRANTED){
            getLocation();
        }
        else{
            Toast.makeText(getContext(),"Permissions denied",Toast.LENGTH_LONG).show();
        }
    }

    @SuppressLint("MissingPermission")
    private void getLocation() {


        LocationManager locationManager =  (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    final Location location = task.getResult();

                    if (location != null) {

                        latitude=String.valueOf(location.getLatitude());
                        longitude=String.valueOf(location.getLongitude());

                        try {
                            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                            Address address = addresses.get(0);

                            area=address.getSubLocality();



                        } catch (IOException e) {
                            e.printStackTrace();
                        }


                    } else {
                        final LocationRequest locationRequest = new LocationRequest().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                                .setInterval(10000).setFastestInterval(1000).setNumUpdates(1);
                        LocationCallback locationCallback = new LocationCallback() {
                            @Override
                            public void onLocationResult(LocationResult locationResult) {
                                Location location1 = locationResult.getLastLocation();

                                latitude=String.valueOf(location.getLatitude());
                                longitude=String.valueOf(location.getLongitude());


                                try {
                                    List<Address> addresses = geocoder.getFromLocation(location1.getLatitude(), location1.getLongitude(), 1);
                                    Address address = addresses.get(0);

                                    area=address.getSubLocality();


                                } catch (IOException e) {
                                    e.printStackTrace();
                                }


                            }
                        };

                        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
                    }
                }
            });
        } else {
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }
    }

    private void postData(){


        mQueue= Volley.newRequestQueue(getContext());

        JSONObject object = new JSONObject();
        try {
            //input your API parameters
            object.put("crimetype",crimeType);
            object.put("description",description);
            object.put("latitude",latitude);
            object.put("longitude",longitude);
            object.put("area",area);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                mJSONURLString,
                object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(getContext(),"String Response : "+ response.toString(),Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getContext(),"String Response : "+ "reported successfully",Toast.LENGTH_SHORT).show();
                    }
                }
        );
        mQueue.add(jsonObjectRequest);


    }


    }