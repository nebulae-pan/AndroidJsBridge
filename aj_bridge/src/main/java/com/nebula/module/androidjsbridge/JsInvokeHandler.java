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

/**
 * deal with all js invoke action and dispatch those action to correspond nativeCallee
 */
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
                        if (annotation.annotationType().equals(JsParamValue.class)) {
                            if (paramKeyList == null) {
                                throw new RuntimeException();
                            }
                            paramKeyList.add(((JsParamValue) annotation).value());
                        }
                        if (annotation.annotationType().equals(JsParam.class)) {
                            if (paramKeyList == null) {
                                throw new RuntimeException();
                            }
                            if (paramKeyList.size() > 0) {
                                throw new RuntimeException();
                            }
                            paramKeyList = null;
                        }
                    }
                }
                //retrieve all annotation, maintain the decorated methods and parameters
                mNativeDisposeSet.put(methodAnnotation.value(), new Pair<>(method, paramKeyList));
            }
        }
//        for (String key : mNativeDisposeSet.keySet()) {
//            Log.d("jsHandleMethod", key + ":" + Arrays.toString(mNativeDisposeSet.get(key).second.toArray()));
//        }
    }

    @JavascriptInterface
    public void invokeDispatcher(String method, int id, String params) {
        Pair<Method, List<String>> pair = mNativeDisposeSet.get(method);
        Method nativeCallee = pair.first;
        List<String> paramKeyList = pair.second;
        List<String> paramList = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(params);
            Object result;
            if (paramKeyList == null) {
                result = nativeCallee.invoke(mTarget, params);
            } else {
                for (String key : paramKeyList) {
                    paramList.add(jsonObject.getString(key));
                }
                result = nativeCallee.invoke(mTarget, paramList.toArray());
            }

            if (result != null && !(result instanceof NativeResult)) {
                Log.e(TAG, String.format("error: native callee method %s's return type is %s.It's must be %s or void", nativeCallee.getName(), result == null ? "void" : result.getClass(), NativeResult.class));
                return;
            }
            if (result == null)
                return;
            NativeResult nativeResult = (NativeResult) result;
            //javascript callback called
            String jsCmd;
            if (!TextUtils.isEmpty(nativeResult.result())) {
                jsCmd = String.format(Locale.getDefault(), "window.nativeCall(%d, \"%s\");", id, nativeResult.result().replaceAll("\"", "\\\\\""));
            } else {
                jsCmd = String.format(Locale.getDefault(), "window.nativeCall(%d);", id);
            }
            //post execute to main thread
            final String cmd = jsCmd;
            mWebView.post(new Runnable() {
                @Override
                public void run() {
                    if (Build.VERSION.SDK_INT >= 14) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                            mWebView.evaluateJavascript(cmd, null);
                        }
                    } else {
                        mWebView.loadUrl("javascript:" + cmd);
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
