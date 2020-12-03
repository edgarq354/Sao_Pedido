package com.elisoft.sao_pedido.viajes;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.elisoft.sao_pedido.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by ELIO on 03/07/2017.
 */


public  class item_viajes extends BaseAdapter {

    protected Activity activity;
    protected ArrayList<CViajes> items;
    Bundle savedInstanceState;


    public item_viajes(Activity activity, ArrayList<CViajes> items) {
        this.activity = activity;
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    public void clear() {
        items.clear();
    }

    public void addAll(ArrayList<CViajes> Pedidos) {
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

        final ViewHolder holder;

        if (convertView == null) {
            holder = new ViewHolder();
            LayoutInflater inf = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inf.inflate(R.layout.item_pedido, null);

            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        CViajes ped =  items.get(position);

        TextView tv_direccion = (TextView) convertView.findViewById(R.id.tv_direccion_pedido);
        TextView tv_direccion_inicio = (TextView) convertView.findViewById(R.id.tv_direccion_pedido);
        TextView tv_fecha = (TextView) convertView.findViewById(R.id.tv_fecha);
        TextView tv_estado = (TextView) convertView.findViewById(R.id.tv_estado);
        TextView tv_nombre_conductor =  convertView.findViewById(R.id.tv_nombre_conductor);
        TextView tv_vehiculo =  convertView.findViewById(R.id.tv_vehiculo);
        TextView tv_cal_vehiculo =   convertView.findViewById(R.id.tv_punto_negocio);
        TextView tv_monto_carrito =   convertView.findViewById(R.id.tv_monto_carrito);
        holder.im_negocio =   convertView.findViewById(R.id.im_negocio);

        /*
         * 2=pedido finalizado correctamente.
         * 3=pedido cancelado por el usuario
         * 4=pedido cancelado por el usuario.
         * 5=pedido cancelado por el taxista por alguna razon.
         * */

        tv_nombre_conductor.setText(ped.getNombre()+" "+ped.getApellido());
        tv_vehiculo.setText("Placa:"+ped.getPlaca());
        tv_direccion.setText(ped.getIndicacion());
        tv_direccion_inicio.setText(String.valueOf(ped.getDireccion_lugar()));
        tv_fecha.setText(ped.getFecha_pedido());
        tv_monto_carrito.setText(ped.getMonto_pedido());



        tv_cal_vehiculo.setText(""+ped.getCalificacion_vehiculo());

        String url_logo = activity.getString(R.string.servidor_web)+"storage/" + ped.getDireccion_logo_lugar();

        Picasso.with(activity).load(url_logo).placeholder(R.mipmap.ic_launcher_round).into(holder.im_negocio);




        if(ped.getEstado_pedido()==2)
        {
            try {
                tv_estado.setText(ped.getMonto_total()+" Bs");
                tv_estado.setBackgroundResource(R.drawable.bk_completado);
                tv_estado.setTextColor(Color.parseColor("#536DFE"));

            }catch (Exception e)
            {
                Log.e("Estado",e.toString());
            }
        }
        else if(ped.getEstado_pedido()==5)
        { try {
            tv_estado.setText(String.valueOf("CANCELO"));
            tv_estado.setBackgroundResource(R.drawable.bk_cancelado);
            tv_estado.setTextColor(Color.parseColor("#fc0101"));
        }catch (Exception e)
        {
            Log.e("Estado cancelo",e.toString());
        }
        }
        else if(ped.getEstado_pedido()==4)
        {
            tv_estado.setText(String.valueOf("CANCELE"));
            tv_estado.setBackgroundResource(R.drawable.bk_cancelado);
            tv_estado.setTextColor(Color.parseColor("#fc0101"));
        }

        return convertView;
    }



    //View Holder class used for reusing the same inflated view. It will decrease the inflation overhead @getView
    private static class ViewHolder {
        de.hdodenhof.circleimageview.CircleImageView im_negocio;

    }

}