package com.elisoft.sao_pedido.mi_perfil;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
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
import com.elisoft.sao_pedido.Principal;
import com.elisoft.sao_pedido.R;
import com.elisoft.sao_pedido.Suceso;
import com.elisoft.sao_pedido.notificaciones.SharedPrefManager;

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
import java.util.HashMap;
import java.util.Map;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class Confirmar_sms_celular extends AppCompatActivity implements View.OnClickListener {

    private String celular;
    Button codeInputButton;
    TextView enviar_mensaje;
    EditText inputCode;
    TextView mensaje;
    Suceso suceso;
    ProgressDialog pDialog;


    private static final String TAG = "VerificationActivity";

    private boolean mShouldFallback = true;
    private static final String[] SMS_PERMISSIONS = { Manifest.permission.INTERNET,
            Manifest.permission.READ_SMS,
            Manifest.permission.RECEIVE_SMS,
            Manifest.permission.ACCESS_NETWORK_STATE };
    ProgressBar cargando;
    Handler handle=new Handler();

    int i=0;
    private boolean mIsSmsVerification;
    public static final String SMS = "sms";
    private String mPhoneNumber;
    TextView tv_tiempo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmar_sms_celular);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mensaje= findViewById(R.id.mensaje);
        codeInputButton= findViewById(R.id.codeInputButton);
        inputCode= findViewById(R.id.inputCode);
        cargando= findViewById(R.id.cargando);
        enviar_mensaje= findViewById(R.id.enviar_mensaje);
        tv_tiempo= findViewById(R.id.tv_tiempo);


        inputCode.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (verificar_codigo(s)) {
                    codeInputButton.setEnabled(true);
                    inputCode.setTextColor(Color.BLACK);
                } else {
                    codeInputButton.setEnabled(false);
                    inputCode.setTextColor(Color.RED);
                }
            }
        });


        try{
            Bundle bundle=getIntent().getExtras();
            celular=""+bundle.getString("celular");
            mPhoneNumber = "+591"+celular;
            final String method ="sms";
            mIsSmsVerification = method.equalsIgnoreCase("sms");
            progress_en_proceso();
            enviar_sms();
            TextView tv_titulo= findViewById(R.id.tv_titulo);
            tv_titulo.setText("Verificar "+mPhoneNumber);

        }catch (Exception e)
        {
            finish();
        }


        codeInputButton.setOnClickListener(this);
        enviar_mensaje.setOnClickListener(this);

    }




    private boolean verificar_codigo(CharSequence s) {
        boolean sw=false;
        try{

            if(s.toString().trim().length()>=4)
            {
                sw=true;
            }
        }catch (Exception e)
        {
            sw=false;
        }
        return sw;
    }
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.codeInputButton) {
            verificar_codigo();
        }
        else if(v.getId()==R.id.enviar_mensaje)
        {
            enviar_mensaje.setVisibility(View.INVISIBLE);
            progress_en_proceso();

            enviar_sms();
        }
    }



    //USUARIO
    public void verificar_codigo() {
        String imei="";
        SharedPreferences perfil = getSharedPreferences("perfil", MODE_PRIVATE);
        final String token = SharedPrefManager.getInstance(this).getDeviceToken();

        imei=perfil.getString("imei", "");

        if (token != null || token != "") {

            servicio_cambiar_numero_celular(
                    perfil.getString("id_usuario", ""),
                    celular,
                    inputCode.getText().toString().trim(),
                    perfil.getString("id_facebook", ""),
                    perfil.getString("id_google", ""),
                    token,
                    imei,
                    perfil.getString("email","")
            );
        }
    }

    private void servicio_cambiar_numero_celular(String id_usuario,
                                                 String celular,
                                                 String codigo,
                                                 String id_facebook,
                                                 String id_google,
                                                 String token,
                                                 String imei,
                                                 String email) {

        //para el progres Dialog
        pDialog = new ProgressDialog( Confirmar_sms_celular.this);
        pDialog.setTitle(getString(R.string.app_name));
        pDialog.setMessage("Verificando el Codigo");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(true);
        pDialog.show();


        try {
            //url del servicio
            String v_url= getString(R.string.servidor) + "frmUsuario.php?opcion=cambiar_numero_celular";

            JSONObject jsonParam= new JSONObject();
            //parametros a enviar
            jsonParam.put("id_usuario", id_usuario);
            jsonParam.put("celular", celular);
            jsonParam.put("codigo", codigo);
            jsonParam.put("id_facebook", id_facebook);
            jsonParam.put("id_google", id_google);
            jsonParam.put("token", token);
            jsonParam.put("imei", imei);
            jsonParam.put("correo", email);
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

                                    JSONArray dato=respuestaJSON.getJSONArray("perfil");
                                    String snombre= dato.getJSONObject(0).getString("nombre");
                                    String sapellido=dato.getJSONObject(0).getString("apellido") ;
                                    String semail= dato.getJSONObject(0).getString("correo") ;
                                    String scelular= dato.getJSONObject(0).getString("celular") ;
                                    String sid= dato.getJSONObject(0).getString("id") ;
                                    String sid_google= dato.getJSONObject(0).getString("id_google") ;
                                    String sid_facebook= dato.getJSONObject(0).getString("id_facebook") ;
                                    String scodigo= dato.getJSONObject(0).getString("codigo") ;
                                    String scorreo= dato.getJSONObject(0).getString("correo") ;
                                    cargar_datos(snombre,sapellido,semail,scelular,sid,scodigo,sid_facebook,sid_google,scorreo);

                                    //final
                                    mensaje_error_final(suceso.getMensaje());
                                } else  {
                                    mensaje_error_final(suceso.getMensaje());
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
            myRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            queue.add(myRequest);

        } catch (Exception e) {

        }

    }



    public void enviar_sms()
    {
        Servicio_mensaje hilo_moto = new Servicio_mensaje();
        hilo_moto.execute(getString(R.string.servidor) + "frmUsuario.php?opcion=enviar_sms", "1",celular);// parametro que recibe el doinbackground

    }


    //USUARIO
    public void obtener_codigo()
    {
        try {

            Uri smsUri = Uri.parse("content://sms/inbox");
            Cursor cursor = getContentResolver().query(smsUri, null, null, null, null);
             /* Moving To First */
            if (!cursor.moveToFirst()) { /* false = cursor is empty */
                return;
            }
            String numero="";
            for (int k = 0; k < cursor.getColumnCount() && !cursor.getString(13).equals("+59177830099"); k++) {
                numero=cursor.getString(12);
                try{
                    cursor.moveToNext();
                }
                catch (Exception e)
                {
                    k=cursor.getColumnCount()+1;
                }

            }
            if (cursor.getString(13).equals("+59177830099")) {
                inputCode.setText(obtener_codigo(cursor.getString(12)));
            }
            cursor.close();
        }catch (Exception e)
        {
            Log.e("sms",e.toString());
        }
    }
    //USUARIO
    public String obtener_codigo(String texto)
    {String codigo="";
        for (int i=0;i<texto.length();i++)
        {
            if(es_numero(String.valueOf(texto.charAt(i))))
            {
                codigo+=texto.charAt(i);
            }
        }
        return codigo;
    }
    //USUARIO
    public boolean es_numero(String numero)
    {
        try{
            Long.parseLong(numero);
        }catch (Exception e)
        {
            return false;
        }
        return true;
    }

    //USUARIO
    public  void progress_en_proceso()
    {

        i=0;
        new Thread(new Runnable() {
            @Override
            public void run() {

                while (i<59)
                {
                    i=i+1;

                    handle.post(new Runnable() {
                        @Override
                        public void run() {
                            cargando.setProgress(i);

                            if(i==60)
                            {
                                tv_tiempo.setText("01:00");
                            }
                            else
                            {
                                tv_tiempo.setText("00:"+i);
                            }

                             if( i==5)
                            {
                                obtener_codigo();

                            }else if( i==10)
                             {
                                 obtener_codigo();

                             }else if( i==20)
                             {
                                 obtener_codigo();

                             }else if( i==30)
                             {
                                 obtener_codigo();

                             }
                            else if( i==50)
                            {
                                obtener_codigo();

                            }else if(i>=55)
                            {
                                enviar_mensaje.setVisibility(View.VISIBLE);
                            }

                        }
                    });
                    try{

                        Thread.sleep(1000);
                    }catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        }).start();


    }
    //USUARIO
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }






    //servicio de verificar si ya esta registrado el cellular.
    public void cargar_datos(String nombre, String apellido, String email, String celular, String id, String codigo,String id_facebook,String id_google ,String correo)
    {
        SharedPreferences usuario=getSharedPreferences("perfil",MODE_PRIVATE);
        SharedPreferences.Editor editar=usuario.edit();
        editar.putString("nombre",nombre);
        editar.putString("apellido",apellido);
        editar.putString("email",email);
        editar.putString("celular",celular);
        editar.putString("id_usuario",id);
        editar.putString("codigo",codigo);
        editar.putString("id_facebook",id_facebook);
        editar.putString("id_google",id_google);
        editar.putString("email",correo);
        editar.putString("login_usuario","1");
        editar.commit();
    }
    public void saltar_principal()
    {
        startActivity(new Intent(this, Principal.class));
    }
    private void iniciar_sesion() {
        SharedPreferences usuario=getSharedPreferences("perfil",MODE_PRIVATE);
        if(usuario.getString("id_usuario","").equals("")==false  && usuario.getString("id_usuario","").equals("null")==false) {

            saltar_principal();

        }
    }
    public void mensaje(String mensaje)
    {
        Toast toast = Toast.makeText(this,mensaje, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.show();
    }



    public class Servicio_mensaje extends AsyncTask<String,Integer,String> {
        //para el usuario

        @Override
        protected String doInBackground(String... params) {

            String cadena = params[0];
            URL url = null;  // url donde queremos obtener informacion
            String devuelve = "-1";
            if(pDialog.isShowing()) {//borre el ==true
                devuelve = "";
                if (params[1] == "1") { //mandar JSON metodo post para login
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
                        jsonParam.put("celular", params[2]);

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

                            //Creamos un objeto JSONObject para poder acceder a los atributos (campos) del objeto.
                            JSONObject respuestaJSON = new JSONObject(result.toString());//Creo un JSONObject a partir del
                            // StringBuilder pasando a cadena.                    }

                            SystemClock.sleep(950);

                            //Accedemos a vector de resultados.
                            suceso = new Suceso(respuestaJSON.getString("suceso"), respuestaJSON.getString("mensaje"));// suceso es el campo en el Json
                            if (suceso.getSuceso().equals("1")) {
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
            }
            return devuelve;
        }


        @Override
        protected void onPreExecute() {
            //para el progres Dialog

            //para el progres Dialog
            pDialog = new ProgressDialog(Confirmar_sms_celular.this);
            pDialog.setTitle(getString(R.string.app_name));
            pDialog.setMessage("Enviando sms de verificación.");
            pDialog.setIndeterminate(true);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //ocultamos proggress dialog
            // Log.e("onPostExcute=", "" + s);
            pDialog.cancel();//ocultamos proggress dialog

            if (s.equals("1")) {

                obtener_codigo();
            }
            else if(s.equals("2"))
            {
                mensaje_error(suceso.getMensaje());
            }
            else if(s.equals(""))
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

    public void mensaje_error(String mensaje)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.app_name));
        builder.setMessage(mensaje);
        builder.setPositiveButton("OK",null);
        builder.create();
        builder.show();
    }
    public void mensaje_error_final(String mensaje)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.app_name));
        builder.setMessage(mensaje);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
               finish();
            }
        });
        builder.create();
        builder.show();
    }




}
