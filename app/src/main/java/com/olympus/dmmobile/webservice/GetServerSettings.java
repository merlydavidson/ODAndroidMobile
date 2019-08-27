package com.olympus.dmmobile.webservice;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import com.olympus.dmmobile.DMApplication;
import com.olympus.dmmobile.DatabaseHandler;
import com.olympus.dmmobile.DictationCard;
import com.olympus.dmmobile.R;

import java.util.List;

public class GetServerSettings {
    private final WebServiceGetSettings mWebServiceGetSettings;
    Context context;
    private SharedPreferences pref;
    private final String PREFS_NAME = "Config";
    private String mGetuuid;
    private String prefUUID;
    private String mUrl;
    private DMApplication dmApplication = null;
    private String mGetemail;
    private String mEmail;
    private Base64_Encoding baseEncoding;
    private String base64value;
    private String mGetSettingsResponse;
    private WebserviceHandler webserviceHandler = null;
    private Settingsparser settingParser;
    private String language;
    private String mResultcode;
    private int isSplittable = 0;
    private String mWorktype;
    private List<Settingsparser.WorkTypeListObjects> worktypeobject;
    private String mAudioDelivery;
    private String mAudioEncrypt;
    private String mAudioFormat;
    private String mAudioPassword;
    private String mWorktypeListname;
    private String mAuthorName;
    private boolean isCriticalErrorOccures = false;
    private SharedPreferences.Editor editor;
    private DatabaseHandler mDbHandler;
    public Cursor dummyCursor = null;
    public final String IS_DUMMY = "IS_DUMMY";
    private DictationCard dictationCard;
    public int dssVersion = -1;
    private final String DICTATION_ID = "_id";

    public GetServerSettings(Context context) {
        this.context = context;
        dmApplication = (DMApplication) context.getApplicationContext();
        dmApplication.setContext(context);
        webserviceHandler = new WebserviceHandler();
        pref = context.getSharedPreferences(PREFS_NAME, 0);
        editor = pref.edit();
        mDbHandler = dmApplication.getDatabaseHandler();
        mWebServiceGetSettings = new WebServiceGetSettings();
        mWebServiceGetSettings.execute();
    }

    private void getSettingsAttribute() {

        if (pref.getString("UUID", mGetuuid) != null)
            prefUUID = pref.getString("UUID", mGetuuid);
        mUrl = dmApplication.getUrl();
        pref = PreferenceManager.getDefaultSharedPreferences(context);
        if (pref.getString(context.getString(R.string.email_key), mGetemail) != null)
            mEmail = pref.getString(context.getString(R.string.email_key), mGetemail);
        baseEncoding = new Base64_Encoding();
        base64value = baseEncoding.base64(prefUUID + ":" + mEmail);
    }

    private class WebServiceGetSettings extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            getSettingsAttribute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                mGetSettingsResponse = webserviceHandler
                        .onRequestBackgroundSettings(mUrl + "/" + prefUUID
                                + "/Settings", base64value);
                if (mGetSettingsResponse != null) {
                    if (!mGetSettingsResponse.equalsIgnoreCase("TimeOut")) {
                        settingParser = new Settingsparser(mGetSettingsResponse);
                        settingParser.parse(getlanguage());
                        mResultcode = settingParser.getRootObjects().get(0).getResult_code().trim();
                    }
                }
            } catch (Exception e) {
                mGetSettingsResponse = null;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            isSplittable = 0;
            try {
                if (mGetSettingsResponse != null) {
                    if (!mGetSettingsResponse.equalsIgnoreCase("TimeOut")) {

                        /*
                         * perform some actions based on the response.
                         */
                        if (mResultcode.equalsIgnoreCase("2000")) {

                            mWorktype = null;
                            worktypeobject = settingParser
                                    .getWorkTypeListObjects();
                            for (int i = 0; i < worktypeobject.size(); i++) {
                                if (mWorktype == null)
                                    mWorktype = worktypeobject.get(i)
                                            .getWorktype();
                                else
                                    mWorktype = mWorktype
                                            + ":"
                                            + worktypeobject.get(i)
                                            .getWorktype();
                            }
                            mAudioDelivery = settingParser.getSettingsObjects()
                                    .get(0).getDelivery();
                            mAudioEncrypt = settingParser.getAudioObjects()
                                    .get(0).getEncryption();
                            mAudioFormat = settingParser.getAudioObjects()
                                    .get(0).getFormat();
                            mAudioPassword = settingParser.getAudioObjects()
                                    .get(0).getPassword();
                            mWorktypeListname = settingParser
                                    .getSettingsObjects().get(0)
                                    .getWorktypelist();
                            mAuthorName = settingParser.getSettingsObjects()
                                    .get(0).getAuthor();
                            dssVersion = Integer.parseInt(settingParser.getAudioObjects()
                                    .get(0).getFormat());
                            getAllDummyDatatoupdate();
                        } else if (mResultcode.equalsIgnoreCase("5000")
                                || mResultcode.equalsIgnoreCase("5001"))
                            onSetRecentSettings();
                            /*
                             * To check the result is critical error.
                             */
                        else if (mResultcode.equalsIgnoreCase("4000")
                                || mResultcode.equalsIgnoreCase("4006")
                                || mResultcode.equalsIgnoreCase("4007")
                                || mResultcode.equalsIgnoreCase("4008")
                                || mResultcode.equalsIgnoreCase("4009")
                                || mResultcode.equalsIgnoreCase("5002")) {
                            isCriticalErrorOccures = true;
//                            baseIntent = new Intent(
//                                    "com.olympus.dmmobile.action.Test");
//                            baseIntent.putExtra("isWantWholeToTimeOut", true);
//                            sendBroadcast(baseIntent);// send a notification to
//                            // background.
//                            baseIntent = null;
                            if (mResultcode.equalsIgnoreCase("4007")) {
                                pref = context.getSharedPreferences(PREFS_NAME, 0);
                                editor = pref.edit();
                                editor.putString("Activation", "Not Activated");
                                editor.commit();
                            }
                            /*
                             * show critical error dialog with respect to the
                             * response.
                             */
//                            if (!dmApplication.isTimeOutDialogOnFront()) {
//                                dmApplication.setTimeOutDialogOnFront(true);
//                                dmApplication.setErrorCode(mResultcode);
//                                baseIntent = new Intent(dmApplication,
//                                        CustomDialog.class);
//                                if (mResultcode.equalsIgnoreCase("5002"))
//                                    dmApplication.setErrorMessage(settingParser
//                                            .getMessage());
//                                baseIntent
//                                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                startActivity(baseIntent);
//                                baseIntent = null;
//                            }
                        } else if (mResultcode.equalsIgnoreCase("4001")
                                || mResultcode.equalsIgnoreCase("4002")
                                || mResultcode.equalsIgnoreCase("4003")
                                || mResultcode.equalsIgnoreCase("4004")
                                || mResultcode.equalsIgnoreCase("4005")) {
                            onSetRecentSettings();
                        } else
                            onSetRecentSettings();
                    } else {
//                        if (dialog.isShowing())
//                            dialog.dismiss();
                        onSetRecentSettings();
                    }
                } else {
//                    if (dialog.isShowing())
//                        dialog.dismiss();
                    onSetRecentSettings();
                }
                if (!isCriticalErrorOccures) {
//                    if (mAudioDelivery.trim().equalsIgnoreCase("3"))
//                        onPromptSendRecordings();
//                    else {
//                        if (mAudioDelivery.equalsIgnoreCase("1"))
//                            isSplittable = 1;
//                        else
//                            isSplittable = 0;
                    onAfterSettingsDelivery();
                    // }
                }
            } catch (Exception e) {
            }
        }
    }

    private void onAfterSettingsDelivery() {
        pref = PreferenceManager.getDefaultSharedPreferences(context);
        editor = pref.edit();
        editor.putString(context.getString(R.string.Audio_delivery), mAudioDelivery);
        editor.putString(context.getString(R.string.Audio_Encryption_key),
                mAudioEncrypt);
        editor.putString(context.getString(R.string.Audio_Format_key), mAudioFormat);
        editor.putString(context.getString(R.string.Audio_Password_key), mAudioPassword);
        editor.putString(context.getString(R.string.Worktype_Server_key), mWorktype);
        editor.putString(context.getString(R.string.Worktype_List_name_Key),
                mWorktypeListname);
        editor.putString(context.getString(R.string.author_key), mAuthorName);
        editor.commit();

        dssVersion = Integer.parseInt(mAudioFormat);
        if (mResultcode != null && mResultcode.equalsIgnoreCase("2000")) {
            pref = PreferenceManager.getDefaultSharedPreferences(context);
            if (pref.getString(context.getResources().getString(R.string.author_key), "").equalsIgnoreCase(mAuthorName))
                mDbHandler.updateDicationName(settingParser.getSettingsObjects().get(0).getAuthor());
        }

    }

    public String getlanguage() {
        String currentLanguage = null;
        SharedPreferences pref = PreferenceManager
                .getDefaultSharedPreferences(context);
        currentLanguage = (pref.getString(context.getString(R.string.language_key), ""));
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

    private void onSetRecentSettings() {
        pref = PreferenceManager.getDefaultSharedPreferences(context);
        mAudioDelivery = pref.getString(context.getString(R.string.Audio_delivery),
                mAudioDelivery);
        mAudioEncrypt = pref.getString(
                context.getString(R.string.Audio_Encryption_key), mAudioEncrypt);
        mAudioFormat = pref.getString(context.getString(R.string.Audio_Format_key),
                mAudioFormat);
        mAudioPassword = pref.getString(context.getString(R.string.Audio_Password_key),
                mAudioPassword);
        mWorktype = pref.getString(context.getString(R.string.Worktype_Server_key),
                mWorktype);
        mWorktypeListname = pref.getString(
                context.getString(R.string.Worktype_List_name_Key), mWorktypeListname);
        mAuthorName = pref.getString(context.getString(R.string.author_key),
                mAuthorName);
        dssVersion=Integer.parseInt(mAudioFormat);
    }

    public void getAllDummyDatatoupdate() {
        dictationCard = new DictationCard();
        dummyCursor = mDbHandler.getDummyUpdate();
        if (dummyCursor != null) {
            if (dummyCursor.moveToFirst()) {
                do {
                    mDbHandler.updateDummyDicationName(mAuthorName,dummyCursor.getInt(dummyCursor.getColumnIndex(DICTATION_ID)));
                    mDbHandler.updateWholeDummyData(dummyCursor.getInt(dummyCursor.getColumnIndex( DICTATION_ID)),dssVersion,mAuthorName);
                } while (dummyCursor.moveToNext());
            }
        }

    }
}
