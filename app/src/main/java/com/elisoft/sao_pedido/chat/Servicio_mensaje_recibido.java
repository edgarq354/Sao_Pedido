package com.elisoft.sao_pedido.chat;

import android.annotation.TargetApi;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

public class Servicio_mensaje_recibido extends Service {
    private String LOG_TAG = null;
   private String id,id_conductor,id_usuario, titulo, mensaje,fecha,hora,estado,yo;

    @Override
    public void onCreate() {
        super.onCreate();
        LOG_TAG = this.getClass().getSimpleName();
        Log.i(LOG_TAG, "In onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        id=intent.getStringExtra("id_chat");
        id_conductor=intent.getStringExtra("id_conductor");
        id_usuario=intent.getStringExtra("id_usuario");
        titulo=intent.getStringExtra("titulo");
        mensaje=intent.getStringExtra("mensaje");
        fecha=intent.getStringExtra("fecha");
        hora=intent.getStringExtra("hora");
        estado=intent.getStringExtra("estado");
        yo=intent.getStringExtra("yo");

        new Thread(new Runnable() {
            public void run() {
                Intent broadcastIntent = new Intent();
                broadcastIntent.setAction(Chat.mBroadcastStringAction);
                broadcastIntent.putExtra("id_chat", id);
                broadcastIntent.putExtra("id_conductor", id_conductor);
                broadcastIntent.putExtra("id_usuario", id_usuario);
                broadcastIntent.putExtra("titulo", titulo);
                broadcastIntent.putExtra("mensaje", mensaje);
                broadcastIntent.putExtra("fecha", fecha);
                broadcastIntent.putExtra("hora", hora);
                broadcastIntent.putExtra("estado", estado);
                broadcastIntent.putExtra("yo", yo);
                sendBroadcast(broadcastIntent);

            }
        }).start();
        return START_REDELIVER_INTENT;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // Wont be called as service is not bound
        Log.i(LOG_TAG, "In onBind");
        return null;
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        Log.i(LOG_TAG, "In onTaskRemoved");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(LOG_TAG, "In onDestroy");
    }
}
