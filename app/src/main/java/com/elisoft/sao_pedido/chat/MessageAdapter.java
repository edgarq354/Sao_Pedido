package com.elisoft.sao_pedido.chat;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.elisoft.sao_pedido.R;

import java.util.List;



public class MessageAdapter extends ArrayAdapter<CMensaje> {
    public MessageAdapter(Context context, int resource, List<CMensaje> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.i(Chat_pedido.TAG, "getView:");

        CMensaje message = getItem(position);

        if(TextUtils.isEmpty(message.getMensaje())){


            convertView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.user_connected, parent, false);

            TextView messageText = convertView.findViewById(R.id.message_body);

            Log.i(Chat_pedido.TAG, "getView: is empty ");
            String userConnected = message.getTitulo();
            messageText.setText(userConnected);

        }else if(message.getYo()==1){
            Log.i(Chat_pedido.TAG, "getView: " + message.getYo() + " "  );
            if(message.getTipo().equals("AUDIO")){
                convertView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.mi_mensaje_audio, parent, false);
                TextView fecha = convertView.findViewById(R.id.fecha);
                fecha.setText(message.getFecha()+"|"+message.getHora());
            }else{
                convertView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.my_message, parent, false);
                TextView messageText = convertView.findViewById(R.id.message_body);
                TextView fecha = convertView.findViewById(R.id.fecha);
                messageText.setText(message.getMensaje());
                fecha.setText(message.getFecha()+"|"+message.getHora());
            }


        }else {
            Log.i(Chat_pedido.TAG, "getView: is not empty");

            if(message.getTipo().equals("AUDIO")){
                convertView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.otro_mensaje_audio, parent, false);
                TextView fecha = convertView.findViewById(R.id.fecha);
                TextView usernameText = (TextView) convertView.findViewById(R.id.name);

                fecha.setVisibility(View.VISIBLE);
                usernameText.setVisibility(View.VISIBLE);

                fecha.setText(message.getFecha()+"|"+message.getHora());
                usernameText.setText(message.getTitulo());
            }else{
                convertView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.their_message, parent, false);
                TextView messageText = convertView.findViewById(R.id.message_body);
                TextView usernameText = (TextView) convertView.findViewById(R.id.name);
                TextView fecha = convertView.findViewById(R.id.fecha);

                fecha.setVisibility(View.VISIBLE);
                messageText.setVisibility(View.VISIBLE);
                usernameText.setVisibility(View.VISIBLE);

                fecha.setText(message.getFecha()+"|"+message.getHora());
                messageText.setText(message.getMensaje());
                usernameText.setText(message.getTitulo());
            }

        }

        return convertView;
    }
}
