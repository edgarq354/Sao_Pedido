package com.elisoft.sao_pedido.empresa;

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


//This is a custom adapter. It has been extended from BaseAdapter because
//we need to overrider the getView function for changing the layout of each Grid View Item
public class EmpresaAdapter extends BaseAdapter {

    private ArrayList<CEmpresa> mEmpresa = new ArrayList<>();
    private LayoutInflater mInflaterEmpresaListItems;
    Context context;



    public EmpresaAdapter(Context context, ArrayList<CEmpresa> array) {
        mEmpresa = array;
        this.context=context;
        mInflaterEmpresaListItems = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    //This function will determine how many items to be displayed
    @Override
    public int getCount() {
        return mEmpresa.size();
    }

    @Override
    public Object getItem(int position) {
        return mEmpresa.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    //This function will iterate through each object in the Data Set. This function will form each item in a Grid View
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final CEmpresa  ped = mEmpresa.get(position);
        final ViewHolder holder;

        if (convertView == null) {

            holder = new ViewHolder();
            convertView = mInflaterEmpresaListItems.inflate(R.layout.adapter_empresa,
                    null);
            holder.tv_razon_social= convertView.findViewById(R.id.tv_razon_social);
            holder.im_logo=convertView.findViewById(R.id.im_logo);
            holder.im_fondo=convertView.findViewById(R.id.cv_fondo);
            holder.tv_minimo=convertView.findViewById(R.id.tv_minimo);
            holder.tv_estado=convertView.findViewById(R.id.tv_estado);
            holder.tv_tiempo_preparacion=convertView.findViewById(R.id.tv_tiempo_preparacion);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }



        int tiempo_pre=ped.getTiempo_preparacion();
        int tiempo_menor=tiempo_pre-5;
        int tiempo_mayor=tiempo_pre+5;

        holder.tv_tiempo_preparacion.setText(tiempo_menor+"-"+tiempo_mayor);
        holder.tv_estado.setText(ped.getAbierto_cerrado());
        holder.tv_minimo.setText("Bs "+ped.getMonto_minimo());

        if(ped.getAbierto_cerrado().equals("Abierto"))
        {
            holder.tv_estado.setBackgroundResource(R.drawable.bk_abierto);
        }else
        {
            holder.tv_estado.setBackgroundResource(R.drawable.bk_cerrado);
        }

        //Change the content here
        if (mEmpresa.get(position) != null) {
            holder.tv_razon_social.setText(ped.getNombre());
            try {
                if(ped.getDireccion_logo().length()>4) {

                    String url_logo = context.getString(R.string.servidor_web)+"storage/" + ped.getDireccion_logo();
                    String url_banner = context.getString(R.string.servidor_web)+"storage/" + ped.getDireccion_banner();
                    try {

                        Picasso.with(context).load(url_logo).placeholder(R.mipmap.ic_launcher_round).into(holder.im_logo);

                        final ImageView img = new ImageView(context);
                        Picasso.with(context).load(url_banner).placeholder(R.mipmap.ic_launcher).into(img, new com.squareup.picasso.Callback() {
                            @Override
                            public void onSuccess() {
                                holder.im_fondo.setBackground(img.getDrawable());

                            }

                            @Override
                            public void onError() {
                            }
                        });



                    }catch (Exception ee)
                    {
                        ee.printStackTrace();
                    }

                }else{

                    holder.im_logo.setImageBitmap(null);
                }




            } catch (Exception e) {

                e.printStackTrace();

            }
        }



        return convertView;
    }



    //View Holder class used for reusing the same inflated view. It will decrease the inflation overhead @getView
    private static class ViewHolder {
        TextView tv_razon_social,tv_minimo,tv_estado,tv_tiempo_preparacion;
        de.hdodenhof.circleimageview.CircleImageView im_logo;
        ImageView im_fondo;
        //CardView cv_fondo;

    }

}
