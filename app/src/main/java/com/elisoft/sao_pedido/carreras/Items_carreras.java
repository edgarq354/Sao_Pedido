package com.elisoft.sao_pedido.carreras;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.elisoft.sao_pedido.Pedido_perfil_taxi;
import com.elisoft.sao_pedido.R;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;


/**
 * Created by ELIO on 28/10/2016.
 */

public  class Items_carreras extends BaseAdapter {

    String url_pagina;
    String nombre;
    protected Activity activity;
    protected ArrayList<CCarrera> items;
    private Context mContext;
    Bundle savedInstanceState;
    ImageView im_mapa;

    CCarrera ped;
    TextView tv_numero;
    String id_carrera="";

    public Items_carreras(Context c, Bundle b, Activity activity, ArrayList<CCarrera> items) {
        this.activity = activity;
        this.items = items;
        this.savedInstanceState = b;
        this.mContext=c;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    public void clear() {
        items.clear();
    }

    public void addAll(ArrayList<CCarrera> Pedidos) {
        for (int i = 0; i < Pedidos.size(); i++) {
            items.add(Pedidos.get(i));
        }
    }

    @Override
    public Object getItem(int arg0) {
        return items.get(arg0);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (convertView == null) {
            LayoutInflater inf = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inf.inflate(R.layout.item_carreras, null);
        }
        ped = items.get(position);



        TextView fecha = v.findViewById(R.id.fecha);
        TextView monto = v.findViewById(R.id.monto);
        ImageView ib_conductor= v.findViewById(R.id.ib_conductor);
        TextView tv_distancia= v.findViewById(R.id.tv_distancia);
        TextView tv_direccion_inicio = v.findViewById(R.id.tv_direccion_inicio);
        TextView tv_direccion_fin = v.findViewById(R.id.tv_direccion_fin);
         tv_numero = v.findViewById(R.id.tv_numero);
        ImageView im_mapa= v.findViewById(R.id.im_mapa);
        WebView wv_mapa = v.findViewById(R.id.wv_mapa);
        LinearLayout ll_ver_datos= v.findViewById(R.id.ll_ver_datos);
        String url=mContext.getString(R.string.servidor)+"ver_carrera.php?id_carrera="+ped.getId()+"&id_pedido="+ped.getId_pedido();
       // wv_mapa.loadUrl(url);
        //wv_mapa.loadUrl("https://universoandroidstudio.blogspot.com/2016/");
        wv_mapa.getSettings().setJavaScriptEnabled(true);
        wv_mapa.setWebViewClient(new WebViewClient());
        wv_mapa.loadUrl(url);
        this.im_mapa=im_mapa;

        //poner en el String todos los puntos registrados.....
        boolean sw=true;

        try {
            int id = ped.getId_pedido();
        }catch (Exception e)
        {
            sw=false;
        }
        final View finalV = v;
       String  url1=  "public/Imagen_Conductor/Perfil-"+String.valueOf(ped.getId_conductor())+".png";
        Picasso.with(mContext).load(mContext.getString(R.string.servidor_web)+url1).placeholder(R.mipmap.ic_perfil).into(ib_conductor);

        ll_ver_datos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent ee = new Intent(mContext, Pedido_perfil_taxi.class);
                ee.putExtra("id_conductor",  String.valueOf(ped.getId_conductor()));
                ee.putExtra("id_vehiculo", String.valueOf(ped.getPlaca()));
                finalV.getContext().startActivity( ee);
            }
        });


        if(sw==true) {
            fecha.setText(ped.getFecha_fin());
            monto.setText( ped.getMonto()+" Bs.");
            tv_distancia.setText( ped.getDistancia()+" mt.");
            tv_direccion_inicio.setText(ped.getDireccion_inicio());
            tv_direccion_fin.setText(ped.getDireccion_fin());

            String st_mapa=ped.getRuta();

            String st_nombre=""+ped.getId_pedido()+"_"+ped.getId()+".jpg";
            if(carrera_en_vista(im_mapa,st_nombre)==false)
            {
                getImage(st_mapa,st_nombre);
            }
        }




        return v;
    }



    private void getImage(String id, String nombre)//
    {this.nombre=nombre;
        class GetImage extends AsyncTask<String,Void,Bitmap> {
            ImageView bmImage;
            String nombre;


            public GetImage(ImageView bmImage, String nombre) {
                this.bmImage = bmImage;
                this.nombre=nombre;
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                super.onPostExecute(bitmap);

                //se edita la imagen para ponerlo en circulo.

                if( bitmap==null)
                { }
                else
                {
                   bmImage.setImageBitmap(bitmap);
                    bmImage.setAdjustViewBounds(true);
                    bmImage.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    bmImage.setPadding(0, 0, 0, 0);
                    guardar_en_memoria(bitmap,nombre);
                    tv_numero.setText(ped.getNumero());
                    tv_numero.setVisibility(View.VISIBLE);
                }


            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected Bitmap doInBackground(String... strings) {
                String url = strings[0];//hace consulta ala Bd para recurar la imagen

                Bitmap mIcon = null;
                try {
                    InputStream in = new java.net.URL(url).openStream();
                    mIcon = BitmapFactory.decodeStream(in);
                } catch (Exception e) {
                    Log.e("Error", e.getMessage());
                }
                return mIcon;
            }
        }

        GetImage gi = new GetImage(im_mapa,nombre);
        gi.execute(id);
    }

    private void guardar_en_memoria(Bitmap bitmapImage, String nombre)
    {
        File file=null;
        FileOutputStream fos = null;
        try {
            String APP_DIRECTORY = "Taxi Corp/";//nombre de directorio
            String MEDIA_DIRECTORY = APP_DIRECTORY + "historial";//nombre de la carpeta
            file = new File(Environment.getExternalStorageDirectory(), MEDIA_DIRECTORY);
            File mypath=new File(file,nombre);//nombre del archivo imagen

            boolean isDirectoryCreated = file.exists();//pregunto si esxiste el directorio creado
            if(!isDirectoryCreated)
                isDirectoryCreated = file.mkdirs();

            if(isDirectoryCreated) {
                fos = new FileOutputStream(mypath);
                // Use the compress method on the BitMap object to write image to the OutputStream
                bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean carrera_en_vista(ImageView imagen, String nombre)
    { boolean sw_carrera=false;

        String mPath = Environment.getExternalStorageDirectory() + File.separator + "Taxi La Carroza/historial"
                + File.separator +nombre;


        File newFile = new File(mPath);
        Bitmap bitmap = BitmapFactory.decodeFile(mPath);

        if( bitmap!=null)
        {
            imagen.setImageBitmap(bitmap);
            imagen.setAdjustViewBounds(true);
            imagen.setScaleType(ImageView.ScaleType.FIT_CENTER);
            imagen.setPadding(0, 0, 0, 0);

            tv_numero.setText(ped.getNumero());
            tv_numero.setVisibility(View.VISIBLE);

            sw_carrera=true;
        }

        return sw_carrera;
    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return true;
        }
    }


}