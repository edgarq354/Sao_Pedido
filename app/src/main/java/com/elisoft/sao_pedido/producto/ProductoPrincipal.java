package com.elisoft.sao_pedido.producto;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.elisoft.sao_pedido.R;
import com.elisoft.sao_pedido.SqLite.AdminSQLiteOpenHelper;
import com.elisoft.sao_pedido.carrito.Detalle_carrito;
import com.squareup.picasso.Picasso;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class ProductoPrincipal extends AppCompatActivity implements View.OnClickListener{



    ImageView im_direccion_imagen;
    TextView tv_nombre,tv_descripcion,tv_precio;
    String url1="";
    LinearLayout ll_carrito;

    ImageButton bt_mas,bt_menos;
    Button bt_agregar,bt_eliminar;
    TextView tv_monto_total,tv_total,tv_cantidad;

    int id_empresa=0;
    int id=0;

    int cantidad=0;
    double precio=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_producto_principal);
        im_direccion_imagen=(ImageView)findViewById(R.id.im_direccion_imagen);
        tv_nombre=(TextView)findViewById(R.id.tv_nombre);
        tv_descripcion=(TextView)findViewById(R.id.tv_descripcion);
        tv_precio=(TextView)findViewById(R.id.tv_precio);
        tv_cantidad=(TextView)findViewById(R.id.tv_cantidad);
        tv_total=(TextView)findViewById(R.id.tv_total);
        tv_monto_total=(TextView)findViewById(R.id.tv_monto_total);

        bt_mas=(ImageButton)findViewById(R.id.bt_mas);
        bt_menos=(ImageButton)findViewById(R.id.bt_menos);
        bt_agregar=(Button)findViewById(R.id.bt_agregar);
        bt_eliminar=(Button)findViewById(R.id.bt_eliminar);

        bt_menos.setOnClickListener(this);
        bt_mas.setOnClickListener(this);
        bt_agregar.setOnClickListener(this);
        bt_eliminar.setOnClickListener(this);

        ll_carrito=(LinearLayout)findViewById(R.id.ll_carrito);
        ll_carrito.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pp=new Intent(ProductoPrincipal.this, Detalle_carrito.class);

                SharedPreferences sp=getSharedPreferences(getString(R.string.sql_pedido),MODE_PRIVATE);
                pp.putExtra("id_pedido",sp.getInt("id",0));
                startActivity(pp);
            }
        });



        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        try{
            Bundle bundle=getIntent().getExtras();
            tv_nombre.setText(bundle.getString("nombre",""));
            tv_descripcion.setText(bundle.getString("descripcion",""));
            tv_precio.setText(" Bs. "+bundle.getString("precio",""));
            precio=Double.parseDouble(bundle.getString("precio","0"));
            url1= bundle.getString("direccion_imagen","");
            id_empresa=bundle.getInt("id_empresa",0);
            id=bundle.getInt("id",0);

            getSupportActionBar().setTitle(bundle.getString("nombre",""));



            if(url1.length()>5){
                Picasso.with(this).load(getString(R.string.servidor_web)+"storage/"+url1).placeholder(R.drawable.ic_logo_carrito).into(im_direccion_imagen);
            }
        }catch (Exception e){
            finish();
        }


        mostrar_monto_total();
        consulta_producto();
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
            case R.id.bt_mas:
                if(cantidad<10)
                {
                    cantidad++;
                }

                tv_cantidad.setText(String.valueOf(cantidad));
                tv_total.setText("Bs. "+String.valueOf(cantidad*precio));



                break;
            case  R.id.bt_menos:
                if(cantidad>0)
                {
                    cantidad--;
                }


                tv_cantidad.setText(String.valueOf(cantidad));
                tv_total.setText("Bs. "+String.valueOf(cantidad*precio));

                break;
            case R.id.bt_eliminar:
                eliminar_producto();
                mensaje_final("Carrito","Eliminado el producto del carrito");
                break;
            case R.id.bt_agregar:
                if (cantidad>0)
                {
                    double monto_total =cantidad*precio;
                    Boolean sw=registrar_nuevo_producto(monto_total);
                    if (sw==true)
                    {
                        mensaje_final("CARRITO","Producto agregado correctamente al carrito.");
                    }else
                    {
                        mensaje("CARRITO","No se ha podido agregar el producto al carrito.");
                    }

                }else
                {
                    SharedPreferences carrito= getSharedPreferences(getString(R.string.sql_pedido),MODE_PRIVATE);
                    double aux_monto_total=Double.parseDouble(carrito.getString("monto_total","0"));
                    if (aux_monto_total<=0) {
                        SharedPreferences.Editor ed_c = carrito.edit();
                        ed_c.putInt("id_empresa", 0);
                        ed_c.putInt("id_categoria", 0);
                        ed_c.commit();
                    }
                    if(cantidad==0)
                    {
                        eliminar_producto();

                    }
                    mensaje_final("Carrito","No ingreso la cantidad");

                }
                break;

        }
    }


    @Override
    protected void onRestart() {
        mostrar_monto_total();
        super.onRestart();
    }

    public Boolean registrar_nuevo_producto(double monto_total) {
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

        } else {
            //NO HAY REGISTRO.

            ContentValues registro = new ContentValues();
            registro.put("id_producto", String.valueOf(id));
            registro.put("id_pedido", pedido.getInt("id", 0));
            registro.put("cantidad", String.valueOf(cantidad));
            registro.put("monto_unidad", String.valueOf(precio));
            registro.put("monto_total", String.valueOf(monto_total));
            registro.put("nombre",tv_nombre.getText().toString());
            registro.put("descripcion",tv_descripcion.getText().toString());
            registro.put("url",url1);

            bd.insert("carrito", null, registro);

            sw_registro = true;
        }
        //sumar monto total de todo el carrito
        fila = bd.rawQuery("select  monto_total from carrito where  id_pedido=" + pedido.getInt("id", 0), null);
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
            bd.close();
        }

        bd.close();

        return sw_registro;
    }

    private void eliminar_producto() {
        try {
            AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, getString(R.string.nombre_sql), null, Integer.parseInt(getString(R.string.version_sql)));
            SharedPreferences pedido = getSharedPreferences(getString(R.string.sql_pedido), MODE_PRIVATE);
            SQLiteDatabase db = admin.getWritableDatabase();
            db.execSQL("delete from carrito where id_producto = " + id + " and id_pedido=" + pedido.getInt("id", 0));
            db.close();
        }catch (Exception e)
        {
            Log.e("Vaciar base",""+e);
        }
        sumar_lista();
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

    public void consulta_producto() {
        SharedPreferences pedido = getSharedPreferences(getString(R.string.sql_pedido), MODE_PRIVATE);
        Boolean sw_registro = false;
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, getString(R.string.nombre_sql), null, Integer.parseInt(getString(R.string.version_sql)));

        SQLiteDatabase bd = admin.getWritableDatabase();
        Cursor fila = bd.rawQuery("select id_pedido,id_producto,cantidad,monto_unidad,monto_total from carrito where  id_producto = " + id + " and id_pedido=" + pedido.getInt("id", 0), null);

        if (fila.moveToFirst()) {  //si ha devuelto 1 fila, vamos al primero (que es el unico)

            do {
                cantidad=  fila.getInt(2);
                precio= fila.getDouble(3);



            } while (fila.moveToNext());

        }
        tv_cantidad.setText(String.valueOf(cantidad));
        tv_total.setText(String.valueOf(cantidad*precio));


        bd.close();

    }

    public  void  mostrar_monto_total()
    {
        SharedPreferences carrito = getSharedPreferences(getString(R.string.sql_pedido), MODE_PRIVATE);
        tv_monto_total.setText("Bs. "+carrito.getString("monto_total","0,00"));
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


}

