package com.elisoft.sao_pedido.loquesea;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.elisoft.sao_pedido.Cancelar_pedido_usuario;
import com.elisoft.sao_pedido.Constants;
import com.elisoft.sao_pedido.Principal_pedido;
import com.elisoft.sao_pedido.Servicio_pedido;
import com.elisoft.sao_pedido.Servicio_pedir_movil;
import com.elisoft.sao_pedido.Suceso;
import com.elisoft.sao_pedido.chat.Servicio_chat;
import com.elisoft.sao_pedido.notificaciones.SharedPrefManager;
import com.elisoft.sao_pedido.servicio.Servicio_cargar_punto_google;
import com.google.android.gms.common.ConnectionResult;
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
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.elisoft.sao_pedido.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Buscar_repartidor extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks,
        LocationListener {

    double monto_envio=0;

    private BroadcastReceiver mReceiver;
    boolean sw_ver_taxi_cerca = false;
    private JSONArray puntos_taxi;
    int sw_acercar_a_mi_ubicacion=0;
    //Marker conductor punto


    private IntentFilter mIntentFilter;
    private static final String LOGTAG = "android-localizacion";

    private static final int PETICION_PERMISO_LOCALIZACION = 101;
    private static final int PETICION_CONFIG_UBICACION = 201;



    private GoogleApiClient apiClient;

    private LocationRequest locRequest;

    AlertDialog alert2 = null;





    private GoogleMap mMap;
    ProgressDialog pDialog;
    String sid_pedido;
    Suceso suceso;
    Marker m_conductor=null;
    Marker m_pasajero=null;

    int m_conductor_cantida=0;

    LatLng myPosition=new LatLng(0,0);
    LatLng ultima_ubicacion = new LatLng(0, 0);
    boolean sw=true;
    double latitud = 0, longitud = 0;
    double latitud_recoger = 0, longitud_recoger = 0;
    String referencia="",direccion="",direccion_recoger="",que_necesitas="";
    int numero_casa=0,clase_vehiculo=1,tipo_pedido_empresa=0,clase_vehiculo_en_pedido=1;

    boolean  sw_cancelar_pedido_durante_el_pedido=false;


    TextView tv_tiempo,tv_mensaje_pedido;
    Button bt_cancelar_pedido;

    boolean sw_destroy=false;



    Marker marker_1=null;
    Marker marker_2=null;
    Marker marker_3=null;
    Marker marker_4=null;
    Marker marker_5=null;
    Marker marker_6=null;
    Marker marker_7=null;
    Marker marker_8=null;
    Marker marker_9=null;
    Marker marker_10=null;
    Marker marker_11=null;
    Marker marker_12=null;
    Marker marker_13=null;
    Marker marker_14=null;
    Marker marker_15=null;
    Marker marker_16=null;
    Marker marker_17=null;
    Marker marker_18=null;
    Marker marker_19=null;
    Marker marker_20=null;
    Marker marker_21=null;
    Marker marker_22=null;
    Marker marker_23=null;
    Marker marker_24=null;
    Marker marker_25=null;

    String cond_1="";
    String cond_2="";
    String cond_3="";
    String cond_4="";
    String cond_5="";
    String cond_6="";
    String cond_7="";
    String cond_8="";
    String cond_9="";
    String cond_10="";
    String cond_11="";
    String cond_12="";
    String cond_13="";
    String cond_14="";
    String cond_15="";
    String cond_16="";
    String cond_17="";
    String cond_18="";
    String cond_19="";
    String cond_20="";
    String cond_21="";
    String cond_22="";
    String cond_23="";
    String cond_24="";
    String cond_25="";



    String fecha_1="";
    String fecha_2="";
    String fecha_3="";
    String fecha_4="";
    String fecha_5="";
    String fecha_6="";
    String fecha_7="";
    String fecha_8="";
    String fecha_9="";
    String fecha_10="";
    String fecha_11="";
    String fecha_12="";
    String fecha_13="";
    String fecha_14="";
    String fecha_15="";
    String fecha_16="";
    String fecha_17="";
    String fecha_18="";
    String fecha_19="";
    String fecha_20="";
    String fecha_21="";
    String fecha_22="";
    String fecha_23="";
    String fecha_24="";
    String fecha_25="";

    String fecha_ultimo="";

    Handler handle=new Handler();
    int interseccion=0;


    Date fecha_conexion;




    RequestQueue queue=null;

    @Override
    public void onBackPressed() {


        SharedPreferences pedido=getSharedPreferences("ultimo_pedido",MODE_PRIVATE);
        SharedPreferences pedido_proceso = getSharedPreferences("pedido_en_proceso", MODE_PRIVATE);
        int id_pedido=0;

        try{
            id_pedido=Integer.parseInt(pedido.getString("id_pedido","0"));
        }catch (Exception e){
            try{
                id_pedido=Integer.parseInt(pedido_proceso.getString("id_pedido","0"));
            }catch (Exception ee){
                id_pedido=0;
            }
        }
        if(id_pedido==0 ){

            super.onBackPressed();
        }
        else  if( pedido.getString("estado","").equals("2")==true||pedido.getString("estado","").equals("3")==true)
        {
            super.onBackPressed();
        } else
        {
            Toast.makeText(this,"Tiene un pedido en Proceso.", Toast.LENGTH_SHORT).show();
        }

        //

    }

    @Override
    protected void onDestroy() {
        sw_destroy=true;
        Log.e("Destroy","1");
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        //apiClient.connect();
        sw_destroy=false;


        if(estaConectado())
        {
            enableLocationUpdates();



//SERVICIO PARA OBTENER LOS DATOS DEL SERVICIO
            SharedPreferences prefe = getSharedPreferences("ultimo_pedido", Context.MODE_PRIVATE);
            try {
                int id_pedido= Integer.parseInt(prefe.getString("id_pedido",""));
                servicio_get_pedido_volley(id_pedido);
            }catch (Exception e) {
                e.printStackTrace();
            }

        }

        super.onStart();
    }

    @Override
    protected void onStop() {
        Log.e("Stop","1");
        if( apiClient != null && apiClient.isConnected() ) {
            //  apiClient.disconnect();
        }

        super.onStop();
    }

    @Override
    protected void onPause() {
        unregisterReceiver(mReceiver);
        Log.e("Pause","1");
        super.onPause();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buscar_repartidor);
        //fin layout flotante
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);


        mIntentFilter = new IntentFilter(Servicio_pedir_movil.ACTION_PROGRESO);

        //inicio layout flotante....
        bt_cancelar_pedido= findViewById(R.id.bt_cancelar_pedido);
        tv_tiempo= findViewById(R.id.tv_tiempo);
        tv_mensaje_pedido= findViewById(R.id.tv_mensaje_pedido);

        m_conductor_cantida=0;

        bt_cancelar_pedido.setOnClickListener(this);


        // getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
                .addOnConnectionFailedListener(this)
                .build();
        enableLocationUpdates();
        //fin de la locatizaocion automatica...




        try {
            Bundle bundle = getIntent().getExtras();
            latitud = bundle.getDouble("latitud", 0);
            longitud = bundle.getDouble("longitud", 0);
            referencia=bundle.getString("referencia","");
            direccion=bundle.getString("direccion","");

            latitud_recoger = bundle.getDouble("latitud_recoger", 0);
            longitud_recoger = bundle.getDouble("longitud_recoger", 0);
            direccion_recoger =bundle.getString("direccion_recoger","");
            que_necesitas =bundle.getString("que_necesitas","");

            sid_pedido= bundle.getString("id_pedido","0");
            clase_vehiculo=bundle.getInt("clase_vehiculo",1);
            tipo_pedido_empresa=bundle.getInt("tipo_pedido_empresa",0);
            monto_envio=bundle.getDouble("monto_envio",0);



            ultima_ubicacion=new LatLng(latitud,longitud);

            if(sid_pedido.equals("")==false && sid_pedido.equals("0")==false)
            {
                SharedPreferences prefe = getSharedPreferences("ultimo_pedido", Context.MODE_PRIVATE);

                try {
                    int id_pedido= Integer.parseInt(prefe.getString("id_pedido",""));
                    servicio_get_pedido_volley(id_pedido);
                }catch (Exception e) {
                    e.printStackTrace();
                }

                flotante_pedir(false);
            }
            else {
                if (latitud == 0 || longitud == 0) {
                    // mensaje_error_final("Por favor geolocalice su ubicación.");
                }
                else {

                    limpiar_pedido_antiguo();
                    pedir_taxi();
                    flotante_pedir(true);
                }
            }

        } catch (Exception e) {
            finish();
        }


        SharedPreferences pedido = getSharedPreferences("ultimo_pedido", MODE_PRIVATE);
        if (pedido.getString("id_pedido", "").equals("") == true) {

        }
        else
        {
            flotante_pedir(false);
            sid_pedido=pedido.getString("id_pedido", "");
        }

        fecha_conexion = new Date();


        //RECEIVER DATO   ----  INICIO
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int estado_pedido=0;
                int estado_finalizo=0;
                int id_pedido=0;
                int tiempo=0;

                if (intent.getAction().equals(Servicio_pedir_movil.ACTION_PROGRESO)) {
                    estado_pedido= intent.getIntExtra("estado",0);
                    id_pedido= intent.getIntExtra("id_pedido",0);
                    tiempo= intent.getIntExtra("tiempo",0);
                    tiempo=60-tiempo;
                    tv_tiempo.setText(""+tiempo);
                } else if (intent.getAction().equals(Servicio_pedir_movil.ACTION_FINAL)) {
                    estado_finalizo= intent.getIntExtra("estado",0);
                    Intent stopIntent = new Intent(Buscar_repartidor.this, Servicio_pedir_movil.class);
                    stopService(stopIntent);
                }
                if(estado_pedido==1 || estado_finalizo==1){
                    verificar_pedido();
                }
                // tv_titulo.setText("e.p:"+estado_pedido);
                // tv_titulo.setText("e.f:"+estado_finalizo);
            }
        };
        registerReceiver(mReceiver, mIntentFilter);
        //RECEIVER DATO   ----  FINAL
    }



    private void flotante_pedir(boolean b) {

        if(b==false) {
            eliminar_carrito();
            SharedPreferences prefe = getSharedPreferences("ultimo_pedido", Context.MODE_PRIVATE);

            Intent intent = new Intent(Buscar_repartidor.this, Servicio_pedido.class);
            intent.setAction(Constants.ACTION_RUN_ISERVICE);
            startService(intent);

            Intent intent1 = new Intent(Buscar_repartidor.this, Servicio_chat.class);
            intent1.setAction(Constants.ACTION_RUN_ISERVICE);
            startService(intent1);

            Intent principal=new Intent(this, Principal_pedido.class);
            principal.putExtra("id_pedido",prefe.getString("id_pedido",""));
            startActivity(principal);
            finish();

        }

    }

    private void eliminar_carrito() {
        SharedPreferences carrito = getSharedPreferences(getString(R.string.sql_loquesea), MODE_PRIVATE);
        SharedPreferences.Editor ed_c = carrito.edit();
        ed_c.putInt("id_empresa", 0);
        ed_c.putInt("id_lugar", 0);
        ed_c.putInt("id_categoria", 0);
        ed_c.putString("nombre_categoria", "");
        ed_c.putString("monto_total","0");
        ed_c.commit();



    }


    @Override
    public void onMapReady(final GoogleMap googleMap) {

        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.style_mapa));

            if (!success) {
                Log.e("style", "Style parsing failed.");
            }
            googleMap.clear();
        } catch (Resources.NotFoundException e) {
            Log.e("style", "Can't find style. Error: ", e);
        }

        mMap = googleMap;


        // Controles UI
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);


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

        if(sw==true && ultima_ubicacion.latitude!=0 && ultima_ubicacion.longitude!=0)
        {
            //agregaranimacion al mover la camara...
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(ultima_ubicacion)      // Sets the center of the map to Mountain View
                    .zoom(18)                   // Sets the zoom
                    .bearing(0)                // Sets the orientation of the camera to east
                    .tilt(0)                   // Sets the tilt of the camera to 30 degrees
                    .build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
        else
        { try {

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(latitud, longitud))      // Sets the center of the map to Mountain View
                    .zoom(18)                   // Sets the zoom
                    .bearing(0)                // Sets the orientation of the camera to east
                    .tilt(0)                   // Sets the tilt of the camera to 30 degrees
                    .build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }catch (Exception e)
        {}
        }




        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                ver_moviles2();
                fecha_conexion = new Date();
            }
        });




        SharedPreferences prefe = getSharedPreferences("ultimo_pedido", Context.MODE_PRIVATE);
        try {
            int id_pedido= Integer.parseInt(prefe.getString("id_pedido",""));
            servicio_get_pedido_volley(id_pedido);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onClick(View v) {


        if(v.getId()==R.id.bt_cancelar_pedido)
        {//cancela el pedido durante la busqueda de un pedido.
            sw_cancelar_pedido_durante_el_pedido=true;

            SharedPreferences pedido=getSharedPreferences("ultimo_pedido",MODE_PRIVATE);
            SharedPreferences pedido_proceso=getSharedPreferences("pedido_en_proceso",MODE_PRIVATE);
            SharedPreferences usuario = getSharedPreferences("perfil", MODE_PRIVATE);
            String id = usuario.getString("id_usuario", "");
            //dibuja en el mapa las taxi que estan cerca...
            //hilo_taxi.execute(getString(R.string.servidor)+"frmTaxi.php?opcion=get_taxis_en_rango", "1","64.455","-18.533");// parametro que recibe el doinbackground
            int id_pedido=0;
            try {
                id_pedido=Integer.parseInt(pedido.getString("id_pedido","0"));
            }catch (Exception e){
                try {
                    id_pedido=Integer.parseInt(pedido_proceso.getString("id_pedido","0"));
                }catch (Exception ee){
                    id_pedido=0;
                }
            }
            if(id_pedido!=0) {
                try {
                    tv_mensaje_pedido.setText("Cancelando su solicitud..");
                    //hilo_taxi_cancelar = new Servicio_pedir_cancelar();
                    // hilo_taxi_cancelar.execute(getString(R.string.servidor) + "frmPedido.php?opcion=cancelar_pedido_usuario", "1", id,String.valueOf(id_pedido));// parametro que recibe el doinbackground
                    servicio_cancelar_pedido_sin_aceptar(id,String.valueOf(id_pedido));
                } catch (Exception e) {

                }
            }else
            {
                finish();
            }

        }
    }

    private void servicio_cancelar_pedido_sin_aceptar(String id_usuario, String id_pedido) {


        try {
            String token= SharedPrefManager.getInstance(this).getDeviceToken();

            JSONObject jsonParam= new JSONObject();
            jsonParam.put("id_usuario",id_usuario);
            jsonParam.put("id_pedido", id_pedido);
            jsonParam.put("token", token);
            String url=getString(R.string.servidor) + "frmPedido.php?opcion=cancelar_pedido_usuario_sin_aceptar";
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

                                    SharedPreferences pedido = getSharedPreferences("pedido_en_proceso", MODE_PRIVATE);
                                    SharedPreferences.Editor editor = pedido.edit();
                                    editor.putString("id_pedido", "");
                                    editor.commit();


                                    ///////////////////////////////////////

                                    eliminar_servicio_cargar_ubicacion();

                                    SharedPreferences pedido2 = getSharedPreferences("ultimo_pedido", MODE_PRIVATE);
                                    String id_pedido=pedido2.getString("id_pedido","");

                                    SharedPreferences.Editor editor2 = pedido2.edit();
                                    editor2.putString("id_pedido", "");
                                    editor2.putString("estado", "4");
                                    editor2.commit();
                                    Intent cancelar_pedido=new Intent(getApplicationContext(), Cancelar_pedido_usuario.class);
                                    cancelar_pedido.putExtra("id_pedido",id_pedido);
                                    startActivity(cancelar_pedido);
                                    finish();

                                }else if(suceso.getSuceso().equals("2"))
                                {

                                    mensaje(suceso.getMensaje());
                                }
                                else
                                {
                                    mensaje_error_final("Error: Al conectar con el Servidor.\nVerifique su acceso a Internet.");
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                mensaje_error_final("Falla en tu conexión a Internet.");
                            }

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    mensaje_error_final("Falla en tu conexión a Internet.");
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
            myRequest.setRetryPolicy(new DefaultRetryPolicy(6000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            queue.add(myRequest);


        } catch (Exception e) {

        }

    }






    public void pedir_taxi() {
        //no tiene pedidos
        SharedPreferences pedido2 = getSharedPreferences("ultimo_pedido", MODE_PRIVATE);
        SharedPreferences.Editor editor2 = pedido2.edit();
        editor2.putString("abordo", "");
        editor2.commit();
        //no tiene pedidos

        SharedPreferences usuario = getSharedPreferences("perfil", MODE_PRIVATE);
        String id = usuario.getString("id_usuario", "");
        String imei = usuario.getString("imei", "");
        String nombre = usuario.getString("nombre", "");
        nombre = nombre + " " + usuario.getString("apellido", "");
        //dibuja en el mapa las taxi que estan cerca...
        //hilo_taxi.execute(getString(R.string.servidor)+"frmTaxi.php?opcion=get_taxis_en_rango", "1","64.455","-18.533");// parametro que recibe el doinbackground
        try {

            tv_mensaje_pedido.setText("Enviando su solicitud a los Taxistas...");
           /*
            hilo_pedir_taxi=new Servicio_pedir_taxi();
            hilo_pedir_taxi.execute(getString(R.string.servidor) + "frmPedido.php?opcion=pedir_taxi", "7",
                    id,
                    String.valueOf(latitud),
                    String.valueOf(longitud),
                    nombre,
                    referencia,
                    String.valueOf(numero_casa),
                    imei,String.valueOf(clase_vehiculo),String.valueOf(tipo_pedido_empresa),direccion);// parametro que recibe el doinbackground
*/


            servicio_pedir_delivery_loquesea(
                    id,
                    String.valueOf(latitud),
                    String.valueOf(longitud),
                    nombre,
                    referencia,
                    String.valueOf(numero_casa),
                    imei,
                    String.valueOf(clase_vehiculo),
                    String.valueOf(tipo_pedido_empresa),
                    direccion
            );

        } catch (Exception e) {
            mensaje("Por favor active su GPS para realizar pedidos.");
        }
    }


    private Bitmap createDrawableFromView(Context context, View view) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        view.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
        view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);

        return bitmap;
    }


    private int dpToPx(Context context, float dpValue) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return Math.round(dpValue * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }


    private void servicio_pedir_delivery_loquesea(String id,
                                     String latitud,
                                     String longitud,
                                     String nombre,
                                     String referencia,
                                     String numero_casa,
                                     String imei,
                                     String clase_vehiculo,
                                     String tipo_pedido_empresa,
                                     String direccion
    ) {

        try {

            SharedPreferences carrito=getSharedPreferences(getString(R.string.sql_loquesea),MODE_PRIVATE);

            String token= SharedPrefManager.getInstance(this).getDeviceToken();

            JSONObject jsonParam= new JSONObject();
            jsonParam.put("id_usuario", id);
            jsonParam.put("latitud", latitud);
            jsonParam.put("longitud", longitud);

            jsonParam.put("latitud_recoger", latitud_recoger);
            jsonParam.put("longitud_recoger", longitud_recoger);
            jsonParam.put("direccion_recoger", direccion_recoger);
            jsonParam.put("que_necesitas", que_necesitas);


            jsonParam.put("nombre", nombre);
            jsonParam.put("indicacion", referencia);
            jsonParam.put("numero_casa", numero_casa);
            jsonParam.put("imei", imei);
            jsonParam.put("clase_vehiculo", clase_vehiculo);
            jsonParam.put("tipo_pedido_empresa", tipo_pedido_empresa);
            jsonParam.put("direccion", direccion);
            jsonParam.put("monto_envio", monto_envio);
            String url=getString(R.string.servidor) + "frmDelivery.php?opcion=pedir_delivery_loquesea";
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
                                    SharedPreferences pedido = getSharedPreferences("pedido_en_proceso", MODE_PRIVATE);
                                    SharedPreferences.Editor editor = pedido.edit();
                                    editor.putString("id_pedido", respuestaJSON.getString("id_pedido"));
                                    editor.commit();

                                    //-----------final-----------
                                    tv_mensaje_pedido.setText("Buscando Traigos disponibles...");
                                    SharedPreferences prefe = getSharedPreferences("pedido_en_proceso", Context.MODE_PRIVATE);

                                    try {
                                        int id_pedido =0;
                                        try{
                                            id_pedido = Integer.parseInt(prefe.getString("id_pedido", "0"));
                                        }catch (Exception e)
                                        {
                                            id_pedido=0;
                                        }
                                        if(id_pedido!=0) {
                                            Intent msgIntent = new Intent(Buscar_repartidor.this, Servicio_pedir_movil.class);
                                            msgIntent.putExtra("id_pedido", id_pedido);
                                            startService(msgIntent);
                                        }
                                    } catch (Exception e) {
                                    }


                                }else if(suceso.getSuceso().equals("2"))
                                {
                                    mensaje_error_final(suceso.getMensaje());

                                }
                                else
                                {
                                    mensaje_error("Error: Al conectar con el Servidor.\nVerifique su acceso a Internet.");
                                }
                            } catch (JSONException e) {


                                e.printStackTrace();
                                mensaje_error("Falla en tu conexión a Internet.");
                            }

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    mensaje_error("Falla en tu conexión a Internet.");
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
            myRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            queue.add(myRequest);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }





    public void servicio_get_pedido_volley(int id_pedido){
        try {

            String token= SharedPrefManager.getInstance(this).getDeviceToken();

            JSONObject jsonParam= new JSONObject();
            jsonParam.put("id_pedido", String.valueOf(id_pedido));
            jsonParam.put("token", token);
            String url=getString(R.string.servidor) + "traigo/frmPedido.php?opcion=get_pedido_por_id_pedido";
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
                                    JSONArray adato = respuestaJSON.getJSONArray("pedido");

                                    String scelular = adato.getJSONObject(0).getString("celular");
                                    String sid_taxi = adato.getJSONObject(0).getString("id_taxi");
                                    String smarca = adato.getJSONObject(0).getString("marca");
                                    String splaca = adato.getJSONObject(0).getString("placa");
                                    String scolor = adato.getJSONObject(0).getString("color");
                                    String sdireccion_imagen_adelante = adato.getJSONObject(0).getString("direccion_imagen_adelante");
                                    String smodelo = adato.getJSONObject(0).getString("modelo");
                                    String snumero_v = adato.getJSONObject(0).getString("numero_movil");
                                    String sestado = adato.getJSONObject(0).getString("estado");
                                    String sid_pedido = adato.getJSONObject(0).getString("id_pedido");
                                    String sid_empresa = adato.getJSONObject(0).getString("id_empresa");
                                    String sempresa = adato.getJSONObject(0).getString("empresa");
                                    String sclase_vehiculo = adato.getJSONObject(0).getString("clase_vehiculo");
                                    String scantidad_pedidos = adato.getJSONObject(0).getString("cantidad_pedidos");
                                    String snombre = adato.getJSONObject(0).getString("nombre_taxi");
                                    try {
                                        clase_vehiculo=Integer.parseInt(sclase_vehiculo);
                                    } catch (Exception e)
                                    {

                                    }



                                    String scalificacion_conductor=adato.getJSONObject(0).getString("calificacion_conductor");
                                    String scalificacion_vehiculo=adato.getJSONObject(0).getString("calificacion_vehiculo");

                                    SharedPreferences pedido=getSharedPreferences("ultimo_pedido",MODE_PRIVATE);
                                    SharedPreferences.Editor editar=pedido.edit();
                                    editar.putString("nombre_taxi",snombre);
                                    editar.putString("celular",scelular);
                                    editar.putString("id_taxi",sid_taxi);
                                    editar.putString("marca",smarca);
                                    editar.putString("placa",splaca);
                                    editar.putString("color",scolor);
                                    editar.putString("direccion_imagen_adelante",sdireccion_imagen_adelante);
                                    editar.putString("modelo",smodelo);
                                    editar.putString("numero_movil",snumero_v);
                                    editar.putString("estado",sestado);
                                    editar.putString("latitud",adato.getJSONObject(0).getString("latitud"));
                                    editar.putString("longitud",adato.getJSONObject(0).getString("longitud"));
                                    editar.putString("id_pedido",sid_pedido);
                                    editar.putString("id_empresa",sid_empresa);
                                    editar.putString("empresa",sempresa);
                                    editar.putString("calificacion_conductor", scalificacion_conductor);
                                    editar.putString("calificacion_vehiculo", scalificacion_vehiculo);
                                    editar.putString("clase_vehiculo",sclase_vehiculo);
                                    editar.putString("cantidad_pedidos",scantidad_pedidos);

                                    editar.commit();

                                    //===========
                                    //devuelve  8
                                    //===========


                                }else if(suceso.getSuceso().equals("2"))
                                {

                                }
                                else
                                {
                                    mensaje_error_final("Error: Al conectar con el Servidor.\nVerifique su acceso a Internet.");
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                mensaje_error_final("Falla en tu conexión a Internet.");
                            }

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    mensaje_error_final("Falla en tu conexión a Internet.");
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
            myRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            queue.add(myRequest);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private void limpiar_pedido_antiguo() {

        SharedPreferences pedido = getSharedPreferences("ultimo_pedido", MODE_PRIVATE);
        SharedPreferences.Editor editar=pedido.edit();
        editar.putString("nombre_taxi", "");
        editar.putString("celular", "");
        editar.putString("marca", "");
        editar.putString("placa", "");
        editar.putString("color", "");
        editar.commit();


        SharedPreferences punto_pedido=getSharedPreferences("punto taxi",MODE_PRIVATE);
        SharedPreferences.Editor editor=punto_pedido.edit();
        editor.putString("latitud", "0");
        editor.putString("longitud", "0");
        editor.putString("rotacion", "0");

        editor.commit();





    }


    public void mensaje(String mensaje)
    {
        Toast toast = Toast.makeText(this,mensaje, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.show();
    }


    public LatLng ultimo_registro() {
        LatLng punto=new LatLng(0,0);
        try
        {
            SharedPreferences punto_pedido=getSharedPreferences("punto taxi",MODE_PRIVATE);
            double lat= Double.parseDouble(punto_pedido.getString("latitud","0"));
            double lon= Double.parseDouble(punto_pedido.getString("longitud","0"));
            clase_vehiculo_en_pedido=  punto_pedido.getInt("clase_vehiculo",1);
            punto=new LatLng(lat,lon);
        }catch (Exception e)
        {

        }
        return punto;
    }
/*
    public LatLng primero_registro( int id_pedido) {
        LatLng punto=new LatLng(0,0);
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this,"taxicorp", null, 1);
        SQLiteDatabase bd = admin.getWritableDatabase();
        Cursor fila = bd.rawQuery("select * from puntos_pedido where id_pedido="+id_pedido+" ORDER BY fecha ASC limit 1 ", null);

        if (fila.moveToFirst()) {  //si ha devuelto 1 fila, vamos al primero (que es el unico)
            double lat=Double.parseDouble(fila.getString(1));
            double lon=Double.parseDouble(fila.getString(2));
            punto=new LatLng(lat,lon);
        }

        bd.close();
        return punto;
    }

*/

    //INICIO DE SERVICIO DE COORDENADAS..

    private void enableLocationUpdates() {

        locRequest = new LocationRequest();
        locRequest.setInterval(1500);
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
                            status.startResolutionForResult(Buscar_repartidor.this, PETICION_CONFIG_UBICACION);
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
        if (ActivityCompat.checkSelfPermission(Buscar_repartidor.this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            //Ojo: estamos suponiendo que ya tenemos concedido el permiso.
            //Sería recomendable implementar la posible petición en caso de no tenerlo.

            Log.i(LOGTAG, "Inicio de recepción de ubicaciones");

            LocationServices.FusedLocationApi.requestLocationUpdates(
                    apiClient, locRequest,Buscar_repartidor.this);
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        //Se ha producido un error que no se puede resolver automáticamente
        //y la conexión con los Google Play Services no se ha establecido.

        Log.e(LOGTAG, "Error grave al conectar con Google Play Services "+result    );
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
    }

    @Override
    public void onConnectionSuspended(int i) {
        //Se ha interrumpido la conexión con Google Play Services

        Log.e(LOGTAG, "Se ha interrumpido la conexión con Google Play Services");
    }

    private void updateUI(Location loc) {
        if (loc != null) {
            double lat = loc.getLatitude();
            double lon = loc.getLongitude();
            myPosition = new LatLng(lat, lon);




            if (sw_acercar_a_mi_ubicacion == 0) {
                //mover la camara del mapa a mi ubicacion.,,
                try {
                    //agregaranimacion al mover la camara...
                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(new LatLng(loc.getLatitude(), loc.getLongitude()))      // Sets the center of the map to Mountain View
                            .zoom(Float.parseFloat("16.7"))                   // Sets the zoom
                            .bearing(0)                // Sets the orientation of the camera to east
                            .tilt(0)                   // Sets the tilt of the camera to 30 degrees
                            .build();

                    mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                    sw_acercar_a_mi_ubicacion = 1;
                    mMap.clear();
                    crear_puntos_conductor();

                } catch (Exception e) {
                    sw_acercar_a_mi_ubicacion = 0;
                }

            }




            //.
            long diferencia=0;
            Date fecha_actual=new Date();
            try {


                //obtienes la diferencia de las fechas
                diferencia = Math.abs(fecha_actual.getTime() - fecha_conexion.getTime());
            }catch (Exception ee)
            {
                diferencia=4100;
            }

            if(diferencia>4000)
            {
                fecha_conexion=new Date();
                ver_moviles2();
            }

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
        }else if(requestCode == 1000)
        {
            int per=0;
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0 ) {
                for (int i=0;i<grantResults.length;i++){
                    if(grantResults[i] == PackageManager.PERMISSION_GRANTED){
                        per++;
                    }
                }

                // permission was granted, yay! Do the
                // contacts-related task you need to do.

            } else {

                finish();
            }

            if(per<grantResults.length){
                finish();
            }else{
                //tiene todos los permisos...

            }
            return;

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
    public void onResume() {
        super.onResume();
        registerReceiver(mReceiver, mIntentFilter);
    }



    @Override
    public void onLocationChanged(Location location) {

        //Log.i(LOGTAG, "Recibida nueva ubicación!");

        //Mostramos la nueva ubicación recibida
        updateUI(location);
    }
    //FIN DE SERVICIO DE COORDENADAS..

    public void mensaje_error(String mensaje)
    {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Ups");
            builder.setMessage(mensaje);
            builder.setPositiveButton("OK", null);
            builder.create();
            builder.show();
        }catch (Exception e)
        {
            finish();
        }
    }

    public void mensaje_error_final(String mensaje)
    {   try {
        if(sw_destroy==false) {
            AlertDialog.Builder builder = new AlertDialog.Builder(Buscar_repartidor.this);
            builder.setTitle("Ups");
            builder.setCancelable(false);
            builder.setMessage(mensaje);
            builder.create();
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogo1, int id) {
                    finish();
                }
            });
            builder.show();
        }else
        {
            finish();
        }
    }catch (Exception e)
    {   Log.e("mensaje_error",e.toString());
    }

    }
    public  int getDistancia(double lat_a,double lon_a, double lat_b, double lon_b){
        long  Radius = 6371000;
        double dLat = Math.toRadians(lat_b-lat_a);
        double dLon = Math.toRadians(lon_b-lon_a);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(Math.toRadians(lat_a)) * Math.cos(Math.toRadians(lat_b)) * Math.sin(dLon /2) * Math.sin(dLon/2);
        double c = 2 * Math.asin(Math.sqrt(a));
        return (int) (Radius * c);
    }
    public void verificar_pedido() {
        SharedPreferences prefe = getSharedPreferences("pedido_en_proceso", Context.MODE_PRIVATE);
        try {
            int id_pedido = Integer.parseInt(prefe.getString("id_pedido", ""));
            servicio_verificar_si_acepto_pedido_sin_notificacion(String.valueOf(id_pedido));
            tv_mensaje_pedido.setText("Esperando la confirmación por el Taxista ...");


        } catch (Exception e) {
        }
    }

    private void servicio_verificar_si_acepto_pedido_sin_notificacion(String id_pedido) {


        try {

            JSONObject jsonParam= new JSONObject();
            jsonParam.put("id_pedido", id_pedido);
            String url=getString(R.string.servidor) + "frmPedido.php?opcion=verificar_si_acepto_pedido_sin_notificacion";
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

                                    JSONArray dato = respuestaJSON.getJSONArray("pedido");
                                    String snombre = dato.getJSONObject(0).getString("nombre_taxi");
                                    String scelular = dato.getJSONObject(0).getString("celular");
                                    String sid_taxi = dato.getJSONObject(0).getString("id_taxi");
                                    String smarca = dato.getJSONObject(0).getString("marca");
                                    String splaca = dato.getJSONObject(0).getString("placa");
                                    String scolor = dato.getJSONObject(0).getString("color");
                                    String sdireccion_imagen_adelante = dato.getJSONObject(0).getString("direccion_imagen_adelante");
                                    String smodelo = dato.getJSONObject(0).getString("modelo");
                                    String snumero_m = dato.getJSONObject(0).getString("numero_movil");
                                    String sid_pedido = dato.getJSONObject(0).getString("id_pedido");
                                    String sempresa=dato.getJSONObject(0).getString("empresa");
                                    String s_id_empresa=dato.getJSONObject(0).getString("id_empresa");
                                    String scalificacion_conductor = dato.getJSONObject(0).getString("calificacion_conductor");
                                    String scalificacion_vehiculo = dato.getJSONObject(0).getString("calificacion_vehiculo");
                                    SharedPreferences pedido = getSharedPreferences("ultimo_pedido", MODE_PRIVATE);
                                    SharedPreferences.Editor editar = pedido.edit();
                                    editar.putString("nombre_taxi", snombre);
                                    editar.putString("celular", scelular);
                                    editar.putString("id_taxi", sid_taxi);
                                    editar.putString("marca", smarca);
                                    editar.putString("placa", splaca);
                                    editar.putString("color", scolor);
                                    editar.putString("direccion_imagen_adelante", sdireccion_imagen_adelante);
                                    editar.putString("modelo", smodelo);
                                    editar.putString("numero_movil", snumero_m);
                                    editar.putString("latitud", dato.getJSONObject(0).getString("latitud"));
                                    editar.putString("longitud", dato.getJSONObject(0).getString("longitud"));
                                    editar.putString("abordo", dato.getJSONObject(0).getString("abordo"));
                                    editar.putString("estado", dato.getJSONObject(0).getString("estado"));
                                    editar.putString("id_pedido", sid_pedido);
                                    editar.putString("calificacion_conductor", scalificacion_conductor);
                                    editar.putString("calificacion_vehiculo", scalificacion_vehiculo);
                                    editar.putString("empresa",sempresa);
                                    editar.putString("id_empresa",s_id_empresa);
                                    editar.commit();

                                    SharedPreferences proceso = getSharedPreferences("pedido_en_proceso", MODE_PRIVATE);
                                    SharedPreferences.Editor editor = proceso.edit();
                                    editor.putString("id_pedido", "");
                                    editor.commit();


                                    ///////////////////// 8 final //////////////////

                                    //verificar si alguien acepto el pedido.

                                    Intent intent = new Intent(Buscar_repartidor.this, Servicio_pedido.class);
                                    intent.setAction(Constants.ACTION_RUN_ISERVICE);
                                    startService(intent);

                                    SharedPreferences spedido=getSharedPreferences("ultimo_pedido",MODE_PRIVATE);


                                    flotante_pedir(false);


                                }else if(suceso.getSuceso().equals("3"))
                                {
//500

                                }
                                else
                                {
                                    //9
                                    mensaje_error_final(suceso.getMensaje());
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                mensaje_error_final("Falla en tu conexión a Internet.");
                            }

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    mensaje_error_final("Falla en tu conexión a Internet.");
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
            myRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            queue.add(myRequest);


        } catch (Exception e) {

        }

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
        boolean sw=false;

        boolean connected = false;
        ConnectivityManager connec = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);

        // Recupera todas las redes (tanto móviles como wifi)
        NetworkInfo[] redes = connec.getAllNetworkInfo();

        for (int i = 0; i < redes.length; i++) {
            // Si alguna red tiene conexión, se devuelve true
            if (redes[i].getState() == NetworkInfo.State.CONNECTED) {
                connected = true;
            }
        }
        sw=connected;

        if(sw==true)
        {
            return true;
        }else{
            mensaje_error_final("Tu Dispositivo no tiene Conexion a Internet."+2645);
            return false;
        }
       /*
        if(conectadoWifi()==true){
            return true;
        }else{
            if(conectadoRedMovil()==true){
                return true;
            }else{

                return false;
            }
        }
        */
    }





    public void verificar_permiso_llamada()
    {
        final String[] PERMISSIONS = { Manifest.permission.INTERNET,
                Manifest.permission.CALL_PHONE,
                Manifest.permission.ACCESS_NETWORK_STATE };

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CALL_PHONE)) {
            //YA LO CANCELE Y VOUELVO A PERDIR EL PERMISO.

            AlertDialog.Builder dialogo1 = new AlertDialog.Builder(this);
            dialogo1.setTitle("Atención!");
            dialogo1.setMessage("Debes otorgar permisos de acceso a LLAMADA.");
            dialogo1.setCancelable(false);
            dialogo1.setPositiveButton("Solicitar permiso", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogo1, int id) {
                    dialogo1.cancel();
                    ActivityCompat.requestPermissions(Buscar_repartidor.this,
                            PERMISSIONS,
                            1);

                }
            });
            dialogo1.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogo1, int id) {
                    dialogo1.cancel();

                }
            });
            dialogo1.show();
        } else {
            ActivityCompat.requestPermissions(Buscar_repartidor.this,
                    PERMISSIONS,
                    1);
        }
    }







    public void datos_de_google()
    {
        try{
            //buscamos una ruta para el motista     SOLO CO ACCESO A INTERNET
            SharedPreferences punto=getSharedPreferences("ultimo_pedido", Context.MODE_PRIVATE);
            double latitud_fin= Double.parseDouble(punto.getString("latitud","0"));
            double longitud_fin= Double.parseDouble(punto.getString("longitud","0"));
            String abordo = punto.getString("abordo", "");

            int distancia =0;
            try {
                distancia = getDistancia(ultima_ubicacion.latitude, ultima_ubicacion.longitude, latitud_fin, longitud_fin);
            }catch (Exception e)
            {
                distancia=0;
            }
            if(distancia>=1000) {

                m_conductor.setTitle("Llegada");
                m_conductor.setSnippet(distancia / 400 +" min.");

                if(abordo.equals("1")) {
                    m_conductor.hideInfoWindow();
                }else{
                    m_conductor.showInfoWindow();
                }
            }else
            {

                m_conductor.setTitle("Llegada");
                m_conductor.setSnippet("1 min.");

                if(abordo.equals("1")) {
                    m_conductor.hideInfoWindow();
                }else{
                    m_conductor.showInfoWindow();
                }

            }



        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }



    //marker conductores





    public void eliminar_servicio_cargar_ubicacion(){
        Intent stopIntent = new Intent(Buscar_repartidor.this, Servicio_cargar_punto_google.class);
        stopIntent.setAction(Constants.ACTION.STOP_ACTION);
        stopService(stopIntent);
    }




    private void ver_moviles2() {
        SharedPreferences ult=getSharedPreferences("ultimo_pedido",MODE_PRIVATE);
        //si no tiene pédidos se le va a mostrar en el mapa....
        int id_pedido=0;
        try{
            id_pedido=Integer.parseInt(ult.getString("id_pedido","0"));
        }catch (Exception e){
            id_pedido=0;
        }

        if(id_pedido==0) {
            try {
                servicio_ver_movil2(String.valueOf(latitud), String.valueOf(longitud));
            }catch (Exception e)
            {
                e.printStackTrace();
            }

        }
    }
    //servicio de cancelar de pedido
    private void servicio_ver_movil2(String latitud,String longitud) {

        try {

            String token= SharedPrefManager.getInstance(this).getDeviceToken();

            JSONObject jsonParam= new JSONObject();
            jsonParam.put("latitud",latitud);
            jsonParam.put("longitud",longitud);
            jsonParam.put("token", token);
            String url=getString(R.string.servidor) + "frmTaxi.php?opcion=get_taxi_en_rango";
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

                                    puntos_taxi=respuestaJSON.getJSONArray("taxi");
                                    ///----final
                                    agregar_en_mapa_ubicaciones_de_taxi2();

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
            myRequest.setRetryPolicy(new DefaultRetryPolicy(3000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            queue.add(myRequest);

        } catch (Exception e) {

        }

    }




    public void agregar_en_mapa_ubicaciones_de_taxi2() {
        try {
            for (int i = 0; i < puntos_taxi.length(); i++) {
                int rotacion = Integer.parseInt(puntos_taxi.getJSONObject(i).getString("rotacion"));
                double lat = Double.parseDouble(puntos_taxi.getJSONObject(i).getString("latitud"));
                double lon = Double.parseDouble(puntos_taxi.getJSONObject(i).getString("longitud"));
                String distancia= puntos_taxi.getJSONObject(i).getString("distancia");
                String id= puntos_taxi.getJSONObject(i).getString("ci");
                String fecha= puntos_taxi.getJSONObject(i).getString("fecha");
                fecha_ultimo=fecha;
                int moto=Integer.parseInt(puntos_taxi.getJSONObject(i).getString("moto"));

                cargar_puntos_movil(lat, lon,rotacion,distancia,id,fecha,moto);


            }
            for (int i = 0; i < puntos_taxi.length(); i++) {
                int rotacion = Integer.parseInt(puntos_taxi.getJSONObject(i).getString("rotacion"));
                double lat = Double.parseDouble(puntos_taxi.getJSONObject(i).getString("latitud"));
                double lon = Double.parseDouble(puntos_taxi.getJSONObject(i).getString("longitud"));
                String distancia= puntos_taxi.getJSONObject(i).getString("distancia");
                String id= puntos_taxi.getJSONObject(i).getString("ci");
                String fecha= puntos_taxi.getJSONObject(i).getString("fecha");

                int moto=Integer.parseInt(puntos_taxi.getJSONObject(i).getString("moto"));
                if(moto==0){
                    cargar_puntos_movil_segundo(lat, lon,rotacion,distancia,id,fecha);
                }else{
                    cargar_puntos_moto_segundo(lat, lon,rotacion,distancia,id,fecha);
                }

            }
            ocultar_conductores_no_activos();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void crear_puntos_conductor()
    {
        try {
            LatLng punto = myPosition;
            marker_1=mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_car_marker))
                    .position(punto)
                    .anchor((float)0.5,(float)0.8)
                    .flat(true)
                    .rotation(0)
                    .visible(false));
            marker_2=mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_car_marker))
                    .position(punto)
                    .anchor((float)0.5,(float)0.8)
                    .flat(true)
                    .rotation(0)
                    .visible(false));
            marker_3=mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_car_marker))
                    .position(punto)
                    .anchor((float)0.5,(float)0.8)
                    .flat(true)
                    .rotation(0)
                    .visible(false));
            marker_4=mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_car_marker))
                    .position(punto)
                    .anchor((float)0.5,(float)0.8)
                    .flat(true)
                    .rotation(0)
                    .visible(false));
            marker_5=mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_car_marker))
                    .position(punto)
                    .anchor((float)0.5,(float)0.8)
                    .flat(true)
                    .rotation(0)
                    .visible(false));
            marker_6=mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_car_marker))
                    .position(punto)
                    .anchor((float)0.5,(float)0.8)
                    .flat(true)
                    .rotation(0)
                    .visible(false));
            marker_7=mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_car_marker))
                    .position(punto)
                    .anchor((float)0.5,(float)0.8)
                    .flat(true)
                    .rotation(0)
                    .visible(false));
            marker_8=mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_car_marker))
                    .position(punto)
                    .anchor((float)0.5,(float)0.8)
                    .flat(true)
                    .rotation(0)
                    .visible(false));
            marker_9=mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_car_marker))
                    .position(punto)
                    .anchor((float)0.5,(float)0.8)
                    .flat(true)
                    .rotation(0)
                    .visible(false));
            marker_10=mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_car_marker))
                    .position(punto)
                    .anchor((float)0.5,(float)0.8)
                    .flat(true)
                    .rotation(0)
                    .visible(false));
            marker_11=mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_car_marker))
                    .position(punto)
                    .anchor((float)0.5,(float)0.8)
                    .flat(true)
                    .rotation(0)
                    .visible(false));

            marker_12=mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_car_marker))
                    .position(punto)
                    .anchor((float)0.5,(float)0.8)
                    .flat(true)
                    .rotation(0)
                    .visible(false));

            marker_13=mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_car_marker))
                    .position(punto)
                    .anchor((float)0.5,(float)0.8)
                    .flat(true)
                    .rotation(0)
                    .visible(false));

            marker_14=mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_car_marker))
                    .position(punto)
                    .anchor((float)0.5,(float)0.8)
                    .flat(true)
                    .rotation(0)
                    .visible(false));

            marker_15=mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_car_marker))
                    .position(punto)
                    .anchor((float)0.5,(float)0.8)
                    .flat(true)
                    .rotation(0)
                    .visible(false));
            marker_16=mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_car_marker))
                    .position(punto)
                    .anchor((float)0.5,(float)0.8)
                    .flat(true)
                    .rotation(0)
                    .visible(false));
            marker_17=mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_car_marker))
                    .position(punto)
                    .anchor((float)0.5,(float)0.8)
                    .flat(true)
                    .rotation(0)
                    .visible(false));
            marker_18=mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_car_marker))
                    .position(punto)
                    .anchor((float)0.5,(float)0.8)
                    .flat(true)
                    .rotation(0)
                    .visible(false));
            marker_19=mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_car_marker))
                    .position(punto)
                    .anchor((float)0.5,(float)0.8)
                    .flat(true)
                    .rotation(0)
                    .visible(false));
            marker_20=mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_car_marker))
                    .position(punto)
                    .anchor((float)0.5,(float)0.8)
                    .flat(true)
                    .rotation(0)
                    .visible(false));
            marker_21=mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_car_marker))
                    .position(punto)
                    .anchor((float)0.5,(float)0.8)
                    .flat(true)
                    .rotation(0)
                    .visible(false));
            marker_22=mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_car_marker))
                    .position(punto)
                    .anchor((float)0.5,(float)0.8)
                    .flat(true)
                    .rotation(0)
                    .visible(false));
            marker_23=mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_car_marker))
                    .position(punto)
                    .anchor((float)0.5,(float)0.8)
                    .flat(true)
                    .rotation(0)
                    .visible(false));
            marker_24=mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_car_marker))
                    .position(punto)
                    .anchor((float)0.5,(float)0.8)
                    .flat(true)
                    .rotation(0)
                    .visible(false));
            marker_25=mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_car_marker))
                    .position(punto)
                    .anchor((float)0.5,(float)0.8)
                    .flat(true)
                    .rotation(0)
                    .visible(false));


        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void cargar_puntos_movil( double lat,double lon,int rotacion,String distancia,String id,String fecha, int moto)
    {

        LatLng ubicacion=new LatLng(lat,lon);

        if(id.equals(cond_1)){
            if(moto==0){
                marker_1.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_car_marker));
            }else{
                marker_1.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_mot_marker));
            }
            fecha_1=fecha;
            marker_1.setVisible(true);
            marker_1.setRotation(rotacion);
            MarkerAnimation.animateMarkerToGB(marker_1, ubicacion, new LatLngInterpolator.Spherical());

        }
        else if(id.equals(cond_2)){
            if(moto==0){
                marker_2.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_car_marker));
            }else{
                marker_2.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_mot_marker));
            }
            fecha_2=fecha;
            marker_2.setVisible(true);
            marker_2.setRotation(rotacion);
            MarkerAnimation.animateMarkerToGB(marker_2, ubicacion, new LatLngInterpolator.Spherical());

        }
        else  if(id.equals(cond_3)){
            if(moto==0){
                marker_3.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_car_marker));
            }else{
                marker_3.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_mot_marker));
            }
            fecha_3=fecha;
            marker_3.setVisible(true);
            marker_3.setRotation(rotacion);
            MarkerAnimation.animateMarkerToGB(marker_3, ubicacion, new LatLngInterpolator.Spherical());
        }
        else if(id.equals(cond_4)){
            if(moto==0){
                marker_4.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_car_marker));
            }else{
                marker_4.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_mot_marker));
            }
            fecha_4=fecha;
            marker_4.setVisible(true);
            marker_4.setRotation(rotacion);

            MarkerAnimation.animateMarkerToGB(marker_4, ubicacion, new LatLngInterpolator.Spherical());
        }
        else if(id.equals(cond_5)){
            if(moto==0){
                marker_5.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_car_marker));
            }else{
                marker_5.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_mot_marker));
            }
            fecha_5=fecha;
            marker_5.setVisible(true);
            marker_5.setRotation(rotacion);

            MarkerAnimation.animateMarkerToGB(marker_5, ubicacion, new LatLngInterpolator.Spherical());
        }
        else if(id.equals(cond_6)){
            if(moto==0){
                marker_6.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_car_marker));
            }else{
                marker_6.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_mot_marker));
            }
            fecha_6=fecha;
            marker_6.setVisible(true);
            marker_6.setRotation(rotacion);

            MarkerAnimation.animateMarkerToGB(marker_6, ubicacion, new LatLngInterpolator.Spherical());
        }
        else if(id.equals(cond_7)){
            if(moto==0){
                marker_7.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_car_marker));
            }else{
                marker_7.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_mot_marker));
            }
            fecha_7=fecha;
            marker_7.setVisible(true);
            marker_7.setRotation(rotacion);

            MarkerAnimation.animateMarkerToGB(marker_7, ubicacion, new LatLngInterpolator.Spherical());
        }
        else if(id.equals(cond_8)){
            if(moto==0){
                marker_8.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_car_marker));
            }else{
                marker_8.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_mot_marker));
            }
            fecha_8=fecha;
            marker_8.setVisible(true);
            marker_8.setRotation(rotacion);

            MarkerAnimation.animateMarkerToGB(marker_8, ubicacion, new LatLngInterpolator.Spherical());
        }
        else if(id.equals(cond_9)){
            if(moto==0){
                marker_9.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_car_marker));
            }else{
                marker_9.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_mot_marker));
            }
            fecha_9=fecha;
            marker_9.setVisible(true);
            marker_9.setRotation(rotacion);

            MarkerAnimation.animateMarkerToGB(marker_9, ubicacion, new LatLngInterpolator.Spherical());
        }

        else if(id.equals(cond_10)){
            if(moto==0){
                marker_10.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_car_marker));
            }else{
                marker_10.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_mot_marker));
            }
            fecha_10=fecha;
            marker_10.setVisible(true);
            marker_10.setRotation(rotacion);

            MarkerAnimation.animateMarkerToGB(marker_10, ubicacion, new LatLngInterpolator.Spherical());
        }

        else if(id.equals(cond_11)){
            if(moto==0){
                marker_11.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_car_marker));
            }else{
                marker_11.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_mot_marker));
            }
            fecha_11=fecha;
            marker_11.setVisible(true);
            marker_11.setRotation(rotacion);

            MarkerAnimation.animateMarkerToGB(marker_11, ubicacion, new LatLngInterpolator.Spherical());
        }

        else if(id.equals(cond_12)){
            if(moto==0){
                marker_12.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_car_marker));
            }else{
                marker_12.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_mot_marker));
            }
            fecha_12=fecha;
            marker_12.setVisible(true);
            marker_12.setRotation(rotacion);

            MarkerAnimation.animateMarkerToGB(marker_12, ubicacion, new LatLngInterpolator.Spherical());
        }

        else if(id.equals(cond_13)){
            if(moto==0){
                marker_13.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_car_marker));
            }else{
                marker_13.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_mot_marker));
            }
            fecha_13=fecha;
            marker_13.setVisible(true);
            marker_13.setRotation(rotacion);

            MarkerAnimation.animateMarkerToGB(marker_13, ubicacion, new LatLngInterpolator.Spherical());
        }

        else if(id.equals(cond_14)){
            if(moto==0){
                marker_14.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_car_marker));
            }else{
                marker_14.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_mot_marker));
            }
            fecha_14=fecha;
            marker_14.setVisible(true);
            marker_14.setRotation(rotacion);

            MarkerAnimation.animateMarkerToGB(marker_14, ubicacion, new LatLngInterpolator.Spherical());
        }
        else if(id.equals(cond_15)){
            if(moto==0){
                marker_15.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_car_marker));
            }else{
                marker_15.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_mot_marker));
            }
            fecha_15=fecha;
            marker_15.setVisible(true);
            marker_15.setRotation(rotacion);

            MarkerAnimation.animateMarkerToGB(marker_15, ubicacion, new LatLngInterpolator.Spherical());
        }
        else if(id.equals(cond_16)){
            if(moto==0){
                marker_16.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_car_marker));
            }else{
                marker_16.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_mot_marker));
            }
            fecha_16=fecha;
            marker_16.setVisible(true);
            marker_16.setRotation(rotacion);

            MarkerAnimation.animateMarkerToGB(marker_16, ubicacion, new LatLngInterpolator.Spherical());
        }
        else if(id.equals(cond_17)){
            if(moto==0){
                marker_17.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_car_marker));
            }else{
                marker_17.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_mot_marker));
            }
            fecha_17=fecha;
            marker_17.setVisible(true);
            marker_17.setRotation(rotacion);

            MarkerAnimation.animateMarkerToGB(marker_17, ubicacion, new LatLngInterpolator.Spherical());
        }
        else if(id.equals(cond_18)){
            if(moto==0){
                marker_18.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_car_marker));
            }else{
                marker_18.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_mot_marker));
            }
            fecha_18=fecha;
            marker_18.setVisible(true);
            marker_18.setRotation(rotacion);

            MarkerAnimation.animateMarkerToGB(marker_18, ubicacion, new LatLngInterpolator.Spherical());
        }
        else if(id.equals(cond_19)){
            if(moto==0){
                marker_19.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_car_marker));
            }else{
                marker_19.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_mot_marker));
            }
            fecha_19=fecha;
            marker_19.setVisible(true);
            marker_19.setRotation(rotacion);

            MarkerAnimation.animateMarkerToGB(marker_19, ubicacion, new LatLngInterpolator.Spherical());
        }
        else if(id.equals(cond_20)){
            if(moto==0){
                marker_20.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_car_marker));
            }else{
                marker_20.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_mot_marker));
            }
            fecha_20=fecha;
            marker_20.setVisible(true);
            marker_20.setRotation(rotacion);

            MarkerAnimation.animateMarkerToGB(marker_20, ubicacion, new LatLngInterpolator.Spherical());
        }
        else if(id.equals(cond_21)){
            if(moto==0){
                marker_21.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_car_marker));
            }else{
                marker_21.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_mot_marker));
            }
            fecha_21=fecha;
            marker_21.setVisible(true);
            marker_21.setRotation(rotacion);

            MarkerAnimation.animateMarkerToGB(marker_21, ubicacion, new LatLngInterpolator.Spherical());
        }
        else if(id.equals(cond_22)){
            if(moto==0){
                marker_22.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_car_marker));
            }else{
                marker_22.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_mot_marker));
            }
            fecha_22=fecha;
            marker_22.setVisible(true);
            marker_22.setRotation(rotacion);

            MarkerAnimation.animateMarkerToGB(marker_22, ubicacion, new LatLngInterpolator.Spherical());
        }
        else if(id.equals(cond_23)){
            if(moto==0){
                marker_23.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_car_marker));
            }else{
                marker_23.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_mot_marker));
            }
            fecha_23=fecha;
            marker_23.setVisible(true);
            marker_23.setRotation(rotacion);

            MarkerAnimation.animateMarkerToGB(marker_23, ubicacion, new LatLngInterpolator.Spherical());
        }
        else if(id.equals(cond_24)){
            if(moto==0){
                marker_24.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_car_marker));
            }else{
                marker_24.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_mot_marker));
            }
            fecha_24=fecha;
            marker_24.setVisible(true);
            marker_24.setRotation(rotacion);

            MarkerAnimation.animateMarkerToGB(marker_24, ubicacion, new LatLngInterpolator.Spherical());
        }
        else if(id.equals(cond_25)){
            if(moto==0){
                marker_25.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_car_marker));
            }else{
                marker_25.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_mot_marker));
            }
            fecha_25=fecha;
            marker_25.setVisible(true);
            marker_25.setRotation(rotacion);

            MarkerAnimation.animateMarkerToGB(marker_25, ubicacion, new LatLngInterpolator.Spherical());
        }




    }

    public void cargar_puntos_movil_segundo( double lat,double lon,int rotacion,String distancia,String id,String fecha)
    {


        LatLng ubicacion=new LatLng(lat,lon);

        if(cond_1.equals(id)&&fecha_1.equals(fecha_ultimo))
        {

        }else if(cond_2.equals(id)&&fecha_2.equals(fecha_ultimo))
        {

        }else if(cond_3.equals(id)&&fecha_3.equals(fecha_ultimo))
        {

        }else if(cond_4.equals(id)&&fecha_4.equals(fecha_ultimo))
        {

        }else if(cond_5.equals(id)&&fecha_5.equals(fecha_ultimo))
        {

        }else if(cond_6.equals(id)&&fecha_6.equals(fecha_ultimo))
        {

        }else if(cond_7.equals(id)&&fecha_7.equals(fecha_ultimo))
        {

        }else if(cond_8.equals(id)&&fecha_8.equals(fecha_ultimo))
        {

        }else if(cond_9.equals(id)&&fecha_9.equals(fecha_ultimo))
        {

        }else if(cond_10.equals(id)&&fecha_10.equals(fecha_ultimo))
        {

        }else if(cond_11.equals(id)&&fecha_11.equals(fecha_ultimo))
        {

        }else if(cond_12.equals(id)&&fecha_12.equals(fecha_ultimo))
        {

        }else if(cond_13.equals(id)&&fecha_13.equals(fecha_ultimo))
        {

        }else if(cond_14.equals(id)&&fecha_14.equals(fecha_ultimo))
        {

        }else if(cond_15.equals(id)&&fecha_15.equals(fecha_ultimo))
        {

        }else if(cond_16.equals(id)&&fecha_16.equals(fecha_ultimo))
        {

        }else if(cond_17.equals(id)&&fecha_17.equals(fecha_ultimo))
        {

        }else if(cond_18.equals(id)&&fecha_18.equals(fecha_ultimo))
        {

        }else if(cond_19.equals(id)&&fecha_19.equals(fecha_ultimo))
        {

        }else if(cond_20.equals(id)&&fecha_20.equals(fecha_ultimo))
        {

        }else if(cond_21.equals(id)&&fecha_21.equals(fecha_ultimo))
        {

        }else if(cond_22.equals(id)&&fecha_22.equals(fecha_ultimo))
        {

        }else if(cond_23.equals(id)&&fecha_23.equals(fecha_ultimo))
        {

        }else if(cond_24.equals(id)&&fecha_24.equals(fecha_ultimo))
        {

        }else if(cond_25.equals(id)&&fecha_25.equals(fecha_ultimo))
        {

        }else if(fecha_1.equals(fecha_ultimo)==false){
            fecha_1=fecha;
            cond_1=id;
            marker_1.setVisible(true);
            marker_1.setRotation(rotacion);
            MarkerAnimation.animateMarkerToGB(marker_1, ubicacion, new LatLngInterpolator.Spherical());
        }
        else if(fecha_2.equals(fecha_ultimo)==false){
            fecha_2=fecha;
            cond_2=id;
            marker_2.setVisible(true);
            marker_2.setRotation(rotacion);

            MarkerAnimation.animateMarkerToGB(marker_2, ubicacion, new LatLngInterpolator.Spherical());
        }
        else  if(fecha_3.equals(fecha_ultimo)==false){
            fecha_3=fecha;
            cond_3=id;
            marker_3.setVisible(true);
            marker_3.setRotation(rotacion);

            MarkerAnimation.animateMarkerToGB(marker_3, ubicacion, new LatLngInterpolator.Spherical());
        }
        else if(fecha_4.equals(fecha_ultimo)==false){
            fecha_4=fecha;
            cond_4=id;
            marker_4.setVisible(true);
            marker_4.setRotation(rotacion);

            MarkerAnimation.animateMarkerToGB(marker_4, ubicacion, new LatLngInterpolator.Spherical());
        }
        else if(fecha_5.equals(fecha_ultimo)==false){
            fecha_5=fecha;
            cond_5=id;
            marker_5.setVisible(true);
            marker_5.setRotation(rotacion);

            MarkerAnimation.animateMarkerToGB(marker_5, ubicacion, new LatLngInterpolator.Spherical());
        }
        else if(fecha_6.equals(fecha_ultimo)==false){
            fecha_6=fecha;
            cond_6=id;
            marker_6.setVisible(true);
            marker_6.setRotation(rotacion);

            MarkerAnimation.animateMarkerToGB(marker_6, ubicacion, new LatLngInterpolator.Spherical());
        }
        else if(fecha_7.equals(fecha_ultimo)==false){
            fecha_7=fecha;
            cond_7=id;
            marker_7.setVisible(true);
            marker_7.setRotation(rotacion);

            MarkerAnimation.animateMarkerToGB(marker_7, ubicacion, new LatLngInterpolator.Spherical());
        }
        else if(fecha_8.equals(fecha_ultimo)==false){
            fecha_8=fecha;
            cond_8=id;
            marker_8.setVisible(true);
            marker_8.setRotation(rotacion);

            MarkerAnimation.animateMarkerToGB(marker_8, ubicacion, new LatLngInterpolator.Spherical());
        }
        else if(fecha_9.equals(fecha_ultimo)==false){
            fecha_9=fecha;
            cond_9=id;
            marker_9.setVisible(true);
            marker_9.setRotation(rotacion);

            MarkerAnimation.animateMarkerToGB(marker_9, ubicacion, new LatLngInterpolator.Spherical());
        }
        else if(fecha_10.equals(fecha_ultimo)==false){
            fecha_10=fecha;
            cond_10=id;
            marker_10.setVisible(true);
            marker_10.setRotation(rotacion);

            MarkerAnimation.animateMarkerToGB(marker_10, ubicacion, new LatLngInterpolator.Spherical());
        }
        else if(fecha_11.equals(fecha_ultimo)==false){
            fecha_11=fecha;
            cond_11=id;
            marker_11.setVisible(true);
            marker_11.setRotation(rotacion);

            MarkerAnimation.animateMarkerToGB(marker_11, ubicacion, new LatLngInterpolator.Spherical());
        }
        else if(fecha_12.equals(fecha_ultimo)==false){
            fecha_12=fecha;
            cond_12=id;
            marker_12.setVisible(true);
            marker_12.setRotation(rotacion);

            MarkerAnimation.animateMarkerToGB(marker_12, ubicacion, new LatLngInterpolator.Spherical());
        }
        else if(fecha_13.equals(fecha_ultimo)==false){
            fecha_13=fecha;
            cond_13=id;
            marker_13.setVisible(true);
            marker_13.setRotation(rotacion);

            MarkerAnimation.animateMarkerToGB(marker_13, ubicacion, new LatLngInterpolator.Spherical());
        }else if(fecha_14.equals(fecha_ultimo)==false){
            fecha_14=fecha;
            cond_14=id;
            marker_14.setVisible(true);
            marker_14.setRotation(rotacion);

            MarkerAnimation.animateMarkerToGB(marker_14, ubicacion, new LatLngInterpolator.Spherical());
        }
        else if(fecha_15.equals(fecha_ultimo)==false){
            fecha_15=fecha;
            cond_15=id;
            marker_15.setVisible(true);
            marker_15.setRotation(rotacion);

            MarkerAnimation.animateMarkerToGB(marker_15, ubicacion, new LatLngInterpolator.Spherical());
        }

        else if(fecha_16.equals(fecha_ultimo)==false){
            fecha_16=fecha;
            cond_16=id;
            marker_16.setVisible(true);
            marker_16.setRotation(rotacion);

            MarkerAnimation.animateMarkerToGB(marker_16, ubicacion, new LatLngInterpolator.Spherical());
        }

        else if(fecha_17.equals(fecha_ultimo)==false){
            fecha_17=fecha;
            cond_17=id;
            marker_17.setVisible(true);
            marker_17.setRotation(rotacion);

            MarkerAnimation.animateMarkerToGB(marker_17, ubicacion, new LatLngInterpolator.Spherical());
        }

        else if(fecha_18.equals(fecha_ultimo)==false){
            fecha_18=fecha;
            cond_18=id;
            marker_18.setVisible(true);
            marker_18.setRotation(rotacion);

            MarkerAnimation.animateMarkerToGB(marker_18, ubicacion, new LatLngInterpolator.Spherical());
        }

        else if(fecha_19.equals(fecha_ultimo)==false){
            fecha_19=fecha;
            cond_19=id;
            marker_19.setVisible(true);
            marker_19.setRotation(rotacion);

            MarkerAnimation.animateMarkerToGB(marker_19, ubicacion, new LatLngInterpolator.Spherical());
        }

        else if(fecha_20.equals(fecha_ultimo)==false){
            fecha_20=fecha;
            cond_20=id;
            marker_20.setVisible(true);
            marker_20.setRotation(rotacion);

            MarkerAnimation.animateMarkerToGB(marker_20, ubicacion, new LatLngInterpolator.Spherical());
        }

        else if(fecha_21.equals(fecha_ultimo)==false){
            fecha_21=fecha;
            cond_21=id;
            marker_21.setVisible(true);
            marker_21.setRotation(rotacion);

            MarkerAnimation.animateMarkerToGB(marker_21, ubicacion, new LatLngInterpolator.Spherical());
        }

        else if(fecha_22.equals(fecha_ultimo)==false){
            fecha_22=fecha;
            cond_22=id;
            marker_22.setVisible(true);
            marker_22.setRotation(rotacion);

            MarkerAnimation.animateMarkerToGB(marker_22, ubicacion, new LatLngInterpolator.Spherical());
        }

        else if(fecha_23.equals(fecha_ultimo)==false){
            fecha_23=fecha;
            cond_23=id;
            marker_23.setVisible(true);
            marker_23.setRotation(rotacion);

            MarkerAnimation.animateMarkerToGB(marker_23, ubicacion, new LatLngInterpolator.Spherical());
        }

        else if(fecha_24.equals(fecha_ultimo)==false){
            fecha_24=fecha;
            cond_24=id;
            marker_24.setVisible(true);
            marker_24.setRotation(rotacion);

            MarkerAnimation.animateMarkerToGB(marker_24, ubicacion, new LatLngInterpolator.Spherical());
        }

        else if(fecha_25.equals(fecha_ultimo)==false){
            fecha_25=fecha;
            cond_25=id;
            marker_25.setVisible(true);
            marker_25.setRotation(rotacion);

            MarkerAnimation.animateMarkerToGB(marker_25, ubicacion, new LatLngInterpolator.Spherical());
        }

    }

    public void cargar_puntos_moto_segundo( double lat,double lon,int rotacion,String distancia,String id,String fecha)
    {


        LatLng ubicacion=new LatLng(lat,lon);

        if(fecha_1.equals(fecha_ultimo)==false){
            marker_1.setRotation(rotacion);
            fecha_1=fecha;
            cond_1=id;
            MarkerAnimation.animateMarkerToGB(marker_1, ubicacion, new LatLngInterpolator.Spherical());
        }
        else if(fecha_2.equals(fecha_ultimo)==false){
            marker_2.setRotation(rotacion);
            fecha_2=fecha;
            cond_2=id;
            MarkerAnimation.animateMarkerToGB(marker_2, ubicacion, new LatLngInterpolator.Spherical());
        }
        else  if(fecha_3.equals(fecha_ultimo)==false){
            marker_3.setRotation(rotacion);
            fecha_3=fecha;
            cond_3=id;
            MarkerAnimation.animateMarkerToGB(marker_3, ubicacion, new LatLngInterpolator.Spherical());
        }
        else if(fecha_4.equals(fecha_ultimo)==false){
            marker_4.setRotation(rotacion);
            fecha_4=fecha;
            cond_4=id;
            MarkerAnimation.animateMarkerToGB(marker_4, ubicacion, new LatLngInterpolator.Spherical());
        }
        else if(fecha_5.equals(fecha_ultimo)==false){
            marker_5.setRotation(rotacion);
            fecha_5=fecha;
            cond_5=id;
            MarkerAnimation.animateMarkerToGB(marker_5, ubicacion, new LatLngInterpolator.Spherical());
        }
        else if(fecha_6.equals(fecha_ultimo)==false){
            marker_6.setRotation(rotacion);
            fecha_6=fecha;
            cond_6=id;
            MarkerAnimation.animateMarkerToGB(marker_6, ubicacion, new LatLngInterpolator.Spherical());
        }
        else if(fecha_7.equals(fecha_ultimo)==false){
            marker_7.setRotation(rotacion);
            fecha_7=fecha;
            cond_7=id;
            MarkerAnimation.animateMarkerToGB(marker_7, ubicacion, new LatLngInterpolator.Spherical());
        }
        else if(fecha_8.equals(fecha_ultimo)==false){
            marker_8.setRotation(rotacion);
            fecha_8=fecha;
            cond_8=id;
            MarkerAnimation.animateMarkerToGB(marker_8, ubicacion, new LatLngInterpolator.Spherical());
        }
        else if(fecha_9.equals(fecha_ultimo)==false){
            marker_9.setRotation(rotacion);
            fecha_9=fecha;
            cond_9=id;
            MarkerAnimation.animateMarkerToGB(marker_9, ubicacion, new LatLngInterpolator.Spherical());
        }
        else if(fecha_10.equals(fecha_ultimo)==false){
            marker_10.setRotation(rotacion);
            fecha_10=fecha;
            cond_10=id;
            MarkerAnimation.animateMarkerToGB(marker_10, ubicacion, new LatLngInterpolator.Spherical());
        }
        else if(fecha_11.equals(fecha_ultimo)==false){
            marker_11.setRotation(rotacion);
            fecha_11=fecha;
            cond_11=id;
            MarkerAnimation.animateMarkerToGB(marker_11, ubicacion, new LatLngInterpolator.Spherical());
        }
        else if(fecha_12.equals(fecha_ultimo)==false){
            marker_12.setRotation(rotacion);
            fecha_12=fecha;
            cond_12=id;
            MarkerAnimation.animateMarkerToGB(marker_12, ubicacion, new LatLngInterpolator.Spherical());
        }
        else if(fecha_13.equals(fecha_ultimo)==false){
            marker_13.setRotation(rotacion);
            fecha_13=fecha;
            cond_13=id;
            MarkerAnimation.animateMarkerToGB(marker_13, ubicacion, new LatLngInterpolator.Spherical());
        }
        else if(fecha_14.equals(fecha_ultimo)==false){
            marker_14.setRotation(rotacion);
            fecha_14=fecha;
            cond_14=id;
            MarkerAnimation.animateMarkerToGB(marker_14, ubicacion, new LatLngInterpolator.Spherical());
        }
        else if(fecha_15.equals(fecha_ultimo)==false){
            marker_15.setRotation(rotacion);
            fecha_15=fecha;
            cond_15=id;
            MarkerAnimation.animateMarkerToGB(marker_15, ubicacion, new LatLngInterpolator.Spherical());
        }
        else if(fecha_15.equals(fecha_ultimo)==false){
            marker_16.setRotation(rotacion);
            fecha_16=fecha;
            cond_16=id;
            MarkerAnimation.animateMarkerToGB(marker_16, ubicacion, new LatLngInterpolator.Spherical());
        }
        else if(fecha_17.equals(fecha_ultimo)==false){
            marker_17.setRotation(rotacion);
            fecha_17=fecha;
            cond_17=id;
            MarkerAnimation.animateMarkerToGB(marker_17, ubicacion, new LatLngInterpolator.Spherical());
        }
        else if(fecha_18.equals(fecha_ultimo)==false){
            marker_18.setRotation(rotacion);
            fecha_18=fecha;
            cond_18=id;
            MarkerAnimation.animateMarkerToGB(marker_18, ubicacion, new LatLngInterpolator.Spherical());
        }
        else if(fecha_19.equals(fecha_ultimo)==false){
            marker_19.setRotation(rotacion);
            fecha_19=fecha;
            cond_19=id;
            MarkerAnimation.animateMarkerToGB(marker_19, ubicacion, new LatLngInterpolator.Spherical());
        }
        else if(fecha_20.equals(fecha_ultimo)==false){
            marker_20.setRotation(rotacion);
            fecha_20=fecha;
            cond_20=id;
            MarkerAnimation.animateMarkerToGB(marker_20, ubicacion, new LatLngInterpolator.Spherical());
        }
        else if(fecha_21.equals(fecha_ultimo)==false){
            marker_21.setRotation(rotacion);
            fecha_21=fecha;
            cond_21=id;
            MarkerAnimation.animateMarkerToGB(marker_21, ubicacion, new LatLngInterpolator.Spherical());
        }
        else if(fecha_22.equals(fecha_ultimo)==false){
            marker_22.setRotation(rotacion);
            fecha_22=fecha;
            cond_22=id;
            MarkerAnimation.animateMarkerToGB(marker_22, ubicacion, new LatLngInterpolator.Spherical());
        }
        else if(fecha_23.equals(fecha_ultimo)==false){
            marker_23.setRotation(rotacion);
            fecha_23=fecha;
            cond_23=id;
            MarkerAnimation.animateMarkerToGB(marker_23, ubicacion, new LatLngInterpolator.Spherical());
        }
        else if(fecha_24.equals(fecha_ultimo)==false){
            marker_24.setRotation(rotacion);
            fecha_24=fecha;
            cond_24=id;
            MarkerAnimation.animateMarkerToGB(marker_24, ubicacion, new LatLngInterpolator.Spherical());
        }
        else if(fecha_25.equals(fecha_ultimo)==false){
            marker_25.setRotation(rotacion);
            fecha_25=fecha;
            cond_25=id;
            MarkerAnimation.animateMarkerToGB(marker_25, ubicacion, new LatLngInterpolator.Spherical());
        }



    }

    public void ocultar_conductores_no_activos()
    {
        if(fecha_1.equals(fecha_ultimo)==false){
            marker_1.setVisible(false);
        }
        if(fecha_2.equals(fecha_ultimo)==false){
            marker_2.setVisible(false);
        }
        if(fecha_3.equals(fecha_ultimo)==false){
            marker_3.setVisible(false);
        }
        if(fecha_4.equals(fecha_ultimo)==false){
            marker_4.setVisible(false);
        }
        if(fecha_5.equals(fecha_ultimo)==false){
            marker_5.setVisible(false);
        }
        if(fecha_6.equals(fecha_ultimo)==false){
            marker_6.setVisible(false);
        }
        if(fecha_7.equals(fecha_ultimo)==false){
            marker_7.setVisible(false);
        }
        if(fecha_8.equals(fecha_ultimo)==false){
            marker_8.setVisible(false);
        }
        if(fecha_9.equals(fecha_ultimo)==false){
            marker_9.setVisible(false);
        }
        if(fecha_10.equals(fecha_ultimo)==false){
            marker_10.setVisible(false);
        }
        if(fecha_11.equals(fecha_ultimo)==false){
            marker_11.setVisible(false);
        }
        if(fecha_12.equals(fecha_ultimo)==false){
            marker_12.setVisible(false);
        }
        if(fecha_13.equals(fecha_ultimo)==false){
            marker_13.setVisible(false);
        }
        if(fecha_14.equals(fecha_ultimo)==false){
            marker_14.setVisible(false);
        }
        if(fecha_15.equals(fecha_ultimo)==false){
            marker_15.setVisible(false);
        }
        if(fecha_16.equals(fecha_ultimo)==false){
            marker_16.setVisible(false);
        }
        if(fecha_17.equals(fecha_ultimo)==false){
            marker_17.setVisible(false);
        }
        if(fecha_18.equals(fecha_ultimo)==false){
            marker_18.setVisible(false);
        }
        if(fecha_19.equals(fecha_ultimo)==false){
            marker_19.setVisible(false);
        }
        if(fecha_20.equals(fecha_ultimo)==false){
            marker_20.setVisible(false);
        }
        if(fecha_21.equals(fecha_ultimo)==false){
            marker_21.setVisible(false);
        }
        if(fecha_22.equals(fecha_ultimo)==false){
            marker_22.setVisible(false);
        }
        if(fecha_23.equals(fecha_ultimo)==false){
            marker_23.setVisible(false);
        }
        if(fecha_24.equals(fecha_ultimo)==false){
            marker_24.setVisible(false);
        }
        if(fecha_25.equals(fecha_ultimo)==false){
            marker_25.setVisible(false);
        }

    }



}
