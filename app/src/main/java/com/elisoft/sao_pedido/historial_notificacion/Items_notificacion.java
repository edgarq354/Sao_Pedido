package com.elisoft.sao_pedido.historial_notificacion;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.elisoft.sao_pedido.R;

import java.util.ArrayList;

/**
 * Created by ELIO on 28/10/2016.
 */

public  class Items_notificacion extends BaseAdapter {

    protected Activity activity;
    protected ArrayList<CNotificacion> items;
    private Context mContext;
    Bundle savedInstanceState;

    public Items_notificacion(Context c, Bundle b, Activity activity, ArrayList<CNotificacion> items) {
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

    public void addAll(ArrayList<CNotificacion> Pedidos) {
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
            v = inf.inflate(R.layout.item_notificacion, null);
            LayoutInflater inflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        CNotificacion ped = items.get(position);




        TextView titulo= v.findViewById(R.id.titulo);
        TextView mensaje = v.findViewById(R.id.mensaje);
        TextView fecha = v.findViewById(R.id.fecha);
        TextView hora = v.findViewById(R.id.hora);
        ImageView leido= v.findViewById(R.id.leido);
        leido.setVisibility(View.INVISIBLE);

        titulo.setText(ped.getTitulo());
        mensaje.setText(ped.getMensaje());
        fecha.setText(ped.getFecha());
        hora.setText(ped.getHora());
        if(ped.getLeido()==0)
        {
            leido.setVisibility(View.VISIBLE);
        }


        return v;
    }



}