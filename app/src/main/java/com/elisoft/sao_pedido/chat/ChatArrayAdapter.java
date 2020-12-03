package com.elisoft.sao_pedido.chat;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class ChatArrayAdapter extends ArrayAdapter<CMensaje> {

    private TextView chatText;
    private List<CMensaje> CMensajeList = new ArrayList<CMensaje>();
    private Context context;
    Suceso suceso;
    int posicion=0;

    String id_conductor,id_usuario,titulo,mensaje;
    ImageView im_leido,im_enviado;

    @Override
    public void add(CMensaje object) {
        CMensajeList.add(object);
        super.add(object);
    }

    public ChatArrayAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
        this.context = context;
    }

    public int getCount() {
        return this.CMensajeList.size();
    }

    public CMensaje getItem(int index) {
        return this.CMensajeList.get(index);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        CMensaje CMensajeObj = getItem(position);
        posicion=position;

        View row = convertView;

        TextView fecha;
        LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (CMensajeObj.left) {
            row = inflater.inflate(R.layout.right, parent, false);
        }else{
            row = inflater.inflate(R.layout.left, parent, false);
        }
        chatText = row.findViewById(R.id.msgr);
        fecha = row.findViewById(R.id.fecha);
        im_leido = row.findViewById(R.id.im_leido);
        im_enviado = row.findViewById(R.id.im_enviado);
        chatText.setText(CMensajeObj.mensaje);
        fecha.setText(CMensajeObj.hora);
        if(CMensajeObj.estado==0){
            im_enviado.setVisibility(View.INVISIBLE);
            im_leido.setVisibility(View.INVISIBLE);
        } if(CMensajeObj.estado==1)
        {
            im_enviado.setVisibility(View.VISIBLE);
            im_leido.setVisibility(View.INVISIBLE);
        }else  if(CMensajeObj.estado==2){
            im_enviado.setVisibility(View.VISIBLE);
            im_leido.setVisibility(View.VISIBLE);
        }else {
            im_enviado.setVisibility(View.INVISIBLE);
            im_leido.setVisibility(View.INVISIBLE);
        }


        id_conductor=String.valueOf(CMensajeObj.getId_conductor());
        id_usuario=String.valueOf(CMensajeObj.getId_usuario());
        titulo= CMensajeObj.getTitulo();
        mensaje= CMensajeObj.getMensaje();
        if(CMensajeObj.getEstado()==0)
        {


            servicio_enviar_pasajero(String.valueOf(CMensajeObj.id_usuario),
                    String.valueOf(CMensajeObj.id_conductor),
                    CMensajeObj.titulo,CMensajeObj.mensaje);

            CMensajeList.get(posicion).estado=1;
        }
        return row;
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

            String url=context.getString(R.string.servidor) + "frmChat.php?opcion=enviar_pasajero";
            RequestQueue queue = Volley.newRequestQueue(context);


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
                                    Intent serviceIntent = new Intent(context, Servicio_guardar_mensaje_enviado.class);
                                    serviceIntent.putExtra("id_chat", id);
                                    serviceIntent.putExtra("id_conductor",id_conductor);
                                    serviceIntent.putExtra("id_usuario", id_usuario);
                                    serviceIntent.putExtra("titulo", titulo);
                                    serviceIntent.putExtra("mensaje",mensaje);
                                    serviceIntent.putExtra("fecha", fecha);
                                    serviceIntent.putExtra("hora", hora);
                                    context.startService(serviceIntent);
                                    vista_estado(1);
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

    public void vista_estado( int estado)
    {
        if( estado==0){
            im_enviado.setVisibility(View.INVISIBLE);
            im_leido.setVisibility(View.INVISIBLE);
        } else if(estado==1)
    {
        im_enviado.setVisibility(View.VISIBLE);
        im_leido.setVisibility(View.INVISIBLE);
    }else  if( estado==2){
        im_enviado.setVisibility(View.VISIBLE);
        im_leido.setVisibility(View.VISIBLE);
    }else {
        im_enviado.setVisibility(View.INVISIBLE);
        im_leido.setVisibility(View.INVISIBLE);
    }
    }

    public void guardar_mensaje_enviado(String id, String id_conductor,String id_usuario,String titulo,String mensaje,String fecha,String hora,String estado,String yo)
    {
try {
    AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(context, context.getString(R.string.nombre_sql), null, Integer.parseInt(context.getString(R.string.version_sql)));

    SQLiteDatabase bd = admin.getWritableDatabase();
    ContentValues registro = new ContentValues();
    registro.put("id", id);
    registro.put("id_conductor", id_conductor);
    registro.put("id_usuario", id_usuario);
    registro.put("fecha", fecha);
    registro.put("hora", hora);
    registro.put("mensaje", mensaje);
    registro.put("titulo", titulo);
    registro.put("estado", estado);
    registro.put("yo", yo);
    bd.insert("chat", null, registro);
    bd.close();
}catch (Exception e){
    Log.d("registro Chat",e.toString());
}

    }
    // comenzar el servicio con el motista....

}