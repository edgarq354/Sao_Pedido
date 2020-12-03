package com.elisoft.sao_pedido;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;


/**
 * Un {@link IntentService} que simula un proceso en primer plano
 * <p>
 */

public class Servicio_pedido extends IntentService {
    Suceso suceso;
    String sid_pedido,sid_pedido2;
    int estado=0,sw_notificacion;
    String monto_total="0",monto_pedido="0",distancia="0",detalle="";
    boolean sw_monto_total=false;
    boolean sw_condulta_monto=true;
    private static final String TAG = Servicio_pedido.class.getSimpleName();
    RequestQueue queue=null;



    public Servicio_pedido() {
        super("Servicio_pedido");
    }



    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (Constants.ACTION_RUN_ISERVICE.equals(action)) {
                handleActionRun();

            }
        }
    }

    /**
     * Maneja la acción de ejecución del servicio
     */
    private void handleActionRun() {
        try {

            SharedPreferences prefe=getSharedPreferences("ultimo_pedido",MODE_PRIVATE);

            sid_pedido=prefe.getString("id_pedido","0");

            // Bucle de simulación de pedido cuando tiene estado del pedido=0
            for (int i = 1; estado==0 || estado==1 && sid_pedido.equals("0")==false; i++) {
                    if(estaConectado()) {
                        Thread.sleep(500);
                        servicio_obtener_ubicacion_por_id_pedido(sid_pedido);
                        // parametro que recibe el doinbackground
                        //Log.d(TAG, i + ""); // Logueo
                        // Retardo de 1 segundo en la iteración

                    }else
                    {
                        Thread.sleep(6000);
                    }

            }


            sw_monto_total=true;
            if(estado==2 && sw_condulta_monto==true) {
                sw_condulta_monto=false;
                sid_pedido2=sid_pedido;
                do{
                    if(estaConectado()) {
                        servicio_monto_total_por_id_pedido(sid_pedido2);
                    }    Thread.sleep(1000);
                }while (sw_monto_total==true);

            }else
            {
                sw_monto_total=false;
            }

            do{
                Thread.sleep(1000);
            }while (sw_monto_total==true);
            // Quitar de primer plano
            stopForeground(true);
            // si nuestro estado esta en 2 o mayor .. quiere decir que no nuestro pedido se finalizo o sino se cancelo... sin nninguna carrera...
            if(estado>1)
            {
                stopService(new Intent(this,Servicio_pedido.class));
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void servicio_monto_total_por_id_pedido(String sid_pedido2) {

        try {

            JSONObject jsonParam= new JSONObject();
            jsonParam.put("id_pedido", sid_pedido2);

            String url=getString(R.string.servidor) + "frmPedido.php?opcion=monto_total_por_id_pedido";
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
                                    monto_total=respuestaJSON.getString("monto_total");
                                    monto_pedido=respuestaJSON.getString("monto_pedido");
                                    distancia=respuestaJSON.getString("distancia");
                                    detalle=respuestaJSON.getString("detalle");
                                    sw_notificacion=Integer.parseInt(respuestaJSON.getString("notificacion"));


                                }
                                else
                                {

                                }
                                sw_monto_total=false;
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
            e.printStackTrace();
        }
    }

    private void servicio_obtener_ubicacion_por_id_pedido(String sid_pedido) {

        try {

            JSONObject jsonParam= new JSONObject();
            jsonParam.put("id_pedido", sid_pedido);

            String url=getString(R.string.servidor) + "frmTaxi.php?opcion=obtener_ubicacion_por_id_pedido";
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
                                    JSONArray dato = respuestaJSON.getJSONArray("punto");
                                    if (dato.getString(0).length() > 8) {
                                        double latitud = Double.parseDouble(dato.getJSONObject(0).getString("latitud"));
                                        double longitud = Double.parseDouble(dato.getJSONObject(0).getString("longitud"));
                                        int rotacion = Integer.parseInt(dato.getJSONObject(0).getString("rotacion"));
                                        int clase_vehiculo=Integer.parseInt(dato.getJSONObject(0).getString("clase_vehiculo"));
                                        estado= Integer.parseInt(dato.getJSONObject(0).getString("estado"));
                                        int moto= Integer.parseInt(dato.getJSONObject(0).getString("moto"));

                                        cargar_puntos_en_tabla(latitud, longitud,rotacion,clase_vehiculo,moto);


                                        SharedPreferences prefe=getSharedPreferences("ultimo_pedido", Context.MODE_PRIVATE);
                                        SharedPreferences.Editor editor=prefe.edit();
                                        editor.putString("punto_optenido","true");
                                        editor.commit();
                                    } else
                                    {

                                        SharedPreferences prefe=getSharedPreferences("ultimo_pedido", Context.MODE_PRIVATE);
                                        SharedPreferences.Editor editor=prefe.edit();
                                        editor.putString("punto_optenido","false");
                                        editor.commit();
                                    }
                                    ///////     final

                                }
                                else
                                {

                                    SharedPreferences prefe=getSharedPreferences("ultimo_pedido", Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor=prefe.edit();
                                    editor.putString("punto_optenido","false");
                                    editor.commit();
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
            myRequest.setRetryPolicy(new DefaultRetryPolicy(10000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            queue.add(myRequest);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onDestroy() {


        SharedPreferences pro=getSharedPreferences("pedido_en_proceso",MODE_PRIVATE);
        SharedPreferences.Editor editar=pro.edit();
        editar.putString("id_pedido","");
        editar.commit();

        SharedPreferences pedido=getSharedPreferences("ultimo_pedido",MODE_PRIVATE);
        SharedPreferences.Editor edit=pedido.edit();
        edit.putString("id_pedido","");
        edit.commit();

        if (queue != null) {
            queue.stop();
        }


        if(estado==2) {


            Toast.makeText(this, "Pedido Finalizado.", Toast.LENGTH_LONG).show();

            if(sw_notificacion==1){
                Double total=Double.parseDouble(monto_total)+Double.parseDouble(monto_pedido);
            Intent dialogIntent = new Intent(getApplicationContext(), Pedido_finalizado.class);
            dialogIntent.putExtra("monto_total", ""+total);
            dialogIntent.putExtra("distancia", distancia);
            dialogIntent.putExtra("id_pedido", sid_pedido2);
            dialogIntent.putExtra("detalle", detalle);
            dialogIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(dialogIntent);
            }
            else
            {
                Intent dialogo_notificacion30 = new Intent(this, Pedido_finalizado_sin_notificacion.class);
                dialogo_notificacion30.putExtra("mensaje","Pedido finalizado");
                dialogo_notificacion30.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(dialogo_notificacion30);
            }

            sid_pedido2="";

        }


        // Emisión para avisar que se terminó el servicio
        Intent localIntent = new Intent(Constants.ACTION_PROGRESS_EXIT);
        LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
    }


    private void servicio_get_estado_pedido(String sid_pedido) {

        try {

            JSONObject jsonParam= new JSONObject();
            jsonParam.put("id_pedido", sid_pedido);

            String url=getString(R.string.servidor) + "frmPedido.php?opcion=get_estado_pedido";
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
                                    estado= Integer.parseInt(respuestaJSON.getString("estado"));
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
            e.printStackTrace();
        }
    }



    private void cargar_puntos_en_tabla(double latitud,double longitud,int rotacion,int clase_vehiculo,int moto) {
        try {
            SharedPreferences punto_pedido=getSharedPreferences("punto taxi",MODE_PRIVATE);
            SharedPreferences.Editor editor=punto_pedido.edit();
            editor.putString("latitud", String.valueOf(latitud));
            editor.putString("longitud", String.valueOf(longitud));
            editor.putString("rotacion", String.valueOf(rotacion));
            editor.putInt("clase_vehiculo", clase_vehiculo);
            editor.putInt("moto", moto);
            editor.commit();

        }catch (Exception e)
        {

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

    //VERIFICAR SI ESTA CON CONEXION WIFI
    protected Boolean conectadoWifi(){
        ConnectivityManager connectivity = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo info = connectivity.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (info != null) {
                return info.isConnected();
            }
        }
        return false;
    }
    //VERIFICAR SI ESTA CON CONEXION DE DATOS
    protected Boolean conectadoRedMovil(){
        ConnectivityManager connectivity = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo info = connectivity.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if (info != null) {
                return info.isConnected();
            }
        }
        return false;
    }

    protected Boolean estaConectado(){
        if(conectadoWifi()){
            return true;
        }else{
            return conectadoRedMovil();
        }
    }
}

