package com.example.paymentgateway;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManger {
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    SessionManger(Context context){
        sharedPreferences = context.getSharedPreferences("data",0);
        editor = sharedPreferences.edit();
        editor.commit();
    }

    public void setPaymentType(String paymentType) {
        editor.putString("paymentType",paymentType);
        editor.commit();
    }

    public String getPaymentType(){
        return sharedPreferences.getString("paymentType","0");
    }
}
