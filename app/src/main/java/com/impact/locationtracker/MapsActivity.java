package com.impact.locationtracker;


import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONObject;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener {

    LatLng temp;

    public static final LatLng bhaktapur = new LatLng(27.673136, 85.422302);


    EditText search;
    Button button, go, normal, hybrid, satellite, terrain,createButton;
    TextView textView;


    final static int PERMISSION_ALL = 1;
    final static String[] PERMISSIONS = {android.Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION};

    private GoogleMap mMap;
    MarkerOptions mo;
    Marker marker;
    LocationManager locationManager;
    EditText editTextLat,editTextLong;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);



        editTextLat= (EditText) findViewById(R.id.idLattitude);
        editTextLong= (EditText) findViewById(R.id.idLongitude);
        createButton= (Button) findViewById(R.id.idCreate);
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String lattitude=editTextLat.getText().toString();
                String  longitude=editTextLong.getText().toString();

                Double userLattitude=Double.valueOf(lattitude).doubleValue();
                Double userLongitude=Double.valueOf(longitude).doubleValue();

                mMap.addMarker(new MarkerOptions().position(new LatLng(userLattitude,userLongitude)));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(userLattitude,userLongitude), 8));







            }
        });




        //start hybrid normal haroooo
        button = (Button) findViewById(R.id.mylocationid);
        textView = (TextView) findViewById(R.id.textviewid);


        search = (EditText) findViewById(R.id.ed_search);

        go = (Button) findViewById(R.id.go_btn);

        normal = (Button) findViewById(R.id.normalbtn);

        hybrid = (Button) findViewById(R.id.hybridbtn);

        satellite = (Button) findViewById(R.id.satellitebtn);

        terrain = (Button) findViewById(R.id.terrainbtn);

        normal.setOnClickListener(new View.OnClickListener()

                                  {
                                      @Override
                                      public void onClick(View v) {
                                          mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

                                      }
                                  }

        );
        hybrid.setOnClickListener(new View.OnClickListener()

                                  {
                                      @Override
                                      public void onClick(View v) {
                                          mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

                                      }
                                  }

        );
        satellite.setOnClickListener(new View.OnClickListener()

                                     {
                                         @Override
                                         public void onClick(View v) {
                                             mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);


                                         }
                                     }

        );

        terrain.setOnClickListener(new View.OnClickListener()

                                   {
                                       @Override
                                       public void onClick(View v) {
                                           mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);


                                       }
                                   }

        );

        //end hybrid normal haru


        //start search ko kaam
        go.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v) {

                String location = search.getText().toString().toLowerCase();

                String[] locationName = new String[]{"bhaktapur", "kathmandu", "patan", "banepa", "jiri"};
                Double[] lattitude = new Double[]{25.3, 25.9, 26.8, 28.5, 31.9};
                Double[] longitude = new Double[]{83.4, 78.56, 89.5, 85.8, 84.8};
                for (int i = 0; i < locationName.length; i++) {

                    if (location.equals(locationName[i])) {
                        mMap.addMarker(new MarkerOptions().position(new LatLng(lattitude[i], longitude[i])).title(locationName[i]));
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lattitude[i], longitude[i]), 8));


                        PolylineOptions poptiion = new PolylineOptions().add(temp).add(new LatLng(lattitude[i], longitude[i])).width(5).color(Color.BLUE).geodesic(true);
                        mMap.addPolyline(poptiion);


                    }


                }


            }
        });


        //end search ko kaam


        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);


        mo = new MarkerOptions().position(new LatLng(0, 0)).title("My Current Location");
        if (Build.VERSION.SDK_INT >= 23 && !isPermissionGranted()) {
            requestPermissions(PERMISSIONS, PERMISSION_ALL);
        } else requestLocation();
        if (!isLocationEnabled())
            showAlert(1);

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        marker = mMap.addMarker(mo);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);


    }

    @Override
    public void onLocationChanged(final Location location) {
        final LatLng myCoordinates = new LatLng(location.getLatitude(), location.getLongitude());
        temp=myCoordinates;
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                marker.setPosition(myCoordinates);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myCoordinates,13));
                textView.setText(location.getLatitude()+"  "+location.getLongitude());

            }
        });
//        PolylineOptions poptiion=new PolylineOptions().add(myCoordinates).add(bhaktapur).add(myCoordinates).width(5).color(Color.BLUE).geodesic(true);
//        mMap.addPolyline(poptiion);
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(bhaktapur,13));



    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
    private void requestLocation() {
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setPowerRequirement(Criteria.POWER_HIGH);
        String provider = locationManager.getBestProvider(criteria, true);
        locationManager.requestLocationUpdates(provider, 10000, 10, this);
    }
    private boolean isLocationEnabled() {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    private boolean isPermissionGranted() {
        if (checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED || checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            Log.v("mylog", "Permission is granted");
            return true;
        } else {
            Log.v("mylog", "Permission not granted");
            return false;
        }
    }
    private void showAlert(final int status) {
        String message, title, btnText;
        if (status == 1) {
            message = "Your Locations Settings is set to 'Off'.\nPlease Enable Location to " +
                    "use this app";
            title = "Enable Location";
            btnText = "Location Settings";
        } else {
            message = "Please allow this app to access location!";
            title = "Permission access";
            btnText = "Grant";
        }
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setCancelable(false);
        dialog.setTitle(title)
                .setMessage(message)
                .setPositiveButton(btnText, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        if (status == 1) {
                            Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(myIntent);
                        } else
                            requestPermissions(PERMISSIONS, PERMISSION_ALL);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        finish();
                    }
                });
        dialog.show();
    }
}
