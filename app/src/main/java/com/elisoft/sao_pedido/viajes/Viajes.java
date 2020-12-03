package com.elisoft.sao_pedido.viajes;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.elisoft.sao_pedido.R;
import com.elisoft.sao_pedido.Suceso;
import com.elisoft.sao_pedido.carreras.Carrera;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


public class Viajes extends AppCompatActivity {
    Suceso suceso;
    private ProgressDialog pDialog;
    ArrayList<CViajes> cviajes=new ArrayList<CViajes>();
    ListView lv_lista;
    Spinner sp_mes;

    TextView tv_error;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viajes);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tv_error=findViewById(R.id.tv_error);
        lv_lista=(ListView)findViewById(R.id.lv_lista);
        sp_mes=(Spinner)findViewById(R.id.sp_mes);

        lv_lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                CViajes hi=new CViajes();
                hi=cviajes.get(i);

                if(hi.getEstado_pedido()==2){
                    ver_video(hi);
                }
            }
        });

        sp_mes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id)
            {
                lista_por_mes((String) adapterView.getItemAtPosition(pos));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {    }
        });



    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    public String get_mes(int mes)
    {
        String s_mes="Todo";

        switch (mes)
        {
            case 1:
                s_mes="Enero";
                break;
            case 2:
                s_mes="Febrero";
                break;
            case 3:
                s_mes="Marzo";
                break;
            case 4:
                s_mes="Abril";
                break;
            case 5:
                s_mes="Mayo";
                break;
            case 6:
                s_mes="Junio";
                break;
            case 7:
                s_mes="Julio";
                break;
            case 8:
                s_mes="Agosto";
                break;
            case 9:
                s_mes="Septiembre";
                break;
            case 10:
                s_mes="Octubre";
                break;
            case 11:
                s_mes="Noviembre";
                break;
            case 12:
                s_mes="Diciembre";
                break;
        }


        return s_mes;
    }
    public void lista_por_mes(String s_mes)
    {
        int mes = 0;
        switch (s_mes)
        {
            case "Enero":
                mes=1;
                break;
            case "Febrero":
                mes=2;
                break;
            case "Marzo":
                mes=3;
                break;
            case "Abril":
                mes=4;
                break;
            case "Mayo":
                mes=5;
                break;
            case "Junio":
                mes=6;
                break;
            case "Julio":
                mes=7;
                break;
            case "Agosto":
                mes=8;
                break;
            case "Septiembre":
                mes=9;
                break;
            case "Octubre":
                mes=10;
                break;
            case "Noviembre":
                mes=11;
                break;
            case "Diciembre":
                mes=12;
                break;
        }

        Calendar calendarNow = Calendar.getInstance();

        int anio = calendarNow.get(Calendar.YEAR);

        SharedPreferences prefe=getSharedPreferences("perfil", Context.MODE_PRIVATE);
        String id=prefe.getString("id_usuario","");
        String ip=getString(R.string.servidor);
        String url=ip+"frmDelivery.php?opcion=lista_delivery_por_id_usuario_mes";
        JSONObject jsonParam = new JSONObject();

        try {
            jsonParam.put("id_usuario", id);
            jsonParam.put("anio", anio);
            jsonParam.put("mes", mes);
        }catch (Exception e)
        {}
        if(mes==0)
        {actualizar();}
        else
        {descargar_historial_volley(jsonParam,url);}
    }
    public void actualizar()
    {


        SharedPreferences prefe=getSharedPreferences("perfil", Context.MODE_PRIVATE);
        String id=prefe.getString("id_usuario","");
        String ip=getString(R.string.servidor);
        /*Servicio hilo_pedido = new Servicio();

        hilo_pedido.execute(ip+"frmPedido.php?opcion=lista_pedido_por_id_usuario", "1",id);// parametro que recibe el doinbackground
        Log.i("Item", "actualizar!");
        */
        String url=ip+"frmDelivery.php?opcion=lista_delivery_por_id_usuario_top50";
        JSONObject jsonParam = new JSONObject();

        try {
            jsonParam.put("id_usuario", id);
        }catch (Exception e)
        {}
        descargar_historial_volley(jsonParam,url);
    }


    private void descargar_historial_volley(JSONObject jsonParam,String v_url) {

        try {




            RequestQueue queue = Volley.newRequestQueue(this);


            JsonObjectRequest myRequest= new JsonObjectRequest(
                    Request.Method.POST,
                    v_url,
                    jsonParam,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject respuestaJSON) {
                            pDialog.dismiss();//ocultamos proggress dialog
                            cviajes=new ArrayList<CViajes>();
                            try {
                                suceso=new Suceso(respuestaJSON.getString("suceso"),respuestaJSON.getString("mensaje"));
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
                                        String monto_pedido=usu.getJSONObject(i).getString("monto_pedido");
                                        int clase_vehiculo=Integer.parseInt(usu.getJSONObject(i).getString("clase_vehiculo"));
                                        int calificacion_vehiculo=Integer.parseInt(usu.getJSONObject(i).getString("calificacion_vehiculo"));
                                        int calificacion_conductor=Integer.parseInt(usu.getJSONObject(i).getString("calificacion_conductor"));

                                        String direccion_lugar=usu.getJSONObject(i).getString("direccion_lugar");
                                        String direccion_logo_lugar=usu.getJSONObject(i).getString("direccion_logo_lugar");



                                        CViajes hi = new CViajes(id_pedido,
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
                                                monto_total,
                                                clase_vehiculo,
                                                calificacion_conductor,
                                                calificacion_vehiculo,
                                                direccion_lugar,
                                                direccion_logo_lugar,
                                                monto_pedido
                                        );
                                        cviajes.add(hi);
                                    }

                                    if(cviajes.size()>0)
                                    {
                                        tv_error.setVisibility(View.INVISIBLE);
                                    }


                                        item_viajes adaptador = new item_viajes(Viajes.this,cviajes);
                                        lv_lista.setAdapter(adaptador);
                                } else  {
                                    cviajes.clear();
                                    item_viajes adaptador = new item_viajes(Viajes.this,cviajes);
                                    lv_lista.setAdapter(adaptador);
                                }



                            } catch (JSONException e) {
                                e.printStackTrace();
                                cviajes.clear();
                                item_viajes adaptador = new item_viajes(Viajes.this,cviajes);
                                lv_lista.setAdapter(adaptador);
                                mensaje_error("Falla en tu conexión a Internet.");
                            }

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    pDialog.cancel();
                    cviajes.clear();
                    item_viajes adaptador = new item_viajes(Viajes.this,cviajes);
                    lv_lista.setAdapter(adaptador);
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

            pDialog = new ProgressDialog(Viajes.this);
            pDialog.setMessage("Descargando la lista de pedidos . . .");
            pDialog.setIndeterminate(true);
            pDialog.setCancelable(true);
            pDialog.show();

            queue.add(myRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }



    private void ver_video(CViajes viaje) {

        try {
            Intent i = new Intent(getApplicationContext(), Carrera.class);

            i.putExtra("id_pedido", String.valueOf(viaje.getId()));
            i.putExtra("id_vehiculo", String.valueOf(viaje.getPlaca()));
            startActivity(i);
        }catch (Exception e)
        {
            Log.e("carrera",e.toString());
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