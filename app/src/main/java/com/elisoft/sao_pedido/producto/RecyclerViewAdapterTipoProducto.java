package com.elisoft.sao_pedido.producto;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.elisoft.sao_pedido.R;
import com.elisoft.sao_pedido.categoria.CCategoria;

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by User on 2/12/2018.
 */

public class RecyclerViewAdapterTipoProducto extends RecyclerView.Adapter<RecyclerViewAdapterTipoProducto.ViewHolder> {

    private static final String TAG = "RecyclerViewAdapter";
    private static ClickListener clickListener;
    //vars
    protected ArrayList<CCategoria> items;
    private Context mContext;

    public RecyclerViewAdapterTipoProducto(Context context, ArrayList<CCategoria> items) {
        this.items = items;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tipo_producto, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder: called.");

        CCategoria ped = items.get(position);
        holder.titulo.setText(ped.getNombre());


    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView titulo;

        public ViewHolder(View itemView) {
            super(itemView);


            itemView.setOnClickListener(this);

            titulo = itemView.findViewById(R.id.tv_nombre);
        }

        @Override
        public void onClick(View v) {
            clickListener.onItemClick(getAdapterPosition(), v);
        }
    }

    public void setOnItemClickListener(ClickListener clickListener) {
        RecyclerViewAdapterTipoProducto.clickListener = clickListener;
    }
    public interface ClickListener {
        void onItemClick(int position, View v);
    }
}
