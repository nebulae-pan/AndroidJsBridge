package com.nebula.module.androidjsbridge;

import android.graphics.Bitmap;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class InjectWebViewClient extends WebViewClient {
    public InjectWebViewClient(WebView webView, Object handleTarget){
        webView.addJavascriptInterface(new JsInvokeHandler(handleTarget),"native");
    }
    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        String injectString = "window.callNative = {};\n" +
                "window.nativeCall = {};\n" +
                "\n" +
                "var count = 0;\n" +
                "var callbackSet = {};\n" +
                "\n" +
                "window.callNative = function(method, params, callback){\n" +
                "\tif(callback){\n" +
                "\t\tcount++;\n" +
                "\t\tcallbackSet[count] = callback;\n" +
                "\t}\n" +
                "\twindow.native.invokeDispatcher(method, JSON.stringify(params));\n" +
                "};\n" +
                "\n" +
                "window.nativeCall = function(params){\n" +
                "\t\n" +
                "};";
        System.out.println(injectString.replaceAll("\n",""));
        view.loadUrl("javascript:" + injectString.replaceAll("\n", ""));
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
    }
}
