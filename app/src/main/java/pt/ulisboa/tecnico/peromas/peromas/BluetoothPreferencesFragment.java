package pt.ulisboa.tecnico.peromas.peromas;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import pt.ulisboa.tecnico.peromas.peromas.bluetooth.ServiceStartPreference;
import pt.ulisboa.tecnico.peromas.peromas.bluetooth.TextWatcher_UUID;

public class BluetoothPreferencesFragment extends PreferenceFragment implements
        OnSharedPreferenceChangeListener {

    EditText edit_uuid;
    private boolean mBlockCompletion = false;
    private boolean background_on;
    private TextView status;


    public BluetoothPreferencesFragment(){}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);

        loadValues();
        background_on = isMyServiceRunning(BackGroundService.class);
        EditTextPreference custom_beacon = (EditTextPreference)findPreference("custom_beacon");
        if(custom_beacon != null){
            edit_uuid = custom_beacon.getEditText();
            edit_uuid.addTextChangedListener(new TextWatcher_UUID(edit_uuid));
        }


    }

    private void loadValues(){
        SharedPreferences sp = getPreferenceScreen().getSharedPreferences();
        String beacon_type_string = sp.getString("beacon_type", "AltBeacon");
        Preference beacon_type = findPreference("beacon_type");
        if(beacon_type != null){
            beacon_type.setSummary(beacon_type_string);
        }
    }



    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if(key.equals("beacon_type")){
            Preference beacon_type = findPreference("beacon_type");
            if(beacon_type != null){
                beacon_type.setSummary(sharedPreferences.getString(key, "AltBeacon"));
            }
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);

        Preference button = findPreference("btn_get_list");
        if(button != null) {
            button.setOnPreferenceClickListener(new PreferenceListener());
        }


        Preference status_service = findPreference("start_service");
        if(status_service != null && status_service instanceof ServiceStartPreference) {
            status_service.setOnPreferenceClickListener(new PreferenceListener());
            String color = Constants.RED;
            if(background_on){
                color = Constants.GREEN;
            }
            ((ServiceStartPreference) status_service).setColor(color);
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    private void changeColor(String color){
        GradientDrawable bgShape = (GradientDrawable)status.getBackground();
        bgShape.setColor(Color.parseColor(color));
        if(color.equals(Constants.RED))
            status.setText("OFF");
        if(color.equals(Constants.GREEN)) {
            status.setText("ON");
        }
    }

    public void launchbackground() {
        // TODO Auto-generated method stub
        if(background_on)
        {
            background_on =false;
            Intent i=new Intent(getActivity(),BackGroundService.class);

            getActivity().stopService(i);
            Log.i("teste", "stop service!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            changeColor(Constants.RED);
            Toast.makeText(getActivity().getApplicationContext(), "service stopped", Toast.LENGTH_SHORT).show();
        }
        else
        {
            background_on = true;
            Intent i=new Intent(getActivity(),BackGroundService.class);
            getActivity().startService(i);
            changeColor(Constants.GREEN);
            Toast.makeText(getActivity().getApplicationContext(), "service started", Toast.LENGTH_SHORT).show();
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

    private class PreferenceListener implements Preference.OnPreferenceClickListener{

        @Override
        public boolean onPreferenceClick(Preference preference) {

            String key = preference.getKey();
            switch (key){
                case "btn_get_list":
                    Toast.makeText(getActivity().getApplicationContext(), "Get Beacon list from site", Toast.LENGTH_SHORT).show();
                    break;
                case "start_service":

                    Preference status_service = findPreference("start_service");
                    status = ((ServiceStartPreference) status_service).getTextView();
                    launchbackground();
                    break;

            }

            return true;
        }
    }

}
