package com.nebula.module.androidjsbridge;

import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@SuppressWarnings("unused")
public class JsInvokeHandler {
    private static final String TAG = "A-JBridge";
    private Map<String, Pair<Method, List<String>>> mNativeDisposeSet = new HashMap<>();
    private Object mTarget;
    private WebView mWebView;

    JsInvokeHandler(WebView webView, Object handleClass) {
        mTarget = handleClass;
        mWebView = webView;
        Method[] methods = handleClass.getClass().getDeclaredMethods();
        for (Method method : methods) {
            JsInvoke methodAnnotation = method.getAnnotation(JsInvoke.class);
            if (methodAnnotation != null) {
                List<String> paramKeyList = new ArrayList<>();
                Annotation[][] paramAnnotations = method.getParameterAnnotations();
                for (Annotation[] annotations : paramAnnotations) {
                    for (Annotation annotation : annotations) {
                        if (annotation.annotationType().equals(JsParam.class)) {
                            paramKeyList.add(((JsParam) annotation).value());
                        }
                    }
                }
                mNativeDisposeSet.put(methodAnnotation.value(), new Pair<>(method, paramKeyList));
            }
        }
        for (String key : mNativeDisposeSet.keySet()) {
            Log.e("jsHandleMethod", key + ":" + Arrays.toString(mNativeDisposeSet.get(key).second.toArray()));
        }
    }

    @JavascriptInterface
    public void invokeDispatcher(String method, int id, String params) {
        Pair<Method, List<String>> pair = mNativeDisposeSet.get(method);
        Method nativeCallee = pair.first;
        List<String> paramKeyList = pair.second;
        List<String> paramList = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(params);
            for (String key : paramKeyList) {
                paramList.add(jsonObject.getString(key));
            }
            Object result = nativeCallee.invoke(mTarget, paramList.toArray());
            if (!(result instanceof NativeResult)) {
                Log.e(TAG, String.format("error: native callee method %s's return type is %s.It's must be %s", nativeCallee.getName(), result == null ? "void" : result.getClass(), NativeResult.class));
                return;
            }
            NativeResult nativeResult = (NativeResult) result;
            if (!TextUtils.isEmpty(nativeResult.result())) {
                final String jsCmd = String.format(Locale.getDefault(), "window.nativeCall(%d, \"%s\");", id, nativeResult.result().replaceAll("\"","\\\\\""));
                Log.e(TAG, "invokeDispatcher: " + jsCmd);
                mWebView.post(new Runnable() {
                    @Override
                    public void run() {
                        if (Build.VERSION.SDK_INT >= 14) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                mWebView.evaluateJavascript(jsCmd, null);
                            }
                        } else {
                            mWebView.loadUrl("javascript:" + jsCmd);
                        }
                    }
                });
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        Log.e("tag:", method + ":" + params);
    }
}
