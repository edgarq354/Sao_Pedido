package com.elisoft.sao_pedido.categoria;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.elisoft.sao_pedido.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by User on 2/12/2018.
 */

public class RecyclerViewAdapterCategoria extends RecyclerView.Adapter<RecyclerViewAdapterCategoria.ViewHolder> {

    private static final String TAG = "RecyclerViewAdapter";
    private static ClickListener clickListener;
    //vars
    protected ArrayList<CCategoria> items;
    private Context mContext;

    public RecyclerViewAdapterCategoria(Context context, ArrayList<CCategoria> items) {
        this.items = items;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_categoria, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder: called.");

        CCategoria ped = items.get(position);
        holder.titulo.setText(ped.getNombre());

        String url = mContext.getString(R.string.servidor_web) + "storage/" + ped.getDireccion_imagen();
        Picasso.with(mContext).load(url).placeholder(R.mipmap.ic_launcher_round
        ).into(holder.im_logo);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView titulo;
        ImageView im_logo;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);

            titulo = itemView.findViewById(R.id.tv_nombre);
            im_logo =  itemView.findViewById(R.id.im_logo);
        }

        @Override
        public void onClick(View v) {
            clickListener.onItemClick(getAdapterPosition(), v);
        }
    }

    public void setOnItemClickListener(ClickListener clickListener) {
        RecyclerViewAdapterCategoria.clickListener = clickListener;
    }
    public interface ClickListener {
        void onItemClick(int position, View v);
    }
}
