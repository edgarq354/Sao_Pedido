package com.elisoft.sao_pedido.registro_inicio_sesion;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.elisoft.sao_pedido.R;
import com.elisoft.sao_pedido.Suceso;

import androidx.appcompat.app.AppCompatActivity;


public class
Autenticar_celular extends AppCompatActivity implements View.OnClickListener {
    Button continuar;
    EditText celular;


    ProgressDialog pDialog;
    Suceso suceso;



    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.continuar) {
            Intent confirmar = new Intent(getApplicationContext(), Confirmar_sms.class);
            confirmar.putExtra("celular", celular.getText().toString());
            startActivity(confirmar);
            finish();
        }
    }

    //USUARIO
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_autenticar_celular);
        continuar = findViewById(R.id.continuar);
        celular = findViewById(R.id.celular);


        continuar.setEnabled(false);


        continuar.setOnClickListener(this);

        celular.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (verificar_celular(s)) {
                    continuar.setEnabled(true);
                    celular.setTextColor(Color.BLACK);
                } else {
                    continuar.setEnabled(false);
                    celular.setTextColor(Color.RED);
                }
            }
        });








        SharedPreferences perfil=getSharedPreferences("perfil",MODE_PRIVATE);
        celular.setHint(perfil.getString("celular",""));

    }



    private boolean verificar_celular(CharSequence s) {
        boolean sw=false;
        try{
            int numero= Integer.parseInt(s.toString());
            if(numero>=60000000 && numero<=79999999)
            {
                sw=true;
            }
        }catch (Exception e)
        {
            sw=false;
        }
        return sw;
    }













}
