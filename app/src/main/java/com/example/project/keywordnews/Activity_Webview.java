package com.example.project.keywordnews;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

/**
 * Created by SCIT on 2018-03-05.
 */

public class Activity_Webview extends Activity {

    WebView webView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_webview);
        webView = findViewById(R.id.webView);
        setTitle("뉴스 원문보기");

        //메인 액티비티에서 보내준 기사의 링크 주소를 가져온다.
        Intent intent = getIntent();
        String link = intent.getStringExtra("link");

        //참고로, AndroidManifest.xml에 액티비티는 등록했겠지? >> 했다.
        WebSettings set = webView.getSettings();
        set.setJavaScriptEnabled(true); //JavaScript 사용 가능 여부
        set.setJavaScriptCanOpenWindowsAutomatically(true); //JS에서 새창결기 가능여부
        webView.setWebViewClient(new WebViewHelper()); //인터넷 뷰어 앱 말고 여기에서 다시 로딩하도록.
        webView.setWebChromeClient(new WebChromeClientHelper());

        //웹뷰에 실행시킨다.
        webView.loadUrl(link);
    }

    class WebViewHelper extends WebViewClient {
        //화면을 그릴때 이벤트가 발생하면 호출된다.
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            webView.loadUrl(url);
            return true;
        }

        //페이지를 로딩하기 시작할 때 실행되는 메소드. 로딩 시작 전 할 일이 있을 때 쓴다.
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            Toast.makeText(Activity_Webview.this, "페이지 로딩 시작", Toast.LENGTH_SHORT).show();
        }

        //페이지 로딩이 완료된 후 실행되는 메소드. 로딩 완료 후 할 일이 있을 때 쓴다.
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            Toast.makeText(Activity_Webview.this, "페이지 로딩 완료", Toast.LENGTH_SHORT).show();
        }
    }

    class WebChromeClientHelper extends WebChromeClient {
        //JavaScript의 alert()를 Android의 대화상자로 변경하여 보여주기(대화상자 띄우려는 시점에 적용)
        @Override
        public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(Activity_Webview.this);
            dialog.setTitle("JS Alert");
            dialog.setMessage(message);
            dialog.setCancelable(false);
            dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    result.confirm();
                }
            });
            dialog.show();
            return true;
        }
    }
}
