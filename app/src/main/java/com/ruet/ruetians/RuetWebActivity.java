package com.ruet.ruetians;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class RuetWebActivity extends AppCompatActivity {

    private WebView ruetWebview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ruet_web);


        ruetWebview = (WebView) findViewById(R.id.ruet_website_webview);

        WebSettings webSettings = ruetWebview.getSettings();//getting the settings

        webSettings.setJavaScriptEnabled(true);

        ruetWebview.setWebViewClient(new WebViewClient());//this is needed for operating the web inside the app

        ruetWebview.loadUrl("https://www.ruet.ac.bd/");//loading the url

    }


}