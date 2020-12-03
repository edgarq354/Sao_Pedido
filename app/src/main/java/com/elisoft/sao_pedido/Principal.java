package com.elisoft.sao_pedido;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
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
import com.elisoft.sao_pedido.SqLite.AdminSQLiteOpenHelper;
import com.elisoft.sao_pedido.categoria.CCategoria;
import com.elisoft.sao_pedido.categoria.RecyclerViewAdapterCategoria;
import com.elisoft.sao_pedido.empresa.CEmpresa;
import com.elisoft.sao_pedido.empresa.EmpresaAdapter;
import com.elisoft.sao_pedido.historial_notificacion.Notificacion;
import com.elisoft.sao_pedido.loquesea.QueNecesitas;
import com.elisoft.sao_pedido.mi_perfil.Perfil_pasajero;
import com.elisoft.sao_pedido.notificaciones.SharedPrefManager;
import com.elisoft.sao_pedido.producto.Productos;
import com.elisoft.sao_pedido.viajes.Viajes;
import com.facebook.login.LoginManager;
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
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class Principal extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        AdapterView.OnItemClickListener,
        OnMapReadyCallback,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks,
        LocationListener
{
    boolean doubleBackToExitPressedOnce = false;
//VARIABLES DE UBICACION
    private GoogleMap mMap;
    private static final String LOGTAG = "android-localizacion";
    private static final int PETICION_PERMISO_LOCALIZACION = 101;
    private static final int PETICION_CONFIG_UBICACION = 201;

    private GoogleApiClient apiClient;
    private LocationRequest locRequest;
    double latitud=0;
    double longitud=0;
// FIN  VARIABLE DE UBICACIONES


    ArrayList<CCategoria> categoria;
    ArrayList<CEmpresa> empresa;
    RecyclerView rv_categoria;
    Suceso suceso;
    GridView gridview;


    AlertDialog alert2 = null;
    int version=0;
    RequestQueue queue=null;

    int sw_obtener_ubicacion_primera=0;

    Button bt_lo_que_deseas;

    int id_categoria_general=0;

    private void runLayoutAnimation( RecyclerView recyclerView) {
        final Context context = recyclerView.getContext();
        final LayoutAnimationController controller =
                AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation);

        recyclerView.setLayoutAnimation(controller);
        recyclerView.getAdapter().notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();
    }

    private void runLayoutAnimation2( GridView gridView) {
        final Context context = gridView.getContext();
        final LayoutAnimationController controller =
                AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation2);

        gridview.setLayoutAnimation(controller);
        gridview.scheduleLayoutAnimation();
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Porfavor presione dos veces para salir", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sw_obtener_ubicacion_primera=0;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);
        Toolbar toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        bt_lo_que_deseas=findViewById(R.id.bt_lo_que_deseas);

        bt_lo_que_deseas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Principal.this, QueNecesitas.class));
            }
        });

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            public void onDrawerOpened(View drawerView) {
                // carga los datos de su perfil. al momento de deslizar el menu. de perfil....
                TextView nombre, celular;
                de.hdodenhof.circleimageview.CircleImageView perfil;
                nombre = drawerView.findViewById(R.id.nombre_completo);
                celular = drawerView.findViewById(R.id.celular);
                perfil = drawerView.findViewById(R.id.perfil);
                try {
                    SharedPreferences prefe = getSharedPreferences("perfil", MODE_PRIVATE);
                    nombre.setText(prefe.getString("nombre", "") + " " + prefe.getString("apellido", ""));
                    celular.setText("+591 " + prefe.getString("celular", ""));
                    imagen_en_vista(perfil);

                    Intent intent = new Intent(Principal.this, Servicio_descargar_imagen_perfil.class);
                    intent.setAction(Constants.ACTION_RUN_ISERVICE);
                    intent.putExtra("id_usuario",Integer.parseInt(prefe.getString("id_usuario", "0")));
                    startService(intent);

                } catch (Exception e) {
                }
            }
        };
        drawer.setDrawerListener(toggle);
        toggle.syncState();


        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        rv_categoria = findViewById(R.id.recyclerView);



/*
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(layoutManager);
*/


        //Initialize Grid View for programming
        gridview = (GridView) findViewById(R.id.gridView);





        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        apiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .addOnConnectionFailedListener(this)
                .build();
        enableLocationUpdates();



        //Add Listener For Grid View Item Click
        gridview.setOnItemClickListener(this);


        cargar_categoria_en_la_lista("");




        try {
            Bundle bundle=getIntent().getExtras();
            int finalizo_solicitud_pedido=bundle.getInt("finalizo_solicitud_pedido",0);
            if(finalizo_solicitud_pedido==1){
                AlertDialog.Builder builder = new AlertDialog.Builder(Principal.this);
                builder.setTitle("Ups");
                builder.setCancelable(false);
                builder.setMessage("Parece que ninguno de núestros conductores a logrado aceptar tu solicitud. Profavor vuelve a intentarlo.");
                builder.create();
                builder.setPositiveButton("OK",  null);
                builder.show();
            }
        }catch (Exception e){

        }


    }

    @Override
    public boolean onSupportNavigateUp() {
        return  super.onSupportNavigateUp();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();
        boolean isWhatsapp = appInstalledOrNot("com.whatsapp");
        switch (id){
            case R.id.it_perfil:
                Intent perfil = new Intent(this, Perfil_pasajero.class);
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    verificar_permiso_camara();
                }
                else if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    verificar_permiso_almacenamiento();
                } else {  startActivity(perfil);   }

                break;
            case R.id.it_notificaciones:
                startActivity(new Intent(this, Notificacion.class));
                break;
            case R.id.it_historial:
                startActivity(new Intent(this, Viajes.class));
                break;
            case R.id.it_compartir:
                if (isWhatsapp)
                    AbrirWhatsApp();
                break;

            case R.id.it_registrar_negocio:

                if (isWhatsapp)
                    AbrirWhatsApp_registrate();
                break;
        }
        return false;
    }

    private boolean appInstalledOrNot(String uri) {
        PackageManager pm = getPackageManager();
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
        }

        return false;
    }

    void AbrirWhatsApp() {

        SharedPreferences perfil=getSharedPreferences("perfil",MODE_PRIVATE);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        int codigoc= Integer.parseInt("1999"+perfil.getString("id_usuario",""));
        String codigo= Integer.toString(codigoc, 16);


        PackageManager packageManager = getPackageManager();
        Intent i = new Intent(Intent.ACTION_VIEW);
        String mensaje="He probado "+getString(R.string.app_name)+" una app para pedir comida sin moverte y sin complicaciones, prueba ahora tu tambien y vive la experiencia traigo.  https://play.google.com/store/apps/details?id=com.elisoft.traigo&hl=es";
        try {
            String url = "https://wa.me/?text=" + URLEncoder.encode(mensaje, "UTF-8");
            i.setPackage("com.whatsapp");
            i.setData(Uri.parse(url));
            if (i.resolveActivity(packageManager) != null) {
                startActivity(i);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    void AbrirWhatsApp_registrate() {

        SharedPreferences perfil=getSharedPreferences("perfil",MODE_PRIVATE);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);




        PackageManager packageManager = getPackageManager();
        Intent i = new Intent(Intent.ACTION_VIEW);
        String mensaje="Hola "+getString(R.string.app_name)+". Deseo registrar mi negocio!!";
        try {
            String url = "https://wa.me/"+getString(R.string.whatsapp)+"/?text=" + URLEncoder.encode(mensaje, "UTF-8");
            i.setPackage("com.whatsapp");
            i.setData(Uri.parse(url));
            if (i.resolveActivity(packageManager) != null) {
                startActivity(i);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        SharedPreferences carrito = getSharedPreferences(getString(R.string.sql_pedido), MODE_PRIVATE);
        CEmpresa cEmpresa= empresa.get(position);

        if(Double.parseDouble(carrito.getString("monto_total","0"))>0)
        {
            CEmpresa lugar=null;
            lugar=obtener_empresa( carrito.getInt("id_empresa",0));
            if(lugar==null){
                eliminar_carrito();
            }

            if(carrito.getInt("id_empresa",0)==cEmpresa.getId()|| lugar==null)
            {
                guardar_share_pedido(
                        String.valueOf(cEmpresa.getLatitud()),
                        String.valueOf(cEmpresa.getLongitud()),
                        String.valueOf(latitud),
                        String.valueOf(longitud),
                        cEmpresa.getId(),
                        cEmpresa.getId_categoria(),
                        cEmpresa.getTiempo_preparacion(),
                        cEmpresa.getNombre(),
                        cEmpresa.getDireccion_logo(),
                        cEmpresa.getDireccion_banner(),
                        cEmpresa.getAbierto_cerrado(),
                        cEmpresa.getMonto_minimo(),
                        cEmpresa.getWhatsapp(),
                        cEmpresa.getSolicitud_whatsapp(),
                        cEmpresa.getSolicitud_aplicacion()
                );
                Intent producto=new Intent(this, Productos.class);
                producto.putExtra("id_empresa",cEmpresa.getId());
                producto.putExtra("id_categoria",cEmpresa.getId_categoria());
                producto.putExtra("tiempo_preparacion",cEmpresa.getTiempo_preparacion());
                startActivity(producto);
            }
            else {
                mensaje_formulario(cEmpresa);
            }
        }else{
            guardar_share_pedido(
                    String.valueOf(cEmpresa.getLatitud()),
                    String.valueOf(cEmpresa.getLongitud()),
                    String.valueOf(latitud),
                    String.valueOf(longitud),
                    cEmpresa.getId(),
                    cEmpresa.getId_categoria(),
                    cEmpresa.getTiempo_preparacion(),
                    cEmpresa.getNombre(),
                    cEmpresa.getDireccion_logo(),
                    cEmpresa.getDireccion_banner(),
                    cEmpresa.getAbierto_cerrado(),
                    cEmpresa.getMonto_minimo(),
                    cEmpresa.getWhatsapp(),
                    cEmpresa.getSolicitud_whatsapp(),
                    cEmpresa.getSolicitud_aplicacion()
            );

            Intent producto=new Intent(this, Productos.class);
            producto.putExtra("id_empresa",cEmpresa.getId());
            producto.putExtra("id_categoria",cEmpresa.getId_categoria());
            producto.putExtra("tiempo_preparacion",cEmpresa.getTiempo_preparacion());
            startActivity(producto);
        }


    }

    private void mensaje_formulario(final CEmpresa cEmpresa) {


        // get prompts.xml view
        LayoutInflater layoutInflater = LayoutInflater.from(Principal.this);
        View promptView = layoutInflater.inflate(R.layout.formulario_tiene_pedido, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Principal.this);
        alertDialogBuilder.setView(promptView);

        Button br_agrergar= (Button) promptView.findViewById(R.id.bt_ver_pedido);
        Button bt_eliminar= (Button) promptView.findViewById(R.id.bt_eliminar_pedido);

        br_agrergar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alert2.cancel();
                SharedPreferences carrito = getSharedPreferences(getString(R.string.sql_pedido), MODE_PRIVATE);
                CEmpresa lugar=null;
                lugar=obtener_empresa( carrito.getInt("id_empresa",0));

                guardar_share_pedido(
                        String.valueOf(lugar.getLatitud()),
                        String.valueOf(lugar.getLongitud()),
                        String.valueOf(latitud),
                        String.valueOf(longitud),
                        lugar.getId(),
                        lugar.getId_categoria(),
                        lugar.getTiempo_preparacion(),
                        lugar.getNombre(),
                        lugar.getDireccion_logo(),
                        lugar.getDireccion_banner(),
                        lugar.getAbierto_cerrado(),
                        lugar.getMonto_minimo(),
                        lugar.getWhatsapp(),
                        lugar.getSolicitud_whatsapp(),
                        lugar.getSolicitud_aplicacion()
                );


                Intent producto=new Intent(Principal.this, Productos.class);
                producto.putExtra("id_empresa",carrito.getInt("id_empresa",0));
                producto.putExtra("id_categoria",carrito.getInt("id_categoria",0));
                producto.putExtra("tiempo_preparacion",carrito.getInt("tiempo_preparacion",0));
                startActivity(producto);
            }
        });

        bt_eliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                eliminar_carrito_saltar_producto(cEmpresa,latitud,longitud);

                alert2.cancel();

            }
        });


        // create an alert dialog
        alert2 = alertDialogBuilder.create();
        alert2.show();
    }

    private void eliminar_carrito_saltar_producto(CEmpresa cEmpresa, double latitud, double longitud) {
        //eliminar pedido anterior
        SharedPreferences carrito = getSharedPreferences(getString(R.string.sql_pedido), MODE_PRIVATE);
        SharedPreferences.Editor ed_ca = carrito.edit();
        ed_ca.putInt("id_lugar", 0);
        ed_ca.putInt("id_empresa", 0);
        ed_ca.putInt("id_categoria", 0);
        ed_ca.putString("nombre_categoria", "");
        ed_ca.putString("monto_total","0");
        ed_ca.commit();


        try {
            AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, getString(R.string.nombre_sql), null, Integer.parseInt(getString(R.string.version_sql)));
            SharedPreferences pedido = getSharedPreferences(getString(R.string.sql_pedido), MODE_PRIVATE);
            SQLiteDatabase db = admin.getWritableDatabase();
            db.execSQL("delete from carrito where   id_pedido=" + pedido.getInt("id", 0));
            db.close();
        }catch (Exception e)
        {
            Log.e("Vaciar base",""+e);
        }



        //registrar datos del nuevo pedido

        SharedPreferences.Editor ed_c = carrito.edit();
        ed_c.putInt("id_empresa", cEmpresa.getId());
        ed_c.putInt("id_categoria", cEmpresa.getId_categoria());
        ed_c.putString("latitud",String.valueOf(cEmpresa.getLongitud()));
        ed_c.putString("longitud",String.valueOf(cEmpresa.getLongitud()));
        ed_c.putString("milatitud",String.valueOf(latitud));
        ed_c.putString("milongitud",String.valueOf(longitud));
        ed_c.putInt("tiempo_preparacion",cEmpresa.getTiempo_preparacion());
        ed_c.putString("nombre_lugar", cEmpresa.getNombre());
        ed_c.putString("direccion_logo_lugar",cEmpresa.getDireccion_logo());
        ed_c.putString("direccion_banner_lugar",cEmpresa.getDireccion_banner());
        ed_c.putString("abierto_cerrado",cEmpresa.getAbierto_cerrado());
        ed_c.putString("monto_minimo",cEmpresa.getMonto_minimo());
        ed_c.putString("whatsapp",cEmpresa.getWhatsapp());
        ed_c.commit();

        //abrir la empresa

        Intent producto=new Intent(Principal.this, Productos.class);
        producto.putExtra("id_empresa",cEmpresa.getId());
        producto.putExtra("id_categoria",cEmpresa.getId_categoria());
        producto.putExtra("tiempo_preparacion",cEmpresa.getTiempo_preparacion());

        startActivity(producto);
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
                    ActivityCompat.requestPermissions(Principal.this,
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
            ActivityCompat.requestPermissions(Principal.this,
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
                    ActivityCompat.requestPermissions(Principal.this,
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
            ActivityCompat.requestPermissions(Principal.this,
                    PERMISSIONS,
                    1);
        }
    }



    public void servicio_lista_de_categoria_delivery(){
        try {



            JSONObject jsonParam= new JSONObject();
            jsonParam.put("latitud", latitud);
            jsonParam.put("longitud", longitud);

            String url=getString(R.string.servidor)+"frmDelivery.php?opcion=lista_de_categoria_delivery_por_ubicacion";
            RequestQueue queue = Volley.newRequestQueue(this);


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
                                    vaciar_categoria();

                                    JSONArray usu = respuestaJSON.getJSONArray("lista");
                                    for (int i = 0; i < usu.length(); i++) {
                                        int id = Integer.parseInt(usu.getJSONObject(i).getString("id"));
                                        int estado_pedido = Integer.parseInt(usu.getJSONObject(i).getString("estado_pedido"));
                                        String nombre = usu.getJSONObject(i).getString("nombre");
                                        String direccion_imagen = usu.getJSONObject(i).getString("direccion_imagen");
                                        cargar_lista_en_categoria(id, nombre,direccion_imagen,estado_pedido);
                                    }


                                    cargar_categoria_en_la_lista("");

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
                    error.printStackTrace();
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


            queue.add(myRequest);


        } catch (Exception e) {
        }
    }

    @Override
    protected void onRestart() {
        if(latitud!=0)
        {
            if(id_categoria_general>0){
                servicio_lista_empresa(id_categoria_general);
            }

        }

        super.onRestart();
    }

    public void servicio_lista_empresa(final int id_categoria){
        try {



            JSONObject jsonParam= new JSONObject();
            jsonParam.put("id_categoria", id_categoria);
            jsonParam.put("latitud", latitud);
            jsonParam.put("longitud", longitud);

            String url=getString(R.string.servidor)+"frmGuia_turistica.php?opcion=lista_de_lugar_delivery";
            RequestQueue queue = Volley.newRequestQueue(this);


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
                                    vaciar_empresa();

                                    JSONArray usu = respuestaJSON.getJSONArray("lista");
                                    for (int i = 0; i < usu.length(); i++) {
                                        int id = Integer.parseInt(usu.getJSONObject(i).getString("id"));
                                        int estado_pedido = Integer.parseInt(usu.getJSONObject(i).getString("estado_pedido"));
                                        String nombre = usu.getJSONObject(i).getString("nombre");
                                        String direccion = usu.getJSONObject(i).getString("direccion");
                                        String telefono = usu.getJSONObject(i).getString("telefono");
                                        String whatsapp = usu.getJSONObject(i).getString("whatsapp");
                                        String latitud = usu.getJSONObject(i).getString("latitud");
                                        String longitud = usu.getJSONObject(i).getString("longitud");
                                        int estado = Integer.parseInt(usu.getJSONObject(i).getString("estado"));
                                        int id_categoria = Integer.parseInt(usu.getJSONObject(i).getString("id_categoria"));
                                        String direccion_logo = usu.getJSONObject(i).getString("direccion_logo");
                                        String direccion_banner = usu.getJSONObject(i).getString("direccion_banner");
                                        int distancia = Integer.parseInt(usu.getJSONObject(i).getString("distancia"));
                                        String facebook = usu.getJSONObject(i).getString("facebook");
                                        String informacion= usu.getJSONObject(i).getString("informacion");
                                        int tiempo_preparacion = Integer.parseInt(usu.getJSONObject(i).getString("tiempo_preparacion"));
                                        String abierto_cerrado= usu.getJSONObject(i).getString("abierto_cerrado");
                                        String monto_minimo= usu.getJSONObject(i).getString("monto_minimo");
                                        String solicitud_whatsapp= usu.getJSONObject(i).getString("solicitud_whatsapp");
                                        String solicitud_aplicacion= usu.getJSONObject(i).getString("solicitud_aplicacion");
                                        cargar_lista_en_empresa(id,
                                                nombre,
                                                direccion,
                                                telefono,
                                                whatsapp,
                                                latitud,
                                                longitud,
                                                estado,
                                                id_categoria,
                                                direccion_logo,
                                                direccion_banner,
                                                distancia,
                                                facebook,
                                                informacion,
                                                estado_pedido,
                                                tiempo_preparacion,
                                                abierto_cerrado,
                                                monto_minimo,
                                                solicitud_whatsapp,
                                                solicitud_aplicacion
                                                );
                                    }

                                    cargar_empresa_en_la_lista("",id_categoria);

                                }
                                else
                                {
                                    cargar_empresa_en_la_lista("",id_categoria);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();

                            }

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
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


            queue.add(myRequest);


        } catch (Exception e) {
        }
    }

    public void cargar_empresa_en_la_lista(String nombre,int id_categoria)
    {

        empresa= new ArrayList<CEmpresa>();
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, getString(R.string.nombre_sql), null, Integer.parseInt(getString(R.string.version_sql)));

        SQLiteDatabase bd = admin.getWritableDatabase();
        Cursor fila = bd.rawQuery("select * from lugar where    nombre LIKE '%"+nombre+"%' and id_categoria='"+id_categoria+"' ORDER BY nombre ASC", null);

        if (fila.moveToFirst()) {  //si ha devuelto 1 fila, vamos al primero (que es el unico)

            do {
                if (fila.getInt(13)==1) {
                    CEmpresa hi =new  CEmpresa(
                            fila.getInt(0),
                            fila.getString(1),
                            fila.getString(2),
                            fila.getString(3),
                            fila.getString(4),
                            fila.getString(5),
                            fila.getString(6),
                            fila.getInt(7),
                            fila.getInt(8),
                            fila.getString(9),
                            fila.getString(14),
                            fila.getInt(10),
                            fila.getString(11),
                            fila.getString(12),
                            fila.getInt(13),
                            fila.getInt(15),
                            fila.getString(16),
                            fila.getString(17),
                            fila.getString(18),
                            fila.getString(19)
                            );
                    empresa.add(hi);

                }
            } while(fila.moveToNext());

        } else
            //   Toast.makeText(this, "No hay registrados",Toast.LENGTH_SHORT).show();

            bd.close();



        //Connect DataSet to Adapter
        EmpresaAdapter empresaAdapter = new EmpresaAdapter(this, empresa);

        //Now Connect Adapter To GridView
        gridview.setAdapter(empresaAdapter);
        runLayoutAnimation2(gridview);

    }


    public void cargar_categoria_en_la_lista(String nombre)
    {
        int id_categoria=0;
        categoria= new ArrayList<CCategoria>();

        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, getString(R.string.nombre_sql), null, Integer.parseInt(getString(R.string.version_sql)));

        SQLiteDatabase bd = admin.getWritableDatabase();
        Cursor fila = bd.rawQuery("select * from categoria where nombre like '%"+nombre+"%'   ", null);

        if (fila.moveToFirst()) {  //si ha devuelto 1 fila, vamos al primero (que es el unico)
            id_categoria=fila.getInt(0);
            do {
                if(fila.getString(3).equals("1"))
                {
                    CCategoria hi =new CCategoria(fila.getInt(0),fila.getString(1),fila.getString(2),fila.getInt(3));
                    categoria.add(hi);
                    // VERIFICA SI SE REGISTRO UN PEDIDO CON PRODUCTOS EN EL CARRITO.
                }

            } while(fila.moveToNext());

        }
        //   Toast.makeText(this, "No hay registrados",Toast.LENGTH_SHORT).show();

        bd.close();


        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.HORIZONTAL);
        rv_categoria.setLayoutManager(llm);
        RecyclerViewAdapterCategoria adapter = new RecyclerViewAdapterCategoria(this, categoria);
        adapter.setOnItemClickListener(new RecyclerViewAdapterCategoria.ClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                Log.d("Categoria", "onItemClick position: " + position);
                CCategoria aux_categoria=categoria.get(position);
                id_categoria_general=aux_categoria.getId();
                servicio_lista_empresa(aux_categoria.getId());
            }
        });

        rv_categoria.setAdapter(adapter);
        runLayoutAnimation(rv_categoria);


        //carga las empresas de la primera categoria
        if(id_categoria>0)
        {
            id_categoria_general=id_categoria;
            servicio_lista_empresa(id_categoria);
        }

    }

    private void cargar_lista_en_categoria(int id, String nombre,String direccion_imagen,int estado_pedido) {
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, getString(R.string.nombre_sql), null, Integer.parseInt(getString(R.string.version_sql)));

        SQLiteDatabase bd = admin.getWritableDatabase();
        ContentValues registro = new ContentValues();
        registro.put("id", id);
        registro.put("nombre",nombre);
        registro.put("direccion_imagen",direccion_imagen);
        registro.put("estado_pedido",estado_pedido);
        bd.insert("categoria", null, registro);
        bd.close();
    }

    private void vaciar_categoria() {
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, getString(R.string.nombre_sql), null, Integer.parseInt(getString(R.string.version_sql)));

        SQLiteDatabase db = admin.getWritableDatabase();
        db.execSQL("delete from categoria");
        db.close();
        Log.e("sqlite ", "vaciar categoria");
    }

    private void vaciar_empresa() {
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, getString(R.string.nombre_sql), null, Integer.parseInt(getString(R.string.version_sql)));

        SQLiteDatabase db = admin.getWritableDatabase();
        db.execSQL("delete from lugar");
        db.close();
        Log.e("sqlite ", "vaciar empresa");
    }



    private void cargar_lista_en_empresa(int id,
                                           String nombre,
                                           String direccion,
                                           String telefono,
                                           String whatsapp,
                                           String latitud,
                                           String longitud,
                                           int estado,
                                           int id_categoria,
                                           String direccion_logo,
                                           String direccion_banner,
                                           int distancia,
                                           String facebook,
                                           String informacion,
                                           int estado_pedido,
                                           int tiempo_preparacion,
                                           String abierto_cerrado,
                                            String monto_minimo,
                                            String solicitud_whatsapp,
                                            String solicitud_aplicacion
                                         ) {
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, getString(R.string.nombre_sql), null, Integer.parseInt(getString(R.string.version_sql)));

        SQLiteDatabase bd = admin.getWritableDatabase();
        ContentValues registro = new ContentValues();
        registro.put("id", id);
        registro.put("nombre",nombre);
        registro.put("direccion",direccion);
        registro.put("telefono",telefono);
        registro.put("whatsapp",whatsapp);
        registro.put("latitud",latitud);
        registro.put("longitud",longitud);
        registro.put("estado",estado);
        registro.put("id_categoria",id_categoria);
        registro.put("direccion_logo",direccion_logo);
        registro.put("direccion_banner",direccion_banner);
        registro.put("distancia",distancia);
        registro.put("facebook",facebook);
        registro.put("informacion",informacion);
        registro.put("estado_pedido",estado_pedido);
        registro.put("tiempo_preparacion",tiempo_preparacion);
        registro.put("abierto_cerrado",abierto_cerrado);
        registro.put("monto_minimo",monto_minimo);
        registro.put("solicitud_whatsapp",solicitud_whatsapp);
        registro.put("solicitud_aplicacion",solicitud_aplicacion);
        bd.insert("lugar", null, registro);
        bd.close();
    }



    private void init() {

        if (!isGooglePlayServicesAvailable()) {
            finish();
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
                            status.startResolutionForResult(Principal.this, PETICION_CONFIG_UBICACION);
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
        if (ActivityCompat.checkSelfPermission(Principal.this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            //Ojo: estamos suponiendo que ya tenemos concedido el permiso.
            //Sería recomendable implementar la posible petición en caso de no tenerlo.

            Log.i(LOGTAG, "Inicio de recepción de ubicaciones");

            LocationServices.FusedLocationApi.requestLocationUpdates(
                    apiClient, locRequest,Principal.this);
        }
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

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult result) {
//Se ha producido un error que no se puede resolver automáticamente
        //y la conexión con los Google Play Services no se ha establecido.

        Log.e(LOGTAG, "Error grave al conectar con Google Play Services "+result    );
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
    public void onLocationChanged(Location location) {
        updateUI(location);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Controles UI
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            View mapView = getSupportFragmentManager().findFragmentById(R.id.map).getView();
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

    private void updateUI(Location loc) {
        if (loc != null) {
            //marker moviles
            latitud=loc.getLatitude();
            longitud=loc.getLongitude();
            if(sw_obtener_ubicacion_primera==0)
            {
                servicio_lista_de_categoria_delivery();
            }
            sw_obtener_ubicacion_primera=1;


            float mm=mMap.getCameraPosition().zoom;

            SharedPreferences carrito = getSharedPreferences(getString(R.string.sql_pedido), MODE_PRIVATE);
            SharedPreferences.Editor ed_c = carrito.edit();
            ed_c.putString("milatitud", String.valueOf(latitud));
            ed_c.putString("milongitud", String.valueOf(longitud));
            ed_c.commit();

            //REGISTRAR LA DIRECCION DEL CLIENTE
            SharedPreferences loquesea = getSharedPreferences(getString(R.string.sql_loquesea), MODE_PRIVATE);
            SharedPreferences.Editor editar = loquesea.edit();
            editar.putString("latitud_entrega", String.valueOf(latitud));
            editar.putString("longitud_entrega", String.valueOf(longitud));
            editar.putString("direccion_entrega", "Mi ubicación");
            editar.commit();

            //verificamos si la ultima ubicacion esta en Cero (0,0)
            //si esta en (0,0) la camara se movia a la ubicacion de la solicitud.
            LatLng camera_ubicacion=null;
            camera_ubicacion=new LatLng(loc.getLatitude(),loc.getLongitude());
            if(mm<8){
                CameraPosition cameraPosition1 = new CameraPosition.Builder()
                        .target(camera_ubicacion)      // Sets the center of the map to Mountain View
                        .zoom(15)                   // Sets the zoom
                        .bearing(0)                // Sets the orientation of the camera to east
                        .tilt(0)                   // Sets the tilt of the camera to 30 degrees
                        .build();

                mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition1));
            }

        } else {
            // lblLatitud.setText("Latitud: (desconocida)");
        }
    }


    private void eliminar_carrito() {
        SharedPreferences carrito = getSharedPreferences(getString(R.string.sql_pedido), MODE_PRIVATE);
        SharedPreferences.Editor ed_c = carrito.edit();
        ed_c.putInt("id_lugar", 0);
        ed_c.putInt("id_empresa", 0);
        ed_c.putInt("id_categoria", 0);
        ed_c.putString("nombre_categoria", "");
        ed_c.putString("monto_total","0");
        ed_c.commit();

        try {
            AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, getString(R.string.nombre_sql), null, Integer.parseInt(getString(R.string.version_sql)));
            SharedPreferences pedido = getSharedPreferences(getString(R.string.sql_pedido), MODE_PRIVATE);
            SQLiteDatabase db = admin.getWritableDatabase();
            db.execSQL("delete from carrito where   id_pedido=" + pedido.getInt("id", 0));
            db.close();
        }catch (Exception e)
        {
            Log.e("Vaciar base",""+e);
        }
    }

    private void guardar_share_pedido(String latitud,
                                      String longitud,
                                      String milatitud,
                                      String milongitud,
                                      int id_empresa,
                                      int id_categoria,
                                      int tiempo_preparacion,
                                      String nombre_lugar,
                                      String direccion_logo_lugar,
                                      String direccion_banner_lugar,
                                      String abierto_cerrado,
                                      String monto_minimo,
                                      String whatsapp,
                                      String solicitud_whatsapp,
                                      String solicitud_aplicacion
                                      ) {
        SharedPreferences carrito = getSharedPreferences(getString(R.string.sql_pedido), MODE_PRIVATE);
        SharedPreferences.Editor ed_c = carrito.edit();
        ed_c.putInt("id_empresa", id_empresa);
        ed_c.putInt("id_categoria", id_categoria);
        ed_c.putString("latitud", latitud);
        ed_c.putString("longitud",longitud);
        ed_c.putString("milatitud",milatitud);
        ed_c.putString("milongitud",milongitud);
        ed_c.putInt("tiempo_preparacion",tiempo_preparacion);
        ed_c.putString("nombre_lugar",nombre_lugar);
        ed_c.putString("direccion_logo_lugar",direccion_logo_lugar);
        ed_c.putString("direccion_banner_lugar",direccion_banner_lugar);
        ed_c.putString("abierto_cerrado",abierto_cerrado);
        ed_c.putString("monto_minimo",monto_minimo);
        ed_c.putString("whatsapp",whatsapp);
        ed_c.putString("solicitud_whatsapp",solicitud_whatsapp);
        ed_c.putString("solicitud_aplicacion",solicitud_aplicacion);
        ed_c.commit();
    }





    public void iniciar_verificacion_version(){
        final SharedPreferences prefe = getSharedPreferences("perfil", MODE_PRIVATE);
        try {
            String devuelve="";
            int id_usuario = Integer.parseInt(prefe.getString("id_usuario", ""));
            String token= SharedPrefManager.getInstance(this).getDeviceToken();

            JSONObject jsonParam= new JSONObject();
            jsonParam.put("id_usuario", String.valueOf(id_usuario));
            jsonParam.put("token", token);
            String url=getString(R.string.servidor) + "traigo/frm_version.php?opcion=traigo";
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
                                version= Integer.valueOf(respuestaJSON.getString("version"));
                                suceso= new Suceso(respuestaJSON.getString("suceso"),respuestaJSON.getString("mensaje"));

                                if (suceso.getSuceso().equals("1")) {

                                    SharedPreferences.Editor editar_share=prefe.edit();
                                    editar_share.putString("cantidad_solicitud",respuestaJSON.getString("cantidad_solicitud"));
                                    editar_share.commit();


                                    boolean sw=verificar_version();
                                    if(sw==false){
                                        boolean sw_p=existe_perfil();
                                        if(sw_p==false){
                                            abrir_perfil_con_mensaje();
                                        }
                                    }
                                }else if(suceso.getSuceso().equals("2"))
                                {
                                    cuenta_iniciar_en_otro_celular(suceso.getMensaje());

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

    public static int getVersionCode(Context context) {
        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            return pi.versionCode;
        } catch (PackageManager.NameNotFoundException ex) {}
        return 0;
    }

    public boolean verificar_version()
    {boolean sw=false;
        // notificacion para verificar la actualizacion nueva
        int actual=getVersionCode(this);
        if(version>actual)
        {
            try {
                AlertDialog.Builder dialogo1 = new AlertDialog.Builder(this);
                dialogo1.setTitle("Actualización");
                dialogo1.setMessage("Hay una nueva version.Por favor actualice la aplicación desde Play Store.");
                dialogo1.setCancelable(false);
                dialogo1.setPositiveButton("ACTUALIZAR", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogo1, int id) {
                        Uri uri = Uri.parse("https://play.google.com/store/apps/details?id=com.elisoft.traigo&hl=es");
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent);
                    }
                });

                dialogo1.show();
                sw = true;
            }catch (Exception e){
                sw=true;
            }
        }
        return sw;
    }

    public void cuenta_iniciar_en_otro_celular(String mensaje)
    {
        try {
            AlertDialog.Builder dialogo1 = new AlertDialog.Builder(this);
            dialogo1.setTitle(getString(R.string.app_name));
            dialogo1.setMessage(mensaje);
            dialogo1.setCancelable(false);
            dialogo1.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogo1, int id) {
                    cerrar_sesion();
                }
            });
            dialogo1.show();
        }catch (Exception e){
        }

    }

    public void cerrar_sesion()
    {
        LoginManager.getInstance().logOut();
        SharedPreferences perfil=getSharedPreferences("perfil",MODE_PRIVATE);
        SharedPreferences.Editor editar=perfil.edit();
        editar.putString("id","");
        editar.putString("id_usuario","");
        editar.putString("nombre","");
        editar.putString("apellido","");
        editar.putString("ci","");
        editar.putString("email","");
        editar.putString("direccion","");
        editar.putString("marca","");
        editar.putString("modelo","");
        editar.putString("placa","");
        editar.putString("celular","");
        editar.putString("credito","");
        editar.putString("login_usuario","");
        editar.putString("login_taxi","");
        editar.commit();
        Intent serv = new Intent(getApplicationContext(), Servicio_pedido.class);
        serv.setAction(Constants.ACTION_RUN_ISERVICE);
        stopService(serv);
        Intent intent=new Intent(getApplicationContext(), Inicio_Activity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP| Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

    }

    public void mensaje_error_final(String mensaje)
    {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getString(R.string.app_name));
            builder.setMessage(mensaje);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogo1, int id) {
                    finish();
                }
            });
            builder.create();
            builder.show();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void mensaje_error(String mensaje)
    {try {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.app_name));
        builder.setMessage(mensaje);
        builder.setPositiveButton("OK", null);
        builder.create();
        builder.show();
    }catch (Exception e)
    {
        Log.e("mensaje_error",e.toString());
    }
    }


    public void  abrir_perfil_con_mensaje(){
        Toast.makeText(this,"Señor usuario porfavor actualice su foto de perfil, para nuestros conductores puedan identificarte.",Toast.LENGTH_SHORT).show();
    }
    public boolean existe_perfil()
    {
        boolean sw=false;
        Drawable dw;
        SharedPreferences perfil=getSharedPreferences("perfil",MODE_PRIVATE);
        String mPath = Environment.getExternalStorageDirectory() + File.separator + getString(R.string.app_name)+"/Imagen"
                + File.separator + perfil.getString("id_usuario","")+"_perfil.jpg";

        File newFile = new File(mPath);
        Bitmap bitmap =  BitmapFactory.decodeFile(newFile.getAbsolutePath());


        sw = bitmap != null;
        return sw;
    }


    private void actualizar() {
        SharedPreferences prefe = getSharedPreferences("perfil", Context.MODE_PRIVATE);
        try {
            int id_usuario = Integer.parseInt(prefe.getString("id_usuario", ""));

            JSONObject jsonParam= new JSONObject();
            jsonParam.put("id_usuario", String.valueOf(id_usuario));
            String url=getString(R.string.servidor) + "traigo/frmPedido.php?opcion=get_pedido_por_id_usuario";
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
                            }catch (Exception e)
                            { }
                            try {



                                suceso=new Suceso(respuestaJSON.getString("suceso"),respuestaJSON.getString("mensaje"));

                                if (suceso.getSuceso().equals("1")) {
                                    JSONArray dato=respuestaJSON.getJSONArray("pedido");
                                    String sid_pedido=dato.getJSONObject(0).getString("id");
                                    String slatitud=dato.getJSONObject(0).getString("latitud");
                                    String slongitud=dato.getJSONObject(0).getString("longitud");
                                    SharedPreferences pedido=getSharedPreferences("ultimo_pedido",MODE_PRIVATE);
                                    SharedPreferences.Editor editar=pedido.edit();
                                    editar.putString("id_pedido",sid_pedido);
                                    editar.putString("latitud",slatitud);
                                    editar.putString("longitud",slongitud);
                                    editar.commit();



                                    Intent intent = new Intent(Principal.this, Servicio_pedido.class);
                                    intent.setAction(Constants.ACTION_RUN_ISERVICE);
                                    startService(intent);

                                    SharedPreferences spedido2 = getSharedPreferences("ultimo_pedido", MODE_PRIVATE);

                                    Intent pedido2 = new Intent(Principal.this, Pedido_usuario.class);
                                    pedido2.putExtra("latitud", Double.parseDouble(spedido2.getString("latitud", "0")));
                                    pedido2.putExtra("longitud", Double.parseDouble(spedido2.getString("longitud", "0")));
                                    pedido2.putExtra("id_pedido", spedido2.getString("id_pedido", "0"));
                                    startActivity(pedido2);


                                } else  {
                                    iniciar_verificacion_version();
                                    //verificar version de la aplicacion.
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
            myRequest.setRetryPolicy(new DefaultRetryPolicy(6000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            queue.add(myRequest);
        } catch (Exception e) {

        }

    }

    @Override
    protected void onStart() {
        actualizar();
        super.onStart();
    }

    public void imagen_en_vista(de.hdodenhof.circleimageview.CircleImageView imagen)
    { Drawable dw;
        SharedPreferences perfil=getSharedPreferences("perfil",MODE_PRIVATE);
        String mPath = Environment.getExternalStorageDirectory() + File.separator + getString(R.string.app_name)+"/Imagen"
                + File.separator + perfil.getString("id_usuario","")+"_perfil.jpg";

        File newFile = new File(mPath);
        Bitmap bitmap =  BitmapFactory.decodeFile(newFile.getAbsolutePath());
        //Convertir Bitmap a Drawable.
        dw = new BitmapDrawable(getResources(), bitmap);
        //se edita la imagen para ponerlo en circulo.

        if( bitmap==null)
        { dw = getResources().getDrawable(R.drawable.ic_perfil_negro);}

        imagen.setImageDrawable(dw);

        imagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Principal.this,Perfil_pasajero.class));
            }
        });
    }

    public CEmpresa obtener_empresa(int id_empresa)
    {
        CEmpresa lugar=null;
        for (int i=0;i<empresa.size();i++)
        {
            if(id_empresa==empresa.get(i).getId())
            {
                lugar=new CEmpresa();
                lugar=empresa.get(i);
            }
        }
        return lugar;
    }
}
