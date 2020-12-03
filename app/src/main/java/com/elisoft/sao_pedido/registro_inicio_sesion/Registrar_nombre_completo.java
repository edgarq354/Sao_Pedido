package com.elisoft.sao_pedido.registro_inicio_sesion;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.elisoft.sao_pedido.R;
import com.elisoft.sao_pedido.Suceso;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


public class Registrar_nombre_completo extends AppCompatActivity implements View.OnClickListener {
    Button siguiente;
    Suceso suceso;
    ProgressDialog pDialog;
    EditText nombre,apellido;
    String celular;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_usuario);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        siguiente= findViewById(R.id.siguiente);
        nombre= findViewById(R.id.et_nombre);
        apellido= findViewById(R.id.et_apellido);

        siguiente.setOnClickListener(this);


        try{
            Bundle bundle=getIntent().getExtras();
            celular=bundle.getString("celular");
        }catch (Exception e)
        {
            finish();
        }
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.siguiente)
        {  if( nombre.getText().toString().trim().length()>=3  && apellido.getText().toString().trim().length()>=3 )
        {
            Intent registrar=new Intent(getApplicationContext(), Completar_registro.class);
            registrar.putExtra("celular",celular);
            registrar.putExtra("nombre",nombre.getText().toString().trim());
            registrar.putExtra("apellido",apellido.getText().toString().trim());
            registrar.putExtra("email","");
            startActivity(registrar);
            finish();
            }
        else
        {
            mensaje("Por favor ingrese los datos correctamente.");
        }
        }
    }






    public void mensaje(String mensaje)
    {
        Toast toast = Toast.makeText(this,mensaje, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.show();
    }



    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    public void mensaje_error(String mensaje)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Importante");
        builder.setMessage(mensaje);
        builder.setPositiveButton("OK", null);
        builder.create();
        builder.show();
    }
}
