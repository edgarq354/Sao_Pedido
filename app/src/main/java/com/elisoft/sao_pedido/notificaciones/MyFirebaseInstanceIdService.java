package com.elisoft.sao_pedido.notificaciones;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by ROMAN on 24/11/2016.
 */
        //esta clace es quien obtiene el Token desde FireBase
public class MyFirebaseInstanceIdService extends FirebaseInstanceIdService {
    private static final String TAG = "MyFirebaseIIDService";

    @Override
    public void onTokenRefresh() {

        // Obtención del token de registro
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();

        // Visualización de la señal en logcat
      //  Log.d(TAG, "Refreshed token: " + refreshedToken);

        // llamando al token de almacén de método y token de paso
        storeToken(refreshedToken);
    }

    private void storeToken(String token) {
        // guardaremos el token en las preferencias compartidas más tarde
        SharedPrefManager.getInstance(getApplicationContext()).saveDeviceToken(token);

    }

}
