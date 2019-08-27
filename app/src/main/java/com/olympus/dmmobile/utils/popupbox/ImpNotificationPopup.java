package com.olympus.dmmobile.utils.popupbox;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.olympus.dmmobile.ActionHandler;
import com.olympus.dmmobile.DMApplication;
import com.olympus.dmmobile.DatabaseHandler;
import com.olympus.dmmobile.DictationCard;
import com.olympus.dmmobile.R;

import java.util.ArrayList;

import static android.text.Layout.JUSTIFICATION_MODE_INTER_WORD;

public class ImpNotificationPopup {
    Context context;
    private AlertDialog.Builder mBuilder;
    int callingFrom = 0;
    public ActionHandler actionhandler;
    private DictationCard dictCard;
    private DatabaseHandler mDbHandler;
    private DMApplication dmApplication = null;
    ArrayList<DictationCard> dictationCards = null;
    private AlertDialog.Builder mAlertDialog;

    public ImpNotificationPopup(Context context) {
        this.context = context;
    }

    public ImpNotificationPopup(Context context, int callingFrom, DictationCard dictCard) {
        this.context = context;
        this.callingFrom = callingFrom;
        this.dictCard = dictCard;
        dmApplication = (DMApplication) context.getApplicationContext();
        mDbHandler = dmApplication.getDatabaseHandler();

    }

    public ImpNotificationPopup(Context context, int callingFrom, ArrayList<DictationCard> cards) {
        this.context = context;
        this.callingFrom = callingFrom;
        this.dictationCards = cards;
        dmApplication = (DMApplication) context.getApplicationContext();
        mDbHandler = dmApplication.getDatabaseHandler();

    }

    public ImpNotificationPopup(Context context, int callingFrom) {
        this.context = context;
        this.callingFrom = callingFrom;

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void showMessageAlert(String notificationMessage) {
        //  mAlertDialog = new AlertDialog.Builder(WorktypeActivity.this);
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        AlertDialog.Builder alertDialogB = new AlertDialog.Builder(context, R.style.CustomAlertDialog);
        View layout = LayoutInflater.from(context).inflate(
                R.layout.imp_notification_popup, null);
//        View layout = inflater.inflate(R.layout.alert_error_message,
//                (ViewGroup) context.findViewById(R.id.alert_err_lot));

        TextView textView = (TextView) layout.findViewById(R.id.txt_error_Msg);
        TextView impsendtxt = (TextView) layout.findViewById(R.id.send_imp_message);
        TextView textViewtitle = (TextView) layout.findViewById(R.id.txt_err_title);
        Button btnClose = (Button) layout.findViewById(R.id.btn_close);
        Button button = (Button) layout.findViewById(R.id.btn_err);
        alertDialogB.setView(layout);
        try {
            textView.setText(notificationMessage);
            if (android.os.Build.VERSION.SDK_INT > 25)
                textView.setJustificationMode(JUSTIFICATION_MODE_INTER_WORD);
//            if (callingFrom == 1) {
//
//                if (android.os.Build.VERSION.SDK_INT > 25)
//                    impsendtxt.setJustificationMode(JUSTIFICATION_MODE_INTER_WORD);
//            }
            if (callingFrom == 2 || callingFrom == 3) {
                impsendtxt.setVisibility(View.VISIBLE);
                impsendtxt.setText(context.getResources().getString(R.string.send_imp_message));
                if (android.os.Build.VERSION.SDK_INT > 25)
                    impsendtxt.setJustificationMode(JUSTIFICATION_MODE_INTER_WORD);

            }
            textViewtitle.setText(context.getResources().getString(R.string.imp_notification_title));
            final AlertDialog alertDialog = alertDialogB.create();

            btnClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (callingFrom == 2) {
                        mDbHandler.updateDummyStatus(dictCard.getDictationId(), 1);
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
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (callingFrom == 2)
                        new ActionSelectionPopup(context, dictCard, callingFrom).showActionAlert();
                    else if (callingFrom == 3)
                        new ActionSelectionPopup(context, dictationCards, callingFrom).showActionAlert();
                    else
                        new ActionSelectionPopup(context).showActionAlert();
                    alertDialog.dismiss();
                }
            });
            if (!((Activity) context).isFinishing()) {
                alertDialog.show();
                if (android.os.Build.VERSION.SDK_INT <= 25) {
                    WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                    lp.copyFrom(alertDialog.getWindow().getAttributes());
                    lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
                    lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                    alertDialog.show();
                    alertDialog.getWindow().setAttributes(lp);
                    // alertDialog.getWindow().setLayout(600, 1100);
                } else {
                    WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                    lp.copyFrom(alertDialog.getWindow().getAttributes());
                    lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
                    lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                    alertDialog.show();
                    alertDialog.getWindow().setAttributes(lp);
                    // alertDialog.getWindow().setLayout(900, 1650);

                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @TargetApi(Build.VERSION_CODES.O)
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void showEmailAlert(String notificationMessage) {
        //  mAlertDialog = new AlertDialog.Builder(WorktypeActivity.this);
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        AlertDialog.Builder alertDialogB = new AlertDialog.Builder(context, R.style.CustomAlertDialog);
        View layout = LayoutInflater.from(context).inflate(
                R.layout.imp_notification_popup, null);
//        View layout = inflater.inflate(R.layout.alert_error_message,
//                (ViewGroup) context.findViewById(R.id.alert_err_lot));

        TextView textView = (TextView) layout.findViewById(R.id.txt_error_Msg);
        TextView impsendtxt = (TextView) layout.findViewById(R.id.send_imp_message);
        TextView textViewtitle = (TextView) layout.findViewById(R.id.txt_err_title);
        Button btnClose = (Button) layout.findViewById(R.id.btn_close);
        Button button = (Button) layout.findViewById(R.id.btn_err);
        button.setVisibility(View.GONE);
        alertDialogB.setView(layout);
        textView.setText(notificationMessage);
        textView.setJustificationMode(JUSTIFICATION_MODE_INTER_WORD);

        textViewtitle.setText(context.getResources().getString(R.string.imp_notification_title));
        final AlertDialog alertDialog = alertDialogB.create();

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                alertDialog.dismiss();
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ActionSelectionPopup(context).showActionAlert();
                alertDialog.dismiss();
            }
        });
        if (!((Activity) context).isFinishing()) {
            alertDialog.show();

            if (android.os.Build.VERSION.SDK_INT <= 25) {
                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(alertDialog.getWindow().getAttributes());
                lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
                lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                alertDialog.show();
                alertDialog.getWindow().setAttributes(lp);
                // alertDialog.getWindow().setLayout(600, 1100);
            } else {
                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(alertDialog.getWindow().getAttributes());
                lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
                lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                alertDialog.show();
                alertDialog.getWindow().setAttributes(lp);
                // alertDialog.getWindow().setLayout(900, 1650);

            }

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
