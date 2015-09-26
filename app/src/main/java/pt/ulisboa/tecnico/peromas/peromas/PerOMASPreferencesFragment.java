package pt.ulisboa.tecnico.peromas.peromas;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import pt.ulisboa.tecnico.peromas.peromas.wifi.RestClient;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class PerOMASPreferencesFragment extends PreferenceFragment implements
        SharedPreferences.OnSharedPreferenceChangeListener {

    private TextView status;
    String not_set = "not set";
    String csrf_token;
    String ip;
       public PerOMASPreferencesFragment(){}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.general_settings);
        csrf_token = not_set;
    }


    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);

             Preference status_service = findPreference("check_cred");///////////////
        if(status_service != null && status_service instanceof CheckCredencialsPreference) {
            status_service.setOnPreferenceClickListener(new PreferenceListener());
        }

        status = ((CheckCredencialsPreference) status_service).getTextView();
        //status.setText(" ");
        //default
        //changeColor("#00B20000");

    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }


    private void changeColor(String color){
        if(status == null) {
            Preference status_service = findPreference("check_cred");
            status = ((CheckCredencialsPreference) status_service).getTextView();
        }
        GradientDrawable bgShape = (GradientDrawable)status.getBackground();
        bgShape.setColor(Color.parseColor(color));
        if(color.equals(Constants.RED))
            status.setText("N");
        else if(color.equals(Constants.GREEN)) {
            status.setText("Y");
        } else{
            status.setText(" ");
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

    }

    private void login(){


        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        final String username = preferences.getString("username_peromas", not_set);
        final String password = preferences.getString("password_peromas", not_set);
        ip = preferences.getString("ip_peromas", not_set);
        if(!ip.startsWith("http://"))
            ip = "http://" + ip;


        if(username.contains(not_set) || password.contains(not_set) || ip.contains(not_set)){

            Toast.makeText(getActivity(),"Username/Password/IP are not defined. Please go to settings.",Toast.LENGTH_SHORT).show();
            return;
        }

        RestClient.getService(ip).loginGET(new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                BufferedReader reader = null;
                try {
                    reader = new BufferedReader(new InputStreamReader(response.getBody().in()));
                    String line;
                    try {
                        while ((line = reader.readLine()) != null) {
                            //sb.append(line);
                            Log.d("myapp", "bbbb" + line);
                            if (line.contains("id=\"csrf_token\"")) {
                                Log.d("myapp", "encontrou csrf_token");
                                String[] values = line.split("value=\"");
                                if (values.length > 0)
                                    //RestClient.setCsrf(values[1].split("\"")[0]);
                                    csrf_token = values[1].split("\"")[0];
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                loginWithToken(username, password, ip, csrf_token.trim());
            }

            @Override
            public void failure(RetrofitError arg0) {
                Log.e("ERROR:", arg0.getLocalizedMessage());
                Log.d("myapp", "ERROR");
                onFailedLogin();
            }
        });
    }

    private void loginWithToken( String username, String password, String ip, String csrf_token){
        RestClient.getService(ip).loginPost(username, password, "y", csrf_token, new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                OnSucessLogin();
            }

            @Override
            public void failure(RetrofitError arg0) {
                Log.e("ERROR:", arg0.getLocalizedMessage());
                Log.d("myapp", "ERROR");
                onFailedLogin();

            }
        });


    }

    private void onFailedLogin(){
        changeColor(Constants.RED);
        Toast.makeText(getActivity(),"Login failed, check credentials.",Toast.LENGTH_SHORT).show();
    }

    private void OnSucessLogin(){
        changeColor(Constants.GREEN);
        Toast.makeText(getActivity(),"Login Successful.",Toast.LENGTH_SHORT).show();
    }


    private class PreferenceListener implements Preference.OnPreferenceClickListener{

        @Override
        public boolean onPreferenceClick(Preference preference) {

            String key = preference.getKey();
            switch (key){
                case "check_cred":
                    login();
                    break;
            }
            return true;
        }
    }

}
