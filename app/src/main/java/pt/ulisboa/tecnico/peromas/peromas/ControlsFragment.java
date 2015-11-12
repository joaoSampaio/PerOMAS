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
import java.util.Map;
import java.util.TreeMap;

import pt.ulisboa.tecnico.peromas.peromas.wifi.RestClient;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class ControlsFragment extends Fragment implements View.OnClickListener {

    private final static int[] CLICKABLE = { R.id.light_switch1, R.id.light_switch2};
    private int errors;
    View rootView;
    private final static String LIGHT_1 = "Light_1";
    private final static String LIGHT_2 = "Light_2";
    private boolean[] isLightOn;
    String username, password, ip, csrf_token;
    String not_set = "not set";

    Handler handler;
    private Bitmap bitmap_lightOn, bitmap_lightOff;

	public ControlsFragment(){}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        rootView = inflater.inflate(R.layout.fragment_controls, container, false);

        for(int id : CLICKABLE)
            rootView.findViewById(id).setOnClickListener(this);
        init();
        return rootView;
    }


    private void enableContinuousLightStatus(){
        if(handler == null)
            handler = new Handler();

        Runnable task = new Runnable() {
            @Override
            public void run() {
                if (errors <= 3) {
                    getStatus();
                    handler.postDelayed(this, 3000);
                }

            }
        };

        task.run();
    }


    private void init(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        errors = 0;
        username = preferences.getString("username_peromas", not_set);
        password = preferences.getString("password_peromas", not_set);
        ip = preferences.getString("ip_peromas", not_set);
        if(!ip.startsWith("http://"))
            ip = "http://" + ip;
        csrf_token ="not set";
        isLightOn = new boolean[2];
    }

    @Override
    public void onStop(){
        super.onStop();
        Log.d("myapp", " onStop");
        recycleBitmaps();
        if(handler != null)
            handler.removeCallbacksAndMessages(null);

    }

    @Override
    public void onStart(){
        super.onStart();
        Log.d("myapp", " onStart");
        loadBitmaps();
        enableContinuousLightStatus();
        //getStatus();

    }



    private void login(){

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
            Toast.makeText(getActivity(), "Wifi is not enabled!.", Toast.LENGTH_SHORT).show();
        }
        return wifi.isWifiEnabled();
    }

    private void turnLight(final String lightId){
        if(isWifiOn()) {
            Map<String, String> params = new TreeMap<>();
            params.put(lightId, lightId);
            RestClient.getService(ip).turnOnLight(params, new Callback<Response>() {
            //RestClient.getService(ip).turnOnLight(lightId, new Callback<Response>() {
                @Override
                public void success(Response response, Response response2) {
                    Log.d("myapp", "success");
                    errors = 0;
                    int index = lightId == LIGHT_1 ? 0 : 1;
                    isLightOn[index] = !isLightOn[index];
                    setLightState();

                }

                @Override
                public void failure(RetrofitError arg0) {
                    Log.e("ERROR:", arg0.getLocalizedMessage());
                    Log.d("myapp", "ERROR");
                    errors++;
                    tryOneMoreTime(lightId);

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
                                    isLightOn[0] = line.contains("btn-warning");

                                }
                                if (line.contains("name=\"Light_2\"")) {
                                    isLoggded = true;
                                    Log.d("myapp", "...contains");
                                    isLightOn[1] = line.contains("btn-warning");
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
//                            new Handler().postDelayed(new Runnable() {
//                                @Override
//                                public void run() {
//                                    getStatus();
//                                }
//                            }, 500);
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
        ImageView light1 = (ImageView)rootView.findViewById(R.id.imageLight1);
        ImageView light2 = (ImageView)rootView.findViewById(R.id.imageLight2);
        Bitmap tmp;
        for(int index = 0; index < 2; index++) {
            if (isLightOn[index]) {
                tmp = bitmap_lightOn;
            } else {
                tmp = bitmap_lightOff;
            }
            ((index == 0) ? light1 : light2).setImageBitmap(tmp);
        }
    }

    private void loadBitmaps(){
        if (bitmap_lightOn == null || bitmap_lightOn.isRecycled()) {
            bitmap_lightOn = BitmapFactory.decodeResource(getResources(), R.drawable.power_button);
        }
        if (bitmap_lightOff == null || bitmap_lightOff.isRecycled()) {
            bitmap_lightOff = BitmapFactory.decodeResource(getResources(), R.drawable.power_button_off);
        }
    }

    private void recycleBitmaps(){
        try {
            ImageView light = (ImageView)rootView.findViewById(R.id.imageLight1);
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void tryOneMoreTime(final String lightId){
        if(errors <= 2) {
            login();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    turnLight(lightId);
                }
            },500);
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.light_switch1:
                turnLight(LIGHT_1);
                break;
            case R.id.light_switch2:
                turnLight(LIGHT_2);
                break;
        }
    }
}
