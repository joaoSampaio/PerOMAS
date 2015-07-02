package pt.ulisboa.tecnico.peromas.peromas.bluetooth;

import android.app.Application;

import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.powersave.BackgroundPowerSaver;

import pt.ulisboa.tecnico.peromas.peromas.BackGroundService;
import pt.ulisboa.tecnico.peromas.peromas.MainActivity;

/**
 * Created by sampaio on 24-06-2015.
 */
public class BluetoothApplication extends Application  {
    private static final String TAG = "teste";
    private BeaconManager beaconManager;
    //private RegionBootstrap regionBootstrap;
    private BackgroundPowerSaver backgroundPowerSaver;
    private boolean haveDetectedBeaconsSinceBoot = false;
    private MainActivity activity = null;
    private BackGroundService backGroundService = null;


    public void onCreate() {
        super.onCreate();
       // BeaconManager beaconManager = org.altbeacon.beacon.BeaconManager.getInstanceForApplication(this);

       // backgroundPowerSaver = new BackgroundPowerSaver(this);


    }






//    public void registerMonitoringActivity(MainActivity activity) {
//        this.activity = activity;
//    }
//    public void registerBackGroundService(BackGroundService service){
//        this.backGroundService = service;
//        Log.i(TAG, "--------------------------------register service -- is null" + (service == null));
//
//    }
//
//    public void bindBeaconManager(){
//        if(beaconManager == null) {
//            Log.i(TAG, "--------------------------------bindBeaconManager");
//
//            beaconManager = BeaconManager.getInstanceForApplication(this);
//            beaconManager.bind(this);
//        }
//    }
//
//    public boolean isBeaconManagerBound() {
//        return this.beaconManager != null;
//    }
//
//    //we only unbind if no one needs it
//    public void unbindBeaconManager(){
//        Log.i(TAG, "--------------------------------unbindBeaconManager" + (backGroundService == null) + (activity == null) );
//        if(backGroundService == null && activity == null) {
//            beaconManager.unbind(this);
//            beaconManager = null;
//        }
//    }
//
//    @Override
//    public void onBeaconServiceConnect() {
//        beaconManager.setRangeNotifier(new RangeNotifier() {
//            @Override
//            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
//                Log.i(TAG, "--------------------------------beacons");
//
//                if(activity != null)
//                    activity.transmitBLEBeacons(beacons);
//                if(backGroundService != null){
//                    Log.i(TAG, "--------------------------------transmit");
//                    //backGroundService.transmitBLEBeacons(beacons);
//                }
//            }
//
//        });
//
//        try {
//            beaconManager.startRangingBeaconsInRegion(new Region("myRangingUniqueId", null, null, null));
//        } catch (RemoteException e) {   }
//    }
}