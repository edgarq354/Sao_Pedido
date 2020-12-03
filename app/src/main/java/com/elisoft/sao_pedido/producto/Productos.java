package com.elisoft.sao_pedido.producto;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.elisoft.sao_pedido.R;
import com.elisoft.sao_pedido.SqLite.AdminSQLiteOpenHelper;
import com.elisoft.sao_pedido.Suceso;
import com.elisoft.sao_pedido.carrito.Detalle_carrito;
import com.elisoft.sao_pedido.categoria.CCategoria;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class Productos extends AppCompatActivity implements
        AdapterView.OnItemClickListener,
        AdapterView.OnItemLongClickListener,
        View.OnClickListener {

    Suceso suceso;
    GridView gridview;

    ArrayList<CProducto> producto;
    ArrayList<CCategoria> categoria;
    RecyclerView rv_categoria;

    int id_categoria=0;


    LinearLayout ll_pedir_ahora;
    LinearLayout ll_carrito;

    TextView tv_monto_total,tv_cantidad;

    int id_empresa=0;
    int id=0;

    SearchView sv_buscar;

    private void runLayoutAnimation( GridView gridView) {
        final Context context = gridView.getContext();
        final LayoutAnimationController controller =
                AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation);

        gridview.setLayoutAnimation(controller);
        gridview.scheduleLayoutAnimation();
    }


    private void runLayoutAnimation2( RecyclerView recyclerView) {
        final Context context = recyclerView.getContext();
        final LayoutAnimationController controller =
                AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation);

        recyclerView.setLayoutAnimation(controller);
        recyclerView.getAdapter().notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();
    }
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_productos);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        gridview = (GridView) findViewById(R.id.gridView);
        rv_categoria = findViewById(R.id.recyclerView);

        tv_cantidad=(TextView)findViewById(R.id.tv_cantidad);
        tv_monto_total=(TextView)findViewById(R.id.tv_monto_total);
        sv_buscar=(SearchView) findViewById(R.id.sv_buscar);


        gridview.setOnItemClickListener(this);
        gridview.setOnItemLongClickListener(this);

        ll_carrito=(LinearLayout)findViewById(R.id.ll_carrito);
        ll_pedir_ahora=(LinearLayout)findViewById(R.id.ll_pedir_ahora);
        ll_carrito.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pp=new Intent(Productos.this, Detalle_carrito.class);

                SharedPreferences sp=getSharedPreferences(getString(R.string.sql_pedido),MODE_PRIVATE);
                pp.putExtra("id_pedido",sp.getInt("id",0));
                startActivity(pp);
            }
        });
        ll_pedir_ahora.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pp=new Intent(Productos.this, Detalle_carrito.class);

                SharedPreferences sp=getSharedPreferences(getString(R.string.sql_pedido),MODE_PRIVATE);
                pp.putExtra("id_pedido",sp.getInt("id",0));
                startActivity(pp);
            }
        });

        sv_buscar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        if(query.toString().length()>1){
                            cargar_producto_en_la_lista(query.toString().trim(),"");
                        }else
                        {
                            cargar_producto_en_la_lista("","");
                        }
// do something on text submit
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        if(newText.toString().length()>1){
                            cargar_producto_en_la_lista(newText.toString().trim(),"");
                        }else
                        {
                            cargar_producto_en_la_lista("","");
                        }
// do something when text changes
                        return false;
                    }
                });

        try{
            Bundle bundle=getIntent().getExtras();
            id_empresa=bundle.getInt("id_empresa",0);
            id_categoria=bundle.getInt("id_categoria",0);
            servicio_lista_de_producto_delivery(String.valueOf(id_empresa));
            servicio_lista_de_tipo_delivery();
        }catch (Exception ee)
        {
            ee.printStackTrace();
        }


      sumar_lista();

    }

    public void servicio_lista_de_tipo_delivery(){
        try {



            JSONObject jsonParam= new JSONObject();
            jsonParam.put("id_empresa", id_empresa);
            String url=getString(R.string.servidor)+"frmDelivery.php?opcion=lista_de_tipo_producto_delivery";
            RequestQueue queue = Volley.newRequestQueue(this);


            JsonObjectRequest myRequest= new JsonObjectRequest(
                    Request.Method.POST,
                    url,
                    jsonParam,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject respuestaJSON) {
                            categoria= new ArrayList<CCategoria>();
                            try {

                                suceso= new Suceso(respuestaJSON.getString("suceso"),respuestaJSON.getString("mensaje"));

                                if (suceso.getSuceso().equals("1")) {


                                    JSONArray usu = respuestaJSON.getJSONArray("lista");
                                    for (int i = 0; i < usu.length(); i++) {
                                        int id = Integer.parseInt(usu.getJSONObject(i).getString("id"));
                                        int estado_pedido = Integer.parseInt(usu.getJSONObject(i).getString("estado_pedido"));
                                        String nombre = usu.getJSONObject(i).getString("nombre");
                                        String direccion_imagen = usu.getJSONObject(i).getString("direccion_imagen");

                                        CCategoria hi =new CCategoria(id,nombre,direccion_imagen,estado_pedido);
                                        categoria.add(hi);
                                    }




                                }
                                else
                                {
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();

                            }

                            LinearLayoutManager llm = new LinearLayoutManager(Productos.this);
                            llm.setOrientation(LinearLayoutManager.HORIZONTAL);
                            rv_categoria.setLayoutManager(llm);
                            RecyclerViewAdapterTipoProducto adapter = new RecyclerViewAdapterTipoProducto(Productos.this, categoria);
                            adapter.setOnItemClickListener(new RecyclerViewAdapterTipoProducto.ClickListener() {
                                @Override
                                public void onItemClick(int position, View v) {
                                    Log.d("Categoria", "onItemClick position: " + position);
                                    CCategoria aux_categoria=categoria.get(position);
                                    cargar_producto_en_la_lista("",String.valueOf(aux_categoria.getId()));
                                }
                            });

                            rv_categoria.setAdapter(adapter);
                            runLayoutAnimation2(rv_categoria);

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


            queue.add(myRequest);


        } catch (Exception e) {
        }
    }

    public void servicio_lista_de_producto_delivery(String id_lugar){
        try {



            JSONObject jsonParam= new JSONObject();
            jsonParam.put("id_lugar", id_lugar);
            String url=getString(R.string.servidor)+"frmGuia_turistica.php?opcion=lista_de_producto_delivery";
            RequestQueue queue = Volley.newRequestQueue(this);


            JsonObjectRequest myRequest= new JsonObjectRequest(
                    Request.Method.POST,
                    url,
                    jsonParam,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject respuestaJSON) {
                            producto= new ArrayList<CProducto>();
                            try {

                                suceso= new Suceso(respuestaJSON.getString("suceso"),respuestaJSON.getString("mensaje"));

                                if (suceso.getSuceso().equals("1")) {

                                    vaciar_producto();

                                    JSONArray usu = respuestaJSON.getJSONArray("lista");


                                    for (int i = 0; i < usu.length(); i++) {
                                        if (Integer.parseInt(usu.getJSONObject(i).getString("estado_pedido"))==1) {

                                            CProducto hi = new CProducto(
                                                    Integer.parseInt(usu.getJSONObject(i).getString("id")),
                                                    usu.getJSONObject(i).getString("nombre"),
                                                    usu.getJSONObject(i).getString("descripcion"),
                                                    usu.getJSONObject(i).getString("precio"),
                                                    usu.getJSONObject(i).getString("imagen1"),
                                                    usu.getJSONObject(i).getString("imagen2"),
                                                    usu.getJSONObject(i).getString("imagen3"),
                                                    usu.getJSONObject(i).getString("imagen4"),
                                                    usu.getJSONObject(i).getString("imagen5"),
                                                    Integer.parseInt(usu.getJSONObject(i).getString("id_lugar")),
                                                    Integer.parseInt(usu.getJSONObject(i).getString("estado_pedido")),
                                                    usu.getJSONObject(i).getString("id_tipo_producto")
                                            );



                                            cargar_lista_en_producto(hi.getId(),
                                                    hi.getId_lugar(),
                                                    hi.getNombre(),
                                                    hi.getDiscripcion(),
                                                    hi.getPrecio(),
                                                    hi.getImagen1(),
                                                    hi.getImagen2(),
                                                    hi.getImagen3(),
                                                    hi.getImagen4(),
                                                    hi.getImagen5(),
                                                    hi.getEstado_pedido(),
                                                    hi.getId_tipo_producto());
                                            producto.add(hi);
                                        }
                                    }

                                }
                                else
                                {
                                }

                                    cargar_lista_producto();
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


            queue.add(myRequest);


        } catch (Exception e) {
        }

    }


    public  void  cargar_lista_producto()
    {
        ProductoAdapter adaptador = new ProductoAdapter(this, producto);

        gridview.setAdapter(adaptador);
        runLayoutAnimation(gridview);
    }

 private void cargar_lista_en_producto(int id,
        int id_lugar,
        String nombre,
        String descripcion,
        String precio,
        String imagen1,
        String imagen2,
        String imagen3,
        String imagen4,
        String imagen5,
        int estado_pedido,
        String id_tipo_producto) {
            try {
                AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, getString(R.string.nombre_sql), null, Integer.parseInt(getString(R.string.version_sql)));

                SQLiteDatabase bd = admin.getWritableDatabase();
                ContentValues registro = new ContentValues();
                registro.put("id_producto", id);
                registro.put("id_lugar", id_lugar);
                registro.put("nombre", nombre);
                registro.put("descripcion", descripcion);
                registro.put("precio", precio);
                registro.put("imagen1", imagen1);
                registro.put("imagen2", imagen2);
                registro.put("imagen3", imagen3);
                registro.put("imagen4", imagen4);
                registro.put("imagen5", imagen5);
                registro.put("estado_pedido", estado_pedido);
                registro.put("id_tipo_producto", id_tipo_producto);
                bd.insert("producto", null, registro);
                bd.close();
            }catch (Exception e)
            {
                e.printStackTrace();
            }

        }

        private void vaciar_producto() {
            AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, getString(R.string.nombre_sql), null, Integer.parseInt(getString(R.string.version_sql)));

            SQLiteDatabase db = admin.getWritableDatabase();
            db.execSQL("delete from producto");
            db.close();
            Log.e("sqlite ", "vaciar producto");
        }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        CProducto cProducto= producto.get(position);


        Boolean sw=registrar_nuevo_producto(cProducto);
        if (sw==true)
        {
            mensaje("CARRITO","Producto agregado correctamente al carrito.");
            sumar_lista();
        }else
        {
            mensaje("CARRITO","No se ha podido agregar el producto al carrito.");
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

        CProducto cProducto= producto.get(position);

        Intent i=new Intent(this,ProductoPrincipal.class);
        i.putExtra("id",cProducto.getId());
        i.putExtra("nombre",cProducto.getNombre());
        i.putExtra("descripcion",cProducto.getDiscripcion());
        i.putExtra("precio",String.valueOf(cProducto.getPrecio()));
        i.putExtra("direccion_imagen",cProducto.getImagen1());
        i.putExtra("id_empresa",cProducto.getId_lugar());
        startActivity(i);
        return false;
    }
    public void mensaje(String titulo,String mensaje)
    {
        /*
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(titulo);
        builder.setMessage(mensaje);
        builder.setPositiveButton("OK",  null);
        builder.create();
        builder.show();
        */
        Toast.makeText(this,mensaje,Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStart() {
        sumar_lista();
        super.onStart();
    }

    public Boolean registrar_nuevo_producto(CProducto cProducto ) {


        SharedPreferences pedido = getSharedPreferences(getString(R.string.sql_pedido), MODE_PRIVATE);
        Boolean sw_registro = false;
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, getString(R.string.nombre_sql), null, Integer.parseInt(getString(R.string.version_sql)));

        SQLiteDatabase bd = admin.getWritableDatabase();
        Cursor fila = bd.rawQuery("select id_pedido,id_producto,cantidad,monto_unidad,monto_total from carrito where  id_producto = " + cProducto.getId() + " and id_pedido=" + pedido.getInt("id", 0), null);

        if (fila.moveToFirst()) {  //si ha devuelto 1 fila, vamos al primero (que es el unico)

            do {
                sw_registro = true;
                int cantidad = Integer.parseInt(fila.getString(2));
                cantidad++;
                double monto_total =cantidad*Double.valueOf(cProducto.getPrecio());

                ContentValues value = new ContentValues();
                value.put("monto_unidad", String.valueOf(cProducto.getPrecio()));
                value.put("monto_total", String.valueOf(monto_total));
                value.put("cantidad", String.valueOf(cantidad));

                bd.update("carrito", value, "id_producto=" + cProducto.getId() + " and id_pedido=" + pedido.getInt("id", 0), null);


            } while (fila.moveToNext());

        } else {
            //NO HAY REGISTRO.

            ContentValues registro = new ContentValues();
            registro.put("id_producto", String.valueOf(cProducto.getId()));
            registro.put("id_pedido", pedido.getInt("id", 0));
            registro.put("cantidad", String.valueOf(1));
            registro.put("monto_unidad", String.valueOf(cProducto.getPrecio()));
            registro.put("monto_total", String.valueOf(cProducto.getPrecio()));
            registro.put("nombre", cProducto.getNombre());
            registro.put("descripcion", cProducto.getDiscripcion());
            registro.put("url", cProducto.getImagen1());

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

        } else {
            SharedPreferences carrito = getSharedPreferences(getString(R.string.sql_pedido), MODE_PRIVATE);
            SharedPreferences.Editor ed_c = carrito.edit();
            ed_c.putInt("id_empresa", 0);
            ed_c.putInt("id_categoria", 0);
            ed_c.putString("nombre_categoria", "");
            ed_c.putString("monto_total", "0");
            ed_c.commit();
            bd.close();
        }

        bd.close();

        return sw_registro;
    }

    public void sumar_lista() {
        int cantidad=0;
        SharedPreferences pedido = getSharedPreferences(getString(R.string.sql_pedido), MODE_PRIVATE);

        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, getString(R.string.nombre_sql), null, Integer.parseInt(getString(R.string.version_sql)));

        SQLiteDatabase bd = admin.getWritableDatabase();
        Cursor fila = bd.rawQuery("select  monto_total,cantidad from carrito where  id_pedido=" + pedido.getInt("id", 0), null);
        double aux_monto_total = 0;
        if (fila.moveToFirst()) {  //si ha devuelto 1 fila, vamos al primero (que es el unico)

            do {
                int cant=Integer.parseInt(fila.getString(1));
                cantidad=cantidad+cant;
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
            ed_c.putInt("id_empresa", 0);
            ed_c.putInt("id_categoria", 0);
            ed_c.putString("nombre_categoria", "");
            ed_c.putString("monto_total","0");
            ed_c.commit();
        }
        bd.close();

        mostrar_monto_total();
        tv_cantidad.setText(cantidad+"x");
    }
    public  void  mostrar_monto_total()
    {
        SharedPreferences carrito = getSharedPreferences(getString(R.string.sql_pedido), MODE_PRIVATE);
        tv_monto_total.setText("Bs. "+carrito.getString("monto_total","0,00"));
    }

    public void cargar_producto_en_la_lista(String nombre,String id_tipo_producto)
    {



        producto= new ArrayList<CProducto>();

        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this, getString(R.string.nombre_sql), null, Integer.parseInt(getString(R.string.version_sql)));

        SQLiteDatabase bd = admin.getWritableDatabase();

        Cursor fila=null;
        if(id_tipo_producto==""){
            fila = bd.rawQuery("select " +
                    "id_producto," +
                    "nombre," +
                    "descripcion," +
                    "precio," +
                    "imagen1," +
                    "imagen2," +
                    "imagen3," +
                    "imagen4," +
                    "imagen5," +
                    "id_lugar," +
                    "estado_pedido," +
                    "id_tipo_producto from producto where nombre like '%"+nombre+"%'   ", null);
        }else{
            fila = bd.rawQuery("select " +
                    "id_producto," +
                    "nombre," +
                    "descripcion," +
                    "precio," +
                    "imagen1," +
                    "imagen2," +
                    "imagen3," +
                    "imagen4," +
                    "imagen5," +
                    "id_lugar," +
                    "estado_pedido," +
                    "id_tipo_producto from producto where nombre like '%"+nombre+"%'  and id_tipo_producto="+id_tipo_producto+" ", null);
        }



        if (fila.moveToFirst()) {  //si ha devuelto 1 fila, vamos al primero (que es el unico)

            do {

                CProducto hi =new CProducto(
                        fila.getInt(0),
                        fila.getString(1),
                        fila.getString(2),
                        fila.getString(3),
                        fila.getString(4),
                        fila.getString(5),
                        fila.getString(6),
                        fila.getString(7),
                        fila.getString(8),
                        fila.getInt(9),
                        fila.getInt(10),
                        fila.getString(11)
                );


                producto.add(hi);


            } while(fila.moveToNext());

        }
        //   Toast.makeText(this, "No hay registrados",Toast.LENGTH_SHORT).show();

        bd.close();


        cargar_lista_producto();
    }

    @Override
    public void onClick(View v) {
    }
}
