package pt.ulisboa.tecnico.peromas.peromas;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.powersave.BackgroundPowerSaver;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import pt.ulisboa.tecnico.peromas.peromas.bluetooth.SendBeaconsToActivityService;
import pt.ulisboa.tecnico.peromas.peromas.model.BeaconWithDate;
import pt.ulisboa.tecnico.peromas.peromas.wifi.SendMessageService;

/**
 * Created by sampaio on 23-06-2015.
 */
public class BackGroundService extends Service implements BeaconConsumer {

    private static String TAG = "teste";
    private BeaconManager beaconManager;
    private Map<String, BeaconWithDate> allBeacons;
    private List<String> uuids;
    private String beacon_type;
    private String server_notify_url;
    boolean doSendMsg;
    private BackGroundService backgroundService;
    private BackgroundPowerSaver backgroundPowerSaver;
    Handler h;
    private MainActivity activity;
    private final IBinder mBinder = new LocalBinder();

    @Override
    public IBinder onBind(Intent arg0) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
        allBeacons = new TreeMap<>();
        doSendMsg = true;
        h = new Handler();
        this.backgroundService = this;
        this.uuids = new ArrayList<>();
        beacon_type = Constants.AltBeacon;
    }

    @Override
    public synchronized void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "--------------------------------stop");

        activity = null;
        this.clean();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.i(TAG, "Service onStartCommand");
        this.uuids = new ArrayList<>();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        String uuid = preferences.getString("custom_beacon", "0");
        Set<String> multi_uuid = preferences.getStringSet("list_beacons", null);
        if(multi_uuid != null){
            uuids.addAll(multi_uuid);
        }
        if(!uuid.equals("0")){
            uuids.add(uuid);
        }

        String new_beacon_type = preferences.getString("beacon_type", Constants.AltBeacon);

        this.server_notify_url = preferences.getString("notification_url", "http://web.ist.utl.pt/ist170638/teste.php");


        Log.d(TAG, "onStart");

        if(beaconManager == null || !beacon_type.equals(new_beacon_type)) {
            //removes previous beaconManager
            this.clean();
            beaconManager = BeaconManager.getInstanceForApplication(this);
            this.beacon_type = new_beacon_type;
            //if the target is an iBeacon we change the layout. (AltBeacon is by default enable)
            if(this.beacon_type.equals(Constants.iBeacon)){
                Log.i("teste", "************************ibeacon:  ");
                beaconManager.getBeaconParsers().add(new BeaconParser().
                        setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"));
            }
            beaconManager.bind(this);

            backgroundPowerSaver = new BackgroundPowerSaver(this);
            // set the duration of the scan to be 2.1 seconds
            beaconManager.setBackgroundScanPeriod(5100l);
            // set the time between each scan to be  30 seconds
            String frequency_beacon = preferences.getString("frequency_beacon", 15+"");
            int times = 1;
            try{
                times = Integer.parseInt(frequency_beacon);
            }catch (Exception e){

            }

            beaconManager.setBackgroundBetweenScanPeriod(times * 1000l);
            beaconManager.setBackgroundMode(true);

            //backgroundPowerSaver = new BackgroundPowerSaver(this);
        }
        return Service.START_STICKY;
    }


    @Override
    public void onBeaconServiceConnect() {
        beaconManager.setRangeNotifier(new RangeNotifier() {
                                           @Override
                                           public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                                               Date date = new Date();
                                               Log.i("teste", "************************beacon size:  " + beacons.size());
                                               for (Beacon beacon : beacons) {
                                                   Log.i("teste", "************************ssss  " + beacon.toString());
                                                   Log.i("teste", "----------------------");
                                               }
                                               if (activity != null) {
                                                   SendBeaconsToActivityService service = new SendBeaconsToActivityService(activity, beacons);
                                                   service.execute();
                                               }

                                               //doSendMsg = false;
                                               for (Beacon beacon : beacons) {
                                                   if (doSendMsg && uuids.contains(beacon.getId1() + "")) {
                                                       //enviar msg web
                                                       Log.i("teste", "************************enviar msg web  ");
                                                       Log.i("teste", "************************enviar msg web  ");
                                                       Log.i("teste", "************************enviar msg web  ");

                                                       doSendMsg = false;
                                                       SendMessageService service = new SendMessageService(server_notify_url, beacon.getId1() + "");
                                                       service.execute();

                                                       //we found the beacon, now we stop searching and try again in 60 seconds
                                                       if (activity == null) {
                                                           beaconManager.unbind(backgroundService);
                                                           beaconManager = null;
                                                       }

                                                       h.postDelayed(new Runnable() {
                                                           public void run() {
                                                               // do something
                                                               doSendMsg = true;
                                                               if (beaconManager == null) {
                                                                   beaconManager = BeaconManager.getInstanceForApplication(backgroundService);
                                                                   beaconManager.bind(backgroundService);
                                                               }
                                                           }
                                                       }, 60000);
                                                   }
                                                   allBeacons.put(beacon.getId1() + "", new BeaconWithDate(beacon.getId1() + "", date, beacon));
                                               }
                                               removeOldBeacons();

                                           }

                                       }

        );

            try

            {
                beaconManager.startRangingBeaconsInRegion(new Region("myRangingUniqueId", null, null, null));
            }

            catch(
            RemoteException e
            )

            {
            }
        }


        @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.i(TAG, "--------------------------------onTaskRemoved");
        Intent restartServiceIntent = new Intent(getApplicationContext(), this.getClass());
        restartServiceIntent.setPackage(getPackageName());

        PendingIntent restartServicePendingIntent = PendingIntent.getService(getApplicationContext(), 1, restartServiceIntent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmService = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmService.set(
                AlarmManager.ELAPSED_REALTIME,
                SystemClock.elapsedRealtime() + 1000,
                restartServicePendingIntent);

        super.onTaskRemoved(rootIntent);
    }

    private void removeOldBeacons() {
        Date now = new Date();
        List<String> idsRemover = new ArrayList<>();
        for (TreeMap.Entry<String, BeaconWithDate> entry : allBeacons.entrySet()) {
            // verificar se passou 10 segundos(10s*1000ms)
            if (now.getTime() - entry.getValue().getLastTimeReceived().getTime() > 10 * 1000) {
                //passou mais de 10 segundos sem update (vamos remover)
                idsRemover.add(entry.getKey());
            }
        }
        for(String id: idsRemover)
            allBeacons.remove(id);
    }

    public class LocalBinder extends Binder {
        public BackGroundService getServiceInstance(){
            return BackGroundService.this;
        }
    }


    private void clean(){
        h.removeCallbacksAndMessages(null);
        if(beaconManager != null) {
            beaconManager.unbind(this);
            beaconManager = null;
        }
    }


    public void registerMonitoringActivity(MainActivity activity) {
        this.activity = activity;
        if(activity != null && beaconManager != null)
            beaconManager.setBackgroundMode(false);
        if(activity == null && beaconManager != null)
            beaconManager.setBackgroundMode(true);
    }

    public boolean isBoundMonitoringActivity(){
        return activity != null;
    }



}


