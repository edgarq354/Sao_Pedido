package com.elisoft.sao_pedido;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import com.elisoft.sao_pedido.registro_inicio_sesion.Iniciar_sesion;

import androidx.appcompat.app.AppCompatActivity;

public class Inicio_Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        new Handler().postDelayed(new Runnable() {


            @Override
            public void run() {
                // This method will be executed once the timer is over
                Intent i = new Intent(Inicio_Activity.this, Iniciar_sesion.class);
                startActivity(i);
                finish();
            }
        }, 6000); // ----Main Activity Start After 6 Sec.

    }
}
