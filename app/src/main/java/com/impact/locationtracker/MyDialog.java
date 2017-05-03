package com.impact.locationtracker;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

/**
 * Created by pra...deep on 4/29/2017.
 */
public class MyDialog extends DialogFragment {
    LayoutInflater inflater;
    View v;
    TextView textView;
    public MapsActivity mapsActivityObj=new MapsActivity();
//    Double dialogLat=mapsActivity.tempLat;
//    Double dialogLongi=mapsActivity.tempLongi;



    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Double dialogLat=mapsActivityObj.getTempLat();
        Double dialogLongi=mapsActivityObj.getTempLongi();

        inflater=getActivity().getLayoutInflater();
        v=inflater.inflate(R.layout.dialogbox,null);




        textView= (TextView) v.findViewById(R.id.id_latlng_textview);
        String show="Your Lattitude="+dialogLat+" and Longitude="+dialogLongi;
        textView.setText(show);




        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        builder.setView(v).setPositiveButton("SAVE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {




            }
        }).setNegativeButton("DISCARD", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });


        return builder.create();
    }
}
