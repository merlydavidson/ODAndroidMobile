package com.olympus.dmmobile.registration.fragments;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.olympus.dmmobile.R;
import com.olympus.dmmobile.utils.CommonFuncArea;

public class RegWizardTwo extends Fragment implements View.OnClickListener {
    Button previousBtn, nextBtn;
    String authEmail, auth_id, dest_accnt;
    EditText authEmailEdt, authIdEdt, destAccntEdt;
    SharedPreferences accountPersisit;
    String prefName = "AccountRegistration";
    SharedPreferences.Editor accntRegEditor;
    String authEmailKey = "authEmail";
    String auth_idKey = "authid";
    String dest_accntkey = "destAccount";
    private TextView titleRegistration;
    private String errorMessage;
    private TextView errorAlertmessage;
    private boolean showMessage=false;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.reg_wizard_two, container, false);
        initializeViews(view);
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
//            case R.id.prev_btn:
//                Fragment newFragment = new RegWizardOne();
//                FragmentTransaction transaction = getFragmentManager().beginTransaction();
//                transaction.replace(R.id.wizard_frag, newFragment);
//                transaction.addToBackStack(null);
//                transaction.commit();
//                break;
            case R.id.next_btn:
                errorMessage();
                if (validateFields() && (errorMessage() == "")) {
                    errorAlertmessage.setVisibility(View.GONE);
                    persisitUserDetails();
                    Fragment wizardThreeFragment = new RegWizardThree();
                    FragmentTransaction transactionThree = getFragmentManager().beginTransaction();
                    transactionThree.replace(R.id.wizard_frag, wizardThreeFragment);
                    transactionThree.addToBackStack(null);
                    transactionThree.commit();
                } else {
                    errorAlertmessage.setVisibility(View.VISIBLE);
                    errorAlertmessage.setText(errorMessage);
                }

        }

    }

    @Override
    public void onPause() {
        super.onPause();
        errorAlertmessage.setVisibility(View.GONE);
    }

    public void initializeViews(View view) {
        accountPersisit = getActivity().getSharedPreferences(prefName, getActivity().MODE_PRIVATE);
        accntRegEditor = accountPersisit.edit();
        authEmailEdt = view.findViewById(R.id.auth_user_email);
        authIdEdt = view.findViewById(R.id.auth_id);
        destAccntEdt = view.findViewById(R.id.dest_accnt);
        //previousBtn = view.findViewById(R.id.prev_btn);
        //   previousBtn.setOnClickListener(this);
        nextBtn = view.findViewById(R.id.next_btn);
        nextBtn.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_click));
        nextBtn.setOnClickListener(this);
        titleRegistration = getActivity().findViewById(R.id.title_reg);
        errorAlertmessage = getActivity().findViewById(R.id.error_alert_message);
        errorAlertmessage.setVisibility(View.GONE);
        titleRegistration.setText(getResources().getString(R.string.user_reg_info_text));
        authIdEdt.setFilters(new InputFilter[]{
                new InputFilter() {
                    public CharSequence filter(CharSequence src, int start,
                                               int end, Spanned dst, int dstart, int dend) {
                        if (src.equals(" ")) {
                            String underScore = src.toString().replace(" ", "_");
                            return underScore;
                        } else if (src.toString().matches("[a-zA-Z0-9_]+")) {
                            return src.toString().toUpperCase();
                        } else if (src.equals("")) {
                            return src;
                        } else {

                            return "";

                        }


                    }
                }
        });

    }

    @Override
    public void onResume() {
        int index=0;
        getpersistDetails();
        ImageView progresstracker = getActivity().findViewById(R.id.progress_tracker_img);
        progresstracker.setImageDrawable(getResources().getDrawable(R.drawable.progresstracker_2));
        titleRegistration.setText(getResources().getString(R.string.start_comp_reg));
        Bundle args = getArguments();
        if (args != null)
        {index = args.getInt("errorcode", 0);
        if(index==4015)
        { if (!showMessage) {
            showMessage = true;
            authEmailEdt.setBackgroundColor(getResources().getColor(R.color.error_red));
            errorAlertmessage.setVisibility(View.VISIBLE);
            errorAlertmessage.setText("* "+getString(R.string.enter_auth_email_hint));
        }}

        }


        super.onResume();
    }

    public boolean validateFields() {
        boolean isValid = true;
        if (!authEmailEdt.getText().toString().equalsIgnoreCase("") && new CommonFuncArea().isValidEmail(authEmailEdt.getText().toString())) {
            authEmail = authEmailEdt.getText().toString();
            if (!authIdEdt.getText().toString().equalsIgnoreCase("") && authIdEdt.getText().toString().length() <= 16) {
                auth_id = authIdEdt.getText().toString();
                if (!destAccntEdt.getText().toString().equalsIgnoreCase("") && new CommonFuncArea().isValidEmail(destAccntEdt.getText().toString())) {
                    dest_accnt = destAccntEdt.getText().toString();

                } else {
                    isValid = false;
                    destAccntEdt.setBackgroundColor(getResources().getColor(R.color.error_red));
                }
            } else {
                isValid = false;
                authIdEdt.setBackgroundColor(getResources().getColor(R.color.error_red));
            }
        } else {
            isValid = false;
            authEmailEdt.setBackgroundColor(getResources().getColor(R.color.error_red));
        }

        if (authEmailEdt.getText().toString().equalsIgnoreCase(""))
            authEmailEdt.setBackgroundColor(getResources().getColor(R.color.error_red));
        if (authIdEdt.getText().toString().equalsIgnoreCase(""))
            authIdEdt.setBackgroundColor(getResources().getColor(R.color.error_red));
        if (destAccntEdt.getText().toString().equalsIgnoreCase(""))
            destAccntEdt.setBackgroundColor(getResources().getColor(R.color.error_red));
        return isValid;

    }

    public String errorMessage() {
        errorMessage = "";
        if (authEmailEdt.getText().toString().equalsIgnoreCase("") || !new CommonFuncArea().isValidEmail(authEmailEdt.getText().toString())) {
            errorMessage ="* "+getString(R.string.enter_auth_email_hint);
        }
        else
        {
            authEmailEdt.setBackgroundColor(getResources().getColor(R.color.white));
        }
        if (authIdEdt.getText().toString().equalsIgnoreCase("") || authIdEdt.getText().toString().length() > 16) {
            if (errorMessage == "")
                errorMessage += "* "+getString(R.string.enter_authorid1_hint);
            else
                errorMessage +="\n* "+getString(R.string.enter_authorid1_hint);
        }
        else
        {
            authIdEdt.setBackgroundColor(getResources().getColor(R.color.white));
        }
        if (destAccntEdt.getText().toString().equalsIgnoreCase("") || !new CommonFuncArea().isValidEmail(destAccntEdt.getText().toString())) {
            if (errorMessage == "")
                errorMessage +="* "+getString(R.string.dest_acct_hint);
            else
                errorMessage +="\n* "+getString(R.string.dest_acct_hint);
        }
        else
        {
            destAccntEdt.setBackgroundColor(getResources().getColor(R.color.white));
        }
        return errorMessage;
    }

    public void persisitUserDetails() {
        accntRegEditor.putString(authEmailKey, authEmailEdt.getText().toString());
        accntRegEditor.putString(auth_idKey, authIdEdt.getText().toString());
        accntRegEditor.putString(dest_accntkey, destAccntEdt.getText().toString());

        accntRegEditor.commit();
    }

    public void getpersistDetails() {
        authEmail = accountPersisit.getString(authEmailKey, "");
        auth_id = accountPersisit.getString(auth_idKey, "");
        dest_accnt = accountPersisit.getString(dest_accntkey, "");

        authEmailEdt.setText(authEmail);
        authIdEdt.setText(auth_id);
        destAccntEdt.setText(dest_accnt);

    }
}

