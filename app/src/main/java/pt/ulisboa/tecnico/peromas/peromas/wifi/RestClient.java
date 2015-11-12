package pt.ulisboa.tecnico.peromas.peromas.wifi;

import com.squareup.okhttp.OkHttpClient;

import java.net.CookieManager;
import java.net.CookiePolicy;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.client.OkClient;

/**
 * Created by sampaio on 26-03-2015.
 */
public class RestClient {

    private static String user;
    private static String pass;
    private static String ip;
    private static String csrf;

    public static String getUser() {
        return user;
    }

    public static void setUser(String user) {
        RestClient.user = user;
    }

    public static String getPass() {
        return pass;
    }

    public static void setPass(String pass) {
        RestClient.pass = pass;
    }

    public static String getIp() {
        return ip;
    }

    public static void setIp(String ip) {
        RestClient.ip = ip;
    }

    public static String getCsrf() {
        return csrf;
    }

    public static void setCsrf(String csrf) {
        RestClient.csrf = csrf;
    }

    private static Api api;

    public static Api getApi(){
        return api;
    }

    public static void clearApi() {
        RestClient.api = null;
    }

    /**
     * Injects cookies to every request
     */
    private static final RequestInterceptor COOKIES_REQUEST_INTERCEPTOR = new RequestInterceptor() {
        @Override
        public void intercept(RequestFacade request) {
//            if (null != cookies && cookies.length() > 0) {
//                //request.addHeader("Cookie", cookies);
//            }
        }
    };

    public static final Api getService(String url) {
        if(RestClient.getApi() == null){
            OkHttpClient client = new OkHttpClient(); //create OKHTTPClient
            CookieManager cookieManager = new CookieManager();
            cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
            client.setCookieHandler(cookieManager); //finally set the cookie handler on client
            OkClient serviceClient = new OkClient(client);


            RestClient.api = new RestAdapter.Builder()
                    .setEndpoint(url)
                    .setRequestInterceptor(COOKIES_REQUEST_INTERCEPTOR)
                    .setLogLevel(RestAdapter.LogLevel.HEADERS)
                    .setClient(serviceClient)
                    .build()
                    .create(Api.class);
        }
        return RestClient.getApi();
    }
}
