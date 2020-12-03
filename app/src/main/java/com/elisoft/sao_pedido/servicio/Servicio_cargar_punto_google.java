package com.elisoft.sao_pedido.servicio;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.elisoft.sao_pedido.Constants;
import com.elisoft.sao_pedido.Principal;
import com.elisoft.sao_pedido.R;
import com.elisoft.sao_pedido.Suceso;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;


public class Servicio_cargar_punto_google extends Service implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {
    /**
     * The Location listener.
     */
//    LocationListener locationListener;
    /**
     * The Location manager.
     */
//    LocationManager locationManager;
//    protected static final int REQUEST_CHECK_SETTINGS = 0x1;
    private GoogleApiClient googleApiClient = null;
    private LocationRequest locationRequest = null;
    //    private LocationSettingsRequest locationSettingsRequest;
    private Location currentLocation;

    private static String LOCATION = "location";


//----- ITEM DE SERVICIOS


    private static final String TAG = Servicio_cargar_punto_google.class.getSimpleName();
    private final static String FOREGROUND_CHANNEL_ID = "foreground_channel_id";
    private NotificationManager mNotificationManager;
    private Handler handler;
    private int count = 0;
    private static int stateService = Constants.STATE_SERVICE.NOT_CONNECTED;
    String fecha = "";
    Suceso suceso;
    double latitud_a;
    double longitud_a;
    int numero = 0;
    int rotacion = 0;
    double altura = 0;

    int id_star = 0;

    private RequestQueue queue = null;


    @Override
    public void onCreate() {
        /*configurar el cliente de google*/
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        /*conectar al servicio*/
        Log.w(TAG, "onCreate: Conectando el google client");
        googleApiClient.connect();


        //NOTIFICACION
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        stateService = Constants.STATE_SERVICE.NOT_CONNECTED;

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        /*remover las actualizaciones*/
        //LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
        if (queue != null) {
            queue.stop();
        }

        /*desconectar del servicio*/
        if (googleApiClient != null) {
            googleApiClient.disconnect();
        }


        //ESTADO DE LA NOTIFICACION
        stateService = Constants.STATE_SERVICE.NOT_CONNECTED;



    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) {
            stopForeground(true);
            stopSelf();
            return START_NOT_STICKY;
        }


        //    mp.start();
        super.onStart(intent, startId);
        id_star = startId;
        Log.e("Google", "Service Started cargarpunto.. " + startId);


        // if user starts the service
        switch (intent.getAction()) {
            case Constants.ACTION.START_ACTION:
                Log.d(TAG, "Received user starts foreground intent");
                startForeground(Constants.NOTIFICATION_ID_FOREGROUND_SERVICE, prepareNotification());

                break;
            case Constants.ACTION.STOP_ACTION:
                stopForeground(true);
                stopSelf();
                break;
            default:
                stopForeground(true);
                stopSelf();
        }


        //return START_NOT_STICKY;
        return START_REDELIVER_INTENT;
    }


    @SuppressLint("WrongConstant")
    private Notification prepareNotification() {
        // handle build version above android oreo
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O &&
                mNotificationManager.getNotificationChannel(FOREGROUND_CHANNEL_ID) == null) {
            CharSequence name = getString(R.string.text_name_notification);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(FOREGROUND_CHANNEL_ID, name, importance);
            channel.enableVibration(false);
            mNotificationManager.createNotificationChannel(channel);
        }

        Intent notificationIntent = new Intent(this, Principal.class);
        notificationIntent.setAction(Constants.ACTION.MAIN_ACTION);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        // if min sdk goes below honeycomb
        /*if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        } else {
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }*/

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        // make a stop intent
        Intent stopIntent = new Intent(this, Servicio_cargar_punto_google.class);
        stopIntent.setAction(Constants.ACTION.STOP_ACTION);
        PendingIntent pendingStopIntent = PendingIntent.getService(this, 0, stopIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.notification);
        //remoteViews.setOnClickPendingIntent(R.id.btn_stop, pendingStopIntent);
        remoteViews.setTextViewText(R.id.tv_fecha, fecha);

        // if it is connected
        switch (stateService) {
            case Constants.STATE_SERVICE.NOT_CONNECTED:
                remoteViews.setTextViewText(R.id.tv_estado, "Preparando la conexión. . .");
                remoteViews.setImageViewResource(R.id.im_estado, R.drawable.ic_advertencia);
                break;
            case Constants.STATE_SERVICE.CONNECTED:
                remoteViews.setTextViewText(R.id.tv_estado, "Movil en camino");
                remoteViews.setImageViewResource(R.id.im_estado, R.drawable.ic_ok);
                break;
            case Constants.STATE_SERVICE.GPS_INACTIVO:
                remoteViews.setTextViewText(R.id.tv_estado, "GPS inactivo");
                remoteViews.setImageViewResource(R.id.im_estado, R.drawable.ic_advertencia);
                break;
            case Constants.STATE_SERVICE.SIN_INTERNET:
                remoteViews.setTextViewText(R.id.tv_estado, "Sin conexión a internet . .");
                remoteViews.setImageViewResource(R.id.im_estado, R.drawable.ic_advertencia);
                break;
        }

        // notification builder
        NotificationCompat.Builder notificationBuilder;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            notificationBuilder = new NotificationCompat.Builder(this, FOREGROUND_CHANNEL_ID);
        } else {
            notificationBuilder = new NotificationCompat.Builder(this);
        }
        notificationBuilder
                .setContent(remoteViews)
                .setSmallIcon(R.drawable.ic_logo_carrito)
                .setCategory(NotificationCompat.CATEGORY_SERVICE)
                .setOnlyAlertOnce(true)
                .setOngoing(true)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            notificationBuilder.setVisibility(Notification.VISIBILITY_PUBLIC);
        }

        return notificationBuilder.build();
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.w(TAG, "onConnected: Conectado");
        /*conectar a la libreria de google*/

        currentLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);


        if (currentLocation != null) {
            Log.e("Google Api", currentLocation.getLatitude() + "," + currentLocation.getLongitude());
        }

        /*iniciar las peticiones*/
        locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(5000)
                .setFastestInterval(5000);

        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);


    }

    @Override
    public void onConnectionSuspended(int i) {
        if (i == CAUSE_SERVICE_DISCONNECTED) {
            Log.w(TAG, "onConnectionSuspended: Desconectado");
            ;
        } else if (i == CAUSE_NETWORK_LOST) {
            Log.w(TAG, "onConnectionSuspended: Sin Red");
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.w(TAG, "onConnectionFailed: Coneccion fallida: " + connectionResult.getErrorCode());
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.w(TAG, "onLocationChanged: " + location.toString());
        currentLocation = location;


        //OBTENER UBICACION


        if (isConnectingToInternet(getApplicationContext())) {
            //ENVIANDO UBICACION
            stateService = Constants.STATE_SERVICE.CONNECTED;
            startForeground(Constants.NOTIFICATION_ID_FOREGROUND_SERVICE, prepareNotification());


            try {
                float precision = location.getAccuracy();
                float bearing = location.getBearing();

                SharedPreferences prefe = getSharedPreferences("ultimo_pedido", Context.MODE_PRIVATE);
                String id_pedido =prefe.getString("id_pedido", "0");
                if ((id_pedido.equals("0")==false && id_pedido.equals("")==false)) {
                    if (precision < 100) {
                        servicio_enviar_ubicacion_punto_volley(prefe.getString("id_pedido", ""), "", location.getLatitude() + "", location.getLongitude() + "");
                    } else {

                    }
                } else {
                    LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
                    Log.e("servicio google", "eliminado.." + id_star);
                    onDestroy();
                }


            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();

            }
        } else {
            // SIN CONEXION A INTERNET
            stateService = Constants.STATE_SERVICE.SIN_INTERNET;
            startForeground(Constants.NOTIFICATION_ID_FOREGROUND_SERVICE, prepareNotification());
            //reconectando("Sin acceso a internet");
        }
    }




    public void servicio_enviar_ubicacion_punto_volley(String id_pedido,String id_usuario, String latitud, String longitud){
        try {


            JSONObject jsonParam= new JSONObject();
            jsonParam.put("id_pedido", id_pedido);
            jsonParam.put("id_usuario", id_usuario);
            jsonParam.put("latitud", latitud);
            jsonParam.put("longitud", longitud);
            String url=getString(R.string.servidor) + "frmUsuario.php?opcion=enviar_ubicacion_punto_pasajero";

            if (queue == null) {
                queue = Volley.newRequestQueue(this);
                Log.e("volley","Setting a new request queue");
            }


            JsonObjectRequest myRequest= new JsonObjectRequest(
                    Request.Method.POST,
                    url,
                    jsonParam,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject respuestaJSON) {

                            try {

                                suceso= new Suceso(respuestaJSON.getString("suceso"),respuestaJSON.getString("mensaje"));

                                if (suceso.getSuceso().equals("1")) {
                                    //OBTENER FECHA DE LA ULTIMA CONEXION
                                    Date date = new Date();
                                    DateFormat hourdateFormat = new SimpleDateFormat("HH:mm:ss dd 'de' MMMM");
                                    fecha=hourdateFormat.format(date);

                                    stateService = Constants.STATE_SERVICE.CONNECTED;
                                    startForeground(Constants.NOTIFICATION_ID_FOREGROUND_SERVICE, prepareNotification());
                                }else if(suceso.getSuceso().equals("2"))
                                {

                                }
                                else
                                {

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();

                            }

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            }
            ){
                public Map<String,String> getHeaders() throws AuthFailureError {
                    Map<String,String> parametros= new HashMap<>();
                    parametros.put("content-type","application/json; charset=utf-8");
                    parametros.put("Authorization","apikey 849442df8f0536d66de700a73ebca-us17");
                    parametros.put("Accept", "application/json");

                    return  parametros;
                }
            };


            // TIEMPO DE ESPERA
            myRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            queue.add(myRequest);


        } catch (Exception e) {
        }
    }




    public static boolean isConnectingToInternet(Context _context) {
        ConnectivityManager connectivity = (ConnectivityManager) _context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }

        }
        return false;
    }


}