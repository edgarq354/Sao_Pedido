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

import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.elisoft.sao_pedido.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Principal_lo_que_sea extends AppCompatActivity implements View.OnClickListener {

    TextView tv_lo_que_necesitas,tv_direccion_recoger,tv_direccion_entrega,tv_monto_envio,tv_tiempo_preparacion,tv_punto_negocio;
    Button bt_siguiente;

    private RequestQueue queue=null;
    Suceso suceso;
    private ProgressDialog pDialog;
    AlertDialog alert2 = null;

    double monto_envio=0;
    int cantidad_solicitud_tarifa=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal_lo_que_sea);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tv_lo_que_necesitas=findViewById(R.id.tv_lo_que_necesitas);
        tv_direccion_recoger=findViewById(R.id.tv_direccion_recoger);
        tv_direccion_entrega=findViewById(R.id.tv_direccion_entrega);
        tv_tiempo_preparacion=findViewById(R.id.tv_tiempo_preparacion);
        tv_punto_negocio=findViewById(R.id.tv_punto_negocio);
        tv_monto_envio=findViewById(R.id.tv_monto_envio);
        bt_siguiente=findViewById(R.id.bt_siguiente);

        tv_direccion_recoger.setOnClickListener(this);
        bt_siguiente.setOnClickListener(this);

        cantidad_solicitud_tarifa=0;
    }

    @Override
    protected void onStart() {
        cargar_datos();
        super.onStart();
    }

    private void cargar_datos() {
        SharedPreferences carrito = getSharedPreferences(getString(R.string.sql_loquesea), MODE_PRIVATE);
        tv_lo_que_necesitas.setText(carrito.getString("que_necesitas",""));
        tv_direccion_recoger.setText(carrito.getString("direccion_recoger",""));
        tv_direccion_entrega.setText(carrito.getString("direccion_entrega",""));
        tv_punto_negocio.setText(carrito.getString("latitud_recoger","")+","+carrito.getString("longitud_recoger",""));
        tv_monto_envio.setText(carrito.getString("monto_envio",""));

        if(Double.parseDouble(carrito.getString("latitud_recoger","0"))!=0){
            servicio_calcular_tarifa();
        }
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
                siguiente();
                break;
            case R.id.tv_direccion_recoger:
                direccion_recoger();
                break;
            case R.id.tv_direccion_entrega:
                direccion_entrega();
                break;
        }

    }

    private void direccion_entrega() {
        startActivity(new Intent(this,Marcar_direccion_cliente.class));
    }

    private void direccion_recoger() {
        startActivity(new Intent(this,Marcar_direccion_lugar.class));
    }

    private void siguiente() {
        SharedPreferences carrito = getSharedPreferences(getString(R.string.sql_loquesea), MODE_PRIVATE);
        if (Double.parseDouble(carrito.getString("latitud_recoger","0"))!=0)
        {
            escribir_referencia_moto(5,0);
        }else
        {
            mensaje_error("Marque la dirección donde se recogera su pedido.");
        }


    }

    public void  escribir_referencia_moto(final int clase_vehiculo,final int tipo_pedido_empresa)
    {

        final SharedPreferences pedido = getSharedPreferences(getString(R.string.sql_pedido), MODE_PRIVATE);

        double total= monto_envio ;

        // get prompts.xml view
        LayoutInflater layoutInflater = LayoutInflater.from(Principal_lo_que_sea.this);
        View promptView = layoutInflater.inflate(R.layout.escribir_pedido, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Principal_lo_que_sea.this);
        alertDialogBuilder.setView(promptView);

        final Button bt_cancelar= promptView.findViewById(R.id.bt_cancelar);
        final Button bt_pedir= promptView.findViewById(R.id.bt_pedir);
        final Button bt_pedir_whatsapp= promptView.findViewById(R.id.bt_pedir_whatsapp);
        final EditText et_referencia= promptView.findViewById(R.id.et_referencia);
        final TextView tv_total= promptView.findViewById(R.id.tv_total);

        et_referencia.setText(pedido.getString("referencia",""));

        tv_total.setText("Envio: "+total+" Bs");

        bt_cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alert2.cancel();
            }
        });

        bt_pedir_whatsapp.setVisibility(View.INVISIBLE);

        bt_pedir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editar=pedido.edit();
                editar.putString("referencia",et_referencia.getText().toString().trim());
                editar.commit();

                pedir_taxi("0", et_referencia.getText().toString().trim(),clase_vehiculo,tipo_pedido_empresa);
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                alert2.cancel();
            }
        });


        // create an alert dialog
        alert2 = alertDialogBuilder.create();
        alert2.show();
    }

    public void pedir_taxi(String numero, String referencia,int clase_vehiculo,int tipo_pedido_empresa){
        ///verifica si el GPS esta activo.
        SharedPreferences pedido = getSharedPreferences(getString(R.string.sql_loquesea), MODE_PRIVATE);



        if (referencia.length() >= 3) {

            SharedPreferences pedido_ultimo1 = getSharedPreferences("ultimo_pedido", MODE_PRIVATE);

            SharedPreferences.Editor editar1=pedido_ultimo1.edit();
            editar1.putString("nombre_taxi", "");
            editar1.putString("celular", "");
            editar1.putString("marca", "");
            editar1.putString("placa", "");
            editar1.putString("color", "");
            editar1.putString("id_pedido", "");
            editar1.putString("que_necesitas", "");
            editar1.putInt("notificacion_cerca", 0);
            editar1.putInt("notificacion_llego", 0);

            editar1.commit();
            limpiar_pedido_antiguo();



            Intent datos_pedido = new Intent(this, Buscar_repartidor.class);
            //datos_pedido.putExtra("latitud", addressLatLng.latitude);
            //datos_pedido.putExtra("longitud", addressLatLng.longitude);
            datos_pedido.putExtra("latitud_recoger",Double.parseDouble( pedido.getString("latitud_recoger","0")));
            datos_pedido.putExtra("longitud_recoger", Double.parseDouble(pedido.getString("longitud_recoger","0")));
            datos_pedido.putExtra("direccion_recoger",pedido.getString("direccion_recoger",""));
            datos_pedido.putExtra("que_necesitas",pedido.getString("que_necesitas",""));

            datos_pedido.putExtra("latitud",Double.parseDouble( pedido.getString("latitud_entrega","0")));
            datos_pedido.putExtra("longitud", Double.parseDouble(pedido.getString("longitud_entrega","0")));
            //datos_pedido.putExtra("direccion",pedido.getString("direccion_entrega",""));
            datos_pedido.putExtra("direccion","Lo que sea");

            datos_pedido.putExtra("referencia", referencia);
            datos_pedido.putExtra("que_necesitas", pedido.getString("que_necesitas",""));
            datos_pedido.putExtra("numero",numero);
            datos_pedido.putExtra("monto_envio",monto_envio);
            datos_pedido.putExtra("clase_vehiculo",clase_vehiculo);
            datos_pedido.putExtra("tipo_pedido_empresa",tipo_pedido_empresa);
            startActivity(datos_pedido);
        } else {
            AlertDialog.Builder dialogo1 = new AlertDialog.Builder(this);
            dialogo1.setTitle("Atención");
            dialogo1.setMessage("Por favor introduzca una referencia para ayudar al conductor a ubicarlo.");
            dialogo1.setCancelable(false);
            dialogo1.setPositiveButton("OK", null);
            dialogo1.show();
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


    public void servicio_calcular_tarifa()
    {
        pDialog = new ProgressDialog(Principal_lo_que_sea.this);
        pDialog.setTitle(getString(R.string.app_name));
        pDialog.setMessage("Calculando la tarifa");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();

        SharedPreferences carrito = getSharedPreferences(getString(R.string.sql_loquesea), MODE_PRIVATE);

        try {
            JSONObject jsonParam= new JSONObject();
            jsonParam.put("latitud_inicio", String.valueOf(carrito.getString("latitud_recoger","0")));
            jsonParam.put("longitud_inicio", String.valueOf(carrito.getString("longitud_recoger","0")));
            jsonParam.put("latitud_fin", String.valueOf(carrito.getString("latitud_entrega","0")));
            jsonParam.put("longitud_fin", String.valueOf(carrito.getString("longitud_entrega","0")));

            String url=getString(R.string.servidor) + "frmCarrera.php?opcion=calcular_tarifa";

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

                                if (suceso.getSuceso().equals("1")) {
                                    String minuto=respuestaJSON.getString("minutos");
                                    String pedido=respuestaJSON.getString("loquesea");

                                    //final
                                    int minuto1= Integer.parseInt(minuto);

                                    SharedPreferences scarrito=getSharedPreferences(getString(R.string.sql_loquesea),MODE_PRIVATE);
                                    int tiempo_pre=scarrito.getInt("tiempo_preparacion",0);
                                    minuto1=tiempo_pre+minuto1;

                                    int tiempo_menor=minuto1+18;
                                    int tiempo_mayor=minuto1+25;

                                    tv_tiempo_preparacion.setText(tiempo_menor+"-"+tiempo_mayor+ " min.");

                                    tv_monto_envio.setText( pedido+" Bs.");

                                    try{
                                        monto_envio=Double.parseDouble(pedido);

                                        SharedPreferences carrito = getSharedPreferences(getString(R.string.sql_loquesea), MODE_PRIVATE);
                                        SharedPreferences.Editor ed_c = carrito.edit();
                                        ed_c.putString("monto_envio", String.valueOf(monto_envio));
                                        ed_c.commit();
                                    }catch (Exception eee)
                                    {

                                    }
                                } else  {
                                    if(cantidad_solicitud_tarifa<3)
                                    {
                                        servicio_calcular_tarifa();
                                    }else{
                                        mensaje_error(suceso.getMensaje());
                                    }
                                    cantidad_solicitud_tarifa++;
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                mensaje_error("Falla en tu conexión a Internet.");
                            }

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    pDialog.cancel();
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


    public void mensaje_error(String mensaje)
    {
        AlertDialog.Builder dialogo1 = new AlertDialog.Builder(this);
        dialogo1.setTitle("Importante");
        dialogo1.setMessage(mensaje);
        dialogo1.setCancelable(false);
        dialogo1.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogo1, int id) {

            }
        });

        dialogo1.show();


    }

}