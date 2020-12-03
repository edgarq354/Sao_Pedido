package com.elisoft.sao_pedido.notificaciones;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioManager;
import android.os.Vibrator;
import android.util.Log;

import com.elisoft.sao_pedido.Constants;
import com.elisoft.sao_pedido.Notificacion_mensaje;
import com.elisoft.sao_pedido.Notificacion_pedido_cancelo;
import com.elisoft.sao_pedido.Pedido_finalizado;
import com.elisoft.sao_pedido.Pedido_finalizado_sin_notificacion;
import com.elisoft.sao_pedido.Pedido_usuario;
import com.elisoft.sao_pedido.Principal;
import com.elisoft.sao_pedido.Principal_pedido;
import com.elisoft.sao_pedido.R;
import com.elisoft.sao_pedido.Servicio_pedido;
import com.elisoft.sao_pedido.SqLite.AdminSQLiteOpenHelper;
import com.elisoft.sao_pedido.chat.Chat;
import com.elisoft.sao_pedido.chat.Servicio_mensaje_recibido;
import com.elisoft.sao_pedido.historial_notificacion.Notificacion;
import com.elisoft.sao_pedido.servicio.Servicio_cargar_punto_google;
import com.facebook.login.LoginManager;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by ROMAN on 24/11/2016.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    String sid_pedido2="0";

    private static final String TAG = "MyFirebaseMsgService";
    private Vibrator vibrator;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        if (remoteMessage.getData().size() > 0) {

            //envio de ultima ubicacion del motista


             Log.e(TAG, "Data Payload: " + remoteMessage.getData().toString());
            try {
                JSONObject json = new JSONObject(remoteMessage.getData().toString());
                sendPushNotification(json);
            } catch (Exception e) {
                Log.e(TAG, "Exception: " + e.getMessage());
            }

        }
    }

    private void sendPushNotification(JSONObject json) {
        // opcionalmente podemos mostrar el json en log
       // Log.e(TAG, "Notification JSON " + json.toString());
      //  volumen();
        try {
            // obtener los datos de json
            JSONObject data = json.getJSONObject("data");

            // análisis de datos json
            sid_pedido2= data.getString("id_pedido");
            String title = data.getString("title");
            String message = data.getString("message");
            String cliente = data.getString("cliente");
            String id_pedido = data.getString("id_pedido");
            String nombre = data.getString("nombre");
            String latitud = data.getString("latitud");
            String longitud = data.getString("longitud");
            String tipo = data.getString("tipo");
            String fecha = data.getString("fecha");
            String hora = data.getString("hora");
            String indicacion = data.getString("indicacion");
            String monto_total = data.getString("monto_total");
            String distancia = data.getString("distancia");
            JSONArray pedido=null;
            if(data.getString("pedido").equals("")==false)
            {
                pedido=data.getJSONArray("pedido");
            }



            MyNotificationManager mNotificationManager = new MyNotificationManager(getApplicationContext());
switch (tipo)
{
    case "1":
        //usuario
        //se iniciar el servicio de obtencion de coordenadas deltaxi...
        SharedPreferences ped2= getSharedPreferences("ultimo_pedido", MODE_PRIVATE);
    try {

        SharedPreferences.Editor editar = ped2.edit();
        editar.putString("nombre_taxi", pedido.getJSONObject(0).getString("nombre_taxi"));
        editar.putString("celular", pedido.getJSONObject(0).getString("celular"));
        editar.putString("id_taxi", pedido.getJSONObject(0).getString("id_taxi"));
        editar.putString("marca", pedido.getJSONObject(0).getString("marca"));
        editar.putString("placa", pedido.getJSONObject(0).getString("placa"));
        editar.putString("color", pedido.getJSONObject(0).getString("color"));
        editar.putString("latitud", pedido.getJSONObject(0).getString("latitud"));
        editar.putString("longitud", pedido.getJSONObject(0).getString("longitud"));
        editar.putString("id_pedido", id_pedido);
        editar.putInt("notificacion_cerca", 0);
        editar.putInt("notificacion_llego", 0);
         editar.commit();
/*
        Intent servicio_contacto = new Intent(MyFirebaseMessagingService.this, Servicio_guardar_contacto.class);
        servicio_contacto.setAction(Constants.ACTION_RUN_ISERVICE);
        servicio_contacto.putExtra("nombre",ped2.getString("nombre_taxi", ""));
        servicio_contacto.putExtra("telefono",ped2.getString("celular", ""));
        startService(servicio_contacto);
*/
    }catch (Exception e)
    {
        e.printStackTrace();
    }
        Intent intent = new Intent(getApplicationContext(), Servicio_pedido.class);
        intent.setAction(Constants.ACTION_RUN_ISERVICE);
        startService(intent);

        Intent numero = new Intent(this, Principal_pedido.class);
        numero.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        numero.putExtra("id_pedido",ped2.getString("id_pedido",""));
        numero.putExtra("latitud",Double.parseDouble(ped2.getString("latitud","0")));
        numero.putExtra("longitud",Double.parseDouble(ped2.getString("longitud","0")));
        startActivity(numero);

        // crear una intención para la notificación
        SharedPreferences datos_pe = getSharedPreferences("perfil", Context.MODE_PRIVATE);
        if(datos_pe.getString("id_usuario","").equals("")==false)
        {
            Intent usuario = new Intent(getApplicationContext(),Pedido_usuario.class);
            mNotificationManager.notificacion_con_pedido_aceptado_activity(title, message, usuario);
        }




        break;
    case "2":
//moto
        // crear una intención para la notificación

        break;
    case "3":
        //enviar notificacion sin acccion.
        break;
    case  "4":
        //notificacion cuando el conductor inicio la carrera.
       /* Intent dialogo_carrera = new Intent(this, Notificacion_iniciar_carrera.class);
        dialogo_carrera.putExtra("mensaje",message);
        dialogo_carrera.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(dialogo_carrera);
        */
        SharedPreferences pedido2 = getSharedPreferences("pedido_en_proceso", MODE_PRIVATE);
        SharedPreferences.Editor editor = pedido2.edit();
        editor.putString("id_pedido", "");
        editor.commit();

        SharedPreferences pedido22 = getSharedPreferences("ultimo_pedido", MODE_PRIVATE);
        SharedPreferences.Editor editor2 = pedido22.edit();
        editor2.putString("abordo", "1");
        editor2.commit();
        mNotificationManager.notificacion_sin_activity(title, message );


        break;


    case  "5":
        // crear una intención para la notificación
        Intent usuari= new Intent(getApplicationContext(),Principal.class);
        mNotificationManager.notificacion_con_activity(title, message, usuari);

        break;
    case  "6":
        // crear una intención para la notificación
        Intent usu= new Intent(getApplicationContext(),Principal.class);
        mNotificationManager.notificacion_con_activity(title, message, usu);
        break;

    case  "7":
        // crear una intención para la notificación
        cargar_notificacion(title,message,cliente,id_pedido,nombre,latitud,longitud,tipo,fecha,hora,indicacion);

        Intent usuarioo= new Intent(MyFirebaseMessagingService.this, Notificacion_mensaje.class);
        usuarioo.putExtra("titulo",title);
        usuarioo.putExtra("mensaje",message);
        mNotificationManager.notificacion_con_activity(title, message, usuarioo);


        break;

    case  "8":
        // notificacion para verificar la actualizacion nueva
        SharedPreferences act = getSharedPreferences("actualizacion_elitex", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit=act.edit();
        edit.putInt("version", Integer.parseInt(data.getString("version")));
        edit.putString("mensaje",data.getString("mensaje"));
        edit.commit();

        break;

    case  "9":
        // crear una intención para la notificación
        SharedPreferences inf= getSharedPreferences("informacion", Context.MODE_PRIVATE);
        SharedPreferences.Editor e=inf.edit();
        e.putString("informacion",data.getString("informacion"));
        e.commit();

        break;

    case  "10":
        //finalizar el pedido
        SharedPreferences pedido_ultimo = getSharedPreferences("ultimo_pedido", MODE_PRIVATE);
        SharedPreferences.Editor editar=pedido_ultimo.edit();
        editar.putString("nombre_taxi", "");
        editar.putString("celular", "");
        editar.putString("marca", "");
        editar.putString("placa", "");
        editar.putString("color", "");
        editar.putString("id_pedido", "");
        editar.putInt("notificacion_cerca", 0);
        editar.putInt("notificacion_llego", 0);

        editar.commit();
        //se vacia los puntos guardados de todos los pedido...
        vaciar_toda_la_base_de_datos();
        eliminar_servicio();
        eliminar_que_necesitas();

        Intent dialogIntent = new Intent(getApplicationContext(), Pedido_finalizado.class);
        dialogIntent.putExtra("monto_total", monto_total);
        dialogIntent.putExtra("distancia", distancia);
        dialogIntent.putExtra("id_pedido", sid_pedido2);
        mNotificationManager.notificacion_con_pedido_finalizado_activity(title, message, dialogIntent);

        dialogIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(dialogIntent);


        break;
    case  "11":
        //notificacion para cerrar sesion..
        cerrar_sesion_usuario();

        break;
    case  "12":
        //pedido cancelado por el taxista para el pasaero..
        //PASAJERO

        SharedPreferences pedido_ultimo1 = getSharedPreferences("ultimo_pedido", MODE_PRIVATE);
        SharedPreferences.Editor editar1=pedido_ultimo1.edit();
        editar1.putString("nombre_taxi", "");
        editar1.putString("celular", "");
        editar1.putString("marca", "");
        editar1.putString("placa", "");
        editar1.putString("color", "");
        editar1.putString("id_pedido", "");
        editar1.putInt("notificacion_cerca", 0);
        editar1.putInt("notificacion_llego", 0);

        editar1.commit();
        //se vacia los puntos guardados de todos los pedido...
        vaciar_toda_la_base_de_datos();
        eliminar_servicio();

        Intent usus1= new Intent(getApplicationContext(),Notificacion.class);
        mNotificationManager.notificacion_con_error_activity(title, message, usus1);




        Intent dialogo_cancelacion = new Intent(this, Notificacion_pedido_cancelo.class);
        dialogo_cancelacion.putExtra("mensaje",message);
        dialogo_cancelacion.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(dialogo_cancelacion);


        break;
    case  "13":
        //pedido cancelado por el pasajero para el taxista..
        // TAXISTA

        SharedPreferences p1 = getSharedPreferences("ultimo_pedido", Context.MODE_PRIVATE);
        SharedPreferences.Editor edi1=p1.edit();
        edi1.putString("id_pedido","");
        edi1.putInt("notificacion_cerca", 0);
        edi1.putInt("notificacion_llego", 0);
        edi1.commit();
        SharedPreferences datos_1= getSharedPreferences("perfil", Context.MODE_PRIVATE);
        if(datos_1.getString("id_taxi","").equals("")==false)
        {
            //cambia el estado a activo. una ves que llego la notificacion de cancelacion...
            //habilitandose para otro pedido.
            SharedPreferences.Editor editor1=datos_1.edit();
            editor1.putString("estado","1");
            editor1.commit();



            Intent taxi= new Intent(getApplicationContext(), Notificacion.class);
            mNotificationManager.notificacion_con_error_activity(title, message, taxi);
        }

        break;

    case  "14":
        //el taxi esta cerca
        Intent usuari1= new Intent(getApplicationContext(),Principal.class);
        mNotificationManager.notificacion_con_activity(title, message, usuari1);
        break;
    case  "15":
        //llego el taxi
        Intent usuari2= new Intent(getApplicationContext(), Principal.class);
        mNotificationManager.notificacion_llego_el_taxi_activity(title, message, usuari2);
        break;



    case "18":
        //RESERVA DE MOVIL EN CAMINO
        try {
            SharedPreferences ped = getSharedPreferences("ultimo_pedido", MODE_PRIVATE);
            SharedPreferences.Editor editar_ped = ped.edit();
            editar_ped.putString("nombre_taxi", pedido.getJSONObject(0).getString("nombre_taxi"));
            editar_ped.putString("celular", pedido.getJSONObject(0).getString("celular"));
            editar_ped.putString("id_taxi", pedido.getJSONObject(0).getString("id_taxi"));
            editar_ped.putString("marca", pedido.getJSONObject(0).getString("marca"));
            editar_ped.putString("placa", pedido.getJSONObject(0).getString("placa"));
            editar_ped.putString("color", pedido.getJSONObject(0).getString("color"));
            editar_ped.putString("latitud", pedido.getJSONObject(0).getString("latitud"));
            editar_ped.putString("longitud", pedido.getJSONObject(0).getString("longitud"));
            editar_ped.putString("id_pedido", id_pedido);
            editar_ped.putInt("notificacion_cerca", 0);
            editar_ped.putInt("notificacion_llego", 0);
            editar_ped.commit();


        }catch (Exception e2)
        {

        }

        Intent intent_reserva = new Intent(getApplicationContext(), Servicio_pedido.class);
        intent_reserva.setAction(Constants.ACTION_RUN_ISERVICE);
        startService(intent_reserva);


        Intent usuario_reserva= new Intent(getApplicationContext(),Notificacion_mensaje.class);
        mNotificationManager.notificacion_con_activity(title, message, usuario_reserva);

        break;

    case  "20":
        // crear una intención para la notificación
        cargar_notificacion(title,message,cliente,id_pedido,nombre,latitud,longitud,tipo,fecha,hora,indicacion);

        Intent usua = new Intent(MyFirebaseMessagingService.this,Notificacion_mensaje.class);
        mNotificationManager.notificacion_hay_un_pedido(title, message, usua);

        break;

    case  "30":
        //finalizar el pedido  sin notificacion
        SharedPreferences pedido_ultimo30 = getSharedPreferences("ultimo_pedido", MODE_PRIVATE);
        SharedPreferences.Editor editar30=pedido_ultimo30.edit();
        editar30.putString("nombre_taxi", "");
        editar30.putString("celular", "");
        editar30.putString("marca", "");
        editar30.putString("placa", "");
        editar30.putString("color", "");
        editar30.putString("id_pedido", "");
        editar30.putInt("notificacion_cerca", 0);
        editar30.putInt("notificacion_llego", 0);
        editar30.commit();
        //se vacia los puntos guardados de todos los pedido...
        vaciar_toda_la_base_de_datos();
        eliminar_que_necesitas();


        // crear una intención para la notificación
        Intent usuario30= new Intent(getApplicationContext(), Pedido_finalizado_sin_notificacion.class);
        mNotificationManager.notificacion_con_activity(title, message, usuario30);

        break;


    case "100":
        Intent serviceIntent = new Intent(this, Servicio_mensaje_recibido.class);
        serviceIntent.putExtra("id_chat", data.getString("id_chat"));
        serviceIntent.putExtra("id_conductor",data.getString("id_conductor"));
        serviceIntent.putExtra("id_usuario", data.getString("id_usuario"));
        serviceIntent.putExtra("titulo", title);
        serviceIntent.putExtra("mensaje",message);
        serviceIntent.putExtra("fecha", fecha);
        serviceIntent.putExtra("hora", hora);
        serviceIntent.putExtra("estado", data.getString("estado"));
        serviceIntent.putExtra("yo",data.getString("yo"));
        startService(serviceIntent);

        Intent chat= new Intent(getApplicationContext(), Chat.class);
        chat.putExtra("id_conductor",data.getString("id_conductor"));
        chat.putExtra("id_usuario",data.getString("id_usuario"));
        chat.putExtra("titulo",title);
        mNotificationManager.notificacion(title, message,chat);
        guardar_mensaje_enviado(data.getString("id_chat"),data.getString("id_conductor"),data.getString("id_usuario"),title,message,fecha,hora,data.getString("estado"),data.getString("yo"),data.getString("tipo_mensaje"));

        break;

    case  "110":
        // 10: confirmado de envio de carrito
        Intent usuari10= new Intent(getApplicationContext(),Notificacion_mensaje.class);
        usuari10.putExtra("mensaje",message);
        mNotificationManager.notificacion_hay_un_pedido(title, message, usuari10);
        break;
    case  "111":
        // 11: Pediedo aceptado por la empresa
        Intent usuari11= new Intent(getApplicationContext(),Notificacion_mensaje.class);
        usuari11.putExtra("mensaje",message);
        mNotificationManager.notificacion_pedido_aceptado(title, message, usuari11);
        break;
    case  "112":
        // 12: Conductor asignado al pedido

        //SU PEDIDO YA ESTAN EN CAMINO.
        //CONDUCTOR COMENZO LA CARRERA.
        SharedPreferences pedido3 = getSharedPreferences("pedido_en_proceso", MODE_PRIVATE);
        SharedPreferences.Editor editor3 = pedido3.edit();
        editor3.putString("id_pedido", "");
        editor3.commit();

        SharedPreferences pedido33 = getSharedPreferences("ultimo_pedido", MODE_PRIVATE);
        SharedPreferences.Editor editor33 = pedido33.edit();
        editor33.putString("abordo", "1");
        editor33.commit();

        Intent usuari12= new Intent(getApplicationContext(),Notificacion_mensaje.class);
        usuari12.putExtra("mensaje",message);
        mNotificationManager.notificacion_conductor_en_camino(title, message, usuari12);
        break;
    case  "113":
        // 13: El conductor comenzo la carrera haci la ubicacion del pasajero

        Intent usuari13= new Intent(getApplicationContext(),Notificacion_mensaje.class);
        usuari13.putExtra("mensaje",message);
        mNotificationManager.notificacion_conductor_en_proceso(title, message, usuari13);
        break;
    case  "115":
        // 15: Pedido completado correctamente
        Intent usuari15= new Intent(getApplicationContext(),Notificacion_mensaje.class);
        usuari15.putExtra("mensaje",message);
        mNotificationManager.notificacion_pedido_finalizado(title, message, usuari15);
        break;

    default:
        // crear una intención para la notificación
        Intent dialogo_no = new Intent(this, Notificacion_mensaje.class);
        dialogo_no.putExtra("mensaje",message);
        dialogo_no.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(dialogo_no);

        break;
}



        } catch (JSONException e) {
           Log.e(TAG, "Json Exception: " + e.getMessage());
        } catch (Exception e) {
           Log.e(TAG, "Exception: " + e.getMessage());
        }
    }






    public void guardar_mensaje_enviado(String id, String id_conductor,String id_usuario,String titulo,String mensaje,String fecha,String hora,String estado,String yo,String tipo) {
        try {
            AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, getString(R.string.nombre_sql), null, Integer.parseInt(getString(R.string.version_sql)));

            SQLiteDatabase bd = admin.getWritableDatabase();
            ContentValues registro = new ContentValues();
            registro.put("id", id);
            registro.put("id_conductor", id_conductor);
            registro.put("id_usuario", id_usuario);
            registro.put("fecha", fecha);
            registro.put("hora", hora);
            registro.put("mensaje", mensaje);
            registro.put("titulo", titulo);
            registro.put("estado", estado);
            registro.put("yo", yo);
            registro.put("tipo", tipo);
            bd.insert("chat", null, registro);
            bd.close();
        } catch (Exception e) {
            Log.d("registro Chat", e.toString());
        }
    }


    private void cargar_notificacion(String title, String message, String cliente, String id_pedido, String nombre, String latitud, String longitud, String tipo, String fecha, String hora, String indicacion) {
        try {
            AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, getString(R.string.nombre_sql), null, Integer.parseInt(getString(R.string.version_sql)));

            SQLiteDatabase bd = admin.getWritableDatabase();
            ContentValues registro = new ContentValues();
            registro.put("titulo", title);
            registro.put("mensaje", message);
            registro.put("cliente", cliente);
            registro.put("nombre", nombre);
            registro.put("tipo", tipo);
            registro.put("fecha", fecha);
            registro.put("hora", hora);
            registro.put("latitud", latitud);
            registro.put("longitud", longitud);
            registro.put("id_pedido", id_pedido);
            registro.put("indicacion", indicacion);
            bd.insert("notificacion", null, registro);
            bd.close();
        }catch (Exception e)
        {
            Log.e("Notificacion",""+e);
        }

    }


    public void vaciar_toda_la_base_de_datos() {

        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, getString(R.string.nombre_sql), null, Integer.parseInt(getString(R.string.version_sql)));

        SQLiteDatabase db = admin.getWritableDatabase();

       // db.execSQL("delete from puntos_pedido");
        db.close();
        // Log.e("sqlite ", "vaciar todo");
    }




    public void cerrar_sesion_usuario()
    {
        LoginManager.getInstance().logOut();
        SharedPreferences perfil=getSharedPreferences("perfil",MODE_PRIVATE);
        SharedPreferences.Editor editar=perfil.edit();
        editar.putString("id","");
        editar.putString("nombre","");
        editar.putString("apellido","");
        editar.putString("ci","");
        editar.putString("email","");
        editar.putString("direccion","");
        editar.putString("marca","");
        editar.putString("modelo","");
        editar.putString("placa","");
        editar.putString("celular","");
        editar.putString("credito","");
        editar.putString("login_usuario","");
        editar.putString("login_taxi","");
        editar.commit();
        vaciar_toda_la_base_de_datos();
        Intent serv = new Intent(getApplicationContext(), Servicio_pedido.class);
        serv.setAction(Constants.ACTION_RUN_ISERVICE);
        stopService(serv);

    }

    private void volumen() {

        try {
            AudioManager audioManager;
            audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            int vol_max_not=audioManager.getStreamMaxVolume(AudioManager.STREAM_NOTIFICATION);
            int vol_max_ring=audioManager.getStreamMaxVolume(AudioManager.STREAM_RING);

            audioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, vol_max_not-2, 0);
            audioManager.setStreamVolume(AudioManager.STREAM_RING, vol_max_ring-2, 0);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void eliminar_servicio(){
        Intent stopIntent = new Intent(MyFirebaseMessagingService.this, Servicio_cargar_punto_google.class);
        stopIntent.setAction(Constants.ACTION.STOP_ACTION);
        stopService(stopIntent);
    }


    private void eliminar_que_necesitas() {
        SharedPreferences carrito = getSharedPreferences(getString(R.string.sql_pedido), MODE_PRIVATE);
        SharedPreferences.Editor ed_c = carrito.edit();
        ed_c.putString("que_necesitas", "");
        ed_c.putString("direccion_recoger","");
        ed_c.putString("direccion_entrega","");
        ed_c.putString("latitud_recoger","");
        ed_c.putString("longitud_recoger","");
        ed_c.putString("monto_envio","");
        ed_c.commit();
    }
}