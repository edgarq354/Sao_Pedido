package com.elisoft.sao_pedido.carrito;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.elisoft.sao_pedido.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class Item_carrito extends BaseAdapter {

    protected Activity activity;
    protected ArrayList<CCarrito> items;
    private Context mContext;

    public Item_carrito(Context c, Activity activity, ArrayList<CCarrito> items) {
        this.activity = activity;
        this.items = items;
        this.mContext = c;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    public void clear() {
        items.clear();
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
            v = inf.inflate(R.layout.item_carrito, null);
            LayoutInflater inflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        final CCarrito ped = items.get(position);

        TextView nombre = (TextView) v.findViewById(R.id.tv_nombre);
        TextView direccion = (TextView) v.findViewById(R.id.tv_descripcion);

        TextView precio = (TextView) v.findViewById(R.id.tv_precio);
        ImageView im_logo = (ImageView) v.findViewById(R.id.im_logo);

        nombre.setText(ped.getCantidad()+" - "+ ped.getNombre());
        direccion.setText(ped.getDescripcion());
        precio.setText( " Bs. "+ped.getMonto_total() );
        try {
            if (ped.getUrl().length() > 4) {

                String url = mContext.getString(R.string.servidor_web) + "storage/" + ped.getUrl();
                Picasso.with(mContext).load(url).placeholder(R.mipmap.ic_launcher).into(im_logo);

            } else {

                im_logo.setImageBitmap(null);
            }

        } catch (Exception e) {

            e.printStackTrace();

        }


        return v;

    }
}






