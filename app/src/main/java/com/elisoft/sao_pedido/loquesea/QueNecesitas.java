package com.elisoft.sao_pedido.loquesea;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.elisoft.sao_pedido.Suceso;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.elisoft.sao_pedido.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class QueNecesitas extends AppCompatActivity implements View.OnClickListener {
 EditText et_que_necesitas;
 Button bt_siguiente;
 TextView tv_horario;
    private ProgressDialog pDialog;
    private RequestQueue queue=null;
    String disponible="1";
    Suceso suceso;
    @Override
    protected void onStart() {
        SharedPreferences carrito = getSharedPreferences(getString(R.string.sql_loquesea), MODE_PRIVATE);
        et_que_necesitas.setText(carrito.getString("que_necesitas",""));
        servicio_mostrar_horario_loquesea();
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_que_necesitas);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        et_que_necesitas=findViewById(R.id.et_que_necesitas);
        bt_siguiente=findViewById(R.id.bt_siguiente);
        tv_horario=findViewById(R.id.tv_horario);

        bt_siguiente.setOnClickListener(this);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.bt_siguiente:
                if(disponible.equals("1")){
                    siguiente();
                }else{
                    AlertDialog.Builder dialogo1 = new AlertDialog.Builder(QueNecesitas.this);
                    dialogo1.setTitle("Atención");
                    dialogo1.setMessage(tv_horario.getText().toString().trim());
                    dialogo1.setCancelable(false);
                    dialogo1.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogo1, int id) {
                        }
                    });
                    dialogo1.show();
                }

                break;
        }
    }

    private void siguiente() {
        SharedPreferences carrito = getSharedPreferences(getString(R.string.sql_loquesea), MODE_PRIVATE);
        SharedPreferences.Editor ed_c = carrito.edit();
        ed_c.putString("que_necesitas", et_que_necesitas.getText().toString().trim());
        ed_c.commit();

        if (et_que_necesitas.getText().toString().trim().length()>3){
            startActivity(new Intent(this,Principal_lo_que_sea
                    .class));
        }else{
            AlertDialog.Builder dialogo1 = new AlertDialog.Builder(this);
            dialogo1.setTitle("Atención");
            dialogo1.setMessage("Introduzca su pedido");
            dialogo1.setCancelable(false);
            dialogo1.setPositiveButton("OK", null);
            dialogo1.show();
        }


    }


    public void servicio_mostrar_horario_loquesea()
    {
        pDialog = new ProgressDialog(QueNecesitas.this);
        pDialog.setTitle(getString(R.string.app_name));
        pDialog.setMessage("Verificando disponibilidad de servicio");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();

        SharedPreferences carrito = getSharedPreferences(getString(R.string.sql_pedido), MODE_PRIVATE);

        try {
            JSONObject jsonParam= new JSONObject();
            jsonParam.put("latitud", String.valueOf(carrito.getString("milatitud","0")));
            jsonParam.put("longitud", String.valueOf(carrito.getString("milongitud","0")));

            String url=getString(R.string.servidor) + "frmCarrera.php?opcion=mostrar_horario_loquesea";

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
                            pDialog.cancel();
                            try {
                                suceso=new Suceso(respuestaJSON.getString("suceso"),respuestaJSON.getString("mensaje"));
                                disponible=suceso.getSuceso();
                                tv_horario.setText(suceso.getMensaje());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    pDialog.cancel();
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
}