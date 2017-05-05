package com.impact.locationtracker;

import java.util.List;

/**
 * Created by pra...deep on 5/4/2017.
 */
public interface DirectionFinderListener {
    void onDirectionFinderStart();
    void onDirectionFinderSuccess(List<Route> route);
}
