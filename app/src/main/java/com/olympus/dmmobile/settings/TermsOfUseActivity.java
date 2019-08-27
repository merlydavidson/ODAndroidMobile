package com.olympus.dmmobile.settings;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;

import com.olympus.dmmobile.R;
import com.olympus.dmmobile.utils.CommonFuncArea;

public class TermsOfUseActivity extends Activity {

    TextView privacyPolicyContent;
    Button close;
    private WebView webView;
    private String language;
    private ProgressDialog mProgressDialog;

    // String javascript = "var anchorPage=document.getElementsByTagName('a');for(var i=0;i<anchorPage.length;i++){if(anchorPage[i].getAttribute('href')=='#scroll-top-position'){anchorPage[i].scrollIntoView(true);}}";
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy);
        this.setTitle(getString(R.string.privacy_policy_title));
        //   privacyPolicyContent = (TextView)findViewById(R.id.privacy_content);
        close = (Button) findViewById(R.id.close_privacy_policy);
        webView = (WebView) findViewById(R.id.privacypolicywebView);
        webView.getSettings().setJavaScriptEnabled(true);
        mProgressDialog = new ProgressDialog(TermsOfUseActivity.this);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage(getResources()
                .getString(R.string.Dictate_Loading));
        mProgressDialog.show();

        webView.loadUrl(new CommonFuncArea(TermsOfUseActivity.this).getUrl());


        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                super.onPageFinished(view, url);
            }
            //            @Override
//            public void onPageFinished(WebView view, String url) {
//                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
//                    webView.evaluateJavascript(javascript, null);
//                } else {
//                    webView.loadUrl("javascript:(function(){" + javascript + "})()");
//                }
//            }
        });

//        Spanned spanned = Html.fromHtml(getString(R.string.privacy_policy_content));
//        privacyPolicyContent.setText(spanned);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


}
