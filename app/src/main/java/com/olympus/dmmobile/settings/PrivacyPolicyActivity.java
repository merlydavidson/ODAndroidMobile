package com.olympus.dmmobile.settings;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

import com.olympus.dmmobile.R;
import com.olympus.dmmobile.utils.CommonFuncArea;

public class PrivacyPolicyActivity extends Activity {

    TextView privacyPolicyContent;
    Button close;
    private WebView webView;
    private String language;
    String javascript=
            "var elem=document.getElementByID(\"scroll-top-position\");" +

            "var off1=elem.getBoundingClientRect();" +
            "var anchoroffset=off1.top;" +

            "window.scrollTo(off1.left,anchoroffset);" ;


    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy);
        this.setTitle(getString(R.string.privacy_policy_title));
        //   privacyPolicyContent = (TextView)findViewById(R.id.privacy_content);
        close = (Button) findViewById(R.id.close_privacy_policy);
        webView = (WebView) findViewById(R.id.privacypolicywebView);
       // webView.getSettings().setJavaScriptEnabled(true);
        mProgressDialog = new ProgressDialog(PrivacyPolicyActivity.this);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage(getResources()
                .getString(R.string.Dictate_Loading));

        final String urlString=new CommonFuncArea(PrivacyPolicyActivity.this).getUrl()+"#scroll-top-position";

                mProgressDialog.show();

        webView.loadUrl(urlString);
        if (mProgressDialog.isShowing())
                   mProgressDialog.dismiss();




        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               finish();
            }
        });
    }

    public String getUrl() {
        String url = "";
        String language = getlanguage();
        if (language.equalsIgnoreCase("en"))
            url = "https://cs.olympus-imaging.jp/en/support/imsg/digicamera/download/policy/olympusdictation/eulaen.cfm#scroll-top-position";
        else if (language.equalsIgnoreCase("de"))
            url = "https://cs.olympus-imaging.jp/en/support/imsg/digicamera/download/policy/olympusdictation/eulade.cfm";

        else if (language.equalsIgnoreCase("fr"))
            url = "https://cs.olympus-imaging.jp/en/support/imsg/digicamera/download/policy/olympusdictation/eulafr.cfm";
        else if (language.equalsIgnoreCase("es"))
            url = "https://cs.olympus-imaging.jp/en/support/imsg/digicamera/download/policy/olympusdictation/eulaes.cfm";
        else if (language.equalsIgnoreCase("sv"))
            url = "https://cs.olympus-imaging.jp/en/support/imsg/digicamera/download/policy/olympusdictation/eulasv.cfm";
        else if (language.equalsIgnoreCase("cs"))
            url = "https://cs.olympus-imaging.jp/en/support/imsg/digicamera/download/policy/olympusdictation/eulacs.cfm";


        return url;

    }

    public String getlanguage() {
        String currentLanguage = null;
        SharedPreferences pref = PreferenceManager
                .getDefaultSharedPreferences(this);
        currentLanguage = (pref.getString(getString(R.string.language_key), ""));
        if (Integer.parseInt(currentLanguage) == 1) {
            language = "en";
        } else if (Integer.parseInt(currentLanguage) == 1) {
            language = "de";
        } else if (Integer.parseInt(currentLanguage) == 2) {
            language = "fr";
        } else if (Integer.parseInt(currentLanguage) == 3) {
            language = "es";
        } else if (Integer.parseInt(currentLanguage) == 4) {
            language = "sv";
        } else if (Integer.parseInt(currentLanguage) == 5) {
            language = "cs";
        }
        return language;
    }
}
