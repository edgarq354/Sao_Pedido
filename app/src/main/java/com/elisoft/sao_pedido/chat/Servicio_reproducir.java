package com.elisoft.sao_pedido.chat;

import android.app.IntentService;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;

import com.elisoft.sao_pedido.Suceso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okio.ByteString;


/**
 * Created by ELIO on 12/01/2018.
 */

public class Servicio_reproducir extends IntentService {
   String  mensaje="";
   int id_chat=0;

    private MediaPlayer mPlayer;
    static List<byte[]> sLista = new ArrayList<>();

    Suceso suceso;


    public Servicio_reproducir() {
        super("Servicio_reproducir");
    }



    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {

                Bundle bundle=intent.getExtras();
                id_chat=bundle.getInt("id_chat");
                mensaje=bundle.getString("mensaje");
                sLista = new ArrayList<>();
                handleActionRun();

        }
    }

    /**
     * Maneja la acción de ejecución del servicio
     */
    private void handleActionRun() {
        try {

//convertimos el audio en BYTES
            ByteString d = ByteString.decodeHex(mensaje);
            byte[] bytes1 = d.toByteArray();

            sLista.add(bytes1);


            escuchar();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void escuchar()
    {
        //escuchar audio
        playReceivedFile();
    }

    private void playReceivedFile() {
        File f = buildAudioFileFromReceivedBytes();

        playAudio(f);
    }


    @NonNull
    private File buildAudioFileFromReceivedBytes() {
        File f = new File(getCacheDir().getAbsolutePath() + "/recibido_"+id_chat+".3gp");
        try {
            f.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        FileOutputStream out = null;
        try {
            out = (new FileOutputStream(f));
        } catch (IOException e) {
            e.printStackTrace();
        }


        try {
            for (byte[] b : sLista) {
                out.write(b);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return f;
    }


    private void playAudio(File f) {
        mPlayer = new MediaPlayer();
        try {
            mPlayer.setDataSource(this, Uri.parse(f.getPath()));
            mPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d("servicio audio", "onClosing: dudation in millis: " + mPlayer.getDuration());

        mPlayer.start();
        actualizar_audio(String.valueOf(id_chat),"1");
    }

    public void actualizar_audio(
            String id,
            String visto
    ) {
        /*
        try {
            AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, getString(R.string.nombre_sql), null, Integer.parseInt(getString(R.string.version_sql)));
            SQLiteDatabase bd = admin.getWritableDatabase();
            ContentValues registro = new ContentValues();
            registro.put("visto", visto);
            bd.update("audio",  registro,"id="+id,null);
            bd.close();
        } catch (Exception e) {
            Log.w("update Audio", e.toString());
        }

         */
    }




}

