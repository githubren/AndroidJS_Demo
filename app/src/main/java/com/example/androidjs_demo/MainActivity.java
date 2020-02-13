package com.example.androidjs_demo;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.util.ArraySet;
import android.util.Log;
import android.view.View;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import java.util.Set;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private WebView mWebView;
    private Button mBtn1,mBtn2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        configWebview();
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void configWebview() {
        WebSettings settings = mWebView.getSettings();
        //允许与js交互
        settings.setJavaScriptEnabled(true);
        //允许js弹窗
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        //加载html文件
        mWebView.loadUrl("file:///android_asset/javascript.html");


        mWebView.setWebChromeClient(new WebChromeClient(){

            @Override
            public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
                //创建一个弹窗
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("安卓与JS交互");
                builder.setMessage(message);
                builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        result.confirm();
                    }
                });
                builder.setCancelable(false);
                builder.create().show();
                return true;
            }

            @Override
            public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
                Uri uri = Uri.parse(message);
                if (uri.getScheme() != null && uri.getScheme().equals("js")) {
                    if (uri.getAuthority() != null && uri.getAuthority().equals("android")) {
                        //通过这个方法将安卓中的数据返回到js中去
                        result.confirm("JS调用安卓的方法");
                    }
                    //处理完了记得return  不然会出错
                    return true;
                }
                return super.onJsPrompt(view, url, message, defaultValue, result);
            }
        });

        //将安卓对象映射成"android"  供js中调用
        mWebView.addJavascriptInterface(new Android(this),"android");

        mWebView.setWebViewClient(new WebViewClient(){

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Uri uri = Uri.parse(url);
                if (uri.getScheme() != null && uri.getScheme().equals("js")) {
                    if (uri.getAuthority() != null && uri.getAuthority().equals("webview")) {
                        Set<String> names = uri.getQueryParameterNames();
//                        tip(names);
                        //调用Android中的方法
                        tipString(uri.getQueryParameter("name"));
                    }
                    //处理完了记得return  不然会出错
                    return true;
                }
                return super.shouldOverrideUrlLoading(view, url);
            }
        });
    }

    private void tip(Set<String> strings) {
        if (strings == null)
            Toast.makeText(this, "null", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(this, strings.toString(), Toast.LENGTH_SHORT).show();
    }

    private void tipString(String str){
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }


    private void initView() {
        mWebView = findViewById(R.id.webview);
        mBtn1 = findViewById(R.id.btn1);
        mBtn2 = findViewById(R.id.btn2);

        mBtn1.setOnClickListener(this);
        mBtn2.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.btn1:
                //通过loadUrl()方法调用JS的方法
                getMethodByLoadUrl();
                break;
            case R.id.btn2:
                //通过evaluateJavascript()方法调用JS的方法
                getMethodByEvaluateJavascript();
                break;
        }
    }

    private void getMethodByEvaluateJavascript() {
        mWebView.evaluateJavascript("javascript:callJS()", new ValueCallback<String>() {
            @Override
            public void onReceiveValue(String value) {
                //js返回值处理
                tipString(value);
            }
        });
    }

    private void getMethodByLoadUrl() {
        mWebView.post(new Runnable() {
            @Override
            public void run() {
                //这种方法调用js的方法没有返回值
                mWebView.loadUrl("javascript:callJS()");
            }
        });
    }
}
