package com.elisoft.sao_pedido;

import android.app.IntentService;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;


/**
 * Created by ELIO on 12/01/2018.
 */

public class Servicio_descargar_imagen_perfil extends IntentService {
    int id_usuario=0;



    public Servicio_descargar_imagen_perfil() {
        super("Servicio_descargar_imagen_perfil");
    }



    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {

                Bundle bundle=intent.getExtras();
                id_usuario=bundle.getInt("id_usuario");
                handleActionRun();

        }
    }

    /**
     * Maneja la acción de ejecución del servicio
     */
    private void handleActionRun() {
        try {

            getImage(String.valueOf(id_usuario));
            // Quitar de primer plano
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private void getImage(String id)//
    {

        Handler uiHandler = new Handler(Looper.getMainLooper());
        uiHandler.post(new Runnable(){
            @Override
            public void run() {
                String url = getString(R.string.servidor)+"/usuario/imagen/perfil/"+id_usuario+"_perfil.png";

                Picasso.with(getApplicationContext()).load(url).placeholder(R.drawable.ic_perfil_negro).into(target);

            }
        });
    }

    Target target = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {

            if( bitmap!=null)
            {
                guardar_en_memoria(bitmap);
            }
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {

        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

        }
    };
    private void guardar_en_memoria(Bitmap bitmapImage)
    {

        if(bitmapImage!=null) {

            try {
                File file=null;
                FileOutputStream fos = null;
                String APP_DIRECTORY = getString(R.string.app_name)+"/";//nombre de directorio
                String MEDIA_DIRECTORY = APP_DIRECTORY + "Imagen";//nombre de la carpeta
                file = new File(Environment.getExternalStorageDirectory(), MEDIA_DIRECTORY);
                File mypath = new File(file, id_usuario+ "_perfil.jpg");//nombre del archivo imagen

                boolean isDirectoryCreated = file.exists();//pregunto si esxiste el directorio creado
                if (!isDirectoryCreated)
                    isDirectoryCreated = file.mkdirs();

                if (isDirectoryCreated) {
                    fos = new FileOutputStream(mypath);
                    // Use the compress method on the BitMap object to write image to the OutputStream
                    bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                    fos.close();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }





}

