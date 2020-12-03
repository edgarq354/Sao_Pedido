package com.elisoft.sao_pedido.carrito;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.elisoft.sao_pedido.Pedido_usuario;
import com.elisoft.sao_pedido.R;
import com.elisoft.sao_pedido.SqLite.AdminSQLiteOpenHelper;
import com.elisoft.sao_pedido.Suceso;
import com.elisoft.sao_pedido.producto.ProductoPrincipal;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class Detalle_carrito extends AppCompatActivity implements View.OnClickListener {

    private RequestQueue queue=null;
        ListView lista ;

        Suceso suceso;
        private ProgressDialog pDialog;

        ArrayList<CCarrito> carrito;
        int id_pedido=0,id_empresa=0;
        TextView tv_tiempo_preparacion;
        TextView tv_monto_total;
        TextView tv_monto_envio;
        Button bt_cancelar,bt_confirmar;
        String direccion="";

        JSONArray jsa_carrito;

        String mensaje_carrito="";

        double latitud=0,longitud=0;
        double latitud_inicio=0,longitud_inicio=0;
        double latitud_fin=0,longitud_fin=0;
        double monto_envio=0;
        AlertDialog alert2 = null;
        SharedPreferences mis_datos;

        LinearLayout ll_direccion_banner_lugar;
        TextView tv_nombre_lugar;


        int sw_boton=0;

    int cantidad_solicitud_tarifa=0;
        @Override
        protected void onRestart() {
            cargar_carrito_en_la_lista();
            mostrar_monto_total();
            super.onRestart();
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_detalle_carrito);
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            tv_tiempo_preparacion = (TextView) findViewById(R.id.tv_tiempo_preparacion);
            tv_monto_envio = (TextView) findViewById(R.id.tv_monto_envio);
            tv_monto_total = (TextView) findViewById(R.id.tv_monto_total);
            lista  = (ListView) findViewById(R.id.lista);
            bt_cancelar=(Button) findViewById(R.id.bt_cancelar);
            bt_confirmar=(Button) findViewById(R.id.bt_confirmar);
            tv_nombre_lugar= findViewById(R.id.tv_nombre_lugar);
            ll_direccion_banner_lugar= findViewById(R.id.ll_direccion_banner_lugar);



            bt_confirmar.setOnClickListener(this);
            bt_cancelar.setOnClickListener(this);


            try {

                SharedPreferences carrito= getSharedPreferences(getString(R.string.sql_pedido),MODE_PRIVATE);
                latitud=Double.parseDouble(carrito.getString("latitud","0"));
                longitud=Double.parseDouble(carrito.getString("longitud","0"));

                latitud_fin=Double.parseDouble(carrito.getString("milatitud","0"));
                longitud_fin=Double.parseDouble(carrito.getString("milongitud","0"));

                latitud_inicio=latitud;
                longitud_inicio=longitud;

                String direccion_banner=carrito.getString("direccion_banner_lugar","");
                tv_nombre_lugar.setText(carrito.getString("nombre_lugar",""));

                String url_banner = getString(R.string.servidor_web)+"storage/" + direccion_banner;
                final ImageView img = new ImageView(this);
                Picasso.with(this).load(url_banner).placeholder(R.mipmap.ic_launcher).into(img, new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() {
                        ll_direccion_banner_lugar.setBackground(img.getDrawable());
                    }

                    @Override
                    public void onError() {
                    }
                });

            }catch (Exception e)
            {

            }

            Bundle bundle=getIntent().getExtras();

            try{
                sw_boton=bundle.getInt("boton",0);
            }catch (Exception e)
            {
                sw_boton=0;
            }




            lista .setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    //ver_producto(carrito.get(i));
                    editar_carrito_producto(carrito.get(i));
                }
            });
            cargar_carrito_en_la_lista();
            mostrar_monto_total();
            mis_datos=getSharedPreferences(getString(R.string.mis_datos),MODE_PRIVATE);
            direccion=obtener_direccion(latitud,longitud);

            cantidad_solicitud_tarifa=0;


            SharedPreferences carrito = getSharedPreferences(getString(R.string.sql_pedido), MODE_PRIVATE);
            double aux_monto_total=Double.parseDouble(carrito.getString("monto_total","0"));
            if (aux_monto_total>0)
            {
                servicio_calcular_tarifa();
            }

        }

        @Override
        public boolean onSupportNavigateUp() {
            finish();
            return super.onSupportNavigateUp();
        }

        public void cargar_datos()
        {
            cargar_carrito_en_la_lista();
            mostrar_monto_total();
        }


        public  void  mostrar_monto_total()
        {
            SharedPreferences carrito = getSharedPreferences(getString(R.string.sql_pedido), MODE_PRIVATE);
            tv_monto_total.setText("Bs. "+carrito.getString("monto_total","0,00"));
            double aux_monto_total=Double.parseDouble(carrito.getString("monto_total","0"));
            if (aux_monto_total<=0)
            {
                finish();
            }
        }

        private void ver_producto(CCarrito cEmpresa) {

            SharedPreferences sp=getSharedPreferences(getString(R.string.sql_pedido),MODE_PRIVATE);

            Intent i=new Intent(this, ProductoPrincipal.class);
            i.putExtra("id",cEmpresa.getId_producto());
            i.putExtra("nombre",cEmpresa.getNombre());
            i.putExtra("descripcion",cEmpresa.getDescripcion());
            i.putExtra("precio",String.valueOf(cEmpresa.getMonto_unidad()));
            i.putExtra("direccion_imagen",cEmpresa.getUrl());
            i.putExtra("id_empresa",sp.getInt("id_empresa",0));
            startActivity(i);
        }

        public void cargar_carrito_en_la_lista()
        {


            SharedPreferences ca= getSharedPreferences(getString(R.string.sql_pedido),MODE_PRIVATE);
            id_empresa=ca.getInt("id_empresa",0);

            carrito = new ArrayList<CCarrito>();
            jsa_carrito=new JSONArray();

            AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, getString(R.string.nombre_sql), null, Integer.parseInt(getString(R.string.version_sql)));


            SQLiteDatabase bd = admin.getWritableDatabase();
            Cursor fila = bd.rawQuery("select id_producto,id_pedido,cantidad,nombre,descripcion,url,monto_unidad,monto_total from carrito where id_pedido="+id_pedido   , null);

            mensaje_carrito="";

            if (fila.moveToFirst()) {  //si ha devuelto 1 fila, vamos al primero (que es el unico)

                do {


                    CCarrito hi =new CCarrito(fila.getInt(0),
                            fila.getInt(1),
                            fila.getInt(2),
                            fila.getString(3),
                            fila.getString(4),
                            fila.getString(5),
                            fila.getDouble(6),
                            fila.getDouble(7)
                    );
                    carrito.add(hi);

                    mensaje_carrito+= fila.getString(2)+" "+fila.getString(3)+" x"+fila.getString(6)+" = "+fila.getString(7)+" bs \n";

                    int id_producto = Integer.parseInt(fila.getString(0));
                    int id_pedido  = Integer.parseInt(fila.getString(1));
                    int cantidad  = Integer.parseInt(fila.getString(2));
                    double monto_unidad  = Double.parseDouble(fila.getString(6));
                    double monto_total  = Double.parseDouble(fila.getString(7));



                    try {
                        JSONObject object = new JSONObject();
                        object.put("id_producto", id_producto);
                        object.put("id_pedido", id_pedido);
                        object.put("cantidad", cantidad);
                        object.put("monto_unidad", monto_unidad);
                        object.put("monto_total", monto_total);

                        jsa_carrito.put(object);
                    }catch (Exception ee)
                    {}


                } while(fila.moveToNext());

            }
            //   Toast.makeText(this, "No hay registrados",Toast.LENGTH_SHORT).show();

            bd.close();
            Item_carrito adaptador = new Item_carrito(this,this,carrito);
            lista.setAdapter(adaptador);
        }

        private void eliminar_carrito() {
            SharedPreferences carrito = getSharedPreferences(getString(R.string.sql_pedido), MODE_PRIVATE);
            SharedPreferences.Editor ed_c = carrito.edit();
            ed_c.putInt("id_empresa", 0);
            ed_c.putInt("id_lugar", 0);
            ed_c.putInt("id_categoria", 0);
            ed_c.putString("nombre_categoria", "");
            ed_c.putString("monto_total","0");
            ed_c.commit();


            try {
                AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, getString(R.string.nombre_sql), null, Integer.parseInt(getString(R.string.version_sql)));
                SharedPreferences pedido = getSharedPreferences(getString(R.string.sql_pedido), MODE_PRIVATE);
                SQLiteDatabase db = admin.getWritableDatabase();
                db.execSQL("delete from carrito where   id_pedido=" + pedido.getInt("id", 0));
                db.close();
            }catch (Exception e)
            {
                Log.e("Vaciar base",""+e);
            }
            sumar_lista();
            finish();
        }
        public void sumar_lista( ) {

            SharedPreferences pedido = getSharedPreferences(getString(R.string.sql_pedido), MODE_PRIVATE);

            AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, getString(R.string.nombre_sql), null, Integer.parseInt(getString(R.string.version_sql)));

            SQLiteDatabase bd = admin.getWritableDatabase();
            Cursor fila = bd.rawQuery("select  monto_total from carrito where  id_pedido=" + pedido.getInt("id", 0), null);
            double aux_monto_total = 0;
            if (fila.moveToFirst()) {  //si ha devuelto 1 fila, vamos al primero (que es el unico)

                do {
                    double d_monto_total = Double.parseDouble(fila.getString(0));
                    aux_monto_total += d_monto_total;


                } while (fila.moveToNext());

                SharedPreferences carrito = getSharedPreferences(getString(R.string.sql_pedido), MODE_PRIVATE);
                SharedPreferences.Editor ed_c = carrito.edit();
                ed_c.putInt("id_empresa", id_empresa);
                ed_c.putString("monto_total", String.valueOf(aux_monto_total));
                ed_c.commit();

            }else
            {
                SharedPreferences carrito = getSharedPreferences(getString(R.string.sql_pedido), MODE_PRIVATE);
                SharedPreferences.Editor ed_c = carrito.edit();
                ed_c.putInt("id_lugar", 0);
                ed_c.putInt("id_categoria", 0);
                ed_c.putString("nombre_categoria", "");
                ed_c.putString("monto_total","0");
                ed_c.commit();
            }
            bd.close();

        }

        @Override
        public void onClick(View v) {
            switch (v.getId())
            {
                case R.id.bt_cancelar:
                    eliminar_carrito();
                    break;
                case R.id.bt_confirmar:
                    confirmar_pedido();
                    break;
            }
        }

        public String obtener_direccion(double lat, double lon) {
            Geocoder geocoder = new Geocoder(getBaseContext());
            List<Address> addresses = null;
            String s_direccion= "";

            try {
                addresses = geocoder.getFromLocation(lat, lon, 1);
            } catch (IOException ioException) {
                // Catch network or other I/O problems.

            } catch (IllegalArgumentException illegalArgumentException) {

            }

            // Handle case where no address was found.
            if (addresses == null || addresses.size() == 0) {
                //error. o no tiene datos recolectados...
            } else {

// Funcion que determina si se obtuvo resultado o no

                // Creamos el objeto address
                Address address = addresses.get(0);


                if (addresses.size() > 0) {
                    int cantidad=addresses.get(0).getMaxAddressLineIndex();
                    for (int i = 0; i <= cantidad; i++)
                    {
                        s_direccion+= addresses.get(0).getAddressLine(i) + ",";
                    }
                }


                // Creamos el string a partir del elemento direccion
                String direccionText = String.format("%s, %s",
                        address.getMaxAddressLineIndex() > 0 ? address.getAddressLine(0) : "",
                        address.getFeatureName());


                //  et_direccion.setText(address.getFeatureName()+" | "+address.getSubAdminArea ()+" | "+address.getSubLocality ()+" | "+address.getLocality ()+" | "+address.getSubLocality ()+" | "+address.getPremises ()+" | "+addresses.get(0).getThoroughfare()+" | "+address.getAddressLine(0));
            }
            return s_direccion;
        }


        private void confirmar_pedido() {
            SharedPreferences pedido = getSharedPreferences(getString(R.string.sql_pedido), MODE_PRIVATE);
            double aux_monto_total=Double.parseDouble(pedido.getString("monto_total","0"));
            double aux_monto_minimo=Double.parseDouble(pedido.getString("monto_minimo","0"));

            if(pedido.getString("abierto_cerrado","Cerrado").equals("Abierto"))
            {
                if (aux_monto_total>=aux_monto_minimo)
                {
                    escribir_referencia_moto(5,0);
                }else
                {
                    mensaje_error("Su pedido no supero el monto minimo de pedido ("+aux_monto_minimo+" Bs)");
                }

            }else{
                mensaje_error("La tienda esta "+pedido.getString("abierto_cerrado","Cerrado"));
            }


        }
        public void mensaje(String titulo,String mensaje)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(titulo);
            builder.setMessage(mensaje);
            builder.setPositiveButton("OK",  null);
            builder.create();
            builder.show();
        }

    public void mensaje_toast(String mensaje)
    {
        Toast.makeText(this,mensaje,Toast.LENGTH_SHORT).show();
    }


    public void mensaje_final(String titulo,String mensaje)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(titulo);
            builder.setMessage(mensaje);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            builder.create();
            builder.show();
        }


        public void mensaje_completado(String titulo,String mensaje)
        {
            eliminar_carrito();

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(titulo);
            builder.setMessage(mensaje);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            builder.create();
            builder.show();
        }




    public void  editar_carrito_producto(final CCarrito carrito)
    {
        ImageView im_direccion_imagen;
        TextView tv_nombre,tv_descripcion,tv_precio;


        ImageButton bt_mas,bt_menos;
        final TextView tv_total,tv_cantidad;

        final int[] cantidad = {0};
        double precio=0;
        // get prompts.xml view
        LayoutInflater layoutInflater = LayoutInflater.from(Detalle_carrito.this);
        View promptView = layoutInflater.inflate(R.layout.formulario_actualizar_cantidad, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Detalle_carrito.this);
        alertDialogBuilder.setView(promptView);

        Button br_agrergar= (Button) promptView.findViewById(R.id.bt_agregar);
        Button bt_eliminar= (Button) promptView.findViewById(R.id.bt_eliminar);
        im_direccion_imagen=(ImageView)promptView.findViewById(R.id.im_direccion_imagen);
        tv_nombre=(TextView)promptView.findViewById(R.id.tv_nombre);
        tv_descripcion=(TextView)promptView.findViewById(R.id.tv_descripcion);
        tv_precio=(TextView)promptView.findViewById(R.id.tv_precio);
        tv_cantidad=(TextView)promptView.findViewById(R.id.tv_cantidad);
        tv_total=(TextView)promptView.findViewById(R.id.tv_total);

        bt_mas=(ImageButton)promptView.findViewById(R.id.bt_mas);
        bt_menos=(ImageButton)promptView.findViewById(R.id.bt_menos);

        cantidad[0] =carrito.getCantidad();
        precio=carrito.getMonto_unidad();
        tv_nombre.setText(carrito.getNombre());
        tv_descripcion.setText(carrito.getDescripcion());
        tv_cantidad.setText(""+carrito.getCantidad());
        tv_total.setText(""+carrito.getMonto_total());
        tv_precio.setText("Bs. "+carrito.getMonto_unidad());

        if(carrito.getUrl().length()>5){
            Picasso.with(this).load(getString(R.string.servidor_web)+"storage/"+carrito.getUrl()).placeholder(R.drawable.ic_logo_carrito).into(im_direccion_imagen);
        }


        final double finalPrecio = precio;
        bt_mas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(cantidad[0] <10)
                {
                    cantidad[0]++;
                }

                tv_cantidad.setText(String.valueOf(cantidad[0]));
                tv_total.setText("Bs. "+String.valueOf(cantidad[0] * finalPrecio));
            }
        });


        bt_menos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(cantidad[0]>0)
                {
                    cantidad[0]--;
                }


                tv_cantidad.setText(String.valueOf(cantidad[0]));
                tv_total.setText("Bs. "+String.valueOf(cantidad[0]*finalPrecio));

            }
        });


        br_agrergar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alert2.cancel();
                final double monto_total =cantidad[0]*finalPrecio;
                Boolean sw= actualizar_producto(monto_total,cantidad[0],finalPrecio,carrito.getId_producto());
                if (sw==true)
                {
                    mensaje_toast("Producto agregado correctamente al carrito.");
                }else
                {
                    mensaje_toast("No se ha podido agregar el producto al carrito.");
                }

                cargar_datos();
            }
        });

        bt_eliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                eliminar_producto(carrito.getId_producto());
                alert2.cancel();
                cargar_datos();
            }
        });


        // create an alert dialog
        alert2 = alertDialogBuilder.create();
        alert2.show();
    }




    public Boolean actualizar_producto(double monto_total,int cantidad, double precio, int id) {
        SharedPreferences pedido = getSharedPreferences(getString(R.string.sql_pedido), MODE_PRIVATE);
        Boolean sw_registro = false;
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, getString(R.string.nombre_sql), null, Integer.parseInt(getString(R.string.version_sql)));

        SQLiteDatabase bd = admin.getWritableDatabase();
        Cursor fila = bd.rawQuery("select id_pedido,id_producto,cantidad,monto_unidad,monto_total from carrito where  id_producto = " + id + " and id_pedido=" + pedido.getInt("id", 0), null);

        if (fila.moveToFirst()) {  //si ha devuelto 1 fila, vamos al primero (que es el unico)
            do {
                sw_registro = true;
                ContentValues value = new ContentValues();
                value.put("monto_unidad", String.valueOf(precio));
                value.put("monto_total", String.valueOf(monto_total));
                value.put("cantidad", String.valueOf(cantidad));
                bd.update("carrito", value, "id_producto=" + id + " and id_pedido=" + pedido.getInt("id", 0), null);
            } while (fila.moveToNext());
        }

        sumar_lista();
        bd.close();

        return sw_registro;
    }

    private void eliminar_producto(int id_producto) {
        try {
            AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, getString(R.string.nombre_sql), null, Integer.parseInt(getString(R.string.version_sql)));
            SharedPreferences pedido = getSharedPreferences(getString(R.string.sql_pedido), MODE_PRIVATE);
            SQLiteDatabase db = admin.getWritableDatabase();
            db.execSQL("delete from carrito where id_producto = " + id_producto + " and id_pedido=" + pedido.getInt("id", 0));
            db.close();
        }catch (Exception e)
        {
            Log.e("Vaciar base",""+e);
        }
        sumar_lista();
    }




    public void  escribir_referencia_moto(final int clase_vehiculo,final int tipo_pedido_empresa)
    {
        final SharedPreferences pedido = getSharedPreferences(getString(R.string.sql_pedido), MODE_PRIVATE);

        String monto_total=pedido.getString("monto_total","0");
        String solicitud_whatsapp=pedido.getString("solicitud_whatsapp","0");
        String solicitud_aplicacion=pedido.getString("solicitud_aplicacion","0");
        double total= monto_envio +Double.parseDouble(monto_total);

        // get prompts.xml view
        LayoutInflater layoutInflater = LayoutInflater.from(Detalle_carrito.this);
        View promptView = layoutInflater.inflate(R.layout.escribir_pedido, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Detalle_carrito.this);
        alertDialogBuilder.setView(promptView);

        final Button bt_cancelar= promptView.findViewById(R.id.bt_cancelar);
        final Button bt_pedir= promptView.findViewById(R.id.bt_pedir);
        final Button bt_pedir_whatsapp= promptView.findViewById(R.id.bt_pedir_whatsapp);
        final EditText et_referencia= promptView.findViewById(R.id.et_referencia);
        final TextView tv_total= promptView.findViewById(R.id.tv_total);

        et_referencia.setText(pedido.getString("referencia",""));

        tv_total.setText("Total "+total+" Bs");

        bt_pedir_whatsapp.setVisibility(View.INVISIBLE);
        bt_pedir.setVisibility(View.INVISIBLE);

        if(solicitud_whatsapp.equals("1")){
            bt_pedir_whatsapp.setVisibility(View.VISIBLE);
        }

        if(solicitud_aplicacion.equals("1")){
            bt_pedir.setVisibility(View.VISIBLE);
        }

        bt_cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alert2.cancel();
            }
        });

        bt_pedir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editar=pedido.edit();
                editar.putString("referencia",et_referencia.getText().toString().trim());
                editar.commit();

               pedir_taxi("0", et_referencia.getText().toString().trim(),clase_vehiculo,tipo_pedido_empresa);
               getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

                alert2.cancel();
            }
        });

        bt_pedir_whatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //slicitud en Vallegrande
                SharedPreferences.Editor editar=pedido.edit();
                editar.putString("referencia",et_referencia.getText().toString().trim());
                editar.commit();

                pedir_por_whatsapp(et_referencia.getText().toString());
                alert2.cancel();
            }
        });
        // create an alert dialog
        alert2 = alertDialogBuilder.create();
        alert2.show();
    }

    private void pedir_por_whatsapp(String indicacion) {
        boolean isWhatsapp = appInstalledOrNot("com.whatsapp");
        if (isWhatsapp)
            AbrirWhatsApp(indicacion);
        else{
            mensaje_error("Necesita instalarse la aplicación de Whatsapp");
        }
    }
    private boolean appInstalledOrNot(String uri) {
        PackageManager pm = getPackageManager();
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
        }

        return false;
    }

    void AbrirWhatsApp(String indicacion) {
        SharedPreferences carrito = getSharedPreferences(getString(R.string.sql_pedido), MODE_PRIVATE);
        double total=monto_envio+Double.parseDouble(carrito.getString("monto_total","0"));

        SharedPreferences datos_perfil = getSharedPreferences("perfil", Context.MODE_PRIVATE);
        String nombre=datos_perfil.getString("nombre","")+" "+datos_perfil.getString("apellido","");

        PackageManager packageManager = getPackageManager();
        Intent i = new Intent(Intent.ACTION_VIEW);
        String mensaje="Hola *"+carrito.getString("nombre_lugar","")+"* soy "+nombre+" " +
                "me puede hacer un envió de la siguiente Lista de Carrito:" +
                " \n \n"+mensaje_carrito+"--------------------------------\n" +
                "\n*Pedido:* "+Double.parseDouble(carrito.getString("monto_total","0"))+" Bs"+
                "\n*Envio:* "+monto_envio+" Bs"+
                "\n--------------------------------"+
                "\n *Total: "+total+ "bs.*\n\n" +
                "_Indicacion:"+indicacion+"._"+
                "\n\n" +
                "a mi ubicación https://maps.google.com/?q="+latitud_fin+","+longitud_fin+"";


        servicio_guardar_copia_whatsapp(mensaje,
                carrito.getInt("id_empresa",0),
                datos_perfil.getString("id_usuario","0"));

        try {
            String url = "https://wa.me/"+carrito.getString("whatsapp","")+"/?text=" + URLEncoder.encode(mensaje, "UTF-8");
            i.setPackage("com.whatsapp");
            i.setData(Uri.parse(url));
            if (i.resolveActivity(packageManager) != null) {
                startActivity(i);
                eliminar_carrito();
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void servicio_guardar_copia_whatsapp(String mensaje,
                                                 int id_empresa,
                                                 String id_usuario) {

        try {
            JSONObject jsonParam= new JSONObject();
            jsonParam.put("mensaje", mensaje);
            jsonParam.put("id_empresa", id_empresa);
            jsonParam.put("id_usuario", id_usuario);

            String url=getString(R.string.servidor) + "frmChat.php?opcion=guardar_copia_whatsapp";

            if (queue == null) {
                queue = Volley.newRequestQueue(this);
            }


            JsonObjectRequest myRequest= new JsonObjectRequest(
                    Request.Method.POST,
                    url,
                    jsonParam,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject respuestaJSON) {
                            pDialog.cancel();
                            try {
                                suceso=new Suceso(respuestaJSON.getString("suceso"),respuestaJSON.getString("mensaje"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    mensaje_error("Falla en tu conexión a Internet.");
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


            myRequest.setRetryPolicy(new DefaultRetryPolicy(10000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            queue.add(myRequest);
        } catch (Exception e) {
        }
    }

    public void pedir_taxi(String numero, String referencia,int clase_vehiculo,int tipo_pedido_empresa){
        ///verifica si el GPS esta activo.
        SharedPreferences pedido = getSharedPreferences(getString(R.string.sql_pedido), MODE_PRIVATE);



                if (referencia.length() >= 3) {

                    SharedPreferences pedido_ultimo1 = getSharedPreferences("ultimo_pedido", MODE_PRIVATE);

                    SharedPreferences.Editor editar1=pedido_ultimo1.edit();
                    editar1.putString("nombre_taxi", "");
                    editar1.putString("celular", "");
                    editar1.putString("marca", "");
                    editar1.putString("placa", "");
                    editar1.putString("color", "");
                    editar1.putString("id_pedido", "");
                    editar1.putInt("notificacion_cerca", 0);
                    editar1.putInt("notificacion_llego", 0);

                    editar1.commit();
                    limpiar_pedido_antiguo();



                    Intent datos_pedido = new Intent(this, Pedido_usuario.class);
                    //datos_pedido.putExtra("latitud", addressLatLng.latitude);
                    //datos_pedido.putExtra("longitud", addressLatLng.longitude);
                    datos_pedido.putExtra("latitud",Double.parseDouble( pedido.getString("latitud","0")));
                    datos_pedido.putExtra("longitud", Double.parseDouble(pedido.getString("longitud","0")));
                    datos_pedido.putExtra("referencia", referencia);
                    datos_pedido.putExtra("numero",numero);
                    datos_pedido.putExtra("monto_envio",monto_envio);
                    datos_pedido.putExtra("clase_vehiculo",clase_vehiculo);
                    datos_pedido.putExtra("direccion",pedido.getString("direccion",""));
                    datos_pedido.putExtra("tipo_pedido_empresa",tipo_pedido_empresa);
                    startActivity(datos_pedido);
                } else {
                    AlertDialog.Builder dialogo1 = new AlertDialog.Builder(this);
                    dialogo1.setTitle("Atención");
                    dialogo1.setMessage("Por favor introduzca una referencia para ayudar al conductor a ubicarlo.");
                    dialogo1.setCancelable(false);
                    dialogo1.setPositiveButton("OK", null);
                    dialogo1.show();
                }
    }



    private void limpiar_pedido_antiguo() {

        SharedPreferences pedido = getSharedPreferences("ultimo_pedido", MODE_PRIVATE);
        SharedPreferences.Editor editar=pedido.edit();
        editar.putString("nombre_taxi", "");
        editar.putString("celular", "");
        editar.putString("marca", "");
        editar.putString("placa", "");
        editar.putString("color", "");
        editar.commit();


        SharedPreferences punto_pedido=getSharedPreferences("punto taxi",MODE_PRIVATE);
        SharedPreferences.Editor editor=punto_pedido.edit();
        editor.putString("latitud", "0");
        editor.putString("longitud", "0");
        editor.putString("rotacion", "0");

        editor.commit();
    }


    public String formatearMinutosAHoraMinuto(int minutos) {
        String formato = "%02d:%02d";
        long horasReales = TimeUnit.MINUTES.toHours(minutos);
        long minutosReales = TimeUnit.MINUTES.toMinutes(minutos) - TimeUnit.HOURS.toMinutes(TimeUnit.MINUTES.toHours(minutos));
        return String.format(formato, horasReales, minutosReales);
    }


    public void servicio_calcular_tarifa()
    {
        //Servicio servicio = new Servicio();
        //servicio.execute(getString(R.string.servidor) + "frmCarrera.php?opcion=calcular_tarifa", "1", String.valueOf(latitud_inicio), String.valueOf(longitud_inicio),String.valueOf(latitud_fin),String.valueOf(longitud_fin));// parametro que recibe el doinbackground
        pDialog = new ProgressDialog(Detalle_carrito.this);
        pDialog.setTitle(getString(R.string.app_name));
        pDialog.setMessage("Calculando la tarifa");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();

        try {
            JSONObject jsonParam= new JSONObject();
            jsonParam.put("latitud_inicio", String.valueOf(latitud_inicio));
            jsonParam.put("longitud_inicio", String.valueOf(longitud_inicio));
            jsonParam.put("latitud_fin", String.valueOf(latitud_fin));
            jsonParam.put("longitud_fin", String.valueOf(longitud_fin));

            String url=getString(R.string.servidor) + "frmCarrera.php?opcion=calcular_tarifa";

            if (queue == null) {
                queue = Volley.newRequestQueue(this);
            }


            JsonObjectRequest myRequest= new JsonObjectRequest(
                    Request.Method.POST,
                    url,
                    jsonParam,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject respuestaJSON) {
                            pDialog.cancel();
                            try {
                                suceso=new Suceso(respuestaJSON.getString("suceso"),respuestaJSON.getString("mensaje"));

                                if (suceso.getSuceso().equals("1")) {
                                    String minuto=respuestaJSON.getString("minutos");
                                    String pedido=respuestaJSON.getString("pedido");

                                    //final
                                    int minuto1= Integer.parseInt(minuto);

                                SharedPreferences scarrito=getSharedPreferences(getString(R.string.sql_pedido),MODE_PRIVATE);
                                    int tiempo_pre=scarrito.getInt("tiempo_preparacion",0);
                                    minuto1=tiempo_pre+minuto1;

                                    int tiempo_menor=minuto1+18;
                                    int tiempo_mayor=minuto1+25;

                                    tv_tiempo_preparacion.setText(tiempo_menor+"-"+tiempo_mayor+ " min.");

                                    tv_monto_envio.setText( pedido+" Bs.");

                                    try{
                                        monto_envio=Double.parseDouble(pedido);

                                        SharedPreferences carrito = getSharedPreferences(getString(R.string.sql_pedido), MODE_PRIVATE);
                                        SharedPreferences.Editor ed_c = carrito.edit();
                                        ed_c.putString("monto_envio", String.valueOf(monto_envio));
                                        ed_c.commit();
                                    }catch (Exception eee)
                                    {

                                    }

                                } else  {
                                    if(cantidad_solicitud_tarifa<3)
                                    {
                                        servicio_calcular_tarifa();
                                    }else{
                                        mensaje_error(suceso.getMensaje());
                                    }
                                    cantidad_solicitud_tarifa++;
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                mensaje_error("Falla en tu conexión a Internet.");
                            }

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    pDialog.cancel();
                    mensaje_error("Falla en tu conexión a Internet.");
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


            myRequest.setRetryPolicy(new DefaultRetryPolicy(10000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            queue.add(myRequest);
        } catch (Exception e) {
            pDialog.cancel();
        }
    }




    public void mensaje_error(String mensaje)
    {
        AlertDialog.Builder dialogo1 = new AlertDialog.Builder(this);
        dialogo1.setTitle("Importante");
        dialogo1.setMessage(mensaje);
        dialogo1.setCancelable(false);
        dialogo1.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogo1, int id) {
                finish();
            }
        });

        dialogo1.show();


    }


    public  double getDistancia(double lat_a,double lon_a, double lat_b, double lon_b){
        long  Radius = 6371000;
        double dLat = Math.toRadians(lat_b-lat_a);
        double dLon = Math.toRadians(lon_b-lon_a);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(Math.toRadians(lat_a)) * Math.cos(Math.toRadians(lat_b)) * Math.sin(dLon /2) * Math.sin(dLon/2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double distancia=(Radius * c);
        return  distancia;
    }
}
