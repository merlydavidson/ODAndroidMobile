package com.olympus.dmmobile.webservice;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.olympus.dmmobile.R;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;

public class TermsOfUse extends AsyncTask<Void,Void,Void> {
    String mUrl = "";
    Context context;
    String response_str = "";
    boolean isVersionChanged = false;
    SharedPreferences sharedPreferencesVersion;
    String PREFNAME="version";
    SharedPreferences.Editor editorVer;
    String firstVerKey="firstVer";
    String secondVerKey="secondVer";
    String thirdVerKey="thirdVer";
    boolean isShowAlert=false;

    public TermsOfUse(Context conntext, String mUrl) {
        this.mUrl = mUrl;
        this.context = conntext;
        sharedPreferencesVersion=conntext.getSharedPreferences(PREFNAME,Context.MODE_PRIVATE);
        editorVer=sharedPreferencesVersion.edit();
    }

    public boolean getHtmlString() throws IOException {
        if (!mUrl.equalsIgnoreCase("")) {

            HttpClient client = new DefaultHttpClient();
            HttpGet request = new HttpGet(mUrl);
            // Get the response
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            response_str = client.execute(request, responseHandler);

        }
        return versionChanged();
    }

    public boolean versionChanged() {

        String result = response_str.substring(response_str.indexOf("!--#@@@##ver") + 12, response_str.indexOf("#@@@#--"));
        String[] arrayString = result.split("[.]");

        String firstVer = arrayString[0];
        String secondVer = arrayString[1];
        String thirdVer = arrayString[2];
       if( sharedPreferencesVersion.getInt(firstVerKey,1)>Integer.parseInt(firstVer)|| sharedPreferencesVersion.getInt(firstVerKey,0)>Integer.parseInt(secondVer)|| sharedPreferencesVersion.getInt(firstVerKey,0)>Integer.parseInt(thirdVer))
       {
           isVersionChanged=true;
           editorVer.putInt(firstVerKey,Integer.parseInt(firstVer));
           editorVer.putInt(secondVerKey,Integer.parseInt(secondVer));
           editorVer.putInt(thirdVerKey,Integer.parseInt(thirdVer));
           editorVer.commit();
       }
        return isVersionChanged;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        try {
           isShowAlert= getHtmlString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        if(isShowAlert)
        {
            AlertDialog.Builder alert = new AlertDialog.Builder(
                    context);
            alert.setTitle(context.getString(R.string.terms_of_use_update_title));
            alert.setMessage(context.getString(R.string.terms_of_use_content_update));
            alert.setPositiveButton(context.getString(R.string.Dictate_Alert_Ok),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog,
                                            int which) {
                            Intent intent=new Intent(context,TermsOfUse.class);
                            context.startActivity(intent);
                        }
                    });
            alert.create().show();
        }
    }
}
