package com.olympus.dmmobile.webservice;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.ContextThemeWrapper;

import com.olympus.dmmobile.DMApplication;
import com.olympus.dmmobile.R;
import com.olympus.dmmobile.registration.interfaces.RegistrationInterface;


public class AccountRegistration extends AsyncTask<String, Void, String> {
    public String appversion = "2.0.0";
    public String name = "";
    public String id = "";
    public String pwd = "";
    public String email = "";
    public String author_id = "";
    public String emailAuthor = "";
    public String pwdAuthor = "";
    public String upgrade = "";
    public String typistEmail = "";
    private DMApplication dmApplication = null;
    private String mActivationResponse = null;
    private WebserviceHandler mWebserviceHandler;
    private AlertDialog.Builder mAlertDialog;
    private ResultXmlParser mResultXmlParser = null;
    private SharedPreferences sharedPreferences;
    Context context;
    private String mResultcode = "";
    RegistrationInterface registrationInterface;
    ProgressDialog progressDialog;
    SharedPreferences.Editor popUpeditor;
    private SharedPreferences mSharedPref;
    String requestHeader = "";


    public static final String mPREFERENCES = "Checkbox";
    private String mUrl = "";
    private SharedPreferences mSharedPreferences;
    private final String PREFS_NAME = "Config";
    private String mGeturl = "";
    private final String DEFAULT_URL = "https://www.dictation-portal.com";


    public AccountRegistration(String appversion, String name, String id, String pwd, String email, String author_id, String emailAuthor, String pwdAuthor, String upgrade, Context context, String typistEmail, ProgressDialog progressDialog) {
        this.appversion = appversion;
        this.name = name;
        this.id = id;
        this.pwd = pwd;
        this.email = email;
        this.author_id = author_id;
        this.emailAuthor = emailAuthor;
        this.pwdAuthor = pwdAuthor;
        this.upgrade = upgrade;
        this.context = context;
        this.progressDialog = progressDialog;
        this.typistEmail = typistEmail;
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }


    @Override
    protected String doInBackground(String... strings) {
        if (context instanceof RegistrationInterface)
            registrationInterface = (RegistrationInterface) context;
        mWebserviceHandler = new WebserviceHandler();
        dmApplication = (DMApplication) context.getApplicationContext();
        StringBuilder request = new StringBuilder();
        requestHeader = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
                + "<ils-request appversion=\"1.0.0\">"
                + "<odp_admin>"
                + "<name>" + name + "</name>"
                + "<id>" + id + "</id>"
                + "<pwd>" + pwd + "</pwd>"
                + "<email>" + email + "</email>"
                + "</odp_admin>"
                + "<author>"
                + "<author_id>" + author_id + "</author_id>"
                + "<email>" + emailAuthor + "</email>"
                + "</author>"
                + "<recipient>"
                + "<email>" + typistEmail + "</email>"
                + "</recipient>"
                + "<smartphone>"
                + "<pwd>" + pwdAuthor + "</pwd>"
                + "</smartphone>"
                + "<od_upgrade_info>"
                + "<upgrade>1</upgrade>"
                + "</od_upgrade_info>"
                + "</ils-request>";
        Log.d("requesttrial", "response " + requestHeader);
        request.append(requestHeader);
        mUrl = mSharedPreferences.getString(context.getResources().getString(R.string.server_url_key), DEFAULT_URL);
        if (mUrl.equalsIgnoreCase("")) {
            mUrl = DEFAULT_URL;
        }
        mActivationResponse = mWebserviceHandler.service_Activation(mUrl + "/smartphone/requesttrial", request.toString());

        if (mActivationResponse != null && !mActivationResponse.equalsIgnoreCase("TimeOut")) {
            mResultXmlParser = new ResultXmlParser(mActivationResponse);
            mResultcode = mResultXmlParser.parse(getLanguage());
        }

        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        // RegistrationInterface registrationInterface = null;
        if (mActivationResponse == null) {
            if (DMApplication.isONLINE()) {
                showMessageAlert1("Can't connect to Server", "Error");
            } else {
                showMessageAlert1(context.getResources().getString(R.string.Dictate_Network_Notavailable), context.getResources().getString(R.string.Alert));

            }


        } else {
            mSharedPref = context.getSharedPreferences(mPREFERENCES, Context.MODE_PRIVATE);
            popUpeditor = mSharedPref.edit();
            String Message = dmApplication.validateResponse(mResultcode);
            String Alert = dmApplication.validateErrorMessage(mResultcode);
            if (Message.equalsIgnoreCase(context.getResources().getString(R.string.Settings_Success))) {
                popUpeditor.putBoolean("popup", false);
                popUpeditor.putBoolean("registered", true);
                popUpeditor.commit();
                new ActivateServer(emailAuthor, id, pwdAuthor, author_id, context, progressDialog);
//                showMessageAlert1("Successfully registered", Message);
//                DMApplication.isRegisterd = true;
//                registrationInterface.getResult(true);


            } else if (mResultcode.equalsIgnoreCase("2000")) {
                new ActivateServer(emailAuthor, id, pwdAuthor, author_id, context, progressDialog);
//                popUpeditor.putBoolean("popup", false);
//                popUpeditor.commit();
//                DMApplication.isRegisterd = true;
//                // new RegCompletedWizard().getResult(true);
//                registrationInterface.getResult(true);
//                //registrationInterface.getResult(true);
//                showMessageAlert1("Successfully Registerd", Message);
            } else {
                //new RegCompletedWizard().getResult(false);
                registrationInterface.getResult(false);
                //   registrationInterface.getResult(false);
                DMApplication.isRegisterd = false;
                if (mResultcode.equalsIgnoreCase("4010")) {
                    progressDialog.dismiss();
                    showMessageAlert1(context.getResources().getString(R.string.not_found_license), Alert);
                } else if (mResultcode.equalsIgnoreCase("4011")) {
                    progressDialog.dismiss();
                    //show alert and activate the device with force value as 1
                    forceUserToActivate(context.getResources().getString(R.string.invalid_args), Alert);
                    //  showMessageAlert1(context.getResources().getString(R.string.invalid_args), Alert);
                } else if (mResultcode.equalsIgnoreCase("4012")) {
                    progressDialog.dismiss();
                    //show alert and activate the device with force value as 1
                    forceUserToActivate(context.getResources().getString(R.string.email_activated), Alert);
                    //showMessageAlert1(context.getResources().getString(R.string.email_activated), Alert);
                } else if (mResultcode.equalsIgnoreCase("4013")) {
                    progressDialog.dismiss();
                    //show alert and activate the device with force value as 1
                    registrationInterface.getErrorCode(Integer.parseInt(mResultcode));

                    //showMessageAlert1(context.getResources().getString(R.string.invalid_odp), Alert);
                } else if (mResultcode.equalsIgnoreCase("4014")) {
                    progressDialog.dismiss();
                    //show alert and activate the device with force value as 1
                    registrationInterface.getErrorCode(Integer.parseInt(mResultcode));
                } else if (mResultcode.equalsIgnoreCase("4015")) {
                    progressDialog.dismiss();
                    //show alert and activate the device with force value as 1
                    registrationInterface.getErrorCode(Integer.parseInt(mResultcode));
                } else {
                    progressDialog.dismiss();
                    forceUserToActivate(Message, Alert);
                    //    showMessageAlert1(Alert, Message);
                }

            }
        }

        //  progressDialog.dismiss();

    }

    public void showMessageAlert1(String Message, String Alert) {
        if (progressDialog.isShowing())
            progressDialog.dismiss();
        mAlertDialog = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.prefScreen));
        mAlertDialog.setTitle(Alert);
        mAlertDialog.setMessage(Message);
        mAlertDialog.setPositiveButton(context.getResources().getString(R.string.Dictate_Alert_Ok), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (mResultcode.equalsIgnoreCase("4013") || mResultcode.equalsIgnoreCase("4014") || mResultcode.equalsIgnoreCase("4015")) {
                    registrationInterface.getErrorCode(Integer.parseInt(mResultcode));
                }

            }
        });
        mAlertDialog.show();
    }

    public void forceUserToActivate(String title, String message) {
        mAlertDialog = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.prefScreen));
        mAlertDialog.setTitle(title);
        mAlertDialog.setMessage(message);
        mAlertDialog.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (progressDialog != null)
                    if (!progressDialog.isShowing())
                        progressDialog.show();
                new AccountRegistration(appversion, name, id, pwd, email, author_id, emailAuthor, pwdAuthor, upgrade, context, typistEmail, progressDialog).execute();
            }
        });
        mAlertDialog.setNegativeButton(R.string.Dictate_Alert_No, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        mAlertDialog.show();
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
}