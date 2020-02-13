package com.example.androidjs_demo;

import android.content.Context;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

public class Android {
    private Context context;

    Android(Context context) {
        this.context = context;
    }

    @JavascriptInterface
    public void test(String msg){
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }
}
