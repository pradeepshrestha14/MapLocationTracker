package com.impact.locationtracker;
// AIzaSyBaNAoU6RBS_8x0uBveZy4_ZqFRnw2zp4I


import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
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

import com.cocoahero.android.geojson.GeoJSON;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener, DirectionFinderListener {
    Double first, second, INT_MAX;


    private Button btnFindPath;
    private EditText etOrigin;
    private EditText etDestination;
    private List<Marker> originMarkers = new ArrayList<>();
    private List<Marker> destinationMarkers = new ArrayList<>();
    private List<Polyline> polylinePaths = new ArrayList<>();
    private ProgressDialog progressDialog;


    LatLng temp;
    private Double tempLat;
    private Double tempLongi;


    public static final LatLng bhaktapur = new LatLng(27.673136, 85.422302);


    EditText search;
    Button button, go, normal, hybrid, satellite, terrain, createButton, nearestButton, btnDialog;
    TextView textView;


    final static int PERMISSION_ALL = 1;
    final static String[] PERMISSIONS = {android.Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION};

    private GoogleMap mMap;
    MarkerOptions mo;
    Marker marker;
    LocationManager locationManager;
    EditText editTextLat, editTextLong;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        btnFindPath = (Button) findViewById(R.id.btnFindPath);
        etOrigin = (EditText) findViewById(R.id.etOrigin);
        etDestination = (EditText) findViewById(R.id.etDestination);
        btnFindPath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendRequest();
            }
        });


        btnDialog = (Button) findViewById(R.id.id_dialog);
        btnDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                MyDialog myDialog = new MyDialog();
//                myDialog.show(getSupportFragmentManager(), "my_dialog");

                AlertDialog.Builder dBuilder = new AlertDialog.Builder(MapsActivity.this);
                View dView = getLayoutInflater().inflate(R.layout.dialogbox, null);
                Button btnConfirm = (Button) dView.findViewById(R.id.saveButton);
                Button btnCancel = (Button) dView.findViewById(R.id.discardButton);

                TextView latlong = (TextView) dView.findViewById(R.id.id_latlng_textview);

                latlong.setText("your lattitude=" + tempLat + "  your Longitude=" + tempLongi);
                dBuilder.setView(dView);
                final AlertDialog dialog = dBuilder.create();
                dialog.show();
                btnConfirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mMap.addMarker(new MarkerOptions().position(temp).icon(BitmapDescriptorFactory.fromResource(R.drawable.start_blue)));
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(temp, 10));
                        dialog.dismiss();


                    }
                });
                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

            }
        });

        editTextLat = (EditText) findViewById(R.id.idLattitude);
        editTextLong = (EditText) findViewById(R.id.idLongitude);
        createButton = (Button) findViewById(R.id.idCreate);
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String lattitude = editTextLat.getText().toString();
                String longitude = editTextLong.getText().toString();

                Double userLattitude = Double.valueOf(lattitude).doubleValue();
                Double userLongitude = Double.valueOf(longitude).doubleValue();

                mMap.addMarker(new MarkerOptions().position(new LatLng(userLattitude, userLongitude)));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(userLattitude, userLongitude), 8));


            }
        });
        nearestButton = (Button) findViewById(R.id.idNearest);
        nearestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] locationName = new String[]{"bhaktapur", "kathmandu", "patan", "banepa", "jiri"};
                Double[] lattitude = new Double[]{27.6710, 27.7172, 27.6644, 27.6332, 27.6276};
                Double[] longitude = new Double[]{85.4298, 85.3240, 85.3188, 85.5277, 86.2260};
                Double[] distance = new Double[5];
                try {
                    for (int i = 0; i < locationName.length; i++) {
//                    double x1,x2,y1,y2,x,y;
//                            x1=tempLat;
//                    y1=tempLongi;
//                    x2=lattitude[i];
//                    y2=longitude[i];
//                    x=(x2-x1)*(x2-x1);
//                    y=(y2-y1)*(y2-y1);
//                    distance[i]=Math.sqrt(x+y);


                        int R = 6371; // km
                        double x = (longitude[i] - tempLongi) * Math.cos((lattitude[i] + tempLat) / 2);
                        double y = (lattitude[i] - tempLat);
                        distance[i] = Math.sqrt(x * x + y * y) * R;

                    }
                } catch (Exception e) {
                    Toast.makeText(MapsActivity.this, "no  location obtained.", Toast.LENGTH_LONG).show();

                }
                double min1, min2;


                min1 = distance[0];
                min2 = distance[1];
                if (min2 < min1) {
                    min1 = distance[1];
                    min2 = distance[0];
                }

                for (int i = 2; i < distance.length; i++) {
                    if (distance[i] < min1) {
                        min2 = min1;
                        min1 = distance[i];
                    } else if (distance[i] < min2) {
                        min2 = distance[i];
                    }
                }
//                Toast.makeText(MapsActivity.this, "distance is:" + min1, Toast.LENGTH_LONG).show();


                for (int i = 0; i < distance.length; i++) {
                    if (distance[i] == min1 || distance[i] == min2) {
//                            mMap.addMarker(new MarkerOptions().position(new LatLng(lattitude[i], longitude[i])).title(locationName[i]));
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(temp, 10));

                        PolylineOptions poptiion = new PolylineOptions().add(temp).add(new LatLng(lattitude[i], longitude[i])).width(5).color(Color.BLUE).geodesic(true);
                        mMap.addPolyline(poptiion);


                    }
                }


            }
        });


        //start hybrid normal haroooo
        button = (Button) findViewById(R.id.mylocationid);
        textView = (TextView) findViewById(R.id.textviewid);


        search = (EditText) findViewById(R.id.ed_search);

        go = (Button) findViewById(R.id.go_btn);

//        normal = (Button) findViewById(R.id.normalbtn);
//
//        hybrid = (Button) findViewById(R.id.hybridbtn);
//
//        satellite = (Button) findViewById(R.id.satellitebtn);
//
//        terrain = (Button) findViewById(R.id.terrainbtn);
//
//        normal.setOnClickListener(new View.OnClickListener()
//
//                                  {
//                                      @Override
//                                      public void onClick(View v) {
//                                          mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
//
//                                      }
//                                  }
//
//        );
//        hybrid.setOnClickListener(new View.OnClickListener()
//
//                                  {
//                                      @Override
//                                      public void onClick(View v) {
//                                          mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
//
//                                      }
//                                  }
//
//        );
//        satellite.setOnClickListener(new View.OnClickListener()
//
//                                     {
//                                         @Override
//                                         public void onClick(View v) {
//                                             mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
//
//
//                                         }
//                                     }
//
//        );
//
//        terrain.setOnClickListener(new View.OnClickListener()
//
//                                   {
//                                       @Override
//                                       public void onClick(View v) {
//                                           mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
//
//
//                                       }
//                                   }
//
//        );
//
//        //end hybrid normal haru

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
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lattitude[i], longitude[i]), 20));


                        PolylineOptions poptiion = new PolylineOptions().add(temp).add(new LatLng(lattitude[i], longitude[i])).width(5).color(Color.BLUE).geodesic(true);
                        mMap.addPolyline(poptiion);


                    }else{
                        List<Address> addressList = null;
                        if (location != null || !location.equals("")) {
                            Geocoder geocoder = new Geocoder(MapsActivity.this);
                            try {
                                addressList = geocoder.getFromLocationName(location, 1);
                            } catch (IOException e) {
                                e.printStackTrace();
                                Toast.makeText(MapsActivity.this, "no internet", Toast.LENGTH_LONG).show();
                            }
                            Address address = addressList.get(0);
                            LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                            String longi = String.valueOf(address.getLatitude());
                            String lat = String.valueOf(address.getLongitude());


                            mMap.addMarker(new MarkerOptions().position(latLng).title("LatLong-" + longi + "," + lat));

                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 8));

                        }

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

    private void sendRequest() {
        String origin = etOrigin.getText().toString();
        String destination = etDestination.getText().toString();
        if (origin.isEmpty()) {
            Toast.makeText(this, "Please enter origin address!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (destination.isEmpty()) {
            Toast.makeText(this, "Please enter destination address!", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            new DirectionFinder(this, origin, destination).execute();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDirectionFinderStart() {
        progressDialog = ProgressDialog.show(this, "Please wait.",
                "Finding direction..!", true);

        if (originMarkers != null) {
            for (Marker marker : originMarkers) {
                marker.remove();
            }
        }

        if (destinationMarkers != null) {
            for (Marker marker : destinationMarkers) {
                marker.remove();
            }
        }

        if (polylinePaths != null) {
            for (Polyline polyline : polylinePaths) {
                polyline.remove();
            }
        }
    }

    @Override
    public void onDirectionFinderSuccess(List<Route> routes) {
        progressDialog.dismiss();
        polylinePaths = new ArrayList<>();
        originMarkers = new ArrayList<>();
        destinationMarkers = new ArrayList<>();

        for (Route route : routes) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(route.startLocation, 16));
            ((TextView) findViewById(R.id.tvDuration)).setText(route.duration.text);
            ((TextView) findViewById(R.id.tvDistance)).setText(route.distance.text);

            originMarkers.add(mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.start_blue))
                    .title(route.startAddress)
                    .position(route.startLocation)));
            destinationMarkers.add(mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.end_green))
                    .title(route.endAddress)
                    .position(route.endLocation)));

            PolylineOptions polylineOptions = new PolylineOptions().
                    geodesic(true).
                    color(Color.BLUE).
                    width(10);

            for (int i = 0; i < route.points.size(); i++)
                polylineOptions.add(route.points.get(i));

            polylinePaths.add(mMap.addPolyline(polylineOptions));
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        marker = mMap.addMarker(mo);

        //................................


        //;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;


        //............................................

        LatLng NEWARK = new LatLng(27.6710, 85.4298);


        GroundOverlayOptions newarkMap = new GroundOverlayOptions()
                .image(BitmapDescriptorFactory.fromResource(R.drawable.end_green))
                .position(NEWARK, 8600f, 6500f);
        mMap.addGroundOverlay(newarkMap);


        //;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

        //.........................................
//        PolygonOptions rectOptions = new PolygonOptions()
//                .add(new LatLng(25.9, 78.56),
//                        new LatLng(26.8, 89.5),
//                        new LatLng(37.45, -122.2),
//                        new LatLng(37.35, -122.2),
//                        new LatLng(25.9, 78.56));

// Get back the mutable Polygon
//        Polygon polygon = mMap.addPolygon(rectOptions);

        //;;;;;;;;;;;;;;;;;;;


        String[] locationName = new String[]{"bhaktapur", "kathmandu", "patan", "banepa", "jiri"};
        Double[] lattitude = new Double[]{27.6710, 27.7172, 27.6644, 27.6332, 27.6276};
        Double[] longitude = new Double[]{85.4298, 85.3240, 85.3188, 85.5277, 86.2260};
        Double[] distance = new Double[5];
        for (int i = 0; i < locationName.length; i++) {

            mMap.addMarker(new MarkerOptions().position(new LatLng(lattitude[i], longitude[i])).title(locationName[i]));

        }

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
        temp = myCoordinates;
        tempLat = location.getLatitude();
        tempLongi = location.getLongitude();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                mMap.addMarker(new MarkerOptions().position(myCoordinates).icon(BitmapDescriptorFactory.fromResource(R.drawable.marker)));


                marker.setPosition(myCoordinates);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myCoordinates, 13));
                textView.setText(location.getLatitude() + "  " + location.getLongitude());

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


    //.........................................................................................
}
