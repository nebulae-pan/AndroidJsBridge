package com.nebula.module.androidjsbridge;

public class NativeResult {
    private String mResult;
    public NativeResult(){

    }

    public NativeResult(String result){
        mResult = result;
    }

    public String result(){
        return mResult;
    }
}
