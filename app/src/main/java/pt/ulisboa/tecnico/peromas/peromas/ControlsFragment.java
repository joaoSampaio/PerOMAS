package pt.ulisboa.tecnico.peromas.peromas;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import pt.ulisboa.tecnico.peromas.peromas.wifi.RestClient;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class ControlsFragment extends Fragment implements View.OnClickListener {

    private final static int[] CLICKABLE = {R.id.btn_light0, R.id.button3, R.id.button4, R.id.light_switch};
    private int errors;
    View rootView;
    private boolean isLightOn;
    String username, password, ip, csrf_token;
    String not_set = "not set";

    private Bitmap bitmap_lightOn, bitmap_lightOff;

	public ControlsFragment(){}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        rootView = inflater.inflate(R.layout.fragment_controls, container, false);

        for(int id : CLICKABLE)
            rootView.findViewById(id).setOnClickListener(this);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        errors = 0;
        username = preferences.getString("username_peromas", not_set);
        password = preferences.getString("password_peromas", not_set);
        ip = preferences.getString("ip_peromas", not_set);
        if(!ip.startsWith("http://"))
            ip = "http://" + ip;
        csrf_token ="not set";
        isLightOn = false;






        return rootView;
    }

    @Override
    public void onStop(){
        super.onStop();
        Log.d("myapp", " onStop");
        recycleBitmaps();

    }

    @Override
    public void onStart(){
        super.onStart();
        Log.d("myapp", " onStart");
        getStatus();

    }



    private void login(){

        if(username.contains(not_set) || password.contains(not_set) || ip.contains(not_set)){

            Toast.makeText(getActivity(),"Username/Password/IP are not defined. Please go to settings.",Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(getActivity(),username,Toast.LENGTH_SHORT).show();

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
                                    csrf_token = values[1].split("\"")[0];
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.d("myapp", "aaaaaaaa" + csrf_token);
                loginWithToken(username, password, ip, csrf_token.trim());
            }

            @Override
            public void failure(RetrofitError arg0) {
                Log.e("ERROR:", arg0.getLocalizedMessage());
                Log.d("myapp", "ERROR");
            }
        });
    }

    private void loginWithToken( String username, String password, String ip, String csrf_token){
        RestClient.getService(ip).loginPost(username, password, "y", csrf_token, new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {

            }

            @Override
            public void failure(RetrofitError arg0) {
                Log.e("ERROR:", arg0.getLocalizedMessage());
                Log.d("myapp", "ERROR");


            }
        });


    }


    private boolean isWifiOn(){
        WifiManager wifi = (WifiManager)getActivity().getSystemService(Context.WIFI_SERVICE);
        if (!wifi.isWifiEnabled()){
            Toast.makeText(getActivity(),"Wifi is not enabled!.",Toast.LENGTH_SHORT).show();
        }
        return wifi.isWifiEnabled();
    }

    private void turnLight(){



        if(isWifiOn()) {
            RestClient.getService(ip).turnOnLight("Light_1", new Callback<Response>() {
                @Override
                public void success(Response response, Response response2) {
                    Log.d("myapp", "success");
                    errors = 0;
                    isLightOn = !isLightOn;
                    setLightState();

                }

                @Override
                public void failure(RetrofitError arg0) {
                    Log.e("ERROR:", arg0.getLocalizedMessage());
                    Log.d("myapp", "ERROR");
                    errors++;
                    tryOneMoreTime();

                }
            });
        }
    }

    private void logout(){
        RestClient.getService(ip).logout(new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                Log.d("myapp", "success");


            }

            @Override
            public void failure(RetrofitError arg0) {
                Log.e("ERROR:", arg0.getLocalizedMessage());
                Log.d("myapp", "ERROR");


            }
        });
    }


    private void getStatus() {

        if (isWifiOn()) {
            RestClient.getService(ip).indexGET(new Callback<Response>() {
                @Override
                public void success(Response response, Response response2) {
                    boolean isLoggded = false;
                    BufferedReader reader = null;
                    try {
                        reader = new BufferedReader(new InputStreamReader(response.getBody().in()));
                        String line;
                        try {
                            while ((line = reader.readLine()) != null) {
                                //sb.append(line);
                                Log.d("myapp", "..." + line);
                                if (line.contains("name=\"Light_1\"")) {
                                    isLoggded = true;
                                    Log.d("myapp", "...contains");
                                    isLightOn = line.contains("btn-warning");
                                    break;
                                }
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (!isLoggded) {
                        errors++;
                        if (errors <= 2) {
                            login();
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    getStatus();
                                }
                            }, 500);
                        }
                    } else {
                        errors = 0;
                        setLightState();
                    }
                }

                @Override
                public void failure(RetrofitError arg0) {
                    Log.e("ERROR:", arg0.getLocalizedMessage());
                    Log.d("myapp", "ERROR");
                }
            });
        }
    }

    private void setLightState(){
        ImageView light = (ImageView)rootView.findViewById(R.id.imageLight);
        if(isLightOn){

            if(bitmap_lightOn == null || bitmap_lightOn.isRecycled()){
                bitmap_lightOn = BitmapFactory.decodeResource(getResources(), R.drawable.power_button);
            }
            light.setImageBitmap(bitmap_lightOn);

        }else{
            if(bitmap_lightOff == null || bitmap_lightOff.isRecycled()){
                bitmap_lightOff = BitmapFactory.decodeResource(getResources(), R.drawable.power_button_off);
            }
            light.setImageBitmap(bitmap_lightOff);
        }
    }

    private void recycleBitmaps(){
        ImageView light = (ImageView)rootView.findViewById(R.id.imageLight);
        if(bitmap_lightOn != null && !bitmap_lightOn.isRecycled()){
            light.setImageBitmap(null);
            bitmap_lightOn.recycle();
            bitmap_lightOn = null;
        }
        if(bitmap_lightOff != null && !bitmap_lightOff.isRecycled()){
            light.setImageBitmap(null);
            bitmap_lightOff.recycle();
            bitmap_lightOff = null;
        }
    }


    private void tryOneMoreTime(){
        if(errors <= 2) {
            login();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    turnLight();
                }
            },500);
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_light0:
                login();
                break;
            case R.id.button3:
                turnLight();
                break;
            case R.id.button4:
                logout();
                break;
            case R.id.light_switch:
                turnLight();
                break;
        }
    }
}
