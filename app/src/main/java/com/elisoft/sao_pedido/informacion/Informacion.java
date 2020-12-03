package com.elisoft.sao_pedido.informacion;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.elisoft.sao_pedido.R;

import androidx.appcompat.app.AppCompatActivity;

public class Informacion extends AppCompatActivity implements View.OnClickListener{
    Button pagina,terminos;
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_informacion);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        pagina= findViewById(R.id.pagina);
        terminos= findViewById(R.id.terminos);


        pagina.setOnClickListener(this);
        terminos.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
          if(v.getId()==R.id.pagina)
            {
                Intent intent=new Intent(this, Pagina.class);
                intent.putExtra("titulo","Terminos y condiciones de uso");
                intent.putExtra("url",getString(R.string.servidor)+"web_sobre_nosotros.php");
                startActivity(intent);
            }
        else if(v.getId()==R.id.terminos)
            {
                Intent intent=new Intent(this, Pagina.class);
                intent.putExtra("titulo","Terminos y condiciones de uso");
                intent.putExtra("url",getString(R.string.servidor)+"terminos_condiciones.php");
                startActivity(intent);
            }
    }

    private void enviar(String[] to, String[] cc,
                        String asunto, String mensaje) {
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.putExtra(Intent.EXTRA_EMAIL, to);
        emailIntent.putExtra(Intent.EXTRA_CC, cc);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, asunto);
        emailIntent.putExtra(Intent.EXTRA_TEXT, mensaje);
        emailIntent.setType("message/rfc822");
        startActivity(Intent.createChooser(emailIntent, "Email "));
    }
}
