package com.nebula.module.androidjsbridge;

import android.graphics.Bitmap;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class InjectWebViewClient extends WebViewClient {
    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        String injectString = "";
        System.out.println(injectString.replaceAll("\n",""));
        view.loadUrl("javascript:" + injectString.replaceAll("\n", ""));
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
    }
}
