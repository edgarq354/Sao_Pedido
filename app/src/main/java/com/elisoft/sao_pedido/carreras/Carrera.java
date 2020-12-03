package com.elisoft.sao_pedido.carreras;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.elisoft.sao_pedido.Pedido_perfil_taxi;
import com.elisoft.sao_pedido.R;
import com.elisoft.sao_pedido.SqLite.AdminSQLiteOpenHelper;
import com.elisoft.sao_pedido.Suceso;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class Carrera extends AppCompatActivity {
    ArrayList<CCarrera> carrera;

    private ProgressDialog pDialog;

    private Servicio hilo_carrera;
    private Suceso suceso;
    int id_pedido;
    String id_vehiculo="";
    Bundle save;


    ImageView im_mapa;


    TextView tv_numero;
    String id_carrera="";


    TextView fecha;
    TextView monto;
    ImageView ib_conductor;
    TextView tv_distancia;
    TextView tv_direccion_inicio;
    TextView tv_direccion_fin;
    WebView wv_mapa;
    LinearLayout ll_ver_datos;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        setContentView(R.layout.activity_carrera);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fecha = findViewById(R.id.fecha);
        monto = findViewById(R.id.monto);
        ib_conductor= findViewById(R.id.ib_conductor);
        tv_distancia= findViewById(R.id.tv_distancia);
        tv_direccion_inicio = findViewById(R.id.tv_direccion_inicio);
        tv_direccion_fin = findViewById(R.id.tv_direccion_fin);
        tv_numero = findViewById(R.id.tv_numero);
        im_mapa= findViewById(R.id.im_mapa);
        wv_mapa = findViewById(R.id.wv_mapa);
        ll_ver_datos= findViewById(R.id.ll_ver_datos);


        try{
            Bundle bundle = getIntent().getExtras();
            id_pedido = Integer.parseInt(bundle.getString("id_pedido"));
            id_vehiculo =  bundle.getString("id_vehiculo");
            cargar_carrera_en_la_lista(id_pedido);
        }catch (Exception e)
        {
            //  finish();
        }



        actualizar();




    }






    public void actualizar()
    {
        SharedPreferences prefe=getSharedPreferences("perfil", Context.MODE_PRIVATE);
        String id=prefe.getString("id_usuario","");


        hilo_carrera = new Servicio();
        hilo_carrera.execute(getString(R.string.servidor) + "frmCarrera.php?opcion=lista_de_carrera_por_pedido_usuario", "1", id, String.valueOf(this.id_pedido));// parametro que recibe el doinbackground


    }

    public void mostrar_datos(final CCarrera carrra)
    {
        final CCarrera ped=carrra;
        String url= getString(R.string.servidor)+"ver_carrera.php?id_carrera="+ped.getId()+"&id_pedido="+ped.getId_pedido();
        // wv_mapa.loadUrl(url);
        //wv_mapa.loadUrl("https://universoandroidstudio.blogspot.com/2016/");
        wv_mapa.getSettings().setJavaScriptEnabled(true);
        wv_mapa.setWebViewClient(new WebViewClient());
        wv_mapa.loadUrl(url);
        this.im_mapa=im_mapa;

        //poner en el String todos los puntos registrados.....
        boolean sw=true;

        try {
            int id = ped.getId_pedido();
        }catch (Exception e)
        {
            sw=false;
        }
        String  url1=  "public/Imagen_Conductor/Perfil-"+String.valueOf(ped.getId_conductor())+".png";
        Picasso.with(this).load(getString(R.string.servidor_web)+url1).placeholder(R.mipmap.ic_perfil).into(ib_conductor);

        ll_ver_datos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent ee = new Intent(Carrera.this, Pedido_perfil_taxi.class);
                ee.putExtra("id_conductor",  String.valueOf(ped.getId_conductor()));
                ee.putExtra("id_vehiculo", String.valueOf(ped.getPlaca()));
                startActivity( ee);
            }
        });


        if(sw==true) {
            fecha.setText(ped.getFecha_fin());
            monto.setText( ped.getMonto()+" Bs.");
            tv_distancia.setText( ped.getDistancia()+" mt.");
            tv_direccion_inicio.setText(ped.getDireccion_inicio());
            tv_direccion_fin.setText(ped.getDireccion_fin());
        }


    }

    // comenzar el servicio con el motista....
    public class Servicio extends AsyncTask<String, Integer, String> {


        @Override
        protected String doInBackground(String... params) {

            String cadena = params[0];
            URL url = null;  // url donde queremos obtener informacion
            String devuelve = "";

            if (params[1] == "1") {
                try {
                    HttpURLConnection urlConn;

                    url = new URL(cadena);
                    urlConn = (HttpURLConnection) url.openConnection();
                    urlConn.setDoInput(true);
                    urlConn.setDoOutput(true);
                    urlConn.setUseCaches(false);
                    urlConn.setRequestProperty("Content-Type", "application/json");
                    urlConn.setRequestProperty("Accept", "application/json");
                    urlConn.connect();

                    //se crea el objeto JSON
                    JSONObject jsonParam = new JSONObject();
                    jsonParam.put("id_usuario",params[2]);
                    jsonParam.put("id_pedido",params[3]);


                    //Envio los prametro por metodo post
                    OutputStream os = urlConn.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8));
                    writer.write(jsonParam.toString());
                    writer.flush();
                    writer.close();

                    int respuesta = urlConn.getResponseCode();

                    StringBuilder result = new StringBuilder();

                    if (respuesta == HttpURLConnection.HTTP_OK) {

                        String line;
                        BufferedReader br = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));
                        while ((line = br.readLine()) != null) {
                            result.append(line);
                        }


                        JSONObject respuestaJSON = new JSONObject(result.toString());//Creo un JSONObject a partir del
                        suceso = new Suceso(respuestaJSON.getString("suceso"), respuestaJSON.getString("mensaje"));
                        // vacia los datos que estan registrados en nuestra base de datos SQLite..
                        vaciar_carrera(id_pedido);
                        if (suceso.getSuceso().equals("1")) {
                            JSONArray usu = respuestaJSON.getJSONArray("carrera");
                            for (int i = 0; i < usu.length(); i++) {
                                String id=usu.getJSONObject(i).getString("id");
                                double latitud_inicio= Double.parseDouble(usu.getJSONObject(i).getString("latitud_inicio"));
                                double longitud_inicio= Double.parseDouble(usu.getJSONObject(i).getString("longitud_inicio"));
                                double latitud_fin= Double.parseDouble(usu.getJSONObject(i).getString("latitud_fin"));
                                double longitud_fin= Double.parseDouble(usu.getJSONObject(i).getString("longitud_fin"));
                                double monto= Double.parseDouble(usu.getJSONObject(i).getString("monto"));
                                String distancia=usu.getJSONObject(i).getString("distancia");
                                String fecha_inicio=usu.getJSONObject(i).getString("fecha_inicio");
                                String direccion_inicio=usu.getJSONObject(i).getString("direccion_inicio");
                                String direccion_fin=usu.getJSONObject(i).getString("direccion_fin");
                                String fecha_fin=usu.getJSONObject(i).getString("fecha_fin");
                                String tiempo=usu.getJSONObject(i).getString("tiempo");
                                String id_pedido=usu.getJSONObject(i).getString("id_pedido");
                                String id_conductor=usu.getJSONObject(i).getString("id_conductor");
                                String ruta=usu.getJSONObject(i).getString("ruta");
                                cargar_lista_en_carrera( id, latitud_inicio, longitud_inicio, latitud_fin, longitud_fin, monto, distancia, fecha_inicio, fecha_fin, tiempo, id_pedido, id_conductor,ruta,direccion_inicio,direccion_fin);
                            }
                            Log.e("Carrera","Finalizo de cargar las carreras.");


                            devuelve = "1";
                        } else {
                            devuelve = "2";
                        }
                    }

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return devuelve;
        }


        @Override
        protected void onPreExecute() {
            //para el progres Dialog



            try {
                pDialog = new ProgressDialog(Carrera.this);
                pDialog.setMessage("Descargando la Carreras. .");
                pDialog.setIndeterminate(true);
                pDialog.setCancelable(true);
                pDialog.show();
            }catch (Exception e)
            {
                mensaje_error("Por favor actualice la aplicación.");
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pDialog.dismiss();//ocultamos proggress dialog
            Log.e("onPostExcute=", "" + s);

            if (s.equals("1")) {
                cargar_carrera_en_la_lista(id_pedido);
            } else if(s.equals("2"))
            {
                mensaje_error(suceso.getMensaje());
            }
            else
            {
                mensaje_error("Error: Al conectar con el servidor.");
            }

        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onCancelled(String s) {
            super.onCancelled(s);
        }

    }

    private void cargar_lista_en_carrera(String id, double latitud_inicio, double longitud_inicio, double latitud_fin, double longitud_fin, double monto, String distancia, String fecha_inicio, String fecha_fin, String tiempo, String id_pedido, String id_usuario, String ruta,String direccion_inicio,String direccion_fin) {

        try {
            AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, getString(R.string.nombre_sql), null, Integer.parseInt(getString(R.string.version_sql)));

            SQLiteDatabase bd = admin.getWritableDatabase();
            ContentValues registro = new ContentValues();
            registro.put("id", id);
            registro.put("latitud_inicio", latitud_inicio);
            registro.put("longitud_inicio", longitud_inicio);
            registro.put("latitud_fin", latitud_fin);
            registro.put("longitud_fin", longitud_fin);
            registro.put("distancia", distancia);
            registro.put("tiempo", tiempo);
            registro.put("fecha_inicio", fecha_inicio);
            registro.put("fecha_fin", fecha_fin);
            registro.put("id_pedido", id_pedido);
            registro.put("id_conductor", id_usuario);
            registro.put("monto", monto);
            registro.put("ruta", ruta);
            registro.put("direccion_inicio", direccion_inicio);
            registro.put("direccion_fin", direccion_fin);
            bd.insert("carrera", null, registro);
            bd.close();
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    public void actualizar_lista() {
        if(carrera.size()>0)
        {
            mostrar_datos(carrera.get(0));
        }


    }



    public void cargar_carrera_en_la_lista( int id_pedido) {
        carrera = new ArrayList<CCarrera>();

        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, getString(R.string.nombre_sql), null, Integer.parseInt(getString(R.string.version_sql)));

        SQLiteDatabase bd = admin.getWritableDatabase();
        Cursor fila = bd.rawQuery("select id,latitud_inicio,longitud_inicio,latitud_fin,longitud_fin,distancia,fecha_inicio,fecha_fin,tiempo,id_pedido,id_conductor,monto,ruta,direccion_inicio,direccion_fin from carrera where id_pedido="+id_pedido+" ORDER BY id ASC ", null);
        int numero=1;
        if (fila.moveToFirst()) {  //si ha devuelto 1 fila, vamos al primero (que es el unico)



            do {
                int id= Integer.parseInt(fila.getString(0));
                double latitud_inicio= Double.parseDouble(fila.getString(1));
                double longitud_inicio= Double.parseDouble(fila.getString(2));
                double latitud_fin= Double.parseDouble(fila.getString(3));
                double longitud_fin= Double.parseDouble(fila.getString(4));
                String distancia= String.valueOf(fila.getString(5));
                String fecha_inicio= String.valueOf(fila.getString(6));
                String fecha_fin= String.valueOf(fila.getString(7));
                String tiempo= String.valueOf(fila.getString(8));
                int i_id_pedido= Integer.parseInt(fila.getString(9));
                int id_conductor= Integer.parseInt(fila.getString(10));
                double monto= Double.parseDouble(fila.getString(11));
                String s_ruta= String.valueOf(fila.getString(12));
                String s_direccion_inicio= String.valueOf(fila.getString(13));
                String s_direccion_fin= String.valueOf(fila.getString(14));
                CCarrera hi = new CCarrera(id,latitud_inicio,longitud_inicio,latitud_fin,longitud_fin,distancia,fecha_inicio,fecha_fin,tiempo,i_id_pedido,id_conductor,monto,s_ruta, String.valueOf(numero),s_direccion_inicio,s_direccion_fin);
                hi.setPlaca(id_vehiculo);
                carrera.add(hi);
                numero++;
            } while (fila.moveToNext());

        } else
            Toast.makeText(this, "No hay registrados",
                    Toast.LENGTH_SHORT).show();

        bd.close();
        actualizar_lista();
    }

    public void vaciar_carrera(int id_pedido) {

        try {
            AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, getString(R.string.nombre_sql), null, Integer.parseInt(getString(R.string.version_sql)));

            SQLiteDatabase db = admin.getWritableDatabase();
            db.delete("carrera", "id_pedido="+id_pedido, null);
            db.close();
            Log.e("sqlite ", "vaciar todas las carrera id_pedido=" + id_pedido);
        } catch (Exception e)
        {
            Log.e("sqlite ", "Error : vaciar todas las carreras por id_pedido= " + id_pedido+" . "+e);
        }
    }

    public void mensaje_error(String mensaje)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Importante");
        builder.setMessage(mensaje);
        builder.setPositiveButton("OK", null);
        builder.create();
        builder.show();
    }




}


