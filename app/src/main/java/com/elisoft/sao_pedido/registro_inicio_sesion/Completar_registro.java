package com.elisoft.sao_pedido.registro_inicio_sesion;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.elisoft.sao_pedido.Principal;
import com.elisoft.sao_pedido.R;
import com.elisoft.sao_pedido.Suceso;
import com.elisoft.sao_pedido.informacion.Pagina;
import com.elisoft.sao_pedido.notificaciones.SharedPrefManager;
import com.facebook.login.LoginManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;


public class Completar_registro extends AppCompatActivity implements View.OnClickListener{
    Button siguiente;
    Suceso suceso;
    ProgressDialog pDialog;
    String celular,email,nombre,apellido,codigo,contasenia,imei="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_completar_registro);

        siguiente= findViewById(R.id.siguiente);

        siguiente.setOnClickListener(this);

        Bundle bundle =getIntent().getExtras();
        try{
            nombre=bundle.getString("nombre");
            celular=bundle.getString("celular");
            apellido=bundle.getString("apellido");
            email=bundle.getString("email");
            codigo="";
            contasenia="";
        }catch (Exception e)
        {
            finish();
        }


    }


    public void verificar_permiso_imei()
    {
        final String[] PERMISSIONS = { Manifest.permission.INTERNET,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.ACCESS_NETWORK_STATE };

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_PHONE_STATE)) {
            //YA LO CANCELE Y VOUELVO A PERDIR EL PERMISO.

            AlertDialog.Builder dialogo1 = new AlertDialog.Builder(this);
            dialogo1.setTitle("Atención!");
            dialogo1.setMessage("Debes otorgar permisos de acceso al ID del Telefono por tema de Seguridad.");
            dialogo1.setCancelable(false);
            dialogo1.setPositiveButton("Solicitar permiso", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogo1, int id) {
                    dialogo1.cancel();
                    ActivityCompat.requestPermissions(Completar_registro.this,
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
            ActivityCompat.requestPermissions(Completar_registro.this,
                    PERMISSIONS,
                    1);
        }
    }


    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.siguiente) {

                    final String token = SharedPrefManager.getInstance(this).getDeviceToken();

                    if (token != null || token == "") {

                        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

                        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            imei = Settings.Secure.getString(
                                    getContentResolver(),
                                    Settings.Secure.ANDROID_ID);

                            servicio_registrar_usuario_autenticar(nombre, apellido, celular, email, contasenia, token, codigo,imei);
                        } else {
                            if (ActivityCompat.checkSelfPermission(Completar_registro.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                                verificar_permiso_imei();
                            }else {
                                imei = "";

                                servicio_registrar_usuario_autenticar(nombre, apellido, celular, email, contasenia, token, codigo,imei);
                            }
                        }



                    } else {

                        mensaje_error("No se a podido generar el Token. porfavor active sus datos de Red e instale Google Pay Service");
                    }

        }
    }

    private void servicio_registrar_usuario_autenticar(String nombre, String apellido, String celular, String email, String contasenia, String token, String codigo, String imei) {

        //para el progres Dialog
        pDialog = new ProgressDialog(Completar_registro.this);
        pDialog.setTitle(getString(R.string.app_name));
        pDialog.setMessage("Registrando.");
        pDialog.setIndeterminate(true);
        pDialog.setCancelable(false);
        pDialog.show();


        try {
            //url del servicio
            String v_url= getString(R.string.servidor) + "traigo/frmUsuario.php?opcion=registrar_usuario_autenticar";

            JSONObject jsonParam= new JSONObject();
            //parametros a enviar
            jsonParam.put("nombre", nombre);
            jsonParam.put("apellido", apellido);
            jsonParam.put("celular",celular);
            jsonParam.put("email", email);
            jsonParam.put("contrasenia", contasenia);
            jsonParam.put("token", token);
            jsonParam.put("codigo", codigo);
            jsonParam.put("aplicacion",getString(R.string.app_name));
            jsonParam.put("imei",imei);
            RequestQueue queue = Volley.newRequestQueue(this);


            JsonObjectRequest myRequest= new JsonObjectRequest(
                    Request.Method.POST,
                    v_url,
                    jsonParam,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject respuestaJSON) {
                            //ocultamos proggress dialog
                            pDialog.dismiss();

                            try {
                                suceso=new Suceso(respuestaJSON.getString("suceso"),respuestaJSON.getString("mensaje"));

                                if (suceso.getSuceso().equals("1")) {

                                    String id=respuestaJSON.getString("id_usuario");
                                    cargar_datos(id);

                                    inicio_principal();

                                } else  {
                                    errorRegistro();
                                }

                                //...final de final....................





                            } catch (JSONException e) {
                                e.printStackTrace();
                                pDialog.dismiss();
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


// TIEMPO DE ESPERA
            myRequest.setRetryPolicy(new DefaultRetryPolicy(6000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            queue.add(myRequest);

        } catch (Exception e) {

        }
    }

    public void Registrate()
    {

    }



    public void mensaje(String mensaje)
    {
        Toast toast = Toast.makeText(this,mensaje, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.show();
    }

    public boolean validar_contrasenia(String contrasenia1, String contrasenia2)
    { boolean sw=false;
        if(contrasenia1.length()>6)
        {
            sw=true;
        }
        return (contrasenia1.equals(contrasenia2) && sw == true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        LoginManager.getInstance().logOut();
        onBackPressed();
        return super.onSupportNavigateUp();
    }





    public void cargar_datos(String id)
    {
        SharedPreferences usuario=getSharedPreferences("perfil",MODE_PRIVATE);
        SharedPreferences.Editor editar=usuario.edit();
        editar.putString("nombre",nombre);
        editar.putString("apellido",apellido);
        editar.putString("email",email);
        editar.putString("celular",celular);
        editar.putString("id_usuario",id);
        editar.putString("login_usuario","1");
        editar.commit();
    }

    private void inicio_principal() {
        SharedPreferences usuario=getSharedPreferences("perfil",MODE_PRIVATE);
        if(usuario.getString("id_usuario","").equals("")==false  && usuario.getString("id_usuario","").equals("null")==false) {



                    saltar_principal();



        }
    }
    public void saltar_principal()
    {
        startActivity(new Intent(this, Principal.class));
        finish();
    }
    private void errorRegistro() {
        mensaje_error(suceso.getMensaje());
        /*mensaje("Usuario Incorrecto.. puede intentar iniciar sesion con su cuenta de facebook.");
        finish();*/
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

    public void terminos(View v)
    {
        Intent intent=new Intent(this, Pagina.class);
        intent.putExtra("titulo","Terminos y condiciones de uso");
        intent.putExtra("url",getString(R.string.servidor)+"terminos_condiciones.php");
        startActivity(intent);
    }
}
