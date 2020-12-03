package com.elisoft.sao_pedido;

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
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.elisoft.sao_pedido.chat.Chat;
import com.elisoft.sao_pedido.notificaciones.SharedPrefManager;
import com.elisoft.sao_pedido.servicio.Servicio_cargar_punto_google;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
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
import com.google.android.gms.maps.model.PolylineOptions;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;

public class Principal_pedido extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks,
        LocationListener {



    int sw_acercar_a_mi_ubicacion=0;
    //Marker conductor punto


    private IntentFilter mIntentFilter;
    private static final String LOGTAG = "android-localizacion";

    private static final int PETICION_PERMISO_LOCALIZACION = 101;
    private static final int PETICION_CONFIG_UBICACION = 201;



    private GoogleApiClient apiClient;

    private LocationRequest locRequest;

    AlertDialog alert2 = null;

    LinearLayout ll_vehiculo,ll_pedido;
    ImageButton bt_delivery;


    ImageButton bt_chat;
    Button bt_cancelar;
    private GoogleMap mMap;
    ProgressDialog pDialog;
    String sid_pedido;
    Suceso suceso;
    Marker m_conductor=null;
    Marker m_pasajero=null;
    Marker m_tienda=null;
    Marker sourceMarker=null;

    int m_conductor_cantida=0;

    LatLng ultima_ubicacion = new LatLng(0, 0);
    boolean sw=true;
    double latitud = 0, longitud = 0;
    LatLng ubicacion_tienda=new LatLng(0,0);

    String referencia="",direccion="";
    int  clase_vehiculo=1, clase_vehiculo_en_pedido=1,moto=1;



    LinearLayout  ll_cancelar;
    TextView tv_mensaje_pedido,
            tv_nombre,
            tv_numero_movil,
            tv_cantidad_pedidos,
            tv_monto_carrito,
            tv_monto_envio,
            tv_monto_total;

    ImageView im_perfil, im_vehiculo;
    LinearLayout bt_ver_perfil;
    TextView rb_calificacion_conductor;
    RatingBar rb_calificacion_vehiculo;







    boolean sw_destroy=false;


    int rotacion=0;


    private AdView mAdView;


    Date fecha_conexion;


    RequestQueue queue=null;


    //INICIO MARCAR RUTA
    private List<LatLng> bangaloreRoute;
    JSONObject rutas=null;
    //FIN MARCAR RUTA


    @Override
    public void onBackPressed() {
        bt_cancelar.setEnabled(true);


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
                finish();
            }

        }
        ajuste_boton_cancelar_en_pedido();





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
        setContentView(R.layout.activity_principal_pedido);



        bt_chat= findViewById(R.id.bt_chat);
        bt_cancelar= findViewById(R.id.bt_cancelar);
        bt_delivery= findViewById(R.id.bt_delivery);
        m_conductor_cantida=0;
        //inicio layout flotante....
        ll_cancelar= findViewById(R.id.ll_cancelar);
        tv_mensaje_pedido= findViewById(R.id.tv_mensaje_pedido);
        bt_ver_perfil= findViewById(R.id.bt_ver_perfil);
        tv_nombre= findViewById(R.id.tv_nombre);
       /* tv_marca= findViewById(R.id.tv_marca);
        tv_placa= findViewById(R.id.tv_placa);
        tv_color= findViewById(R.id.tv_color);
        tv_modelo= findViewById(R.id.tv_modelo);*/
        tv_numero_movil= findViewById(R.id.tv_numero_movil);

        tv_monto_carrito=findViewById(R.id.tv_monto_carrito);
        tv_monto_envio=findViewById(R.id.tv_monto_envio);
        tv_monto_total=findViewById(R.id.tv_monto_total);

        im_perfil= findViewById(R.id.im_perfil);
        im_vehiculo= findViewById(R.id.im_vehiculo);
        ll_pedido= findViewById(R.id.ll_pedido);
        rb_calificacion_conductor= findViewById(R.id.rb_conductor);
        rb_calificacion_vehiculo= findViewById(R.id.rb_vehiculo);
        ll_vehiculo= findViewById(R.id.ll_vehiculo);
        tv_cantidad_pedidos=(TextView)findViewById(R.id.tv_cantidad_pedidos);
//fin layout flotante
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mIntentFilter = new IntentFilter();







        bt_chat.setOnClickListener(this);
        bt_cancelar.setOnClickListener(this);
        bt_ver_perfil.setOnClickListener(this);
        bt_delivery.setOnClickListener(this);

        ll_vehiculo.setOnClickListener(this);
        im_vehiculo.setOnClickListener(this);

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


//publicidad admob
        MobileAds.initialize(this, getString(R.string.id_AdMob_aplicacion));
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);




        SharedPreferences pedido = getSharedPreferences("ultimo_pedido", MODE_PRIVATE);
         sid_pedido=pedido.getString("id_pedido", "");

        try {
            int id_pedido= Integer.parseInt(pedido.getString("id_pedido",""));
            servicio_get_pedido_volley(id_pedido);
        }catch (Exception e) {
            e.printStackTrace();
        }


        fecha_conexion = new Date();
        eliminar_que_necesitas();
    }


    private  void ajuste_boton_cancelar_en_pedido(){
        SharedPreferences prefe = getSharedPreferences("ultimo_pedido", Context.MODE_PRIVATE);
        String abordo = prefe.getString("abordo", "0");
        if(abordo.equals("0")){
            ll_cancelar.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            //ll_cancelar.setVisibility(View.VISIBLE);
        }else if(abordo.equals("1")){
            ll_cancelar.setLayoutParams(new LinearLayout.LayoutParams(0,0));
            //ll_cancelar.setVisibility(View.INVISIBLE);
        }
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


        m_conductor=mMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_mot_marker))
                .position(new LatLng(0,0))
                .anchor((float)0.5,(float)0.8)
                .flat(true)
                .rotation(rotacion));

        m_pasajero=mMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_point1))
                .anchor((float) 0.5, (float) 0.8)
                .flat(true)
                .position(new LatLng(0, 0)));

        m_tienda=mMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_punto_fin_1))
                .anchor((float) 0.5, (float) 0.8)
                .flat(true)
                .position(new LatLng(0, 0))
                .title("Tienda")
                );


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



        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                //--------------- CUADRO DE INFORMACION  ---------------------------------
                View marker_view = ((LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custom_marker, null);
                TextView addressSrc = (TextView) marker_view.findViewById(R.id.addressTxt);
                TextView etaTxt = (TextView) marker_view.findViewById(R.id.etaTxt);
                etaTxt.setVisibility(View.VISIBLE);


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
                    int tiempo=Math.round(distancia/200);

                    double aux_dist=Double.valueOf(distancia) / Double.valueOf(1000);
                    String aux_dist2=String.format("%.2f",aux_dist);
                    addressSrc.setText(aux_dist2+" km");
                    //addressSrc.setText(velocidad);
                    etaTxt.setText(tiempo  +" min.");
                }else if(distancia>600)
                {
                    //m_conductor.setTitle("Llegada");
                    addressSrc.setText(distancia+" mts");
                    //addressSrc.setText(velocidad);
                    etaTxt.setText("3 min");
                }else if(distancia>300)
                {
                    //m_conductor.setTitle("Llegada");
                    addressSrc.setText(distancia+" mts");
                    //addressSrc.setText(velocidad);
                    etaTxt.setText("2 min");
                }else
                {
                    //m_conductor.setTitle("Llegada");
                    addressSrc.setText(distancia+" mts");
                    //addressSrc.setText(velocidad);
                    etaTxt.setText("1 min");
                }



                if (sourceMarker == null) {
                    //etaTxt.setVisibility(View.GONE);

                    MarkerOptions marker_opt_source = new MarkerOptions().position(ultima_ubicacion);
                    marker_opt_source.icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(getApplicationContext(), marker_view))).anchor(0.00f, 0.20f);
                    sourceMarker = mMap.addMarker(marker_opt_source);




                }else
                {
                    try {
                        sourceMarker.setIcon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(getApplicationContext(), marker_view)));

                        Point PickupPoint = mMap.getProjection().toScreenLocation(ultima_ubicacion);
                        sourceMarker.setAnchor(PickupPoint.x < dpToPx(getApplicationContext(), 200) ? 0.00f : 1.00f, PickupPoint.y < dpToPx(getApplicationContext(), 100) ? 0.20f : 1.20f);
                        sourceMarker.setPosition(ultima_ubicacion);
                        if (abordo.equals("1")) {
                            sourceMarker.setVisible(true);
                        } else {
                            sourceMarker.setVisible(false);
                        }
                    }catch (Exception ee)
                    {
                        ee.printStackTrace();
                    }
                }

            }
        });

        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                fecha_conexion = new Date();
                servicio_taxi_ruta();
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

        SharedPreferences prefe = getSharedPreferences("ultimo_pedido", Context.MODE_PRIVATE);

        if(v.getId()==R.id.bt_chat){
            SharedPreferences preferencias = getSharedPreferences("ultimo_pedido", Context.MODE_PRIVATE);
            Intent it_chat=new Intent(getApplicationContext(), Chat.class);
            it_chat.putExtra("id_conductor",preferencias.getString("id_taxi",""));
            it_chat.putExtra("titulo",preferencias.getString("nombre_taxi",""));
            startActivity(it_chat);
        }else if(v.getId()==R.id.bt_delivery)
        {
            Intent ed_c = new Intent(Principal_pedido.this ,Detalle_pedido_delivery.class);
            ed_c.putExtra("id_pedido",Integer.parseInt(prefe.getString("id_pedido","0")));
            startActivity(ed_c);
        }  else if(v.getId()==R.id.bt_cancelar)
        {
            try {

                double lat = Double.parseDouble(prefe.getString("latitud", "0"));
                double lon = Double.parseDouble(prefe.getString("longitud", "0"));
                int distancia = getDistancia(ultima_ubicacion.latitude, ultima_ubicacion.longitude, lat, lon);

                if(distancia>500) {

                    AlertDialog.Builder dialogo1 = new AlertDialog.Builder(this);
                    dialogo1.setTitle(getString(R.string.app_name));
                    dialogo1.setMessage("¿Desea cancelar el Pedido?");
                    dialogo1.setCancelable(false);
                    dialogo1.setPositiveButton("SI", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogo1, int id) {
                            //cargamos los datos

                            SharedPreferences pedido=getSharedPreferences("ultimo_pedido",MODE_PRIVATE);
                            SharedPreferences usuario = getSharedPreferences("perfil", MODE_PRIVATE);
                            String id_usuario = usuario.getString("id_usuario", "");
                            //dibuja en el mapa las taxi que estan cerca...
                            //hilo_taxi.execute(getString(R.string.servidor)+"frmTaxi.php?opcion=get_taxis_en_rango", "1","64.455","-18.533");// parametro que recibe el doinbackground
                            if(pedido.getString("id_pedido","").equals("")==false && pedido.getString("id_pedido","0").equals("0")==false) {
                                try {

                                    // hilo_taxi_cancelar=new Servicio_pedir_cancelar();
                                    // hilo_taxi_cancelar.execute(getString(R.string.servidor) + "frmPedido.php?opcion=cancelar_pedido_usuario", "1", id_usuario,pedido.getString("id_pedido",""));// parametro que recibe el doinbackground
                                    servicio_cancelar_pedido_en_camino( id_usuario,pedido.getString("id_pedido",""));

                                } catch (Exception e) {

                                }
                            }else
                            {
                                finish();
                            }

                        }
                    });
                    dialogo1.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogo1, int id) {

                        }
                    });
                    dialogo1.show();

                }else
                {
                    AlertDialog.Builder dialogo1 = new AlertDialog.Builder(this);
                    dialogo1.setTitle(getString(R.string.app_name));
                    dialogo1.setMessage("Su Taxi esta a "+distancia+" mt. de distancia. ¿Esta seguro en Cancelar su pedido?");
                    dialogo1.setCancelable(true);
                    dialogo1.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogo1, int id) {
                            dialogo1.cancel();
                        }
                    });
                    dialogo1.setPositiveButton("SI", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogo1, int id) {
                            //cargamos los datos
                            //Servicio_pedir_taxi hilo_taxi = new Servicio_pedir_taxi();
                            SharedPreferences pedido=getSharedPreferences("ultimo_pedido",MODE_PRIVATE);
                            SharedPreferences usuario = getSharedPreferences("perfil", MODE_PRIVATE);
                            String id_usuario = usuario.getString("id_usuario", "");
                            //dibuja en el mapa las taxi que estan cerca...
                            //hilo_taxi.execute(getString(R.string.servidor)+"frmTaxi.php?opcion=get_taxis_en_rango", "1","64.455","-18.533");// parametro que recibe el doinbackground
                            if(pedido.getString("id_pedido","").equals("")==false && pedido.getString("id_pedido","0").equals("0")==false) {
                                try {
                                    // hilo_taxi.execute(getString(R.string.servidor) + "frmPedido.php?opcion=cancelar_pedido_usuario", "1", id_usuario,pedido.getString("id_pedido",""));// parametro que recibe el doinbackground

                                    servicio_cancelar_pedido_en_camino( id_usuario,pedido.getString("id_pedido",""));
                                } catch (Exception e) {

                                }
                            }else
                            {
                                finish();
                            }
                        }
                    });

                    dialogo1.show();
                }
            }catch (Exception e)
            {

            }

        }


    }




    private void servicio_cancelar_pedido_en_camino(String id_usuario, String id_pedido) {


        try {
            String token= SharedPrefManager.getInstance(this).getDeviceToken();

            JSONObject jsonParam= new JSONObject();
            jsonParam.put("id_usuario",id_usuario);
            jsonParam.put("id_pedido", id_pedido);
            jsonParam.put("token", token);
            String url=getString(R.string.servidor) + "frmPedido.php?opcion=cancelar_pedido_usuario";
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
                                    Intent cancelar_pedido=new Intent(getApplicationContext(),Cancelar_pedido_usuario.class);
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
            myRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            queue.add(myRequest);


        } catch (Exception e) {

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


    private void servicio_pedir_taxi(String id,
                                     String latitud,
                                     String longitud,
                                     String nombre,
                                     String referencia,
                                     String numero_casa,
                                     String imei,
                                     String clase_vehiculo,
                                     String tipo_pedido_empresa,
                                     String direccion) {

        try {



            String token= SharedPrefManager.getInstance(this).getDeviceToken();

            JSONObject jsonParam= new JSONObject();
            jsonParam.put("id_usuario", id);
            jsonParam.put("latitud", latitud);
            jsonParam.put("longitud", longitud);
            jsonParam.put("nombre", nombre);
            jsonParam.put("indicacion", referencia);
            jsonParam.put("numero_casa", numero_casa);
            jsonParam.put("imei", imei);
            jsonParam.put("clase_vehiculo", clase_vehiculo);
            jsonParam.put("tipo_pedido_empresa", tipo_pedido_empresa);
            jsonParam.put("direccion", direccion);
            jsonParam.put("token", token);
            String url=getString(R.string.servidor) + "frmPedido.php?opcion=pedir_taxi";
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
                                    tv_mensaje_pedido.setText("Esperando la confirmación por el Taxista ...");
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
                                            Intent msgIntent = new Intent(Principal_pedido.this, Servicio_pedir_movil.class);
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
                                    String smonto_pedido = adato.getJSONObject(0).getString("monto_pedido");
                                    String smonto_total = adato.getJSONObject(0).getString("monto_total");
                                    String snombre_lugar = adato.getJSONObject(0).getString("nombre_lugar");
                                    String sdireccion_lugar = adato.getJSONObject(0).getString("direccion_lugar");
                                    String stelefono_lugar = adato.getJSONObject(0).getString("telefono_lugar");
                                    String swhatsapp_lugar = adato.getJSONObject(0).getString("whatsapp_lugar");
                                    String sdireccion_logo_lugar = adato.getJSONObject(0).getString("direccion_logo_lugar");
                                    String sdireccion_banner_lugar = adato.getJSONObject(0).getString("direccion_banner_lugar");
                                    double latitud_lugar = Double.parseDouble(adato.getJSONObject(0).getString("latitud_lugar"));
                                    double longitud_lugar = Double.parseDouble(adato.getJSONObject(0).getString("longitud_lugar"));

                                    ubicacion_tienda=new LatLng(latitud_lugar,longitud_lugar);

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
                                    editar.putString("direccion_logo_lugar",sdireccion_logo_lugar);
                                    editar.putString("nombre_lugar",snombre_lugar);

                                    editar.commit();

                                    //===========
                                    //devuelve  8
                                    //===========

                                    SharedPreferences spedido=getSharedPreferences("ultimo_pedido",MODE_PRIVATE);
                                    tv_nombre.setText(spedido.getString("nombre_taxi",""));
                                    /*tv_marca.setText(spedido.getString("marca",""));
                                    tv_placa.setText(spedido.getString("placa",""));
                                    tv_modelo.setText(spedido.getString("modelo",""));
                                    tv_color.setText(spedido.getString("color",""));
                                   */ tv_numero_movil.setText(spedido.getString("nombre_lugar",""));
                                    tv_cantidad_pedidos.setText("("+spedido.getString("cantidad_pedidos","0")+")");
                                    tv_monto_envio.setText("Envio: "+smonto_total+" Bs");
                                    tv_monto_carrito.setText("Pedido: "+smonto_pedido+" Bs");

                                    double total=Double.parseDouble(smonto_total)+Double.parseDouble(smonto_pedido);

                                    tv_monto_total.setText("Total: "+total+" Bs");


                                    getImage(spedido.getString("id_taxi",""));
                                    getImageVehiculo(spedido.getString("direccion_logo_lugar",""));

                                    try{
                                        float conductor= Float.parseFloat(pedido.getString("calificacion_conductor","0"));
                                        float vehiculo= Float.parseFloat(pedido.getString("calificacion_vehiculo","0"));
                                        rb_calificacion_conductor.setText(""+conductor);
                                        rb_calificacion_vehiculo.setRating(vehiculo);
                                    }catch (Exception e)
                                    {
                                    }


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



    public void servicio_notificacion_volley(String id_pedido,String id_usuario, String detalle){
        try {

            String token= SharedPrefManager.getInstance(this).getDeviceToken();

            JSONObject jsonParam= new JSONObject();
            jsonParam.put("token", token);
            jsonParam.put("id_pedido", id_pedido);
            jsonParam.put("id_usuario", id_usuario);
            jsonParam.put("detalle", detalle);
            String url=getString(R.string.servidor) + "frmPedido.php?opcion=enviar_notificacion_usuario";
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
                                    mensaje(suceso.getMensaje());
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
            myRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            queue.add(myRequest);


        } catch (Exception e) {
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
            rotacion= Integer.parseInt(punto_pedido.getString("rotacion","0"));
            clase_vehiculo_en_pedido=  punto_pedido.getInt("clase_vehiculo",1);
            moto=  punto_pedido.getInt("moto",1);
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
                            status.startResolutionForResult(Principal_pedido.this, PETICION_CONFIG_UBICACION);
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
        if (ActivityCompat.checkSelfPermission(Principal_pedido.this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            //Ojo: estamos suponiendo que ya tenemos concedido el permiso.
            //Sería recomendable implementar la posible petición en caso de no tenerlo.

            Log.i(LOGTAG, "Inicio de recepción de ubicaciones");

            LocationServices.FusedLocationApi.requestLocationUpdates(
                    apiClient, locRequest,Principal_pedido.this);
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
            //marker moviles
            if (sw_acercar_a_mi_ubicacion == 0) {
                sw_acercar_a_mi_ubicacion = 1;
                // mMap.clear();
            }
            //marker moviles




            LatLng fin = new LatLng(0, 0);
            //fin = ultimo_registro(Integer.parseInt(sid_pedido));
            fin=ultimo_registro();

           /*
            if(fin.latitude==0||fin.longitude==0) {
                fin=new LatLng(latitud,longitud);
            }
            */

            ultima_ubicacion=fin;

            String abordo="";

            try{
                SharedPreferences prefe = getSharedPreferences("ultimo_pedido", Context.MODE_PRIVATE);
                abordo = prefe.getString("abordo", "0");

                    //agrergado

                        SharedPreferences.Editor editor=prefe.edit();
                        editor.putString("latitud",String.valueOf(loc.getLatitude()));
                        editor.putString("longitud",String.valueOf(loc.getLongitude()));
                        editor.commit();

                        m_pasajero.setPosition(new LatLng(loc.getLatitude(),loc.getLongitude()));
                        m_tienda.setPosition(ubicacion_tienda);

                    //agregado


            }catch (Exception e)
            {

            }
            if(moto==1){

                m_conductor.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_mot_marker));
                m_conductor.setPosition(fin);
                m_conductor.setRotation(rotacion);
            }else{
                    m_conductor.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_car_marker));
                    m_conductor.setPosition(fin);
                    m_conductor.setRotation(rotacion);

                    MarkerAnimation.animateMarkerToGB(m_conductor, fin, new LatLngInterpolator.Spherical());
            }

            float mm=mMap.getCameraPosition().zoom;


            //verificamos si la ultima ubicacion esta en Cero (0,0)
            //si esta en (0,0) la camara se movia a la ubicacion de la solicitud.

            LatLng camera_ubicacion=ultima_ubicacion;
            if(camera_ubicacion.latitude==0||camera_ubicacion.longitude==0) {
                camera_ubicacion=new LatLng(latitud,longitud);
            }

            if(mm<8){
                CameraPosition cameraPosition1 = new CameraPosition.Builder()
                        .target(camera_ubicacion)      // Sets the center of the map to Mountain View
                        .zoom(15)                   // Sets the zoom
                        .bearing(0)                // Sets the orientation of the camera to east
                        .tilt(0)                   // Sets the tilt of the camera to 30 degrees
                        .build();

                mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition1));
            }else{
                mMap.moveCamera(CameraUpdateFactory.newLatLng(camera_ubicacion));
            }




            if(abordo.equals("0")){
                //datos_de_google();
                ll_cancelar.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                //ll_cancelar.setVisibility(View.VISIBLE);
            }else if(abordo.equals("1")){
                m_conductor.hideInfoWindow();
                ll_cancelar.setLayoutParams(new LinearLayout.LayoutParams(0,0));
                //ll_cancelar.setVisibility(View.INVISIBLE);
            }







        } else {
            // lblLatitud.setText("Latitud: (desconocida)");
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

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int estado_pedido=0;
            int estado_finalizo=0;
            int id_pedido=0;

            if (intent.getAction().equals(Servicio_pedir_movil.ACTION_PROGRESO)) {
                estado_pedido= intent.getIntExtra("estado",0);
                id_pedido= intent.getIntExtra("id_pedido",0);
            } else if (intent.getAction().equals(Servicio_pedir_movil.ACTION_FINAL)) {
                estado_finalizo= intent.getIntExtra("estado",0);
                Intent stopIntent = new Intent(Principal_pedido.this, Servicio_pedir_movil.class);
                stopService(stopIntent);
            }
            if(estado_pedido==1 || estado_finalizo==1){
                verificar_pedido();
            }
            // tv_titulo.setText("e.p:"+estado_pedido);
            // tv_titulo.setText("e.f:"+estado_finalizo);
        }
    };

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
            AlertDialog.Builder builder = new AlertDialog.Builder(Principal_pedido.this);
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
                                    String snombre_lugar = dato.getJSONObject(0).getString("nombre_lugar");
                                    String sdireccion_lugar = dato.getJSONObject(0).getString("direccion_lugar");
                                    String stelefono_lugar = dato.getJSONObject(0).getString("telefono_lugar");
                                    String swhatsapp_lugar = dato.getJSONObject(0).getString("whatsapp_lugar");
                                    String sdireccion_logo_lugar = dato.getJSONObject(0).getString("direccion_logo_lugar");
                                    String sdireccion_banner_lugar = dato.getJSONObject(0).getString("direccion_banner_lugar");
                                    double latitud_lugar = Double.parseDouble(dato.getJSONObject(0).getString("latitud_lugar"));
                                    double longitud_lugar = Double.parseDouble(dato.getJSONObject(0).getString("longitud_lugar"));



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
                                    editar.putString("direccion_logo_lugar",sdireccion_logo_lugar);
                                    editar.putString("nombre_lugar",snombre_lugar);
                                    editar.commit();

                                    SharedPreferences proceso = getSharedPreferences("pedido_en_proceso", MODE_PRIVATE);
                                    SharedPreferences.Editor editor = proceso.edit();
                                    editor.putString("id_pedido", "");
                                    editor.commit();


                                    ///////////////////// 8 final //////////////////

                                    //verificar si alguien acepto el pedido.

                                    Intent intent = new Intent(Principal_pedido.this, Servicio_pedido.class);
                                    intent.setAction(Constants.ACTION_RUN_ISERVICE);
                                    startService(intent);



                                    SharedPreferences spedido=getSharedPreferences("ultimo_pedido",MODE_PRIVATE);
                                    tv_nombre.setText(spedido.getString("nombre_taxi",""));
                                  /*  tv_marca.setText(spedido.getString("marca",""));
                                    tv_placa.setText(spedido.getString("placa",""));
                                    tv_modelo.setText(spedido.getString("modelo",""));
                                    tv_color.setText(spedido.getString("color",""));*/
                                    tv_numero_movil.setText(spedido.getString("nombre_lugar",""));
                                    tv_cantidad_pedidos.setText("("+spedido.getString("cantidad_pedidos","0")+")");
/*
                Intent servicio_contacto = new Intent(Principal_pedido.this, Servicio_guardar_contacto.class);
                servicio_contacto.setAction(Constants.ACTION_RUN_ISERVICE);
                servicio_contacto.putExtra("nombre",pedido.getString("nombre_taxi", ""));
                servicio_contacto.putExtra("telefono",pedido.getString("celular", ""));
                startService(servicio_contacto);
*/
                                    try{
                                        float conductor= Float.parseFloat(spedido.getString("calificacion_conductor","0"));
                                        float vehiculo= Float.parseFloat(spedido.getString("calificacion_vehiculo","0"));
                                        rb_calificacion_conductor.setText(""+conductor);
                                        rb_calificacion_vehiculo.setRating(vehiculo);
                                    }catch (Exception e)
                                    {

                                    }


                                    getImage(spedido.getString("id_taxi",""));
                                    getImageVehiculo(spedido.getString("direccion_logo_lugar",""));



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

    private void getImage(String id)//
    {
        String  url=  getString(R.string.servidor_web)+"public/Imagen_Conductor/Perfil-"+id+".png";
        Picasso.with(this).load(url).into(target);
    }

    Target target = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {

            Drawable dw = new BitmapDrawable(getResources(), bitmap);
            //se edita la imagen para ponerlo en circulo.

            if( bitmap==null)
            { dw = getResources().getDrawable(R.drawable.ic_perfil_negro);}

            imagen_circulo(dw,im_perfil);
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {

        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

        }
    };


    private void getImageVehiculo(String direccionImagen)//
    {
        //String  url=  getString(R.string.servidor_web)+"public/Imagen_Conductor/Perfil-"+id+".png";
        String  url=  getString(R.string.servidor_web)+"storage/"+direccionImagen.replace("../storage/","");
        Picasso.with(this).load(url).into(targetVehiculo);
    }

    Target targetVehiculo = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {

            Drawable dw = new BitmapDrawable(getResources(), bitmap);
            //se edita la imagen para ponerlo en circulo.

            if( bitmap==null)
            { dw = getResources().getDrawable(R.drawable.ic_vehiculo_negro);}

            imagen_circulo(dw,im_vehiculo);
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {

        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

        }
    };

    public void imagen_circulo(Drawable id_imagen, ImageView imagen) {
        Bitmap originalBitmap = ((BitmapDrawable) id_imagen).getBitmap();
        if (originalBitmap.getWidth() > originalBitmap.getHeight()) {
            originalBitmap = Bitmap.createBitmap(originalBitmap, 0, 0, originalBitmap.getHeight(), originalBitmap.getHeight());
        } else if (originalBitmap.getWidth() < originalBitmap.getHeight()) {
            originalBitmap = Bitmap.createBitmap(originalBitmap, 0, 0, originalBitmap.getWidth(), originalBitmap.getWidth());
        }

//creamos el drawable redondeado
        RoundedBitmapDrawable roundedDrawable =
                RoundedBitmapDrawableFactory.create(getResources(), originalBitmap);

//asignamos el CornerRadius
        roundedDrawable.setCornerRadius(originalBitmap.getWidth());
        try {
            imagen.setImageDrawable(roundedDrawable);
        }catch (Exception e)
        {

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




    public void verificar_permiso_camara()
    {
        final String[] CAMERA_PERMISSIONS = { Manifest.permission.INTERNET,
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_NETWORK_STATE };

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
            //YA LO CANCELE Y VOUELVO A PERDIR EL PERMISO.

            AlertDialog.Builder dialogo1 = new AlertDialog.Builder(this);
            dialogo1.setTitle("Atención!");
            dialogo1.setMessage("Debes otorgar permisos de acceso a CAMARA.");
            dialogo1.setCancelable(false);
            dialogo1.setPositiveButton("Solicitar permiso", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogo1, int id) {
                    dialogo1.cancel();
                    ActivityCompat.requestPermissions(Principal_pedido.this,
                            CAMERA_PERMISSIONS,
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
            ActivityCompat.requestPermissions(Principal_pedido.this,
                    CAMERA_PERMISSIONS,
                    1);
        }
    }

    public void verificar_permiso_almacenamiento()
    {
        final String[] PERMISSIONS = { Manifest.permission.INTERNET,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_NETWORK_STATE };

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            //YA LO CANCELE Y VOUELVO A PERDIR EL PERMISO.

            AlertDialog.Builder dialogo1 = new AlertDialog.Builder(this);
            dialogo1.setTitle("Atención!");
            dialogo1.setMessage("Debes otorgar permisos de acceso a ALMACENAMIENTO.");
            dialogo1.setCancelable(false);
            dialogo1.setPositiveButton("Solicitar permiso", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogo1, int id) {
                    dialogo1.cancel();
                    ActivityCompat.requestPermissions(Principal_pedido.this,
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
            ActivityCompat.requestPermissions(Principal_pedido.this,
                    PERMISSIONS,
                    1);
        }
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
                    ActivityCompat.requestPermissions(Principal_pedido.this,
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
            ActivityCompat.requestPermissions(Principal_pedido.this,
                    PERMISSIONS,
                    1);
        }
    }

    public void eliminar_servicio_cargar_ubicacion(){
        Intent stopIntent = new Intent(Principal_pedido.this, Servicio_cargar_punto_google.class);
        stopIntent.setAction(Constants.ACTION.STOP_ACTION);
        stopService(stopIntent);
    }


    public void servicio_taxi_ruta()
    {
        SharedPreferences prefe = getSharedPreferences("ultimo_pedido", Context.MODE_PRIVATE);
      double lat=Double.parseDouble(prefe.getString("latitud","0"));
      double lon=Double.parseDouble(prefe.getString("longitud","0"));

        //agrergado
        try {
            JSONObject jsonParam= new JSONObject();

            String url="https://maps.googleapis.com/maps/api/directions/json?origin=" + ubicacion_tienda.latitude + "," + ubicacion_tienda.longitude + "&destination=" + lat + "," + lon + "&mode=driving&key="+getString(R.string.google_api_key);
            if (queue == null) {
                queue = Volley.newRequestQueue(this);
            }


            JsonObjectRequest myRequest= new JsonObjectRequest(
                    Request.Method.POST,
                    url,
                    jsonParam,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject respuestaJSON) {
                            String metros,minuto,normal,de_lujo,con_aire,maletero,pedido,reserva,moto,moto_pedido;
                            try {
                                rutas= new JSONObject(respuestaJSON.toString());//Creo un JSONObject a partir del
                                //final
                                dibujar_ruta(rutas);
                            } catch (JSONException e) {
                                e.printStackTrace();

                                    mensaje_error("No pudimos conectarnos al servidor.\nVuelve a intentarlo.");
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


            myRequest.setRetryPolicy(new DefaultRetryPolicy(10000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            queue.add(myRequest);
        } catch (Exception e) {
            pDialog.cancel();
        }
    }



    public void dibujar_ruta(JSONObject jObject){

        //DIBUJAR ANIMACION
        //createRoute();
        // startAnim();



        if (bangaloreRoute == null) {
            bangaloreRoute = new ArrayList<>();
        } else {
            bangaloreRoute.clear();
        }


        String tiempo="";
        JSONArray jRoutes = null;
        JSONArray jLegs = null;
        JSONArray jSteps = null;
        LatLng punto=new LatLng(0,0);
        PolylineOptions polylineOptions = new PolylineOptions();

        try {

            jRoutes = jObject.getJSONArray("routes");

            /** Traversing all routes */
            for(int i=0;i<jRoutes.length();i++){
                jLegs = ( (JSONObject)jRoutes.get(i)).getJSONArray("legs");
                /** Traversing all legs */
                for(int j=0;j<jLegs.length();j++){
                    jSteps = ( (JSONObject)jLegs.get(j)).getJSONArray("steps");
                    /** Traversing all steps */
                    for(int k=0;k<jSteps.length();k++){
                        String polyline = "";
                        polyline = (String)((JSONObject)((JSONObject)jSteps.get(k)).get("polyline")).get("points");
                        List<LatLng> list = decodePoly(polyline);

                        /** Traversing all points */
                        for(int l=0;l<list.size();l++){
                            double lat= list.get(l).latitude;
                            double lon= list.get(l).longitude;
                            punto = new LatLng(lat, lon);
                            polylineOptions.add(punto);

                            //PUNTO AGREGADO
                            bangaloreRoute.add(punto);

                        }
                    }

                    tiempo=(String)((JSONObject)((JSONObject)jLegs.get(j)).get("duration")).get("text");
                }
            }




            //    mMap.addPolyline(polylineOptions.width(8).color(Color.BLACK));

        } catch (JSONException e) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
            dibujar_ruta(rutas);
            e.printStackTrace();
        }catch (Exception e){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
            dibujar_ruta(rutas);
        }


        startAnim();
    }
    private void startAnim() {
        if (mMap != null && bangaloreRoute.size()>1) {
            MapAnimator.getInstance().animateRoute(mMap, bangaloreRoute);
        } else {
            //Toast.makeText(getApplicationContext(), "No hay ruta", Toast.LENGTH_LONG).show();
        }
    }

    private List<LatLng> decodePoly(String encoded) {

        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }

    private void eliminar_que_necesitas() {
        SharedPreferences carrito = getSharedPreferences(getString(R.string.sql_pedido), MODE_PRIVATE);
        SharedPreferences.Editor ed_c = carrito.edit();
        ed_c.putString("que_necesitas", "");
        ed_c.putString("direccion_recoger","");
        ed_c.putString("direccion_entrega","");
        ed_c.putString("latitud_recoger","");
        ed_c.putString("longitud_recoger","");
        ed_c.putString("monto_envio","");
        ed_c.commit();
    }

}
