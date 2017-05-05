package com.impact.locationtracker;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

/**
 * Created by pra...deep on 5/4/2017.
 */
public class Route {

    public com.impact.locationtracker.Distance distance;
    public com.impact.locationtracker.Duration duration;
    public String endAddress;
    public LatLng endLocation;
    public String startAddress;
    public LatLng startLocation;

    public List<LatLng> points;

}
