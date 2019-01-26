package com.nebula.module.androidjsbridge;

import android.util.Log;
import android.util.Pair;
import android.webkit.JavascriptInterface;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
public class JsInvokeHandler {
    private Map<String, Pair<Method, List<String>>> mNativeDisposeSet = new HashMap<>();
    private Object mTarget;

    JsInvokeHandler(Object handleClass) {
        mTarget = handleClass;
        Method[] methods = handleClass.getClass().getDeclaredMethods();
        for (Method method : methods) {
            Annotation methodAnnotation = method.getAnnotation(JsInvoke.class);
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
                mNativeDisposeSet.put(((JsInvoke) methodAnnotation).value(), new Pair<>(method, paramKeyList));
            }
        }
        for (String key : mNativeDisposeSet.keySet()) {
            Log.e("jsHandleMethod", key + ":" + Arrays.toString(mNativeDisposeSet.get(key).second.toArray()));
        }
    }

    @JavascriptInterface
    public void invokeDispatcher(String method, String params) {
        Pair<Method,List<String>> pair = mNativeDisposeSet.get(method);
        Method nativeCallee = pair.first;
        List<String> paramKeyList = pair.second;
        List<String> paramList = new ArrayList<>();
        try {
            Log.e("tag", params);
            JSONObject j = new JSONObject("{\"name\":\"zhang\",\"gender\":\"male\"}");
            JSONObject jsonObject = new JSONObject(params);
            for (String key : paramKeyList) {
                paramList.add(jsonObject.getString(key));
            }
            nativeCallee.invoke(mTarget, paramList.toArray());
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
