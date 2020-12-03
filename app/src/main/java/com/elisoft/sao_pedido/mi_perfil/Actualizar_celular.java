package com.elisoft.sao_pedido.mi_perfil;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.elisoft.sao_pedido.R;
import com.elisoft.sao_pedido.informacion.Pagina;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class Actualizar_celular extends AppCompatActivity implements View.OnClickListener {
    Button continuar;
    EditText celular;


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.continuar) {
            Intent confirmar = new Intent(getApplicationContext(), Confirmar_sms_celular.class);
            confirmar.putExtra("celular", celular.getText().toString());
            startActivity(confirmar);
            finish();
        } else if (v.getId() == R.id.tv_condiciones) {
            Intent intent=new Intent(this, Pagina.class);
            intent.putExtra("titulo","Terminos y condiciones de uso");
            intent.putExtra("url",getString(R.string.servidor)+"terminos_condiciones.php");
            startActivity(intent);
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
        setContentView(R.layout.activity_actualizar_celular);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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







    }


    //motista
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
