package com.elisoft.sao_pedido.chat;



import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.elisoft.sao_pedido.R;
import com.elisoft.sao_pedido.SqLite.AdminSQLiteOpenHelper;
import com.elisoft.sao_pedido.Suceso;
import com.elisoft.sao_pedido.notificaciones.SharedPrefManager;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;
import okio.ByteString;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class Chat_pedido extends AppCompatActivity {
    //AUDIO
    private static final String LOG_TAG = "AudioRecordTest";
    public static final int RC_RECORD_AUDIO = 1000;
    public static String sRecordedFileName;
    private MediaRecorder mRecorder;
    MediaPlayer mp;
    ImageView im_grabar;
    boolean sw_grabacion=false;
    private MediaPlayer mPlayer;

    //MENSAJE

    LinearLayout.LayoutParams cero = new LinearLayout.LayoutParams(0, 0);
    LinearLayout.LayoutParams wrap_52  = new LinearLayout.LayoutParams(52, 52);


    private ListView listView;
    private MessageAdapter messageAdapter;
    private EditText et_mensaje;
    private ImageView fb_send;
    ImageView im_perfil;
    TextView tv_nombre;
    Suceso suceso;
    private boolean side =true;
    SharedPreferences perfil;
    Integer id_conductor;
    String id_usuario,nombre;
    public static String uniqueId;

    public static final String mBroadcastStringAction = "com.elisoft.string";
    private IntentFilter mIntentFilter;
    private Boolean hasConnection = false;

    private Thread thread2;
    private boolean startTyping = false;
    private int time = 2;
    public static final String TAG  = "MainActivity";




    private Socket mSocket;
    {
        try {
            mSocket = IO.socket("http://35.226.67.205:5000/");
        } catch (URISyntaxException e) {}
    }

    @SuppressLint("HandlerLeak")
    Handler handler2=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Log.i(TAG, "handleMessage: escritura detenida " + startTyping);
            if(time == 0){
                setTitle("SocketIO");
                Log.i(TAG, "handleMessage: typing stopped time is " + time);
                startTyping = false;
                time = 2;
            }

        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        uniqueId = SharedPrefManager.getInstance(this).mostrarToken();
        Log.i(TAG, "onCreate: " + uniqueId);

        fb_send = findViewById(R.id.bt_enviar);
        listView = findViewById(R.id.msgview);
        et_mensaje = findViewById(R.id.msg);
        im_perfil = findViewById(R.id.im_perfil);
        tv_nombre = findViewById(R.id.tv_nombre);




//audio
        im_grabar=(ImageView)findViewById(R.id.im_grabar);
        sRecordedFileName = getCacheDir().getAbsolutePath() + "/"+new Date().getTime()+".3gp";

        im_grabar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.d(LOG_TAG, "onTouch: " + event.getAction());
                if (event.getAction() == MotionEvent.ACTION_DOWN) {

                    preparar_mensaje_canal();
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    detener_grabacion();
                }
                return true;
            }
        });





        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                CMensaje audio=messageAdapter.getItem(i);
                if(audio.getTipo().equals("AUDIO"))
                {
                    Intent intent = new Intent(getApplicationContext(), Servicio_reproducir.class);
                    intent.putExtra("id_chat",audio.getId());
                    intent.putExtra("mensaje",audio.getMensaje());
                    startService(intent);
                }
            }
        });




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
        id_usuario=perfil.getString("id_usuario", "0");
        nombre=perfil.getString("nombre", "0");



        if(savedInstanceState != null){
            hasConnection = savedInstanceState.getBoolean("hasConnection");
        }

        if(hasConnection){

        }else {
            mSocket.connect();
            mSocket.on("connect user", onNewUser);
            mSocket.on("chat message", onNewMessage);
            mSocket.on("chat"+id_usuario+":"+id_conductor, onNewMessage);
            mSocket.on("on typing", onTyping);

            JSONObject userId = new JSONObject();
            try {
                userId.put("titulo", nombre + " Connected");
                userId.put("id_usuario", id_usuario );
                userId.put("id_conductor", id_conductor );
                mSocket.emit("connect user", userId);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        Log.i(TAG, "onCreate: " + hasConnection);
        hasConnection = true;

        //Intent filter para recibir datos del servicio.
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(mBroadcastStringAction);


        List<CMensaje> messageFormatList = new ArrayList<>();
        messageAdapter = new MessageAdapter(this, R.layout.item_message, messageFormatList);
        listView.setAdapter(messageAdapter);

        wrap_52  = new LinearLayout.LayoutParams(im_grabar.getLayoutParams().width, im_grabar.getLayoutParams().height);
        fb_send.setLayoutParams(cero);
        onTypeButtonEnable();


        lista_de_chat(id_usuario,String.valueOf(id_conductor));
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("hasConnection", hasConnection);
    }

    public void onTypeButtonEnable(){
        et_mensaje.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                JSONObject onTyping = new JSONObject();
                try {
                    onTyping.put("typing", true);
                    onTyping.put("titulo", nombre);
                    onTyping.put("uniqueId", uniqueId);
                    mSocket.emit("on typing", onTyping);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (charSequence.toString().trim().length() > 0) {
                    fb_send.setLayoutParams(wrap_52);
                } else {
                    fb_send.setLayoutParams(cero);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }


    // Aqui se recepciona los mensajes mediante socket.io
    Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.i(TAG, "run: ");
                    Log.i(TAG, "run: " + args.length);
                    JSONObject data = (JSONObject) args[0];
                    String username;
                    String message;
                    String id;
                    try {
                        username = data.getString("titulo");
                        message = data.getString("message");
                        id = data.getString("uniqueId");

                        String id_chat="";
                        String id_cond="";
                        String id_usu="";
                        String titulo="";
                        String mensaje="";
                        String fecha="";
                        String hora="";
                        String tipo="";
                        int yo=0;

                        id_chat = data.getString("id_chat");
                        id_cond = data.getString("id_conductor");
                        id_usu = data.getString("id_usuario");
                        titulo = data.getString("titulo");
                        mensaje = data.getString("mensaje");
                        fecha = data.getString("fecha");
                        hora = data.getString("hora");
                        tipo = data.getString("tipo");


                        Log.i("MENSAJE", "Usuario: " + username +" mesnaje: "+ message+"  id:" + id);

                        if(id.equals(SharedPrefManager.getInstance(Chat_pedido.this).mostrarToken())==true) {
                            yo=1;
                        }else{
                            yo=0;
                        }


                        CMensaje  format = new CMensaje(false, mensaje, titulo, fecha, hora,1, Integer.parseInt(id_usu), Integer.parseInt(id_cond),yo,tipo,Integer.parseInt(id_chat));
                        messageAdapter.add(format);
                        Log.i(TAG, "run:5 ");

                    } catch (Exception e) {
                        return;
                    }
                }
            });
        }
    };

    Emitter.Listener onNewUser = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    int length = args.length;

                    if(length == 0){
                        return;
                    }
                    //Aquí recibo un error extraño
                    //Here i'm getting weird error..................///////run :1 and run: 0
                    Log.i(TAG, "run: ");
                    Log.i(TAG, "run: " + args.length);
                    String titulo =args[0].toString();
                    try {
                        JSONObject object = new JSONObject(titulo);
                        titulo = object.getString("titulo");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    CMensaje format = new CMensaje(false, "", titulo, "", "",1, 0, 0,1,"TEXTO",0);
                    messageAdapter.add(format);
                    listView.smoothScrollToPosition(0);
                    listView.scrollTo(0, messageAdapter.getCount()-1);
                    Log.i(TAG, "run: " + titulo);
                }
            });
        }
    };


    Emitter.Listener onTyping = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    Log.i(TAG, "run: " + args[0]);
                    try {
                        Boolean typingOrNot = data.getBoolean("typing");
                        String userName = data.getString("titulo") + " is Typing......";
                        String id = data.getString("uniqueId");

                        if(id.equals(uniqueId)){
                            typingOrNot = false;
                        }else {
                            setTitle(userName);
                        }

                        if(typingOrNot){

                            if(!startTyping){
                                startTyping = true;
                                thread2=new Thread(
                                        new Runnable() {
                                            @Override
                                            public void run() {
                                                while(time > 0) {
                                                    synchronized (this){
                                                        try {
                                                            wait(1000);
                                                            Log.i(TAG, "run: typing " + time);
                                                        } catch (InterruptedException e) {
                                                            e.printStackTrace();
                                                        }
                                                        time--;
                                                    }
                                                    handler2.sendEmptyMessage(0);
                                                }

                                            }
                                        }
                                );
                                thread2.start();
                            }else {
                                time = 2;
                            }

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    public void sendMessage(String id,
                            String id_conductor,
                            String id_usuario,
                            String titulo,
                            String mensaje,
                            String fecha,
                            String hora,
                            String tipo){
        Log.i(TAG, "sendMessage: ");

        if(TextUtils.isEmpty(mensaje)){
            Log.i(TAG, "sendMessage:2 ");
            return;
        }
        et_mensaje.setText("");
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("message", mensaje);
            jsonObject.put("uniqueId", uniqueId);

            jsonObject.put("id_chat", id);
            jsonObject.put("id_conductor", id_conductor );
            jsonObject.put("id_usuario", id_usuario );
            jsonObject.put("titulo", titulo);
            jsonObject.put("mensaje", mensaje);
            jsonObject.put("fecha", fecha);
            jsonObject.put("hora", hora);
            jsonObject.put("tipo", tipo);


        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.i(TAG, "sendMessage: 1"+ mSocket.emit("mensaje_conductor", jsonObject));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if(isFinishing()){
            Log.i(TAG, "onDestroy: ");

            JSONObject userId = new JSONObject();
            try {
                userId.put("titulo", nombre + " DisConnected");
                mSocket.emit("connect user", userId);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            mSocket.disconnect();
            mSocket.off("chat message", onNewMessage);
            mSocket.off("chat"+id_usuario+":"+id_conductor, onNewMessage);
            mSocket.off("mensaje_conductor", onNewMessage);
            mSocket.off("connect user", onNewUser);
            mSocket.off("on typing", onTyping);
            messageAdapter.clear();
        }else {
            Log.i(TAG, "onDestroy: is rotating.....");
        }

    }

    private boolean enviar_mensaje() {
        if(et_mensaje.getText().toString().trim().length()>0) {
            servicio_enviar_pasajero(id_usuario,String.valueOf(id_conductor),nombre,et_mensaje.getText().toString());

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



    @Override
    public void onPause() {
        super.onPause();
        if (mRecorder != null) {
            mRecorder.release();
            mRecorder = null;
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }


    @AfterPermissionGranted(RC_RECORD_AUDIO)
    private void startRecording() {
        String[] perms = {Manifest.permission.RECORD_AUDIO};
        if (EasyPermissions.hasPermissions(this, perms)) {

            sRecordedFileName=getCacheDir().getAbsolutePath() + "/"+new Date().getTime()+".3gp";
            mRecorder = new MediaRecorder();
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mRecorder.setOutputFile(sRecordedFileName);
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

            try {
                mRecorder.prepare();
            } catch (IOException e) {
                Log.e(LOG_TAG, "prepare() failed");
            }

            mRecorder.start();
        } else {
            EasyPermissions.requestPermissions(this, "Debes otorgar permisos de acceso al Microfono para enviar audio", RC_RECORD_AUDIO, perms);
        }
    }

    private void stopRecording() {
         try {
             mRecorder.stop();
             mRecorder.release();
         }catch (Exception e)
         {
         }

        mRecorder = null;
    }

    private void setRecordIcon(boolean record) {
        if (record) {
            im_grabar.setImageDrawable(
                    VectorDrawableCompat.create(getResources(), R.drawable.ic_enviando_audio_conexion, getTheme()));
        } else {
            im_grabar.setImageDrawable(
                    VectorDrawableCompat.create(getResources(), R.drawable.ic_error_conexion, getTheme()));
        }
    }


    private void preparar_mensaje_canal()
    {
        im_grabar.setImageDrawable(
                VectorDrawableCompat.create(getResources(), R.drawable.ic_preparando_conexion, getTheme()));
        SharedPreferences prefe = getSharedPreferences("perfil_conductor", MODE_PRIVATE);
        inicia_grabacion();
    }

    private void inicia_grabacion()
    {
        mp= MediaPlayer.create(this, R.raw.sonido_conexion);
        mp.start();
        sw_grabacion=true;
        setRecordIcon(true);
        startRecording();
    }

    private void detener_grabacion()
    {
        if(sw_grabacion==true) {
            sw_grabacion=false;

            setRecordIcon(false);
            stopRecording();
            send();

        }
    }



    public void send() {
        sendAudio();
    }

    public void sendAudio() {
        FileChannel in = null;

        try {
            File f = new File(sRecordedFileName);
            in = new FileInputStream(f).getChannel();

            //mSocket.send(START);

            sendAudioBytes(in);

            //mSocket.send(END);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void sendAudioBytes(FileChannel in) throws IOException {
        ByteBuffer buff = ByteBuffer.allocateDirect(32);

        String audio="";

        while (in.read(buff) > 0) {

            buff.flip();
            String bytes = ByteString.of(buff).toString();
            //mSocket.send(bytes);

            try {
                String hexValue = bytes.substring(bytes.indexOf("hex=") + 4, bytes.length() - 1);

                audio+=hexValue;
                /*
                servicio_enviar_audio(String.valueOf(id_chat),
                        hexValue,
                        String.valueOf(numero),
                        getString(R.string.servidor) + "frmAudio.php?opcion=enviar_audio");
                */

                ByteString d = ByteString.decodeHex(hexValue);
                byte[] bytes1 = d.toByteArray();


            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }

            buff.clear();
        }

        servicio_enviar_pasajero_audio(id_usuario,String.valueOf(id_conductor),nombre,audio,"AUDIO");

    }


    private void servicio_enviar_pasajero(final String id_usuario,
                                          final String id_conductor,
                                          final String titulo,
                                          final String mensaje) {


        try {

            JSONObject jsonParam= new JSONObject();
            jsonParam.put("id_usuario", id_usuario);
            jsonParam.put("id_conductor", id_conductor);
            jsonParam.put("titulo", titulo);
            jsonParam.put("mensaje", mensaje);

            String url=getString(R.string.servidor) + "frmChat.php?opcion=enviar_pasajero";
            RequestQueue queue = Volley.newRequestQueue(this);


            JsonObjectRequest myRequest= new JsonObjectRequest(
                    Request.Method.POST,
                    url,
                    jsonParam,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject respuestaJSON) {
                            String id,fecha="",hora="";
                            try {
                                suceso= new Suceso(respuestaJSON.getString("suceso"),respuestaJSON.getString("mensaje"));

                                if (suceso.getSuceso().equals("1")) {
                                    id=respuestaJSON.getString("id");
                                    fecha=respuestaJSON.getString("fecha");
                                    hora=respuestaJSON.getString("hora");


                                    ///////////////////// 8 final //////////////////
                                    Intent serviceIntent = new Intent(Chat_pedido.this, Servicio_guardar_mensaje_enviado.class);
                                    serviceIntent.putExtra("id_chat", id);
                                    serviceIntent.putExtra("id_conductor",id_conductor);
                                    serviceIntent.putExtra("id_usuario", id_usuario);
                                    serviceIntent.putExtra("titulo", titulo);
                                    serviceIntent.putExtra("mensaje",mensaje);
                                    serviceIntent.putExtra("fecha", fecha);
                                    serviceIntent.putExtra("hora", hora);
                                    serviceIntent.putExtra("tipo", "TEXTO");
                                    startService(serviceIntent);

                                    sendMessage(id,id_conductor,id_usuario,titulo,mensaje,fecha,hora,"TEXTO");


                                }
                                else
                                {
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                }
            }
            ){
                public Map<String,String> getHeaders() throws AuthFailureError {
                    Map<String,String> parametros= new HashMap<>();
                    parametros.put("content-type","application/json; charset=utf-8");
                    parametros.put("Authorization","apikey 849442df8f0536d66de700a73ebca-us17");
                    parametros.put("Accept", "application/json");

                    return  parametros;
                }
            };


            // TIEMPO DE ESPERA
            myRequest.setRetryPolicy(new DefaultRetryPolicy(10000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            queue.add(myRequest);


        } catch (Exception e) {

        }
    }

    private void servicio_enviar_pasajero_audio(final String id_usuario,
                                          final String id_conductor,
                                          final String titulo,
                                          final String mensaje,
                                          final String tipo) {


        try {

            JSONObject jsonParam= new JSONObject();
            jsonParam.put("id_usuario", id_usuario);
            jsonParam.put("id_conductor", id_conductor);
            jsonParam.put("titulo", titulo);
            jsonParam.put("mensaje", mensaje);
            jsonParam.put("tipo", tipo);

            String url=getString(R.string.servidor) + "frmChat.php?opcion=enviar_pasajero_audio";
            RequestQueue queue = Volley.newRequestQueue(this);


            JsonObjectRequest myRequest= new JsonObjectRequest(
                    Request.Method.POST,
                    url,
                    jsonParam,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject respuestaJSON) {
                            String id,fecha="",hora="";
                            try {
                                suceso= new Suceso(respuestaJSON.getString("suceso"),respuestaJSON.getString("mensaje"));

                                if (suceso.getSuceso().equals("1")) {
                                    id=respuestaJSON.getString("id");
                                    fecha=respuestaJSON.getString("fecha");
                                    hora=respuestaJSON.getString("hora");


                                    ///////////////////// 8 final //////////////////
                                    Intent serviceIntent = new Intent(Chat_pedido.this, Servicio_guardar_mensaje_enviado.class);
                                    serviceIntent.putExtra("id_chat", id);
                                    serviceIntent.putExtra("id_conductor",id_conductor);
                                    serviceIntent.putExtra("id_usuario", id_usuario);
                                    serviceIntent.putExtra("titulo", titulo);
                                    serviceIntent.putExtra("mensaje",mensaje);
                                    serviceIntent.putExtra("fecha", fecha);
                                    serviceIntent.putExtra("hora", hora);
                                    serviceIntent.putExtra("tipo", tipo);
                                    startService(serviceIntent);

                                    sendMessage(id,id_conductor,id_usuario,titulo,mensaje,fecha,hora,"AUDIO");


                                }
                                else
                                {
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                }
            }
            ){
                public Map<String,String> getHeaders() throws AuthFailureError {
                    Map<String,String> parametros= new HashMap<>();
                    parametros.put("content-type","application/json; charset=utf-8");
                    parametros.put("Authorization","apikey 849442df8f0536d66de700a73ebca-us17");
                    parametros.put("Accept", "application/json");

                    return  parametros;
                }
            };


            // TIEMPO DE ESPERA
            myRequest.setRetryPolicy(new DefaultRetryPolicy(10000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            queue.add(myRequest);


        } catch (Exception e) {

        }
    }

    public  void lista_de_chat(String id_usuario, String id_conductor)
    {

        try {

            AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, getString(R.string.nombre_sql), null, Integer.parseInt(getString(R.string.version_sql)));

            SQLiteDatabase bd = admin.getWritableDatabase();
            Cursor fila = bd.rawQuery("select id,id_conductor,id_usuario,titulo,mensaje,fecha,hora,estado,yo,tipo from chat where id_usuario=" + id_usuario + " and id_conductor=" + id_conductor + " order by id asc ", null);

            if (fila.moveToFirst()) {  //si ha devuelto 1 fila, vamos al primero (que es el unico)

                do {

                    String id =  fila.getString(0);
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
                    if (yo.equals("0")) {
                        sw_left = true;

                    }else{
                        yo="1";
                    }


                    CMensaje format = new CMensaje(sw_left, mensaje, titulo, fecha, hora, Integer.parseInt(estado), Integer.parseInt(id_usu), Integer.parseInt(id_cond),Integer.parseInt(yo),tipo,Integer.parseInt(id));
                    messageAdapter.add(format);




                } while (fila.moveToNext());

            } else {

            }

            bd.close();

        }catch (Exception e)
        {
            e.printStackTrace();
        }


    }


}
