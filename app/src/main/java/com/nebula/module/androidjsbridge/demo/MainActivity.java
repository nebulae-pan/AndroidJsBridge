package com.nebula.module.androidjsbridge.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.nebula.module.androidjsbridge.InjectWebViewClient;
import com.nebula.module.androidjsbridge.JsInvoke;
import com.nebula.module.androidjsbridge.JsParam;
import com.nebula.module.androidjsbridge.NativeResult;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        WebView webView = findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new InjectWebViewClient(webView, this));

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                System.out.println("webView:" + consoleMessage.message());
                return super.onConsoleMessage(consoleMessage);
            }
        });
        webView.loadUrl("file:///android_asset/test.html");
    }

    @JsInvoke("nativeScan")
    public NativeResult scanCode(@JsParam("name") String name) {
        Log.e("tag", "name = " + name);
        JsBuilder jsBuilder = new JsBuilder();
        jsBuilder.addParam("grade","90")
                .addParam("location","xi'an");
        return new NativeResult(jsBuilder.string());
    }

    public static class JsBuilder{
        private JSONObject mJsonObject = new JSONObject();
        public JsBuilder(){}

        public JsBuilder addParam(String key, String value){
            try {
                mJsonObject.put(key, value);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return this;
        }

        public String string(){
            return mJsonObject.toString();
        }
    }
}
