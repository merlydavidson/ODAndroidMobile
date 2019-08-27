package com.olympus.dmmobile.utils.popupbox;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.olympus.dmmobile.DMApplication;
import com.olympus.dmmobile.DatabaseHandler;
import com.olympus.dmmobile.DictationCard;
import com.olympus.dmmobile.R;
import com.olympus.dmmobile.registration.RegisterActivity;
import com.olympus.dmmobile.settings.ServerOptionsActivity;

import java.util.ArrayList;

import static android.text.Layout.JUSTIFICATION_MODE_INTER_WORD;

public class ActionSelectionPopup {
    Context context;
    private AlertDialog.Builder mBuilder;
    AlertDialog alertDialog=null;
    DictationCard dictationCard;
    ArrayList<DictationCard> dictationCards;
    int callingFrom=0;
    private DatabaseHandler mDbHandler;
    private DMApplication dmApplication = null;
    private AlertDialog.Builder mAlertDialog;

    public ActionSelectionPopup(Context context) {
        this.context = context;
    }

    public ActionSelectionPopup(Context context, DictationCard dictationCard, int callingFrom) {
        this.context = context;
        this.dictationCard = dictationCard;
        this.callingFrom = callingFrom;
        dmApplication = (DMApplication) context.getApplicationContext();
        mDbHandler = dmApplication.getDatabaseHandler();
    }

    public ActionSelectionPopup(Context context, ArrayList<DictationCard> dictationCards, int callingFrom) {
        this.context = context;
        this.dictationCards = dictationCards;
        this.callingFrom = callingFrom;
        dmApplication = (DMApplication) context.getApplicationContext();
        mDbHandler = dmApplication.getDatabaseHandler();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void showActionAlert() {


        AlertDialog.Builder alertDialogB = new AlertDialog.Builder(context, R.style.CustomAlertDialog);
        View layout = LayoutInflater.from(context).inflate(
                R.layout.action_selection_popup, null);
        try {

            TextView ini_txt = (TextView) layout.findViewById(R.id.txt_ini_reg);
            TextView comp_txt = (TextView) layout.findViewById(R.id.txt_comp_reg);
            TextView actionIniTitle = (TextView) layout.findViewById(R.id.txt_ini_title);
            TextView actionCompTitle = (TextView) layout.findViewById(R.id.txt_cmp_title);
            Button btnClose = (Button) layout.findViewById(R.id.btn_close);
            Button btnIni = (Button) layout.findViewById(R.id.btn_ini_reg);
            Button btnCmp = (Button) layout.findViewById(R.id.btn_cmp);
            alertDialogB.setView(layout);
            ini_txt.setText(context.getResources().getString(R.string.ini_body_text));
            if(android.os.Build.VERSION.SDK_INT>25)
            ini_txt.setJustificationMode(JUSTIFICATION_MODE_INTER_WORD);
            comp_txt.setText(context.getResources().getString(R.string.comp_body_text));
            if(android.os.Build.VERSION.SDK_INT>25)
            comp_txt.setJustificationMode(JUSTIFICATION_MODE_INTER_WORD);
            actionIniTitle.setText(context.getResources().getString(R.string.action_title));
            actionCompTitle.setText(context.getResources().getString(R.string.action_cmp_title));
            alertDialog = alertDialogB.create();
            btnClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (callingFrom == 2) {
                        mDbHandler.updateDummyStatus(dictationCard.getDictationId(), 1);
                        if (DMApplication.isONLINE())
                            showMessageAlert1(context.getResources().getString(R.string.dummyMessage));
                    }
                    if (callingFrom == 3) {
                        for (DictationCard dictationCard : dictationCards) {
                            mDbHandler.updateDummyStatus(dictationCard.getDictationId(), 1);
                        }
                        if (DMApplication.isONLINE())
                            showMessageAlert1(context.getResources().getString(R.string.dummyMessage));
                    }
                    alertDialog.dismiss();
                }
            });
            btnIni.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent initRegActivity = new Intent(context, RegisterActivity.class);

                    context.startActivity(initRegActivity);
                    alertDialog.dismiss();
                }
            });
            btnCmp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent serverSettingsActivity = new Intent(context, ServerOptionsActivity.class);
                    context.startActivity(serverSettingsActivity);
                    alertDialog.dismiss();
                }
            });
            if (!((Activity) context).isFinishing()) {
                alertDialog.show();
                if (android.os.Build.VERSION.SDK_INT <= 25)
                {
                    WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                    lp.copyFrom(alertDialog.getWindow().getAttributes());
                    lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
                    lp.height = WindowManager.LayoutParams.MATCH_PARENT;
                    alertDialog.show();
                    alertDialog.getWindow().setAttributes(lp);
                    // alertDialog.getWindow().setLayout(600, 1100);
                }

                else {
                    WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                    lp.copyFrom(alertDialog.getWindow().getAttributes());
                    lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
                    lp.height = WindowManager.LayoutParams.MATCH_PARENT;
                    alertDialog.show();
                    alertDialog.getWindow().setAttributes(lp);
                    // alertDialog.getWindow().setLayout(900, 1650);

                }
                // alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

                // This is line that does all the magic
                alertDialog.getWindow().setBackgroundDrawableResource(
                        R.drawable.rounded_layout);
            }
        } catch(Exception e)
        {

        }
    }
    public void showMessageAlert1(String Message) {

        mAlertDialog = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.prefScreen));

        mAlertDialog.setMessage(Message);
        mAlertDialog.setPositiveButton(context.getResources().getString(R.string.Dictate_Alert_Ok), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        mAlertDialog.show();
    }
}
