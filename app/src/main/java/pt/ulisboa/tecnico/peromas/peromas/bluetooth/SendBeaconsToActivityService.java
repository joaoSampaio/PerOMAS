package pt.ulisboa.tecnico.peromas.peromas.bluetooth;

import android.os.AsyncTask;

import org.altbeacon.beacon.Beacon;

import java.util.Collection;

import pt.ulisboa.tecnico.peromas.peromas.MainActivity;


public class SendBeaconsToActivityService extends AsyncTask<Void, Void, Void> {

    private MainActivity activity;
    private Collection<Beacon> beacons;
    public SendBeaconsToActivityService(MainActivity activity, Collection<Beacon> beacons){
        this.activity = activity;
        this.beacons = beacons;
    }


    @Override
    protected void onPreExecute(){
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(Void... params) {

        if(activity != null)
            activity.transmitBLEBeacons(beacons);

        activity = null;
        return null;
    }





}
