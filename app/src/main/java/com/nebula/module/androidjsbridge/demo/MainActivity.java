package com.nebula.module.androidjsbridge.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.nebula.module.androidjsbridge.InjectWebViewClient;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        WebView webView = findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new InjectWebViewClient());
        webView.setWebChromeClient(new WebChromeClient(){
            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                System.out.println("webView:" + consoleMessage.message());
                return super.onConsoleMessage(consoleMessage);
            }
        });
//        webView.loadUrl("javascript:window.callNative.call = function(){};");
        webView.loadUrl("file:///android_asset/test.html");
    }
}
