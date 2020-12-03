package com.elisoft.sao_pedido;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class Pedido_finalizado_sin_notificacion extends AppCompatActivity implements View.OnClickListener {
    TextView tv_mensaje;
    Button bt_aceptar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedido_finalizado_sin_notificacion);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        tv_mensaje= findViewById(R.id.tv_mensaje);
        bt_aceptar= findViewById(R.id.bt_aceptar);

        try{
            Bundle bundle=getIntent().getExtras();
            String mensaje="";
            mensaje=bundle.getString("mensaje","");
            tv_mensaje.setText(mensaje);

        }catch (Exception e)
        {
            finish();
        }

        bt_aceptar.setOnClickListener(this);


    }

    @Override
    public void onClick(View view) {
        startActivity(new Intent(this,Principal.class));
        finish();
    }
}
