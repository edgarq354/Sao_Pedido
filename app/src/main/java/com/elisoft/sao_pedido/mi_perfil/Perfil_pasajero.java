package com.elisoft.sao_pedido.mi_perfil;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.elisoft.sao_pedido.Constants;
import com.elisoft.sao_pedido.Inicio_Activity;
import com.elisoft.sao_pedido.Principal_pedido;
import com.elisoft.sao_pedido.R;
import com.elisoft.sao_pedido.Servicio_pedido;
import com.elisoft.sao_pedido.SqLite.AdminSQLiteOpenHelper;
import com.elisoft.sao_pedido.Suceso;
import com.facebook.login.LoginManager;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;


public class Perfil_pasajero extends AppCompatActivity implements View.OnClickListener {
    EditText et_nombre,et_apellido;
            TextView tv_cantidad_solicitud;
    TextView et_celular;
    EditText et_correo;
    ImageButton bt_actualizar_dato;
    //LinearLayout bt_editar_password;
    boolean sw=false;
    boolean click;
  //  ImageView perfil;

    de.hdodenhof.circleimageview.CircleImageView im_perfil_pasajero;
    LinearLayout ll_cerrar_sesion,ll_historial;
    Button bt_regresar;

    Suceso suceso;
    ProgressDialog pDialog;



    private String mPath;
    private Uri path;

    Intent CropIntent;





    private   String CARPETA_RAIZ="";
    private   String RUTA_IMAGEN="";

    final int COD_SELECCIONA=10;
    final int COD_FOTO=20;

    String path2;



    String id_usuario="";

    AlertDialog alert2 = null;


    private static final String[] SMS_PERMISSIONS = { Manifest.permission.INTERNET,
            Manifest.permission.READ_SMS,
            Manifest.permission.RECEIVE_SMS,
            Manifest.permission.ACCESS_NETWORK_STATE };

    @Override
    protected void onStart() {
        cargar_datos();
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_usuario);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ll_cerrar_sesion=findViewById(R.id.ll_cerrar_sesion);
        im_perfil_pasajero=findViewById(R.id.im_perfil_pasajero);

        et_nombre= findViewById(R.id.et_nombre);
        et_apellido = findViewById(R.id.et_apellido);


        et_correo= findViewById(R.id.et_correo);
        et_celular= findViewById(R.id.et_celular);

        im_perfil_pasajero.setOnClickListener(this);
        ll_cerrar_sesion.setOnClickListener(this);
        et_celular.setOnClickListener(this);
        //perfil= findViewById(R.id.perfil);

        bt_actualizar_dato= findViewById(R.id.bt_actualizar_dato);
        //bt_editar_password= findViewById(R.id.bt_editar_password);

        cargar_datos();
        click=false;

        bt_actualizar_dato.setOnClickListener(this);


        imagen_en_vista(null);



         CARPETA_RAIZ=getString(R.string.app_name)+"/";
        RUTA_IMAGEN=CARPETA_RAIZ+"misFotos";
        SharedPreferences perfil2=getSharedPreferences("perfil",MODE_PRIVATE);
        id_usuario=perfil2.getString("id_usuario","");
    }



    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }





    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.ll_cerrar_sesion:
            cerrar_sesion();
            break;
            case R.id.im_perfil_pasajero:
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    verificar_permiso_camara();
                }
                else if ((ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)||(ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
                    verificar_permiso_almacenamiento();
                } else {

                    cargarImagen();
                }
            break;
            case R.id.et_celular:
                verificar_permiso_sms();
                break;
            case R.id.bt_actualizar_dato:
                finish();
                break;

        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            //ubicacion de la imagen

            switch (requestCode){
                case 1:


                    try {



                        if(savebitmap()==true)
                        {
                            subir_imagen_servidor("imagen_1");
                        }else{
                            mensaje("Vuelve a tomar la foto.");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this,"Error al subir la imagen. ->"+e.toString(),Toast.LENGTH_LONG).show();
                    }

                    break;


                case COD_SELECCIONA:
                    Uri uri=data.getData();
                    Bitmap bitmap = null;
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),uri);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    guardar_en_memoria(bitmap);

                    CropImage(uri);




                    // perfil.setImageURI(miPath);
                    break;

                case COD_FOTO:
                    MediaScannerConnection.scanFile(this, new String[]{path2}, null,
                            new MediaScannerConnection.OnScanCompletedListener() {
                                @Override
                                public void onScanCompleted(String path, Uri uri) {
                                    Log.i("Ruta de almacenamiento","Path: "+path2);
                                    CropImage(uri);
                                }
                            });


                    //Convertir MPath a Bitmap
                    // File newFile2 = new File(path2);
                    //  Uri uri2 = Uri.fromFile(newFile2);






                    break;


            }


        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("file_path", mPath);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        mPath = savedInstanceState.getString("file_path");
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == 100){
            if(grantResults.length == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "Permisos aceptados", Toast.LENGTH_SHORT).show();
                im_perfil_pasajero.setEnabled(true);
            }
        }else if(requestCode == 101){
            startActivity(new Intent(this,Actualizar_celular.class));
            cargar_datos();
        }else{
            showExplanation();
        }
    }

    public void  cargarImagen()
    {

        // get prompts.xml view
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View promptView = layoutInflater.inflate(R.layout.seleccionar_opcion, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setView(promptView);


        final LinearLayout ll_camara= promptView.findViewById(R.id.ll_camara);
        final LinearLayout ll_galeria= promptView.findViewById(R.id.ll_galeria);


        ll_camara.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tomarFotografia();
                alert2.cancel();
            }
        });

        ll_galeria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                abrir_galeria();
                alert2.cancel();
            }
        });

        // create an alert dialog
        alert2 = alertDialogBuilder.create();
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.x=0;
        lp.y=0;


        alert2.getWindow().setAttributes(lp);

        alert2.getWindow().getAttributes().gravity= Gravity.BOTTOM;
        alert2.getWindow().getAttributes().horizontalMargin=0.01F;
        alert2.getWindow().getAttributes().verticalMargin=0.01F;
        alert2.show();


    }

    private void showExplanation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
        builder.setTitle("Permisos denegados");
        builder.setMessage("Para usar las funciones de la app necesitas aceptar los permisos");
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }
        });
        try {
            builder.show();
        }catch (Exception e){
            Log.e("Permiso Explanation","sin permiso en configuracion");
        }
    }

    public Bitmap imagen_cuadrado(Bitmap originalBitmap)
    {
        if (originalBitmap.getWidth() > originalBitmap.getHeight()){
            originalBitmap = Bitmap.createBitmap(originalBitmap, 0, 0, originalBitmap.getHeight(), originalBitmap.getHeight());
        }else if (originalBitmap.getWidth() < originalBitmap.getHeight()) {
            originalBitmap = Bitmap.createBitmap(originalBitmap, 0, 0, originalBitmap.getWidth(), originalBitmap.getWidth());
        }
        return originalBitmap;
    }
    public void cargar_datos()
    {
        /*
        SharedPreferences perfil=getSharedPreferences("perfil",MODE_PRIVATE);
        nombre.setText(perfil.getString("nombre",""));
        apellido.setText(perfil.getString("apellido",""));
        if(perfil.getString("celular","").length()>7)
        {
            celular.setText(perfil.getString("celular",""));
        }
        email.setText(perfil.getString("email",""));

         */
        SharedPreferences perfil=getSharedPreferences("perfil",MODE_PRIVATE);
        et_nombre.setText(perfil.getString("nombre",""));
        et_apellido.setText(perfil.getString("apellido",""));

        if(perfil.getString("celular","").length()>7)
        {
            et_celular.setText(perfil.getString("celular",""));
        }
        et_correo.setText(perfil.getString("email",""));

    }

    public void fb_reconfiguracion(boolean direccion)
    {
        /*
        click=direccion;
        //  editar.setImageResource(R.drawable.ic_menu_manage);

        this.nombre.setEnabled(direccion);
        this.apellido.setEnabled(direccion);
        this.celular.setEnabled(direccion);
        this.email.setEnabled(direccion);

        if(direccion)
        {
            //   editar.setImageResource(R.drawable.ic_menu_send);

        }

        */
    }

    private void guardar_en_memoria(Bitmap bitmapImage)
    {
        File file=null;
        FileOutputStream fos = null;

        SharedPreferences perfil=getSharedPreferences("perfil",MODE_PRIVATE);
        try {
            String APP_DIRECTORY = getString(R.string.app_name)+"/";//nombre de directorio
            String MEDIA_DIRECTORY = APP_DIRECTORY + "Imagen";//nombre de la carpeta
            file = new File(Environment.getExternalStorageDirectory(), MEDIA_DIRECTORY);

            boolean isDirectoryCreated = file.exists();//pregunto si esxiste el directorio creado
            if(!isDirectoryCreated)
                isDirectoryCreated = file.mkdirs();

            if(isDirectoryCreated) {
                fos = new FileOutputStream(new File(path2));
                // Use the compress method on the BitMap object to write image to the OutputStream
                bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.close();


            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void validacion()
    {
/*
                        //cargamos los datos.
                        Servicio servicio=new Servicio();
                        SharedPreferences perfil =getSharedPreferences("perfil",MODE_PRIVATE);
                        servicio.execute(getString(R.string.servidor)+"frmUsuario.php?opcion=actualizar_dato","1",perfil.getString("id_usuario",""),nombre.getText().toString(),apellido.getText().toString(),celular.getText().toString(),email.getText().toString());
*/


    }



    public void imagen_en_vista(ImageView imagen)
    { Drawable dw;
        SharedPreferences perfil=getSharedPreferences("perfil",MODE_PRIVATE);
        String mPath = Environment.getExternalStorageDirectory() + File.separator + getString(R.string.app_name)+"/Imagen"
                + File.separator + perfil.getString("id_usuario","")+"_perfil.jpg";


        File newFile = new File(mPath);
        Bitmap bitmap =  BitmapFactory.decodeFile(newFile.getAbsolutePath());
        //Convertir Bitmap a Drawable.
        dw = new BitmapDrawable(getResources(), bitmap);
        //se edita la imagen para ponerlo en circulo.

        if( bitmap==null)
        { dw = getResources().getDrawable(R.drawable.ic_perfil_negro);}

          im_perfil_pasajero.setImageDrawable(dw);
        //imagen_circulo(dw,imagen);


    }

    public void imagen_circulo(Drawable id_imagen, ImageView imagen) {
        im_perfil_pasajero.setImageDrawable(id_imagen);
    }

    public static Bitmap ReducirImagen_b(Bitmap BitmapOrg, int w, int h) {

        int width = BitmapOrg.getWidth();
        int height = BitmapOrg.getHeight();
        int newWidth = w;
        int newHeight = h;
        // calculamos el escalado de la imagen destino
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // para poder manipular la imagen
        // debemos crear una matriz
        Matrix matrix = new Matrix();
        // Cambiar el tamaño del mapa de bits
        matrix.postScale(scaleWidth, scaleHeight);
        // volvemos a crear la imagen con los nuevos valores
        Bitmap resizedBitmap = Bitmap.createBitmap(BitmapOrg, 0, 0, width, height, matrix, true);

        return resizedBitmap;
    }

    /*Ahora que se tiene la imagen cargado en mapa de bits.
Vamos a convertir este mapa de bits a cadena de base64
este método es para convertir este mapa de bits a la cadena de base64*/
    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }
    public void mensaje(String mensaje)
    {
        Toast toast = Toast.makeText(this,mensaje, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.show();
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

    private void CropImage(Uri uri) {
        Uri uri2=Uri.fromFile(new File(path2));
        try{
            CropIntent = new Intent("com.android.camera.action.CROP");
            CropIntent.setDataAndType(uri,"image/*");

            CropIntent.putExtra("crop","true");
            CropIntent.putExtra(MediaStore.EXTRA_OUTPUT,uri2);
            CropIntent.putExtra("outputX",6000);
            CropIntent.putExtra("outputY",6000);
            CropIntent.putExtra("aspectX",1);
            CropIntent.putExtra("aspectY",1);
            CropIntent.putExtra("scaleUpIfNeeded",true);
            CropIntent.putExtra("return-data",true);

            startActivityForResult(CropIntent,1);
        }
        catch (ActivityNotFoundException ex)
        {

        }

    }


    public void verificar_permiso_sms()
    {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_SMS)) {
            //YA LO CANCELE Y VOUELVO A PERDIR EL PERMISO.

            AlertDialog.Builder dialogo1 = new AlertDialog.Builder(this);
            dialogo1.setTitle("Atención!");
            dialogo1.setMessage("Debes otorgar permisos de SMS para realizar la autenficación");
            dialogo1.setCancelable(false);
            dialogo1.setPositiveButton("Solicitar permiso", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogo1, int id) {
                    dialogo1.cancel();
                    ActivityCompat.requestPermissions(Perfil_pasajero.this,
                            SMS_PERMISSIONS,
                            101);

                }
            });
            dialogo1.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogo1, int id) {
                    dialogo1.cancel();

                }
            });
            dialogo1.show();
        } else {
            ActivityCompat.requestPermissions(Perfil_pasajero.this,
                    SMS_PERMISSIONS,
                    101);
        }
    }

    public void verificar_permiso_camara()
    {
        final String[] CAMERA_PERMISSIONS = { Manifest.permission.INTERNET,
                Manifest.permission.CAMERA,
                Manifest.permission.ACCESS_NETWORK_STATE };

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
            //YA LO CANCELE Y VOUELVO A PERDIR EL PERMISO.

            AlertDialog.Builder dialogo1 = new AlertDialog.Builder(this);
            dialogo1.setTitle("Atención!");
            dialogo1.setMessage("Debes otorgar permisos de acceso a CAMARA.");
            dialogo1.setCancelable(false);
            dialogo1.setPositiveButton("Solicitar permiso", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogo1, int id) {
                    dialogo1.cancel();
                    ActivityCompat.requestPermissions(Perfil_pasajero.this,
                            CAMERA_PERMISSIONS,
                            1);

                }
            });
            dialogo1.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogo1, int id) {
                    dialogo1.cancel();

                }
            });
            dialogo1.show();
        } else {
            ActivityCompat.requestPermissions(Perfil_pasajero.this,
                    CAMERA_PERMISSIONS,
                    1);
        }
    }

    public void verificar_permiso_almacenamiento()
    {
        final String[] CAMERA_PERMISSIONS = { Manifest.permission.INTERNET,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_NETWORK_STATE };

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)||
                ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            //YA LO CANCELE Y VOUELVO A PERDIR EL PERMISO.

            AlertDialog.Builder dialogo1 = new AlertDialog.Builder(this);
            dialogo1.setTitle("Atención!");
            dialogo1.setMessage("Debes otorgar permisos de acceso a ALMACENAMIENTO.");
            dialogo1.setCancelable(false);
            dialogo1.setPositiveButton("Solicitar permiso", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogo1, int id) {
                    dialogo1.cancel();
                    ActivityCompat.requestPermissions(Perfil_pasajero.this,
                            CAMERA_PERMISSIONS,
                            1);

                }
            });
            dialogo1.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogo1, int id) {
                    dialogo1.cancel();

                }
            });
            dialogo1.show();
        } else {
            ActivityCompat.requestPermissions(Perfil_pasajero.this,
                    CAMERA_PERMISSIONS,
                    1);
        }
    }

    public void  cerrar_sesion_principal()
    {
        AlertDialog.Builder dialogo1 = new AlertDialog.Builder(this);
        dialogo1.setTitle(getString(R.string.app_name));
        dialogo1.setMessage("¿Cerrar Sesion?");
        dialogo1.setCancelable(false);
        dialogo1.setPositiveButton("SI", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogo1, int id) {
                //cargamos los datos
                SharedPreferences prefe = getSharedPreferences("ultimo_pedido", Context.MODE_PRIVATE);
                try {
                    int id_pedido = Integer.parseInt(prefe.getString("id_pedido", ""));
                    if (id_pedido == 0) {
                        cerrar_sesion();
                    } else if (id_pedido != 0 ) {
                        mensaje_cerrar_sesion("No se puede Cerrar Sesion porque tiene un pedido en camino.",id_pedido);

                    }
                } catch (Exception e) {
                    cerrar_sesion();
                }


            }
        });
        dialogo1.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogo1, int id) {

            }
        });
        dialogo1.show();
    }

    public void vaciar_toda_la_base_de_datos() {
        try {
            AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, getString(R.string.nombre_sql), null, Integer.parseInt(getString(R.string.version_sql)));

            SQLiteDatabase db = admin.getWritableDatabase();
            db.execSQL("delete from notificacion");
            db.execSQL("delete from direccion");
            db.execSQL("delete from puntos_pedido");
            db.execSQL("delete from pedido_usuario");
            db.close();
        }catch (Exception e)
        {
            Log.e("Vaciar base",""+e);
        }
        // Log.e("sqlite ", "vaciar todo");
    }

    public void cerrar_sesion()
    {
        LoginManager.getInstance().logOut();
        SharedPreferences perfil=getSharedPreferences("perfil",MODE_PRIVATE);
        SharedPreferences.Editor editar=perfil.edit();
        editar.putString("id","");
        editar.putString("id_usuario","");
        editar.putString("nombre","");
        editar.putString("apellido","");
        editar.putString("ci","");
        editar.putString("email","");
        editar.putString("direccion","");
        editar.putString("marca","");
        editar.putString("modelo","");
        editar.putString("placa","");
        editar.putString("celular","");
        editar.putString("credito","");
        editar.putString("login_usuario","");
        editar.putString("login_taxi","");
        editar.commit();
        vaciar_toda_la_base_de_datos();
        Intent serv = new Intent(getApplicationContext(), Servicio_pedido.class);
        serv.setAction(Constants.ACTION_RUN_ISERVICE);
        stopService(serv);
        Intent intent=new Intent(getApplicationContext(), Inicio_Activity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP| Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public void mensaje_cerrar_sesion(String mensaje, final int id_pedido)
    {
        try {
            AlertDialog.Builder dialogo1 = new AlertDialog.Builder(this);
            dialogo1.setTitle("Atención");
            dialogo1.setMessage(mensaje);
            dialogo1.setCancelable(false);
            dialogo1.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogo1, int id) {
                    Intent pedido=new Intent(getApplication(), Principal_pedido.class);
                    pedido.putExtra("id_pedido",String.valueOf(id_pedido));
                    startActivity(pedido);
                    finish();
                }
            });

            dialogo1.show();
        }catch (Exception e){

        }
    }



    private void tomarFotografia() {
        File fileImagen=new File(Environment.getExternalStorageDirectory(),RUTA_IMAGEN);
        boolean isCreada=fileImagen.exists();
        String nombreImagen="";
        if(isCreada==false){
            isCreada=fileImagen.mkdirs();
        }

        if(isCreada==true){
            nombreImagen=(System.currentTimeMillis()/1000)+".jpg";
        }


        path2=Environment.getExternalStorageDirectory()+
                File.separator+RUTA_IMAGEN+File.separator+nombreImagen;

        File imagen=new File(path2);

        Intent intent=null;
        intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        ////
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.N)
        {
            String authorities=getApplicationContext().getPackageName()+".provider";
            Uri imageUri= FileProvider.getUriForFile(this,authorities,imagen);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        }else
        {
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imagen));
        }
        startActivityForResult(intent,COD_FOTO);

        ////
    }

    public void abrir_galeria()
    {
        File fileImagen=new File(Environment.getExternalStorageDirectory(),RUTA_IMAGEN);
        boolean isCreada=fileImagen.exists();
        String nombreImagen="";
        if(isCreada==false){
            isCreada=fileImagen.mkdirs();
        }

        if(isCreada==true){
            nombreImagen=(System.currentTimeMillis()/1000)+".jpg";
        }


        path2=Environment.getExternalStorageDirectory()+
                File.separator+RUTA_IMAGEN+File.separator+nombreImagen;

        File imagen=new File(path2);

        Intent pictureActionIntent = null;
        pictureActionIntent = new Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pictureActionIntent.setType("image/*");
        startActivityForResult(
                pictureActionIntent,
                COD_SELECCIONA);
        //startActivityForResult(intent.createChooser(intent,"Seleccione la Aplicación"),COD_SELECCIONA);
    }




    public boolean savebitmap() {

        boolean result = false;
        // nombre = fecha + valoresGenerales.isTipoArchivoImagenExtencionPng;

        String filename = path2;

        OutputStream outStream = null;

        File file = new File(filename );


        int m_inSampleSize = 0;
        int m_compress = 80;

        // 100 dejarlo original
        //  0  comprimir al maximo .. no se recomeinda.


        try {

            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inPurgeable = true;
            bmOptions.inSampleSize = m_inSampleSize;
            //Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), bmOptions);
            Bitmap bitmap = BitmapFactory.decodeFile(filename, bmOptions);
            // make a new bitmap from your file
            //Bitmap bitmap = BitmapFactory.decodeFile(file.getName());

            outStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, m_compress, outStream);
            outStream.flush();
            outStream.close();
            result = true;



        } catch (Exception e) {
            e.printStackTrace();
            result = false;
        }
        Log.e("file", "" + file);
        return result;

    }


    private void subir_imagen_servidor(String opcion_subir_imagen ) {



        switch (opcion_subir_imagen)
        {

            case "imagen_1":
                serverUpdate(getString(R.string.servidor) + "frmUsuario.php?opcion=insertar_imagen_perfil",opcion_subir_imagen);
                break;

        }
    }

    class ServerUpdate extends AsyncTask<String,String,String> {

        ProgressDialog pDialog;
        String resultado="",tipo="";
        @Override
        protected String doInBackground(String... arg0) {
            resultado=uploadFoto(arg0[0],arg0[1] );
            tipo=arg0[1];


            if(suceso.getSuceso().equals("1"))
                runOnUiThread(new Runnable(){
                    @Override
                    public void run() {

                        // TODO Auto-generated method stub
                        Toast.makeText(Perfil_pasajero.this, suceso.getMensaje(),Toast.LENGTH_LONG).show();
                    }
                });
            else
                runOnUiThread(new Runnable(){
                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        AlertDialog.Builder builder = new AlertDialog.Builder(Perfil_pasajero.this);
                        builder.setTitle("Importante");
                        builder.setMessage(suceso.getMensaje());
                        builder.setPositiveButton("OK", null);
                        builder.create();
                        builder.show();
                    }
                });
            return null;
        }
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Perfil_pasajero.this);
            pDialog.setMessage("Subiendo la imagen, espere..." );
            pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pDialog.setCancelable(false);
            pDialog.show();
        }
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            pDialog.dismiss();


            if(resultado.equals("2"))
            {
                File newFile = new File(path2);
                Bitmap bitmap =  BitmapFactory.decodeFile(newFile.getAbsolutePath());
                //Convertir Bitmap a Drawable.
                Drawable dw = new BitmapDrawable(getResources(), bitmap);
                //se edita la imagen para ponerlo en circulo.

                if( bitmap==null)
                { dw = getResources().getDrawable(R.drawable.ic_perfil_negro);}

                imagen_circulo(dw,null);
                guardar_en_memoria_BITMAP(bitmap);



            }else if(resultado.equals("500"))
            {
                mensaje(suceso.getMensaje());
            }else
            {
                mensaje("Falla en tu conexion a internet.");
            }
        }

    }



    private void serverUpdate(String url,String tipo){
        File file = new File(path2);
        if (file.exists()){
            new ServerUpdate().execute(url,tipo);
        }
        else
        {
            Log.e("Imagen","No se pudo localizar la imagen");
        }
    }


    ///PRUEBA DE INSERTAR IMAGEN

    private String uploadFoto(String url,String tipo){
        String devuelve="";
        File file = new File(path2);

        HttpClient httpclient = new DefaultHttpClient();
        httpclient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
        HttpPost httppost = new HttpPost(url);

        MultipartEntity mpEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
        ContentBody foto = new FileBody(file);
        mpEntity.addPart("imagen", foto);


        try {
            mpEntity.addPart("id_usuario", new StringBody(id_usuario));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        httppost.setEntity(mpEntity);


        String resultado;
        HttpResponse response;
        try {
            response=httpclient.execute(httppost);
            HttpEntity entity =response.getEntity();

            InputStream inputStream= entity.getContent();
            resultado=convertStreamToString(inputStream);

            JSONObject respuestaJSON = new JSONObject(resultado);//Creo un JSONObject a partir del
            suceso=new Suceso(respuestaJSON.getString("suceso"),respuestaJSON.getString("mensaje"));

            if (suceso.getSuceso().equals("1")) {

                if(tipo=="imagen_1"){

                    devuelve="2";
                }else{
                    devuelve = "500";
                }
            }else
            {
                devuelve = "500";
            }

            httpclient.getConnectionManager().shutdown();


            return devuelve;
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return devuelve;
    }

    public String convertStreamToString(InputStream is) throws IOException{
        if(is!=null)
        {
            StringBuilder sb= new StringBuilder();
            String line;
            try{
                BufferedReader reader=new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
                while ((line=reader.readLine())!=null){
                    sb.append(line).append("\n");
                }

            }finally {
                is.close();
            }
            return  sb.toString();
        }else {
            return "";
        }
    }


    private void guardar_en_memoria_BITMAP(Bitmap bitmapImage)
    {

        if(bitmapImage!=null) {

            try {
                File file=null;
                FileOutputStream fos = null;
                String APP_DIRECTORY = getString(R.string.app_name)+"/";//nombre de directorio
                String MEDIA_DIRECTORY = APP_DIRECTORY + "Imagen";//nombre de la carpeta
                file = new File(Environment.getExternalStorageDirectory(), MEDIA_DIRECTORY);
                File mypath = new File(file, id_usuario+ "_perfil.jpg");//nombre del archivo imagen

                boolean isDirectoryCreated = file.exists();//pregunto si esxiste el directorio creado
                if (!isDirectoryCreated)
                    isDirectoryCreated = file.mkdirs();

                if (isDirectoryCreated) {
                    fos = new FileOutputStream(mypath);
                    // Use the compress method on the BitMap object to write image to the OutputStream
                    bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                    fos.close();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }





}
