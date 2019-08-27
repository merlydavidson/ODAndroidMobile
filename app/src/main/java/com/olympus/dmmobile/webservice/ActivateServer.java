package com.olympus.dmmobile.webservice;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.text.util.Rfc822Token;
import android.text.util.Rfc822Tokenizer;
import android.util.Log;
import android.view.ContextThemeWrapper;

import com.olympus.dmmobile.DMApplication;
import com.olympus.dmmobile.R;
import com.olympus.dmmobile.registration.interfaces.RegistrationInterface;
import com.olympus.dmmobile.settings.Recipient;

import java.util.List;

public class ActivateServer {
    int forced;
    String author;
    String recipientEmail;
    String recipientName;
    private boolean enableDeveloperOptions;
    boolean followServerSettings;
    public static String ENABLE_DEVELOPER_OPTIONS = "developerMode";
    Rfc822Token[] tokens = null;
    Context context;
    ProgressDialog mProgressDialog;
    private final String DEFAULT_URL = "https://www.dictation-portal.com";
    private String SERVER_URL = "";
    private DMApplication dmApplication = null;
    private String mUUID;
    public static final String PREFS_NAME = "Config";
    private SharedPreferences sharedPreferences;
    private String mGetuuid;
    String authEmail = "";
    String custid = "", custPwd = "";
    private String mActivationResponse = null;
    private WebserviceHandler mWebserviceHandler;
    private ResultXmlParser mResultXmlParser = null;
    private String mResultcode;
    private AlertDialog.Builder mAlertDialog;
    private String mGetSettingsResponse;
    private String mBasevalue;
    private Base64_Encoding mBaseEncoding;
    private Intent mBaseIntent = null;
    private Settingsparser mSettingParser;
    private String mSavedUrl;
    private String mUsername;
    private String mPassword;
    private String mEmail;
    private String mAudioEncrypt;
    private String mAudioPassword;
    private String mAudioFormat;
    private String mAudioDelivery;
    private SharedPreferences.Editor mEditor;
    private List<Settingsparser.WorkTypeListObjects> mWorktypeobject;
    private String mWorktype = null;
    private String mWorktypeListname;
    private String mAuthor;
    private final int FORCED_ACTIVATION = 1;
    RegistrationInterface registrationInterface;


    public ActivateServer(String authemail, String custId, String custPWD, String author, Context context, ProgressDialog progressDialog) {
        this.forced = 0;
        this.author = author;
        //this.recipientName = name;
        // this.recipientEmail = email;
        this.context = context;
        this.mProgressDialog = progressDialog;
        // this.followServerSettings = followServerSettings;
        this.authEmail = authemail;
        this.custid = custId;
        this.custPwd = custPWD;
        getUUID();
        SERVER_URL = DEFAULT_URL;
        dmApplication = (DMApplication) context.getApplicationContext();
        dmApplication.setContext(context);
        mWebserviceHandler = new WebserviceHandler();
        mBaseEncoding = new Base64_Encoding();
        mBasevalue = mBaseEncoding.base64(mUUID + ":" + authemail);
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        mAuthor = pref.getString(context.getResources().getString(R.string.author_key), "");
//        enableDeveloperOptions = context.getIntent().getBooleanExtra(ENABLE_DEVELOPER_OPTIONS, false);
//        if (!enableDeveloperOptions) {
//            mSavedUrl = DEFAULT_URL;
//        } else {
        mSavedUrl = pref.getString(context.getResources().getString(R.string.server_url_key), "https://www.dictation-portal.com/");
        // }

        activateThisDevice(0);


    }

    class WebServiceActivation extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
//        mProgressDialog = new ProgressDialog(context);
//        mProgressDialog.setCancelable(false);
//        mProgressDialog.setMessage(context.getResources()
//                .getString(R.string.Dictate_Loading));
//        mProgressDialog.show();
            super.onPreExecute();
            getUUID();
            if (!mProgressDialog.isShowing()) {
                mProgressDialog.show();
            }
            SERVER_URL = DEFAULT_URL;

            if (!TextUtils.isEmpty(recipientEmail)) {
                tokens = Rfc822Tokenizer.tokenize(recipientEmail);

            }
        }

        @Override
        protected Void doInBackground(Void... params) {
            StringBuilder request = new StringBuilder();

            String requestHeader = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
                    + "<ils-request appversion=\"" + dmApplication.getIlsRequestAppVersion() + "\">"
                    + "<auth>"
                    + "<uuid>" + mUUID + "</uuid>"
                    + "<email><![CDATA[" + authEmail + "]]></email>"
                    + "</auth>"
                    + "<customer>"
                    + "<id><![CDATA[" + custid + "]]></id>"
                    + "<pwd><![CDATA[" + custPwd + "]]></pwd>"
                    + "</customer>"
                    + "<smartphone>"
                    + "<model><![CDATA[" + Build.MANUFACTURER + " " + Build.MODEL + "]]></model>"
                    + "<osversion><![CDATA[" + Build.VERSION.RELEASE + "]]></osversion>"
                    + "<author>" + author + "</author>"
                    + "<recipients>";


            String recipientNode = "";

            //***********************
            if (tokens != null && tokens.length > 0) {
                for (Rfc822Token token : tokens) {
					/*System.out.println("name : " + token.getName());
					System.out.println("value : " + token.getAddress());*/
                    if (token.getName() != null && !token.getName().equalsIgnoreCase("")) {
                        recipientNode += "<address>"
                                + "<email><![CDATA[" + token.getAddress() + "]]></email>"
                                + "<name><![CDATA[" + token.getName() + "]]></name>"
                                + "</address>";
                    } else {
                        recipientNode += "<address>" + "<email><![CDATA[" + token.getAddress()
                                + "]]></email>" + "</address>";
                    }
                }
            }


            String requestFooter = "</recipients>"
                    + "<forced>" + forced + "</forced>"
                    + "</smartphone>"
                    + "</ils-request>";


            request.append(requestHeader);
            if (!followServerSettings) {
                request.append(recipientNode);
                request.append(requestFooter);
            } else {
                request.append(requestFooter);
            }
            Log.d("activateaccount", "header " + requestHeader);
            Log.d("activateaccount", "footer " + requestFooter);
            // Url = mEditServerUrl.getText().toString().trim();    // replace Url with SERVER_URL
            mActivationResponse = mWebserviceHandler.service_Activation(mSavedUrl + "/smartphone", request.toString());
            if (mActivationResponse != null && !mActivationResponse.equalsIgnoreCase("TimeOut")) {
                mResultXmlParser = new ResultXmlParser(mActivationResponse);
                mResultcode = mResultXmlParser.parse(getLanguage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            //System.out.println("Result code: "+mResultcode);
            //System.out.println("Resp : "+mActivationResponse);
            if (mActivationResponse == null) {
                mProgressDialog.dismiss();
                if (DMApplication.isONLINE()) {
                    showMessageAlert1(context.getResources().getString(R.string.Ils_Result_Cannot_Connect_Server), context.getResources().getString(R.string.Settings_Error));
                }
//            else {
//                showMessageAlertNonetwork(context.getResources().getString(R.string.Dictate_Network_Notavailable), context.getResources().getString(R.string.Alert));
//            }

            } else if (mActivationResponse.equalsIgnoreCase("TimeOut")) {
                mProgressDialog.dismiss();
            } else {

                String Message = dmApplication.validateResponse(mResultcode);
                String Alert = dmApplication.validateErrorMessage(mResultcode);
                if (Message.equalsIgnoreCase(context.getResources().getString(R.string.Settings_Success))) {
                    setRecipientForceUpdate(false);
                    new WebServicegetSettings().execute();

                } else {
                    if (mProgressDialog.isShowing())
                        mProgressDialog.dismiss();
                    if (mResultcode.equalsIgnoreCase("5002")) {
                        forceUserToActivate(Message, Alert);
                    } else if (mResultcode.equalsIgnoreCase("4012")) {
                        //show alert and activate the device with force value as 1
                        forceUserToActivate(Message, Alert);
                    } else if (mResultcode.equalsIgnoreCase("4003")) {
                        //show alert and activate the device with force value as 1
                        forceUserToActivate(Message, Alert);
                    } else {
                        forceUserToActivate(Message, Alert);
                    }

                }
            }

        }
    }

    public void getUUID() {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, 0);
        mUUID = sharedPreferences.getString("UUID", mGetuuid).trim();

    }

    private void setRecipientForceUpdate(boolean status) {
        SharedPreferences pref = PreferenceManager
                .getDefaultSharedPreferences(context);
        SharedPreferences.Editor mEditor = pref.edit();
        mEditor.putBoolean(Recipient.RECIPIENT_FORCE_UPDATE_TAG, status);
        mEditor.commit();
    }

    private String getLanguage() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        switch (Integer.parseInt(sharedPreferences.getString(context.getResources().getString(R.string.language_key), "1"))) {
            case 1:
                return "en";
            case 2:
                return "de";
            case 3:
                return "fr";
            case 4:
                return "es";
            case 5:
                return "sv";
            case 6:
                return "cs";
            default:
                return "en";
        }
    }

    public void showMessageAlert1(String Message, String Alert) {
        mAlertDialog = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.prefScreen));
        mAlertDialog.setTitle(Alert);
        mAlertDialog.setMessage(Message);
        mAlertDialog.setPositiveButton(context.getResources().getString(R.string.Dictate_Alert_Ok), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        mAlertDialog.show();
    }

    private class WebServicegetSettings extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            //  mGetSettingsResponse = mWebserviceHandler.service_Settings(SERVER_URL + "/smartphone/" + mUUID.trim() + "/" + "Settings", mBasevalue);
            mGetSettingsResponse = mWebserviceHandler.service_Settings(mSavedUrl + "/smartphone/" + mUUID.trim() + "/" + "Settings", mBasevalue);
            if (mGetSettingsResponse != null && !mGetSettingsResponse.equalsIgnoreCase("TimeOut")) {
                mSettingParser = new Settingsparser(mGetSettingsResponse);
                mSettingParser.parse(getLanguage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (context instanceof RegistrationInterface)
                registrationInterface = (RegistrationInterface) context;
            //System.out.println("Get Settings response - "+mGetSettingsResponse);
            String Message;
            String Alert;
            if (mProgressDialog.isShowing())
                mProgressDialog.dismiss();
            if (mGetSettingsResponse == null) {
                showMessageAlert1(context.getResources().getString(R.string.Settings_Error_Correct), context.getResources().getString(R.string.Ils_Result_Fail_Activate));
            } else if (mGetSettingsResponse.equalsIgnoreCase("TimeOut")) {

            } else {
                mResultcode = mSettingParser.getRootObjects().get(0).getResult_code();
                if (mResultcode.equalsIgnoreCase("2000")) {

                    Alert = dmApplication.validateErrorMessage(mResultcode);
                    Message = dmApplication.validateResponse(mResultcode);
                } else {
                    if (mResultcode.equalsIgnoreCase("4000") || mResultcode.equalsIgnoreCase("4006") ||
                            mResultcode.equalsIgnoreCase("4007") || mResultcode.equalsIgnoreCase("4008") ||
                            mResultcode.equalsIgnoreCase("4009") || mResultcode.equalsIgnoreCase("5002")) {
                        mBaseIntent = new Intent("com.olympus.dmmobile.action.Test");
                        mBaseIntent.putExtra("isWantWholeToTimeOut", true);
                        context.sendBroadcast(mBaseIntent);
                        mBaseIntent = null;
                    }
                    Message = dmApplication.validateResponse(mResultcode);
                    Alert = dmApplication.validateErrorMessage(mResultcode);
                }
                if (mResultcode.equalsIgnoreCase("2000")) {
                    registrationInterface.getResult(true);
                    // mSavedUrl = mEditServerUrl.getText().toString();
                    mEmail = authEmail;
                    mUsername = custid;
                    mPassword = custPwd;
                    //   mEditAuthor.setText(mSettingParser.getSettingsObjects().get(0).getAuthor());
                    resultStatus();
                    setConfiguration();
                    setDeviceConfig();
                  //  showMessageAlert1(context.getResources().getString(R.string.Settings_Server_Connection), context.getResources().getString(R.string.Settings_Success));
                    new GetServerSettings(context);
                } else {
                    if (mResultcode.equalsIgnoreCase("5002"))
                        Alert = mSettingParser.getMessage();
                    forceUserToActivate(Message, Alert);
                }

            }
        }
    }

    public void resultStatus() {
        mWorktypeobject = mSettingParser.getWorkTypeListObjects();
        for (int i = 0; i < mWorktypeobject.size(); i++) {
            if (mWorktype == null) {
                mWorktype = mWorktypeobject.get(i).getWorktype();
            } else {
                mWorktype = mWorktype + ":" + mWorktypeobject.get(i).getWorktype();
            }
        }
        mAudioDelivery = mSettingParser.getSettingsObjects().get(0).getDelivery();
        mAudioEncrypt = mSettingParser.getAudioObjects().get(0).getEncryption();
        mAudioFormat = mSettingParser.getAudioObjects().get(0).getFormat();
        mAudioPassword = mSettingParser.getAudioObjects().get(0).getPassword();
        mWorktypeListname = mSettingParser.getSettingsObjects().get(0).getWorktypelist();
        mAuthor = mSettingParser.getSettingsObjects().get(0).getAuthor();

    }

    public void setConfiguration() {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        mEditor = pref.edit();
        mEditor.putString(context.getResources().getString(R.string.server_url_key), mSavedUrl);        // replaced mUrl with SERVER_URL
        mEditor.putString(context.getResources().getString(R.string.login_user_key), mUsername);
        mEditor.putString(context.getResources().getString(R.string.login_password_key), mPassword);
        mEditor.putString(context.getResources().getString(R.string.email_key), mEmail);
        mEditor.putString(context.getResources().getString(R.string.Audio_delivery), mAudioDelivery);
        mEditor.putString(context.getResources().getString(R.string.Audio_Encryption_key), mAudioEncrypt);
        mEditor.putString(context.getResources().getString(R.string.Audio_Format_key), mAudioFormat);
        mEditor.putString(context.getResources().getString(R.string.Audio_Password_key), mAudioPassword);
        mEditor.putString(context.getResources().getString(R.string.Worktype_Server_key), mWorktype);
        mEditor.putString(context.getResources().getString(R.string.Worktype_List_name_Key), mWorktypeListname);
        mEditor.putString(context.getResources().getString(R.string.author_key), mAuthor);
        mEditor.commit();
        pref = PreferenceManager.getDefaultSharedPreferences(context);
        if (pref.getString(context.getResources().getString(R.string.author_key), "").equalsIgnoreCase(mAuthor))
            dmApplication.getDatabaseHandler().updateDicationName(mSettingParser.getSettingsObjects().get(0).getAuthor().trim());
        dmApplication.setUrl(mSavedUrl + "/smartphone");
        mWorktype = null;

    }

    public void setDeviceConfig() {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, 0);
        mEditor = sharedPreferences.edit();
        mEditor.putString("Activation", "Activated");
        mEditor.putString("App-Version", dmApplication.getApplicationVersion());
        mEditor.putString("Manufacturer", Build.MANUFACTURER + " " + Build.MODEL);
        mEditor.putString("Osversion", Build.VERSION.RELEASE);
        mEditor.commit();
    }

    public void forceUserToActivate(String title, String message) {
        mAlertDialog = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.prefScreen));
        mAlertDialog.setTitle(title);
        mAlertDialog.setMessage(message);
        mAlertDialog.setPositiveButton(context.getResources().getString(R.string.Dictate_Alert_Yes), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                activateThisDevice(FORCED_ACTIVATION);
            }
        });
        mAlertDialog.setNegativeButton(R.string.Dictate_Alert_No, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        mAlertDialog.show();
    }

    private void activateThisDevice(int force) {

        String name = readSelectedRecipientNameFromPreference();
        String email = "";
        recipientEmail = readSelectedRecipientEmailFromPreference();
        followServerSettings = false;

        if (name.equalsIgnoreCase("")) {
            name = recipientEmail;
        }
        if (name.equalsIgnoreCase("Use Portal Settings (Default)") || name.equalsIgnoreCase("Použít nastavení portálu (výchozí)")
                || name.equalsIgnoreCase("Portaleinstellungen verwenden (Standardeinstellung)") || name.equalsIgnoreCase("Usar ajustes de portal (por defecto)")
                || name.equalsIgnoreCase("Utiliser les paramètres du portail (par défaut)") || name.equalsIgnoreCase("Använd portalinställningar (standard)")) {
            name = "";
            email = "";
            followServerSettings = true;
        }
        new WebServiceActivation().execute();
    }

    private String readSelectedRecipientNameFromPreference() {
        SharedPreferences pref = PreferenceManager
                .getDefaultSharedPreferences(context);
        String name = pref.getString(
                Recipient.SELECTED_RECIPIENT_NAME_TAG,
                context.getString(R.string.Follow_Server_Settings));
        return name;
    }

    private String readSelectedRecipientEmailFromPreference() {
        SharedPreferences pref = PreferenceManager
                .getDefaultSharedPreferences(context);
        String email = pref.getString(
                Recipient.SELECTED_RECIPIENT_EMAIL_TAG,
                "");
        return email;
    }

}
