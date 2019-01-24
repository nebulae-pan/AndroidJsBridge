package com.nebula.module.androidjsbridge;

import android.util.Log;
import android.webkit.JavascriptInterface;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsInvokeHandler {
    Map<String, List<String>> jsMethodSet = new HashMap<>();
    List<String> paramKeyList;
    JsInvokeHandler(Object handleClass){
        Method[] methods = handleClass.getClass().getMethods();
        for(Method method:methods){
            if (method.getAnnotation(JsInvoke.class) != null) {
                Annotation[][] paramAnnotations = method.getParameterAnnotations();
            }
        }
    }

    @JavascriptInterface
    public void invokeDispatcher(String method, String params){
        Log.e("tag:", method + ":" + params);
    }
}
