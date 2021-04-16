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
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.List;

public class locate extends Fragment {

    FusedLocationProviderClient fusedLocationProviderClient;
    Geocoder geocoder;
    private String lat,lon,area;
    private int intent_cond=0;

    public locate() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_locate, container, false);

        Button view_map=(Button)view.findViewById(R.id.view);

        fusedLocationProviderClient= LocationServices.getFusedLocationProviderClient(getContext());
        geocoder=new Geocoder(getContext());


        view_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(ActivityCompat.checkSelfPermission(
                        getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(getContext(),Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){

                    getLocation();
                }
                else{
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},100);
                }
                intent_cond+=1;

                if(intent_cond==2){
                    intent_cond=0;
                    Intent i=new Intent(getActivity(),map.class);
                    i.putExtra("lat",lat);
                    i.putExtra("lon",lon);
                    i.putExtra("area",area);
                    startActivity(i);
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
    private int getLocation() {


        LocationManager locationManager =  (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    final Location location = task.getResult();

                    if (location != null) {

                        lat=String.valueOf(location.getLatitude());
                        lon=String.valueOf(location.getLongitude());
                        //Toast.makeText(getContext(), String.valueOf(location.getLongitude())+" "+String.valueOf(location.getLongitude()), Toast.LENGTH_SHORT).show();

                        try {
                            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                            Address address = addresses.get(0);

                            area=String.valueOf(address.getSubLocality());
                            //Toast.makeText(getContext(), String.valueOf(address.getSubLocality()), Toast.LENGTH_SHORT).show();


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

                                lat=String.valueOf(location.getLatitude());
                                lon=String.valueOf(location.getLongitude());
                                //Toast.makeText(getContext(), String.valueOf(location.getLongitude())+" "+String.valueOf(location.getLongitude()), Toast.LENGTH_SHORT).show();

                                try {
                                    List<Address> addresses = geocoder.getFromLocation(location1.getLatitude(), location1.getLongitude(), 1);
                                    Address address = addresses.get(0);

                                    area=String.valueOf(address.getSubLocality());
                                    //Toast.makeText(getContext(), String.valueOf(address.getSubLocality()), Toast.LENGTH_SHORT).show();

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
        return 1;
    }

}