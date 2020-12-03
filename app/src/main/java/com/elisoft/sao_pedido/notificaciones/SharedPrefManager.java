package com.elisoft.sao_pedido.notificaciones;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.UUID;


//aqui almacenamos los TOken

public class SharedPrefManager {

    private static final String SHARED_PREF_NAME = "FCMSharedPref";
    private static final String TAG_TOKEN = "tagtoken";
    private static final String TAG_TOKEN_2 = "token";

    private static SharedPrefManager mInstance;
    private static Context mCtx;

    private SharedPrefManager(Context context) {
        mCtx = context;
    }

    public static synchronized SharedPrefManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new SharedPrefManager(context);
        }
        return mInstance;
    }

    // este método guardará el token del dispositivo en las preferencias compartidas
    public boolean saveDeviceToken(String token){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(TAG_TOKEN, token);
        editor.apply();


        guardarToken();
        return true;
    }

    //este método obtendrá el token de dispositivo de las preferencias compartidas
    public String getDeviceToken(){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return  sharedPreferences.getString(TAG_TOKEN, null);
    }

    // este método guardará el token del dispositivo en las preferencias compartidas
    public boolean guardarToken(){
       String token= UUID.randomUUID().toString();
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(TAG_TOKEN_2, token);
        editor.apply();
        return true;
    }

    //este método obtendrá el token de dispositivo de las preferencias compartidas
    public String mostrarToken(){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        String token=sharedPreferences.getString(TAG_TOKEN_2, null);
        if (token==null){
            guardarToken();
        }

        SharedPreferences sharedActualizado = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        token=sharedActualizado.getString(TAG_TOKEN_2, null);
        return  token;
    }
}
