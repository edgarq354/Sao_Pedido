package com.elisoft.sao_pedido;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Notificacion_mensaje extends AppCompatActivity implements  View.OnClickListener{
    TextView tv_mensaje;
    Button bt_aceptar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notificacion_mensaje);

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
        getSupportActionBar().hide();

    }
    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.bt_aceptar)
        {
            finish();
        }
    }

}
