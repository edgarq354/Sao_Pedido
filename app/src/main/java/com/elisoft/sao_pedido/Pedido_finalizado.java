package com.elisoft.sao_pedido;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.elisoft.sao_pedido.SqLite.AdminSQLiteOpenHelper;
import com.elisoft.sao_pedido.notificaciones.SharedPrefManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import androidx.appcompat.app.AppCompatActivity;


public class Pedido_finalizado extends AppCompatActivity implements View.OnClickListener{

    TextView tv_mensaje,tv_distancia,tv_detalle ;
    EditText et_descripcion;
    Button bt_aceptar;
    RatingBar rb_conductor,rb_vehiculo;
    int id_pedido=0;
    Suceso suceso;
    ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedido_finalizado);


        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        tv_mensaje= findViewById(R.id.tv_mensaje);
        tv_distancia= findViewById(R.id.tv_distancia);
        bt_aceptar= findViewById(R.id.bt_aceptar);
        rb_conductor= findViewById(R.id.rb_conductor);
        rb_vehiculo= findViewById(R.id.rb_vehiculo);
        et_descripcion= findViewById(R.id.et_descripcion);
        tv_detalle=(TextView)findViewById(R.id.tv_detalle);
        try{
            Bundle bundle=getIntent().getExtras();
            id_pedido= Integer.parseInt(bundle.getString("id_pedido"));
            tv_distancia.setText("Distancia recorrido "+bundle.getString("distancia")+" Mtrs.");
            tv_mensaje.setText(""+bundle.getString("monto_total","")+" Bs.");
            if(bundle.getString("detalle","").equals("")==false){
                tv_detalle.setText(bundle.getString("detalle",""));
            }

        }catch (Exception e)
        {
            finish();
        }

        bt_aceptar.setOnClickListener(this);
        getSupportActionBar().hide();

    }




    @Override
    protected void onStop() {
        guardar_calificacion();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        guardar_calificacion();
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        SharedPreferences mis_datos = getSharedPreferences(getString(R.string.finalizo), MODE_PRIVATE);
        if (mis_datos.getInt("id_pedido", 0) == id_pedido)
        {
            finish();
        }else{
            mostrar_calificacion();
        }

        super.onStart();
    }

    @Override
    protected void onPause() {
        guardar_calificacion();
        super.onPause();
    }

    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.bt_aceptar)
        {
            String puntuacion_conductor= String.valueOf(rb_conductor.getRating());
            String puntuacion_vehiculo= String.valueOf(rb_vehiculo.getRating());

            servicio_calificar_pedido(String.valueOf(id_pedido),puntuacion_conductor,puntuacion_vehiculo,et_descripcion.getText().toString());
        }
    }



    //servicio de cancelar de pedido
    private void servicio_calificar_pedido(String id_pedido,String punto_conductor,String punto_vehiculo,String descripcion) {


        try {

            pDialog = new ProgressDialog(Pedido_finalizado.this);
            pDialog.setTitle(getString(R.string.app_name));
            pDialog.setMessage("Subiendo la calificación");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();

            String token= SharedPrefManager.getInstance(this).getDeviceToken();

            JSONObject jsonParam= new JSONObject();
            jsonParam.put("id_pedido", id_pedido);
            jsonParam.put("punto_conductor", punto_conductor);
            jsonParam.put("punto_vehiculo", punto_vehiculo);
            jsonParam.put("descripcion", descripcion);

            jsonParam.put("token", token);


            String url=getString(R.string.servidor) + "frmPedido.php?opcion=cargar_puntuacion";
            RequestQueue queue = Volley.newRequestQueue(this);


            JsonObjectRequest myRequest= new JsonObjectRequest(
                    Request.Method.POST,
                    url,
                    jsonParam,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject respuestaJSON) {



                            try {
                                pDialog.dismiss();

                                suceso= new Suceso(respuestaJSON.getString("suceso"),respuestaJSON.getString("mensaje"));

                                if (suceso.getSuceso().equals("1")) {

                                    JSONArray usu=respuestaJSON.getJSONArray("historial");
                                    for (int i=0;i<usu.length();i++)
                                    {
                                        int id_pedido= Integer.parseInt(usu.getJSONObject(i).getString("id"));
                                        int id_taxi= Integer.parseInt(usu.getJSONObject(i).getString("id_conductor"));
                                        int estado_pedido= Integer.parseInt(usu.getJSONObject(i).getString("estado"));
                                        String indicacion=usu.getJSONObject(i).getString("direccion");
                                        String fecha_pedido=usu.getJSONObject(i).getString("fecha_pedido");
                                        double latitud= Double.parseDouble(usu.getJSONObject(i).getString("longitud"));
                                        double longitud= Double.parseDouble(usu.getJSONObject(i).getString("longitud"));
                                        String nombre=usu.getJSONObject(i).getString("nombre");
                                        String apellido=usu.getJSONObject(i).getString("apellido");
                                        String celular=usu.getJSONObject(i).getString("celular");
                                        String marca=usu.getJSONObject(i).getString("marca");
                                        String placa=usu.getJSONObject(i).getString("placa");
                                        String descripcion=usu.getJSONObject(i).getString("detalle");
                                        String monto_total=usu.getJSONObject(i).getString("monto_total");


                                        cargar_lista_en_historial( id_pedido,
                                                id_taxi,
                                                estado_pedido,
                                                fecha_pedido,
                                                nombre,
                                                apellido,
                                                celular,
                                                marca,
                                                placa,
                                                indicacion,
                                                descripcion,
                                                latitud,
                                                longitud,
                                                monto_total);
                                    }
                                    //-------------- final --------
                                    startActivity(new Intent(getApplicationContext(),Principal.class));
                                    finish();

                                }else if(suceso.getSuceso().equals("2"))
                                {

                                    startActivity(new Intent(getApplicationContext(),Principal.class));
                                    finish();
                                }
                                else
                                {
                                    startActivity(new Intent(getApplicationContext(),Principal.class));
                                    finish();
                                }
                            } catch (JSONException e) {
                                pDialog.dismiss();

                                e.printStackTrace();
                                startActivity(new Intent(getApplicationContext(),Principal.class));
                                finish();
                            }

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    pDialog.dismiss();
                    startActivity(new Intent(getApplicationContext(),Principal.class));
                    finish();
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
            e.printStackTrace();
        }

    }


















    private void cargar_lista_en_historial( int id,
                                            int id_taxi,
                                            int estado_pedido,
                                            String fecha_pedido,
                                            String nombre,
                                            String apellido,
                                            String celular,
                                            String marca,
                                            String placa,
                                            String indicacion,
                                            String descripcion,
                                            double latitud,
                                            double longitud,
                                            String monto_total)
    {
        try {
            AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, getString(R.string.nombre_sql), null, Integer.parseInt(getString(R.string.version_sql)));

            SQLiteDatabase bd = admin.getWritableDatabase();
            ContentValues registro = new ContentValues();
            registro.put("id", String.valueOf(id));
            registro.put("id_conductor", String.valueOf(id_taxi));
            registro.put("estado_pedido", String.valueOf(estado_pedido));
            registro.put("fecha_pedido", fecha_pedido);
            registro.put("latitud", String.valueOf(latitud));
            registro.put("longitud", String.valueOf(longitud));
            registro.put("nombre", nombre);
            registro.put("apellido", apellido);
            registro.put("celular", celular);
            registro.put("marca", marca);
            registro.put("placa", placa);
            registro.put("indicacion", indicacion);
            registro.put("descripcion", descripcion);
            registro.put("monto_total", monto_total);
            bd.insert("pedido_usuario", null, registro);
            bd.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }




    public void guardar_calificacion()
    {
        SharedPreferences mis_datos;
        mis_datos=getSharedPreferences(getString(R.string.finalizo),MODE_PRIVATE);

        SharedPreferences.Editor editor=mis_datos.edit();
        editor.putString("descripcion",et_descripcion.getText().toString());
        editor.putInt("conductor",rb_conductor.getNumStars());
        editor.putInt("vehiculo",rb_vehiculo.getNumStars());
        editor.putInt("id_pedido",id_pedido);
        editor.commit();

    }
    public void mostrar_calificacion()
    {
        SharedPreferences mis_datos;
        mis_datos=getSharedPreferences(getString(R.string.finalizo),MODE_PRIVATE);
        et_descripcion.setText(mis_datos.getString("descripcion",""));
        rb_conductor.setRating(mis_datos.getInt("conductor",0));
        rb_vehiculo.setRating(mis_datos.getInt("vehiculo",0));

    }

}
