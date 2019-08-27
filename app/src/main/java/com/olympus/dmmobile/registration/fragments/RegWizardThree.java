package com.olympus.dmmobile.registration.fragments;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.olympus.dmmobile.R;
import com.olympus.dmmobile.utils.CommonFuncArea;

public class RegWizardThree extends Fragment implements View.OnClickListener {
    EditText authEmailEdt;
    Button backBtn3, nextBtn3;
    String authMail = "";
    String authMailKey = "authMail3";
    SharedPreferences accountPersisit;
    String prefName = "AccountRegistration";
    SharedPreferences.Editor accntRegEditor;
    boolean isShowPassword = false;
    private TextView titleRegistration;
    private String errorMessage;
    private TextView errorAlertmessage;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.reg_wizard_three, container, false);
        initializeViews(view);
        return view;
    }

    public void initializeViews(View view) {
        accountPersisit = getActivity().getSharedPreferences(prefName, getActivity().MODE_PRIVATE);
        accntRegEditor = accountPersisit.edit();
        authEmailEdt = view.findViewById(R.id.auth_email);
        // backBtn3 = view.findViewById(R.id.prev_btn3);
        // backBtn3.setOnClickListener(this);
        nextBtn3 = view.findViewById(R.id.next_btn3);
        nextBtn3.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_click));
        nextBtn3.setOnClickListener(this);
        titleRegistration = getActivity().findViewById(R.id.title_reg);
        titleRegistration.setText(getResources().getString(R.string.password_reg_info_text));
        errorAlertmessage = getActivity().findViewById(R.id.error_alert_message);
        errorAlertmessage.setVisibility(View.GONE);
        authEmailEdt.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (authEmailEdt.getRight() - authEmailEdt.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        if (!isShowPassword) {
                            isShowPassword = true;
                            authEmailEdt.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.password_show, 0);
                            authEmailEdt.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                        } else {
                            isShowPassword = false;
                            authEmailEdt.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.password_hide, 0);
                            authEmailEdt.setTransformationMethod(PasswordTransformationMethod.getInstance());
                        }
                        return true;
                    }

                }


                return false;
            }
        });
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
//            case R.id.prev_btn3:
//                Fragment secondFragment = new RegWizardTwo();
//                FragmentTransaction transaction = getFragmentManager().beginTransaction();
//                transaction.replace(R.id.wizard_frag, secondFragment);
//                transaction.addToBackStack(null);
//                transaction.commit();
//                break;
            case R.id.next_btn3:
                errorMessage();
                if (validate() && errorMessage() == "") {
                    errorAlertmessage.setVisibility(View.GONE);

                    persisitUserDetails();
                    Fragment thirdFragment = new RegCompletedWizard();
                    FragmentTransaction thTransaction = getFragmentManager().beginTransaction();
                    thTransaction.replace(R.id.wizard_frag, thirdFragment);
                    thTransaction.addToBackStack(null);
                    thTransaction.commit();
                } else {
                    errorAlertmessage.setVisibility(View.VISIBLE);
                    errorAlertmessage.setText(errorMessage);
                }
                break;
        }
    }

    @Override
    public void onResume() {
        getpersistDetails();
        ImageView progresstracker = getActivity().findViewById(R.id.progress_tracker_img);
        progresstracker.setImageDrawable(getResources().getDrawable(R.drawable.progresstracker_3));
        titleRegistration.setText(getResources().getString(R.string.password_reg_info_text));
        super.onResume();
    }

    public boolean validate() {
        boolean isValid = true;
        if (new CommonFuncArea().validatePassword(authEmailEdt.getText().toString())) {
            authMail = authEmailEdt.getText().toString();

        } else {
            isValid = false;
            authEmailEdt.setBackgroundColor(getResources().getColor(R.color.error_red));
        }

        return isValid;
    }

    public String errorMessage() {
        errorMessage = "";
        if (!new CommonFuncArea().validatePassword(authEmailEdt.getText().toString())) {
            errorMessage = "* "+getString(R.string.password3_hint);
        }
        else
        {
            authEmailEdt.setBackgroundColor(getResources().getColor(R.color.white));
        }
        return errorMessage;
    }

    public void persisitUserDetails() {
        accntRegEditor.putString(authMailKey, authEmailEdt.getText().toString());


        accntRegEditor.commit();
    }

    public void getpersistDetails() {
        authMail = accountPersisit.getString(authMailKey, "");

        authEmailEdt.setText(authMail);


    }
}

