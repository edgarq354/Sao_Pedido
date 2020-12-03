package com.elisoft.sao_pedido;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.elisoft.sao_pedido.notificaciones.SharedPrefManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class Cancelar_pedido_usuario extends AppCompatActivity implements View.OnClickListener {

    Button bt_cancelar;
    EditText et_otro;
    RadioButton rb_uno,rb_dos,rb_tres,rb_cuatro,rb_cinco,rb_otro;
    Suceso suceso;
    ProgressDialog pDialog;
    int id_pedido=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cancelar_pedido_usuario);
        bt_cancelar= findViewById(R.id.bt_cancelar);
        rb_uno= findViewById(R.id.rb_uno);
        rb_dos= findViewById(R.id.rb_dos);
        rb_tres= findViewById(R.id.rb_tres);
        rb_cuatro= findViewById(R.id.rb_cuatro);
        rb_cinco= findViewById(R.id.rb_cinco);
        rb_otro= findViewById(R.id.rb_otro);
        et_otro= findViewById(R.id.et_otro);
        bt_cancelar.setOnClickListener(this);
        try{
            Bundle bundle=getIntent().getExtras();
            id_pedido= Integer.parseInt(bundle.getString("id_pedido"));
        }catch (Exception e)
        {
            finish();
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

    @Override
    public void onClick(View view) {

        if(R.id.bt_cancelar==view.getId()) {
            String detalle = et_otro.getText().toString();
            if (rb_uno.isChecked()) {
                detalle = rb_uno.getText().toString().trim();
            } else if (rb_dos.isChecked()) {
                detalle = rb_dos.getText().toString().trim();
            } else if (rb_tres.isChecked()) {
                detalle = rb_tres.getText().toString().trim();
            } else if (rb_cuatro.isChecked()) {
                detalle = rb_cuatro.getText().toString().trim();
            } else if (rb_cinco.isChecked()) {
                detalle = rb_cinco.getText().toString().trim();
            } else if(rb_otro.isChecked()){
                detalle=et_otro.getText().toString().trim();
            }
            SharedPreferences perfil = getSharedPreferences("perfil", Context.MODE_PRIVATE);
             servicio_cancelar_pedido_en_camino( perfil.getString("id_usuario", ""), String.valueOf(id_pedido),detalle);
        }
    }


    //servicio de cancelar de pedido
    private void servicio_cancelar_pedido_en_camino(String id_usuario, String id_pedido,String detalle) {


        try {

            pDialog = new ProgressDialog(Cancelar_pedido_usuario.this);
            pDialog.setTitle(getString(R.string.app_name));
            pDialog.setMessage("Cargando detalle");
            pDialog.setIndeterminate(true);
            pDialog.setCancelable(false);
            pDialog.show();

            String token= SharedPrefManager.getInstance(this).getDeviceToken();

            JSONObject jsonParam= new JSONObject();
            jsonParam.put("id_usuario",id_usuario);
            jsonParam.put("id_pedido", id_pedido);
            jsonParam.put("detalle", detalle);
            jsonParam.put("token", token);
            String url=getString(R.string.servidor) + "frmPedido.php?opcion=detalle_cancelar_pedido_usuario";
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

                                    finish();

                                }else if(suceso.getSuceso().equals("2"))
                                {

                                    mensaje(suceso.getMensaje());
                                }
                                else
                                {
                                    mensaje_error("Error: Al conectar con el Servidor.\nVerifique su acceso a Internet.");
                                }
                            } catch (JSONException e) {
                                pDialog.dismiss();

                                e.printStackTrace();
                                mensaje_error("Falla en tu conexión a Internet.");
                            }

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    pDialog.dismiss();
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


            queue.add(myRequest);


        } catch (Exception e) {

        }

    }




    public void mensaje_error(String mensaje)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.app_name));
        builder.setMessage(mensaje);
        builder.setPositiveButton("OK", null);
        builder.create();
        builder.show();
    }
    private void eliminar_pedido() {

        SharedPreferences pedido = getSharedPreferences("ultimo_pedido", MODE_PRIVATE);
        SharedPreferences.Editor editar=pedido.edit();
        editar.putString("nombre_taxi", "");
        editar.putString("celular", "");
        editar.putString("marca", "");
        editar.putString("placa", "");
        editar.putString("color", "");
        editar.putString("id_pedido", "");
        editar.commit();
    }

    public void mensaje(String mensaje)
    {
        Toast toast = Toast.makeText(this,mensaje, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.show();
    }



}
