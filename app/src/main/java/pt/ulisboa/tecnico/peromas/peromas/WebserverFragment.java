package pt.ulisboa.tecnico.peromas.peromas;

import android.app.Activity;
import android.app.Fragment;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.koushikdutta.async.http.server.AsyncHttpServer;
import com.koushikdutta.async.http.server.AsyncHttpServerRequest;
import com.koushikdutta.async.http.server.AsyncHttpServerResponse;
import com.koushikdutta.async.http.server.HttpServerRequestCallback;

import pt.ulisboa.tecnico.peromas.peromas.kiosk.PrefUtils;

public class WebserverFragment extends Fragment {

    private View rootView;
    private int count;
    private TextView txtwebserver;
    private Button hiddenExitButton;
	public WebserverFragment(){}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        rootView = inflater.inflate(R.layout.fragment_webserver, container, false);
        txtwebserver = (TextView)rootView.findViewById(R.id.txtwebserver);
        rootView.findViewById(R.id.btn_server).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchServer();
            }
        });

        rootView.findViewById(R.id.btn_stop).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopServer();
            }
        });

        hiddenExitButton = (Button) rootView.findViewById(R.id.hiddenExitButton);
        hiddenExitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Break out!
                PrefUtils.setKioskModeActive(false, getActivity().getApplicationContext());
                Toast.makeText(getActivity(), "You can leave the app now!", Toast.LENGTH_SHORT).show();
            }
        });

        return rootView;
    }


    private void launchServer(){
        MainActivity activity = (MainActivity)getActivity();
        if(activity.getServer() == null)
            activity.setServer(new AsyncHttpServer());
        AsyncHttpServer server = activity.getServer();

        //List<WebSocket> _sockets = new ArrayList<WebSocket>();
        String msg = "";
        try {
            count = 0;
            server.get("/", new HttpServerRequestCallback() {
                @Override
                public void onRequest(AsyncHttpServerRequest request, AsyncHttpServerResponse response) {
                    response.send("Hello!!! ->count:" + count);
                    count++;
                }
            });

// listen on port 5000
            server.listen(5000);

            WifiManager wm = (WifiManager) getActivity().getSystemService(Activity.WIFI_SERVICE);
            String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
            msg = "The server is running in -> " + ip + ":5000";
        } catch (Exception e) {
            e.printStackTrace();
            msg = "The server was not launched!";
        } finally {
            txtwebserver.setText(msg);
        }

    }

    private void stopServer(){
        MainActivity activity = (MainActivity)getActivity();
        if(activity.getServer() != null) {
            activity.getServer().stop();
            txtwebserver.setText("Server stopped");
            activity.setServer(null);
        }
    }

}
