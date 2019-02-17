package com.nebula.module.androidjsbridge.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.TextView;

import com.nebula.module.androidjsbridge.InjectWebViewClient;
import com.nebula.module.androidjsbridge.JsInvoke;
import com.nebula.module.androidjsbridge.JsParam;
import com.nebula.module.androidjsbridge.JsParamValue;
import com.nebula.module.androidjsbridge.NativeResult;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    private TextView mText;

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
        mText = findViewById(R.id.text);
    }

    @JsInvoke("nativeMethod1")
    public void method1(@JsParamValue("name") String name,
                        @JsParamValue("gender") String gender) {
        mText.setText(String.format("name = \"%s\", gender = \"%s\"", name, gender));
    }

    @JsInvoke("nativeMethod2")
    public NativeResult method2(@JsParamValue("name") String name) {
        mText.setText(String.format("name = \"%s\"", name));
        JsBuilder jsBuilder = new JsBuilder();
        jsBuilder.addParam("grade","90")
                .addParam("location","xi'an");
        return new NativeResult(jsBuilder.string());
    }

    @JsInvoke("nativeMethod3")
    public NativeResult method3(@JsParam String param){
        mText.setText(param);
        return new NativeResult();
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
