package com.ruet.ruetians;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class RuetiansWebActivity extends AppCompatActivity {

    private WebView ruetiansWebview;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ruetians_web);

        ruetiansWebview = (WebView) findViewById(R.id.ruetians_website_webview);

        WebSettings webSettings = ruetiansWebview.getSettings();//getting the settings

        webSettings.setJavaScriptEnabled(true);

        ruetiansWebview.setWebViewClient(new WebViewClient());//this is needed for operating the web inside the app

        ruetiansWebview.loadUrl("https://ruetians.com/");//loading the url
    }
}