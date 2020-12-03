package com.elisoft.sao_pedido.chat;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;

import com.elisoft.sao_pedido.R;
import com.elisoft.sao_pedido.SqLite.AdminSQLiteOpenHelper;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class Servicio_guardar_mensaje_enviado   extends IntentService {

    String id,id_conductor,  id_usuario,  titulo,  mensaje,  fecha,  hora,  estado="1",  yo="1", tipo="";

    public Servicio_guardar_mensaje_enviado() {
        super("Servicio_guardar_mensaje_enviado");
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            Bundle bundle=intent.getExtras();

            id=bundle.getString("id_chat");
            id_conductor=bundle.getString("id_conductor");
            id_usuario=bundle.getString("id_usuario");
            titulo=bundle.getString("titulo");
            mensaje=bundle.getString("mensaje");
            fecha=bundle.getString("fecha");
            hora=bundle.getString("hora");
            tipo=bundle.getString("tipo");
            handleActionRun();

        }
    }

    /**
     * Maneja la acción de ejecución del servicio
     */
    private void handleActionRun() {
        try {

            guardar_mensaje_enviado(id,id_conductor,id_usuario,titulo,mensaje,fecha,hora,estado,yo);
            // Quitar de primer plano
        } catch (Exception e) {
            e.printStackTrace();
        }
    }







    public void guardar_mensaje_enviado(String id, String id_conductor,String id_usuario,String titulo,String mensaje,String fecha,String hora,String estado,String yo)
    {
        try {
            AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this,
                    getString(R.string.nombre_sql), null, Integer.parseInt(getString(R.string.version_sql)));

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
        }catch (Exception e){
            Log.d("registro Chat",e.toString());
        }

    }




}