package com.elisoft.sao_pedido.informacion;

import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.elisoft.sao_pedido.R;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class Pagina extends AppCompatActivity {

    String pagina="",titulo="";

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pagina);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        WebView myWebView = findViewById(R.id.wv_condiciones);


        try {
            Bundle bundle= getIntent().getExtras();
            titulo=bundle.getString("titulo","");
            pagina=bundle.getString("url","");



            this.setTitle(titulo);

            myWebView.getSettings().setJavaScriptEnabled(true);
            myWebView.setWebViewClient(new WebViewClient());
            myWebView.loadUrl(pagina);
        } catch (Exception e)
        {

        }


    }

}

