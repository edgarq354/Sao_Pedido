package com.elisoft.sao_pedido.loquesea;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;

import com.elisoft.sao_pedido.AndroidPermissions;
import com.elisoft.sao_pedido.Constants;
import com.elisoft.sao_pedido.GeocodeAddressIntentService;
import com.elisoft.sao_pedido.NewGPSTracker;
import com.elisoft.sao_pedido.Suceso;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.os.Handler;
import android.os.ResultReceiver;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.elisoft.sao_pedido.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

public class Marcar_direccion_lugar extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener, GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks,
        LocationListener {

    boolean sw_ver_taxi_cerca = false;
    private JSONArray puntos_taxi;
    JSONObject rutas=null;

    EditText et_direccion;
    TextView tv_direccion;


    private static final String LOGTAG = "android-localizacion";

    private static final int PETICION_PERMISO_LOCALIZACION = 101;
    private static final int PETICION_CONFIG_UBICACION = 201;
    private Context mContext;
    private GoogleApiClient apiClient;

    private LocationRequest locRequest;

    LatLng myPosition;
    String s_direccion="";
    Suceso suceso;



    private  AddressResultReceiver mResultReceiver;
    int fetchType = Constants.USE_ADDRESS_LOCATION;
    private LatLng addressLatLng;
    private NewGPSTracker gpsTracker;


    int limpiar_mapa=0;

    Switch sw_mapa;
    private GoogleMap mMap;


    int sw_acercar_a_mi_ubicacion;

    double latitud_buscador=0,longitud_buscador=0;

    boolean sw_obteniendo_direccion;

    LocationManager manager = null;





    //  inicio de modificacion de pedir taxi
    protected GoogleApiClient mGoogleApiClient;

    private static final LatLngBounds BOUNDS_INDIA = new LatLngBounds(new LatLng(-24,-68), new LatLng(-8,-58));


    //fin de buscador de ubicacion...




    LinearLayout ll_agregar;




    double latitud = 0, longitud = 0;
    String direccion="";
    /*
     * VARIABLE DIRECCION
     * 0:ninguno
     * 1:casa
     * 2:trabajo
     * */



    @Override
    protected void onStart() {
        boolean sw=estaConectado();
        super.onStart();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_marcar_direccion_lugar);

        ll_agregar=(LinearLayout)findViewById(R.id.ll_agregar);
        et_direccion=findViewById(R.id.et_direccion);
        tv_direccion=findViewById(R.id.tv_direccion);
        sw_mapa=(Switch)findViewById(R.id.sw_mapa);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        sw_mapa.setOnClickListener(this);
        ll_agregar.setOnClickListener(this);
        tv_direccion.setOnClickListener(this);


        sw_acercar_a_mi_ubicacion = 0;



        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


// localizacion automatica
        //Construcción cliente API Google
        apiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .build();

        addressLatLng=new LatLng(0,0);
        enableLocationUpdates();
        gpsTracker = new NewGPSTracker(getApplicationContext());
        //fin de la locatizaocion automatica...

        try{
            SharedPreferences carrito = getSharedPreferences(getString(R.string.sql_loquesea), MODE_PRIVATE);
            latitud_buscador= Double.parseDouble(carrito.getString("latitud_recoger","0"));
            longitud_buscador= Double.parseDouble(carrito.getString("longitud_recoger","0"));
            et_direccion.setText(carrito.getString("direccion_recoger",""));
            if(latitud_buscador!=0 && longitud_buscador!=0){
                sw_acercar_a_mi_ubicacion=2;
            }
        }catch (Exception e)
        {
            sw_acercar_a_mi_ubicacion=0;
        }

        sw_obteniendo_direccion=false;



        buildGoogleApiClient();



        //fin de implementacion de buscador de ubicaicon.



        getSupportActionBar().setDisplayHomeAsUpEnabled(true);






        buildGoogleApiClient();





        //fin de implementacion de buscador de ubicaicon.





    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }













    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Controles UI
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            mMap.setMyLocationEnabled(true);
            View mapView = (View) getSupportFragmentManager().findFragmentById(R.id.map).getView();
//bicacion del button de Myubicacion de el fragento..
            View btnMyLocation = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));

            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
            params.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
            params.setMargins(20, 0, 0, 0);
            btnMyLocation.setLayoutParams(params);

            init();


        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Mostrar diálogo explicativo
            } else {
                // Solicitar permiso
                ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        1);
            }

        }
    }


    private void enableLocationUpdates() {

        locRequest = new LocationRequest();
        locRequest.setInterval(1000);
        locRequest.setFastestInterval(100);
        locRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest locSettingsRequest =
                new LocationSettingsRequest.Builder()
                        .addLocationRequest(locRequest)
                        .build();

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(
                        apiClient, locSettingsRequest);

        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult locationSettingsResult) {
                final Status status = locationSettingsResult.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:

                        Log.i(LOGTAG, "Configuración correcta");
                        startLocationUpdates();

                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            Log.i(LOGTAG, "Se requiere actuación del usuario");
                            status.startResolutionForResult(Marcar_direccion_lugar.this, PETICION_CONFIG_UBICACION);
                        } catch (IntentSender.SendIntentException e) {
                            Log.i(LOGTAG, "Error al intentar solucionar configuración de ubicación");
                        }

                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Log.i(LOGTAG, "No se puede cumplir la configuración de ubicación necesaria");

                        break;
                }
            }
        });
    }


    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(Marcar_direccion_lugar.this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            //Ojo: estamos suponiendo que ya tenemos concedido el permiso.
            //Sería recomendable implementar la posible petición en caso de no tenerlo.

            Log.i(LOGTAG, "Inicio de recepción de ubicaciones");

            LocationServices.FusedLocationApi.requestLocationUpdates(
                    apiClient, locRequest, Marcar_direccion_lugar.this);
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        //Se ha producido un error que no se puede resolver automáticamente
        //y la conexión con los Google Play Services no se ha establecido.

        Log.e(LOGTAG, "Error grave al conectar con Google Play Services");
        Log.v("Google API Callback","Connection Failed");
        Log.v("Error Code", String.valueOf(result.getErrorCode()));
        Toast.makeText(this, Constants.API_NOT_CONNECTED, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        //Conectado correctamente a Google Play Services

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PETICION_PERMISO_LOCALIZACION);
        } else {

            Location lastLocation =
                    LocationServices.FusedLocationApi.getLastLocation(apiClient);

            updateUI(lastLocation);
        }

        Log.v("Google API Callback", "Connection Done");
    }

    @Override
    public void onConnectionSuspended(int i) {
        //Se ha interrumpido la conexión con Google Play Services

        Log.e(LOGTAG, "Se ha interrumpido la conexión con Google Play Services");
        Log.v("Google API Callback", "Connection Suspended");
        Log.v("Code", String.valueOf(i));
    }

    private void updateUI(Location loc) {
        if (loc != null) {

            double lat = loc.getLatitude();
            double lon = loc.getLongitude();
            myPosition = new LatLng(lat, lon);

            // cargamos los puntos de las taxis en nuestro mapa....


            //////////////////get in AsyncTask//////////////////////
            getAddressIntentService(lat, lon);
            ////////////////

            if (sw_acercar_a_mi_ubicacion == 0) {
                //mover la camara del mapa a mi ubicacion.,,
                try {
                    //agregaranimacion al mover la camara...
                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(new LatLng(loc.getLatitude(), loc.getLongitude()))      // Sets the center of the map to Mountain View
                            .zoom(16)                   // Sets the zoom
                            .bearing(0)                // Sets the orientation of the camera to east
                            .tilt(0)                   // Sets the tilt of the camera to 30 degrees
                            .build();
                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                    sw_acercar_a_mi_ubicacion = 1;
                } catch (Exception e) {
                    sw_acercar_a_mi_ubicacion = 0;
                }

            }
            else if(sw_acercar_a_mi_ubicacion==2)
            {
                //mover la camara del mapa a mi ubicacion.,,
                try {
                    //agregaranimacion al mover la camara...
                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(new LatLng(latitud_buscador, longitud_buscador))      // Sets the center of the map to Mountain View
                            .zoom(16)                   // Sets the zoom
                            .bearing(0)                // Sets the orientation of the camera to east
                            .tilt(0)                   // Sets the tilt of the camera to 30 degrees
                            .build();
                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));



                    sw_acercar_a_mi_ubicacion = 1;
                } catch (Exception e) {
                    sw_acercar_a_mi_ubicacion = 0;
                }
            }

            if(direccion.length()==4 && sw_acercar_a_mi_ubicacion!=3)
            {
                SharedPreferences s_p=getSharedPreferences("buscar_direccion",MODE_PRIVATE);
                //MOVER LA UBICACION DEL PUNTERO A LA UBICACION BUSCADA
                double lat1= Double.parseDouble(s_p.getString("latitud","0"));
                double lon1= Double.parseDouble(s_p.getString("longitud","0"));
                try {
                    //agregaranimacion al mover la camara...
                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(new LatLng(lat1, lon1))      // Sets the center of the map to Mountain View
                            .zoom(16)                   // Sets the zoom
                            .bearing(0)                // Sets the orientation of the camera to east
                            .tilt(0)                   // Sets the tilt of the camera to 30 degrees
                            .build();
                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                    sw_acercar_a_mi_ubicacion = 3;
                } catch (Exception e) {
                    sw_acercar_a_mi_ubicacion = 3;
                }
            }


            //agregaranimacion al mover la camara...

        } else {

            Log.e("Latitud","(desconocida)");
            Log.e("Longitud","(desconocida)");
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PETICION_PERMISO_LOCALIZACION) {
            if (grantResults.length == 1
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                //Permiso concedido

                @SuppressWarnings("MissingPermission")
                Location lastLocation =
                        LocationServices.FusedLocationApi.getLastLocation(apiClient);

                updateUI(lastLocation);

            } else {
                //Permiso denegado:
                //Deberíamos deshabilitar toda la funcionalidad relativa a la localización.

                Log.e(LOGTAG, "Permiso denegado");
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case PETICION_CONFIG_UBICACION:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        startLocationUpdates();
                        break;
                    case Activity.RESULT_CANCELED:
                        Log.i(LOGTAG, "El usuario no ha realizado los cambios de configuración necesarios");

                        break;
                }
                break;
        }
    }

    @Override
    public void onLocationChanged(Location location) {

        Log.i(LOGTAG, "Recibida nueva ubicación!");
        //Mostramos la nueva ubicación recibida

        updateUI(location);
    }
    //FIN DE SERVICIO DE COORDENADAS..

    public void mensaje_error(String mensaje)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.app_name));
        builder.setMessage(mensaje);
        builder.setPositiveButton("OK", null);
        builder.create();
        builder.show();
    }



    private void getAddressIntentService(double lat, double lng) {
        Intent intent = new Intent(this, GeocodeAddressIntentService.class);
        intent.putExtra(Constants.RECEIVER, mResultReceiver);
        intent.putExtra(Constants.FETCH_TYPE_EXTRA, fetchType);
        intent.putExtra(Constants.LOCATION_LATITUDE_DATA_EXTRA,lat);
        intent.putExtra(Constants.LOCATION_LONGITUDE_DATA_EXTRA,lng);
        Log.e("TaxiCorp", "Starting Service");
        startService(intent);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case  R.id.sw_mapa:

                boolean b=sw_mapa.isChecked();

                if(b==true) {
                    mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                }
                else
                {
                    mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                }
                break;

            case R.id.ll_agregar:
                String direccion_texto=et_direccion.getText().toString().trim();
                if(direccion_texto.length()==0){
                    et_direccion.setText(s_direccion.trim());
                }

                SharedPreferences carrito = getSharedPreferences(getString(R.string.sql_loquesea), MODE_PRIVATE);
                SharedPreferences.Editor ed_c = carrito.edit();
                ed_c.putString("latitud_recoger", String.valueOf(addressLatLng.latitude));
                ed_c.putString("longitud_recoger", String.valueOf(addressLatLng.longitude));
                ed_c.putString("direccion_recoger", et_direccion.getText().toString().trim() );
                ed_c.commit();
                //guardar nueva direccion en Datos de direccion..
                finish();

                break;
            case R.id.tv_direccion:
                et_direccion.setText(s_direccion);
                break;

        }

    }








    //clase de AddresResulReciever para obtener los datos de Adresss,, latitud y longitud
    class AddressResultReceiver extends ResultReceiver {
        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, final Bundle resultData) {
            if (resultCode == Constants.SUCCESS_RESULT) {
                final Address address = resultData.getParcelable(Constants.RESULT_ADDRESS);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("setUpMap",resultData.getString(Constants.RESULT_DATA_KEY));
                    }
                });
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("setUpMap",resultData.getString(Constants.RESULT_DATA_KEY));
                    }
                });
            }
        }
    }
    private void init() {

        mResultReceiver = new  AddressResultReceiver(null);
        if (!isGooglePlayServicesAvailable()) {
            finish();
        }


        if (Build.VERSION.SDK_INT >= 23) {
            if (!AndroidPermissions.getInstance().checkLocationPermission(this)) {
                AndroidPermissions.getInstance().displayLocationPermissionAlert(this);
            } else {
                setUpMap();
            }
        } else {
            setUpMap();
        }

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        SharedPreferences s_p=getSharedPreferences("buscar_direccion",MODE_PRIVATE);
        //MOVER LA UBICACION DEL PUNTERO A LA UBICACION BUSCADA
        double lat= Double.parseDouble(s_p.getString("latitud","0"));
        double lon= Double.parseDouble(s_p.getString("longitud","0"));

        if(lat==0|| lon ==0)
        {
            lat=myPosition.latitude;
            lon=myPosition.longitude;
        }
        //agregaranimacion al mover la camara...
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(lat,lon))      // Sets the center of the map to Mountain View
                .zoom(16)                   // Sets the zoom
                .bearing(0)                // Sets the orientation of the camera to east
                .tilt(0)                   // Sets the tilt of the camera to 30 degrees
                .build();
        try {
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }catch (Exception e)
        {
            Log.e("move camara",e.toString());
        }


    }

    private boolean isGooglePlayServicesAvailable() {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (ConnectionResult.SUCCESS == status) {
            return true;
        } else {
            GooglePlayServicesUtil.getErrorDialog(status, this, 0).show();
            return false;
        }
    }

    private void setUpMap() {

        if (gpsTracker.checkLocationState()) {
            gpsTracker.startLocationUpdates();
            LatLng latLang = new LatLng(gpsTracker.getLatitude(), gpsTracker.getLongitude());
            //First time if you don't have latitude and longitude user default address
            if (gpsTracker.getLatitude() == 0) {

            }





        }else {
//ir a configuracion de gps
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            alertDialog.setTitle(R.string.GPSAlertDialogTitle);
            alertDialog.setMessage(R.string.GPSAlertDialogMessage);
            alertDialog.setPositiveButton(R.string.action_settings, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                }
            });
            alertDialog.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            alertDialog.show();
            //gpsTracker.showSettingsAlert();
        }

        mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {

                if (cameraPosition != null) {
                    //  mMap.clear();
                    Log.e("centerLat", String.valueOf(cameraPosition.target.latitude));
                    Log.e("centerLong", String.valueOf(cameraPosition.target.longitude));
                    //muestra el nombre de la direccion en la que se encuenta la Latirud y longitud. en el TextView...
                    // mostrar_adrres(direccion.latitud, direccion.longitud);

                    //mueve a mi ubicacion

                    addressLatLng = new LatLng(cameraPosition.target.latitude, cameraPosition.target.longitude);
                    final String[] direccion = {""};
                    try {
                        if(sw_obteniendo_direccion==false) {
                            new Thread(new Runnable() {
                                public void run() {
                                    sw_obteniendo_direccion=true;
                                    direccion[0] =obtener_direccion( addressLatLng.latitude, addressLatLng.longitude);

                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            sw_obteniendo_direccion=false;
                                            try {
                                                s_direccion= String.valueOf(direccion[0]);
                                                tv_direccion.setText(s_direccion.trim());
                                            }catch (Exception e)
                                            {
                                                Log.e("addres:",e.toString());
                                            }

                                        }
                                    });
                                }
                            }).start();
                        }
                    }catch (Exception e)
                    {

                    }
                    //////////////////get in AsyncTask//////////////////////
                    getAddressIntentService(cameraPosition.target.latitude, cameraPosition.target.longitude);

                } else {
                    getAddressIntentService(cameraPosition.target.latitude, cameraPosition.target.longitude);

                }
                //////////////////get in AsyncTask//////////////////////
            }
        });
    }





    public String obtener_direccion(double lat, double lon) {
        Geocoder geocoder = new Geocoder(getBaseContext());
        List<Address> addresses = null;
        String s_direccion= "";

        try {
            addresses = geocoder.getFromLocation(lat, lon, 1);
        } catch (IOException ioException) {
            // Catch network or other I/O problems.

        } catch (IllegalArgumentException illegalArgumentException) {

        }

        // Handle case where no address was found.
        if (addresses == null || addresses.size() == 0) {
            //error. o no tiene datos recolectados...
        } else {

// Funcion que determina si se obtuvo resultado o no

            // Creamos el objeto address
            Address address = addresses.get(0);


            if (addresses.size() > 0) {
                int cantidad=addresses.get(0).getMaxAddressLineIndex();
                for (int i = 0; i <= cantidad; i++)
                {
                    s_direccion+= addresses.get(0).getAddressLine(i) + ",";
                }
            }


            // Creamos el string a partir del elemento direccion
            String direccionText = String.format("%s, %s",
                    address.getMaxAddressLineIndex() > 0 ? address.getAddressLine(0) : "",
                    address.getFeatureName());


            //  et_direccion.setText(address.getFeatureName()+" | "+address.getSubAdminArea ()+" | "+address.getSubLocality ()+" | "+address.getLocality ()+" | "+address.getSubLocality ()+" | "+address.getPremises ()+" | "+addresses.get(0).getThoroughfare()+" | "+address.getAddressLine(0));
        }
        return s_direccion;
    }




    //VERIFICAR SI ESTA CON CONEXION WIFI
    protected Boolean conectadoWifi(){
        ConnectivityManager connectivity = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo info = connectivity.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (info != null) {
                return info.isConnected();
            }
        }
        return false;
    }
    //VERIFICAR SI ESTA CON CONEXION DE DATOS
    protected Boolean conectadoRedMovil(){
        ConnectivityManager connectivity = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo info = connectivity.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if (info != null) {
                return info.isConnected();
            }
        }
        return false;
    }

    protected Boolean estaConectado(){
        if(conectadoWifi()){
            return true;
        }else{
            if(conectadoRedMovil()){
                return true;
            }else{
                mensaje_error("Tu Dispositivo no tiene Conexion a Internet.");
                return false;
            }
        }
    }


    //principio de buscar direcciones..
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .build();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!mGoogleApiClient.isConnected() && !mGoogleApiClient.isConnecting()){
            Log.v("Google API","Connecting");
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if(mGoogleApiClient.isConnected()){
            Log.v("Google API","Dis-Connecting");
            mGoogleApiClient.disconnect();
        }
    }
    //fin de buscador de direcciones..












}

