package com.elisoft.sao_pedido.chat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.elisoft.sao_pedido.R;
import com.elisoft.sao_pedido.SqLite.AdminSQLiteOpenHelper;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;

public class Chat extends AppCompatActivity {


    private ChatArrayAdapter chatArrayAdapter;
    private ListView listView;
    private EditText et_mensaje;
    private ImageView fb_send;
    ImageView im_perfil;
    TextView tv_nombre;

    private boolean side =true;
    SharedPreferences perfil;
    Integer id_conductor;


    public static final String mBroadcastStringAction = "com.elisoft.string";
    private IntentFilter mIntentFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fb_send = findViewById(R.id.bt_enviar);
        listView = findViewById(R.id.msgview);
        et_mensaje = findViewById(R.id.msg);
        im_perfil = findViewById(R.id.im_perfil);
        tv_nombre = findViewById(R.id.tv_nombre);

        chatArrayAdapter = new ChatArrayAdapter(getApplicationContext(), R.layout.right);
        listView.setAdapter(chatArrayAdapter);







        et_mensaje.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    return enviar_mensaje();
                }
                return false;
            }
        });
        fb_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                enviar_mensaje();
            }
        });

        listView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        listView.setAdapter(chatArrayAdapter);

        //to scroll the list view to bottom on data change
        chatArrayAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                listView.setSelection(chatArrayAdapter.getCount() - 1);
            }
        });

        try{
            Bundle bundle=getIntent().getExtras();
            id_conductor=Integer.parseInt(bundle.getString("id_conductor",""));
            tv_nombre.setText(bundle.getString("titulo",""));

            getImage(String.valueOf(id_conductor));
        }catch (Exception e)
        {
            finish();
        }


        perfil=getSharedPreferences("perfil",MODE_PRIVATE);
         lista_contacto(perfil.getString("id_usuario",""),String.valueOf(id_conductor));

        //Intent filter para recibir datos del servicio.
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(mBroadcastStringAction);

        Intent chat= new Intent(getApplicationContext(), Chat_pedido.class);
        chat.putExtra("id_conductor",String.valueOf(id_conductor));
        chat.putExtra("titulo",tv_nombre.getText().toString());
        startActivity(chat);
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }



    private boolean enviar_mensaje() {
        if(et_mensaje.getText().toString().trim().length()>0) {
            chatArrayAdapter.add(new CMensaje(side, et_mensaje.getText().toString(), perfil.getString("nombre", ""), now(), hora(), 0, Integer.parseInt(perfil.getString("id_usuario", "0")), id_conductor, 1,"TEXTO",0));
        }
        et_mensaje.setText("");

        return true;
    }
  
    public static String now() {
        String DATE_FORMAT_NOW = "yyyy-MM-dd HH:mm:ss";
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
        return sdf.format(cal.getTime());
    }
    public static String hora() {
        String DATE_FORMAT_NOW = "HH:mm:ss";
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
        return sdf.format(cal.getTime());
    }

    @Override
    public void onResume() {
        super.onResume();
        registerReceiver(mReceiver, mIntentFilter);
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals(mBroadcastStringAction)) {

                chatArrayAdapter.add(new CMensaje(false, intent.getStringExtra("mensaje"),intent.getStringExtra("titulo"),intent.getStringExtra("fecha"),intent.getStringExtra("hora"),Integer.parseInt(intent.getStringExtra("estado")),Integer.parseInt(intent.getStringExtra("id_usuario")),Integer.parseInt(intent.getStringExtra("id_conductor")),Integer.parseInt(intent.getStringExtra("yo")),"TEXTO",Integer.parseInt(intent.getStringExtra("id"))));

                Intent stopIntent = new Intent(Chat.this,
                        Servicio_mensaje_recibido.class);
                stopService(stopIntent);
            }
        }
    };

    @Override
    protected void onPause() {
        unregisterReceiver(mReceiver);
        super.onPause();
    }

    private void getImage(String id)//
    {
        String  url=  getString(R.string.servidor_web)+"public/Imagen_Conductor/Perfil-"+id+".png";
        Picasso.with(this).load(url).into(target);
    }

    Target target = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {

            Drawable dw = new BitmapDrawable(getResources(), bitmap);
            //se edita la imagen para ponerlo en circulo.

            if( bitmap==null)
            { dw = getResources().getDrawable(R.drawable.ic_logo_carrito);}

            imagen_circulo(dw,im_perfil);
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {

        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

        }
    };


    public void imagen_circulo(Drawable id_imagen, ImageView imagen) {
        Bitmap originalBitmap = ((BitmapDrawable) id_imagen).getBitmap();
        if (originalBitmap.getWidth() > originalBitmap.getHeight()) {
            originalBitmap = Bitmap.createBitmap(originalBitmap, 0, 0, originalBitmap.getHeight(), originalBitmap.getHeight());
        } else if (originalBitmap.getWidth() < originalBitmap.getHeight()) {
            originalBitmap = Bitmap.createBitmap(originalBitmap, 0, 0, originalBitmap.getWidth(), originalBitmap.getWidth());
        }

//creamos el drawable redondeado
        RoundedBitmapDrawable roundedDrawable =
                RoundedBitmapDrawableFactory.create(getResources(), originalBitmap);

//asignamos el CornerRadius
        roundedDrawable.setCornerRadius(originalBitmap.getWidth());
        try {
            imagen.setImageDrawable(roundedDrawable);
        }catch (Exception e)
        {

        }
    }



    public  void lista_contacto(String id_usuario, String id_conductor)
    {
        chatArrayAdapter = new ChatArrayAdapter(getApplicationContext(), R.layout.right);


        try {

            AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, getString(R.string.nombre_sql), null, Integer.parseInt(getString(R.string.version_sql)));

            SQLiteDatabase bd = admin.getWritableDatabase();
            Cursor fila = bd.rawQuery("select id,id_conductor,id_usuario,titulo,mensaje,fecha,hora,estado,yo,tipo from chat where id_usuario=" + id_usuario + " and id_conductor=" + id_conductor + " order by id asc ", null);

            if (fila.moveToFirst()) {  //si ha devuelto 1 fila, vamos al primero (que es el unico)

                do {

                    int id = Integer.parseInt(fila.getString(0));
                    String id_cond = fila.getString(1);
                    String id_usu = fila.getString(2);
                    String titulo = fila.getString(3);
                    String mensaje = fila.getString(4);
                    String fecha = fila.getString(5);
                    String hora = fila.getString(6);
                    String estado = fila.getString(7);
                    String yo = fila.getString(8);
                    String tipo = fila.getString(9);

                    boolean sw_left = false;
                    if (yo.equals("1")) {
                        sw_left = true;
                    }
                    chatArrayAdapter.add(new CMensaje(sw_left, mensaje, titulo, fecha, hora, Integer.parseInt(estado), Integer.parseInt(id_usu), Integer.parseInt(id_cond), Integer.parseInt(yo),tipo,id));

                } while (fila.moveToNext());

            } else {

            }

            bd.close();

        }catch (Exception e)
        {
            e.printStackTrace();
        }
        listView.setAdapter(chatArrayAdapter);

    }




}
