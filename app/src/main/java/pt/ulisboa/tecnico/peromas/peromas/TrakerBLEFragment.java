package pt.ulisboa.tecnico.peromas.peromas;

import android.app.ActivityManager;
import android.app.Fragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import org.altbeacon.beacon.Beacon;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import pt.ulisboa.tecnico.peromas.peromas.adapter.BeaconsDetectedAdapter;
import pt.ulisboa.tecnico.peromas.peromas.model.BeaconWithDate;

public class TrakerBLEFragment extends Fragment {

    private ListView listView;
    private BeaconsDetectedAdapter adapter;
    private Map<String, BeaconWithDate> allBeacons;
    private Button scan;
    private View rootView;
    private boolean background_on;
    private BackGroundService myService;

	public TrakerBLEFragment(){}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        rootView = inflater.inflate(R.layout.fragment_ble_traker, container, false);


        listView = (ListView) rootView.findViewById(R.id.listBeacons);
        allBeacons = new TreeMap<>();
        scan = (Button)rootView.findViewById(R.id.scanble);
        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleScan();
            }
        });
        background_on = isMyServiceRunning(BackGroundService.class);

        if(!isMyServiceRunning(BackGroundService.class)){
            Toast.makeText(getActivity().getApplicationContext(), "Please enable Location Service", Toast.LENGTH_SHORT).show();
        }



        return rootView;
    }

    void doBindService() {
        // Establish a connection with the service.  We use an explicit
        // class name because there is no reason to be able to let other
        // applications replace our component.
        getActivity().bindService(new Intent(getActivity(),
                BackGroundService.class), mConnection, Context.BIND_AUTO_CREATE);
        Log.d("teste", "+++++++++++++++++++++++++doBindService");

    }

    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've binded to LocalService, cast the IBinder and get LocalService instance
            BackGroundService.LocalBinder binder = (BackGroundService.LocalBinder) service;
            myService = binder.getServiceInstance(); //Get instance of your service!
            //myService.registerMonitoringActivity((MainActivity) getActivity());
            Log.d("teste", "+++++++++++++++++++++++++onServiceConnected");
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {

        }
    };

    public void onBeaconNotifier(Collection<Beacon> beacons){
        Date date = new Date();
        for(Beacon beacon: beacons){
            allBeacons.put(beacon.getId1()+"", new BeaconWithDate(beacon.getId1()+"",date, beacon ));
        }
        removeOldBeacons();
        updateAdapter();
    }



    private void updateAdapter() {

        getActivity().runOnUiThread(new Runnable() {
            public void run() {
                if (adapter == null) {
                    adapter = new BeaconsDetectedAdapter((MainActivity) getActivity(), allBeacons);
                    listView.setAdapter(adapter);
                } else {
                    //adapter.setBeacons(allBeacons);
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        doBindService();



        //((BluetoothApplication) getActivity().getApplicationContext()).registerMonitoringActivity((MainActivity)getActivity());
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("teste", "++++++++++++++++++++++onPauseonPauseonPauseonPause+++");
        if(myService != null)
            myService.registerMonitoringActivity(null);
        myService = null;
        getActivity().unbindService(mConnection);
    }

    public void toggleScan(){
        Log.d("teste", "+++++++++++++++++++++++++");

        if(isMyServiceRunning(BackGroundService.class)){
            if(myService == null){
                Log.d("teste", "++++++++++++myService == null+++++++++++++");
                Toast.makeText(getActivity().getApplicationContext(), "Please try again", Toast.LENGTH_SHORT).show();
                return;
            }

            Log.d("teste", "++++++++++yyyy+++++++++++++++" + myService.isBoundMonitoringActivity());
            if(!myService.isBoundMonitoringActivity()){
                Log.d("teste", "++++++++++!isBoundMonitoringActivity+++++++++++++++");
                myService.registerMonitoringActivity((MainActivity) getActivity());
                scan.setText("Stop Scanner");
                if(adapter != null){
                    adapter.notifyDataSetChanged();
                }

            }else{
                Log.d("teste", "++++++++++isBoundMonitoringActivity+++++++++++++++");
                myService.registerMonitoringActivity(null);
                scan.setText("Start Scanner");
                if(adapter != null) {
                    allBeacons = new TreeMap<>();
                    adapter.setBeacons(allBeacons);
                    adapter.notifyDataSetChanged();
                }
            }
        }else{
            Log.d("teste", "+******************************nao esta a correr");
            Toast.makeText(getActivity().getApplicationContext(), "Please enable Location Service", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
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
}
