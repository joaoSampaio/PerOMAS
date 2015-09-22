package pt.ulisboa.tecnico.peromas.peromas.wifi;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;



public class SendMessageService extends AsyncTask<Void, Void, Void> {

    private static final int SOCKET_TIMEOUT = 10000;
    private String server_notify_url;
    private String ip;

    public SendMessageService(String server_notify_url, String ip){
        this.server_notify_url = server_notify_url;
        this.ip = decipherIp(ip);
    }

    private String decipherIp(String ip){
        ip = ip.replace("-", "");
        String p1 = ip.substring(0,3);
        String p2 = ip.substring(3,6);
        String p3 = ip.substring(6,9);
        String p4 = ip.substring(9,12);
        int p1int = Integer.parseInt(p1);
        int p2int = Integer.parseInt(p2);
        int p3int = Integer.parseInt(p3);
        int p4int = Integer.parseInt(p4);

        return p1int+"."+p2int+"."+p3int+"."+p4int;

    }


    @Override
    protected void onPreExecute(){
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(Void... params) {

        Log.d("ff","enviado");
        //HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(server_notify_url);
        try {
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
            nameValuePairs.add(new BasicNameValuePair("id", "12345"));
            nameValuePairs.add(new BasicNameValuePair("message", ip));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));


            HttpParams param = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(param, 3000);
            HttpConnectionParams.setSoTimeout(param, 3000);
            HttpClient httpclient = new DefaultHttpClient(param);
            //httpClient = new DefaultHttpClient(param);


            httpclient.execute(httppost);
        } catch (ClientProtocolException e) {
            Log.d("teste", e.getMessage());
        } catch (IOException e) {
            Log.d("teste", e.getMessage());
        }
        return null;
    }





}
