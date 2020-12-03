package com.elisoft.sao_pedido;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.elisoft.sao_pedido.notificaciones.SharedPrefManager;
import com.elisoft.sao_pedido.servicio.Servicio_cargar_punto_google;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class Servicio_pedir_movil extends IntentService {
    public static final String ACTION_PROGRESO="servicio_pedido_proceso";
    public static final String ACTION_FINAL="servicio_pedido_finalizo";
   int  id_pedido=0;
   int estado_pedido=0;
   Suceso suceso;
   Intent padre;
    private static final String TAG = Servicio_pedir_movil.class.getSimpleName();
    RequestQueue queue=null;

    int tiempo=0;


    public Servicio_pedir_movil( ) {

        super("Servicio_pedir_movil");
    }



    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            id_pedido= intent.getIntExtra("id_pedido", 0);
            final String action = intent.getAction();
            handleActionRun();
            padre=intent;

        }
    }

    /**
     * Maneja la acción de ejecución del servicio
     */
    private void handleActionRun() {
        try {
            int i=0;

            tiempo=0;
            int tiempo_notificacion=0;
            int diametro_maximo=2000;
            int diametro_minimo=1000;

            do{

                tiempo++;
                if(tiempo%8==0 )
                {
                    if(diametro_maximo==2000)
                    {
                        diametro_minimo=1000;
                        verificar_pedido( diametro_minimo,  diametro_maximo,  1);
                        diametro_maximo=5000;
                    }else if(diametro_maximo==5000)
                    {
                        diametro_minimo=3000;
                        verificar_pedido( diametro_minimo,  diametro_maximo,  1);
                        diametro_maximo+=1000;
                    }else{
                        verificar_pedido( diametro_minimo,  diametro_maximo,  0);
                    }
                }

               // /Comunicamos el progreso
                Intent bcIntent = new Intent();
                bcIntent.setAction(ACTION_PROGRESO);
                bcIntent.putExtra("id_pedido", id_pedido);
                bcIntent.putExtra("estado", estado_pedido);
                bcIntent.putExtra("tiempo", tiempo);
                sendBroadcast(bcIntent);

                SharedPreferences pedido = getSharedPreferences("ultimo_pedido", MODE_PRIVATE);
                try{
                    id_pedido=Integer.parseInt(pedido.getString("id_pedido",""));

                }catch (Exception e){
                    try{
                        SharedPreferences prefe = getSharedPreferences("pedido_en_proceso", Context.MODE_PRIVATE);
                        id_pedido = Integer.parseInt(prefe.getString("id_pedido", "0"));
                    }catch (Exception ee){
                       id_pedido=0;
                    }

                }

                tareaLarga();
            }while( tiempo<60 &&  estado_pedido==0 && id_pedido!=0);


            if(estado_pedido==1)
            {
                SharedPreferences pedido = getSharedPreferences("ultimo_pedido", MODE_PRIVATE);
                Intent bcIntent = new Intent();
                bcIntent.setAction(ACTION_FINAL);
                bcIntent.putExtra("estado", 1);
                sendBroadcast(bcIntent);

                reproducir_sonido();
                Intent numero = new Intent(this, Pedido_usuario.class);
                numero.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                numero.putExtra("id_pedido",pedido.getString("id_pedido",""));
                numero.putExtra("latitud",Double.parseDouble(pedido.getString("latitud","0")));
                numero.putExtra("longitud",Double.parseDouble(pedido.getString("longitud","0")));
                startActivity(numero);
            }else {
                try {

                    SharedPreferences pedido=getSharedPreferences("ultimo_pedido",MODE_PRIVATE);
                    SharedPreferences pedido_proceso=getSharedPreferences("pedido_en_proceso",MODE_PRIVATE);
                    SharedPreferences usuario = getSharedPreferences("perfil", MODE_PRIVATE);
                    String id = usuario.getString("id_usuario", "");
                    //dibuja en el mapa las taxi que estan cerca...
                    //hilo_taxi.execute(getString(R.string.servidor)+"frmTaxi.php?opcion=get_taxis_en_rango", "1","64.455","-18.533");// parametro que recibe el doinbackground
                    int id_pedido=0;
                    try {
                        id_pedido=Integer.parseInt(pedido.getString("id_pedido","0"));
                    }catch (Exception e){
                        try {
                            id_pedido=Integer.parseInt(pedido_proceso.getString("id_pedido","0"));
                        }catch (Exception ee){
                            id_pedido=0;
                        }
                    }
                    if(id_pedido!=0) {
                        try {

                            servicio_pedir_cancelar_volley(id,String.valueOf(id_pedido));
                        } catch (Exception e) {
                            saltar_menu_principal();
                        }
                    }else
                    {
                        saltar_menu_principal();
                    }



                }catch (Exception e)
                {
                    Log.e("mensaje_error",e.toString());
                    saltar_menu_principal();
                }



            }
            // Quitar de primer plano
            stopForeground(true);
            // si nuestro estado esta en 2 o mayor .. quiere decir que no nuestro pedido se finalizo o sino se cancelo... sin nninguna carrera...

            stopService(new Intent(this,Servicio_pedir_movil.class));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void reproducir_sonido() {
        MediaPlayer mp;
        mp= MediaPlayer.create(this, R.raw.su_taxi_esta_en_camino);
        mp.start();
    }

    public  void  saltar_menu_principal()
    {
        Intent bcIntent = new Intent();
        bcIntent.setAction(ACTION_FINAL);
        bcIntent.putExtra("estado", 1);
        sendBroadcast(bcIntent);
        Intent numero = new Intent(this, Principal.class);
        numero.putExtra("finalizo_solicitud_pedido",1);
        numero.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(numero);
    }

    private void tareaLarga()
    {
        try {
            Thread.sleep(1000);
        } catch(InterruptedException e) {}
    }

    @Override
    public void onDestroy() {
        Log.e("servicio notificacion:","Servicio destruido...");

        // Emisión para avisar que se terminó el servicio
        Intent localIntent = new Intent(Constants.ACTION_PROGRESS_EXIT);
        LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
    }

    public void verificar_pedido(int diametro_minimo, int diametro_maximo, int enviar_notificacion) {
        try {

            servicio_pedir_taxi_volley( String.valueOf(id_pedido),String.valueOf(diametro_minimo),String.valueOf(diametro_maximo),String.valueOf(enviar_notificacion));

        } catch (Exception e) {
        }
    }


    //servicio de pedir taxi: enviar notificacion
    private void servicio_pedir_taxi_volley(String id_pedido,String diametro_minimo,String diametro_maximo,String enviar_notificacion) {


        try {



            String token= SharedPrefManager.getInstance(this).getDeviceToken();

            JSONObject jsonParam= new JSONObject();
            jsonParam.put("id_pedido",id_pedido);
            jsonParam.put("diametro_minimo", diametro_minimo);
            jsonParam.put("diametro_maximo", diametro_maximo);
            jsonParam.put("enviar_notificacion", enviar_notificacion);


            jsonParam.put("token", token);


            String url=getString(R.string.servidor) + "traigo/frmPedido.php?opcion=verificar_si_acepto_pedido_2";
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
                                    String sid_pedido = dato.getJSONObject(0).getString("id_pedido");
                                    String sempresa=dato.getJSONObject(0).getString("empresa");
                                    String s_id_empresa=dato.getJSONObject(0).getString("id_empresa");
                                    String scalificacion_conductor = dato.getJSONObject(0).getString("calificacion_conductor");
                                    String scalificacion_vehiculo = dato.getJSONObject(0).getString("calificacion_vehiculo");

                                    SharedPreferences pedido = getSharedPreferences("ultimo_pedido", MODE_PRIVATE);
                                    SharedPreferences.Editor editar = pedido.edit();
                                    editar.putString("nombre_taxi", snombre);
                                    editar.putString("celular", scelular);
                                    editar.putString("id_taxi", sid_taxi);
                                    editar.putString("marca", smarca);
                                    editar.putString("placa", splaca);
                                    editar.putString("color", scolor);
                                    editar.putString("latitud", dato.getJSONObject(0).getString("latitud"));
                                    editar.putString("longitud", dato.getJSONObject(0).getString("longitud"));
                                    editar.putString("abordo", dato.getJSONObject(0).getString("abordo"));
                                    editar.putString("estado", dato.getJSONObject(0).getString("estado"));
                                    editar.putString("id_pedido", sid_pedido);
                                    editar.putString("calificacion_conductor", scalificacion_conductor);
                                    editar.putString("calificacion_vehiculo", scalificacion_vehiculo);
                                    editar.putString("empresa",sempresa);
                                    editar.putString("id_empresa",s_id_empresa);
                                    editar.commit();

                                    SharedPreferences proceso = getSharedPreferences("pedido_en_proceso", MODE_PRIVATE);
                                    SharedPreferences.Editor editor = proceso.edit();
                                    editor.putString("id_pedido", "");
                                    editor.commit();
                                    estado_pedido=1;

                                    ejecutar_servicio_volley();


                                } else if (suceso.getSuceso().equals("2")) {
                                } else {
                                    estado_pedido=3;
                                }
                            } catch (JSONException e) {

                                e.printStackTrace();

                            }

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
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



    private void servicio_pedir_cancelar_volley(String id_usuario,String id_pedido) {


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
                                    //-----------------final-------------
                                    SharedPreferences pedido2 = getSharedPreferences("ultimo_pedido", MODE_PRIVATE);
                                    SharedPreferences.Editor editor2 = pedido2.edit();
                                    editor2.putString("id_pedido", "");
                                    editor2.putString("estado", "4");
                                    editor2.commit();


                                } else if (suceso.getSuceso().equals("2")) {
                                } else {

                                }

                                saltar_menu_principal();

                            } catch (JSONException e) {

                                e.printStackTrace();

                            }

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
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



    public void ejecutar_servicio_volley()
    {
        try{
            // startActivity(new Intent(BootUpService.this, Menu_taxi.class));
            Intent pushIntent=new Intent(Servicio_pedir_movil.this, Servicio_cargar_punto_google.class);
            pushIntent.setAction(Constants.ACTION.START_ACTION);



            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                startForegroundService(pushIntent);

            } else {
                startService(pushIntent);
            }

        }catch (Exception ee)
        {
            Toast.makeText(Servicio_pedir_movil.this, ee.toString(), Toast.LENGTH_LONG).show();
        }

    }




}

