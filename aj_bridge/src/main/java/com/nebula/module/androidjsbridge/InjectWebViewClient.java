package com.nebula.module.androidjsbridge;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class InjectWebViewClient extends WebViewClient {
    @SuppressLint("AddJavascriptInterface")
    public InjectWebViewClient(WebView webView, Object handleTarget){
        webView.addJavascriptInterface(new JsInvokeHandler(webView, handleTarget),"native");
    }
    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        //injected javascript code
        String injectString = "window.callNative = {};\n" +
                "window.nativeCall = {};" +
                "var count = 0;\n" +
                "var callbackSet = {};\n" +
                "\n" +
                "window.callNative = function(method, params, callback){\n" +
                "    if(arguments.length == 0){\n" +
                "        throw new Error(\"arguments illegal! callNative() must has more than one parameter.\");\n" +
                "    }\n" +
                "    if(typeof method != 'string'){\n" +
                "        throw new Error(\"the first parameter's type must be String!\");\n" +
                "    }\n" +
                "    if(typeof params == 'object'){\n" +
                "        params = JSON.stringify(params);\n" +
                "    }else if(typeof params == 'function'){\n" +
                "        callback = params;\n" +
                "        params = '';\n" +
                "    }else if(typeof params != 'string'){\n" +
                "        throw new Error(\"the second parameter's type must be JSON format string, JSON object or function\");\n" +
                "    }\n" +
                "    if(callback){\n" +
                "        if(typeof callback != 'function'){\n" +
                "            throw Error(\"the third parameter's type must be function\");\n" +
                "        }\n" +
                "        count++;\n" +
                "        callbackSet[count] = callback;\n" +
                "    }\n" +
                "    window.native.invokeDispatcher(method, count, params);\n" +
                "};\n" +
                "\n" +
                "window.nativeCall = function(id, params){\n" +
                "    var callback = callbackSet[id];\n" +
                "    if(!callback){\n" +
                "        return;\n" +
                "    }\n" +
                "    if(params){\n" +
                "        callback(JSON.parse(params));\n" +
                "    }else{\n" +
                "        callback();\n" +
                "    }\n" +
                "};";
        System.out.println(injectString.replaceAll("\n",""));
        view.loadUrl("javascript:" + injectString.replaceAll("\n", ""));
    }

    @Override
    public void onPageStarted(WebView webView, String url, Bitmap favicon) {
        super.onPageStarted(webView, url, favicon);
    }
}
