package com.olympus.dmmobile.registration.fragments;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.ContextThemeWrapper;
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

public class RegWizardOne extends Fragment implements View.OnClickListener, View.OnFocusChangeListener {
    EditText userNameEdt, authorIdEdt, passwordEdt, emailEdt;
    TextView errorAlertmessage;
    TextView titleRegistration;
    Button nextOneBtn;
    private AlertDialog.Builder mAlertDialog;
    SharedPreferences accountPersisit;
    String prefName = "AccountRegistration";
    SharedPreferences.Editor accntRegEditor;
    String personalNameKey = "personal_name";
    String authorIDKey = "author_ID";
    String passwordkey = "password";
    String emailIdkey = "emailID";
    String personalName = "";
    String authorID = "";
    String password = "";
    String emailId = "";
    String errorMessage = "";
    View view;
    boolean isShowPassword = false;
    boolean showMessage = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.reg_wizard_one, container, false);
        initializeViews(view);

        return view;
    }

    @Override
    public void onResume() {

        ImageView progresstracker = getActivity().findViewById(R.id.progress_tracker_img);
        titleRegistration.setText(getResources().getString(R.string.reg_info_text));
        progresstracker.setImageDrawable(getResources().getDrawable(R.drawable.progresstracker_1));
        Bundle args = getArguments();
        if (args != null) {
            int index = args.getInt("errorcode", 0);
            if (index == 4013 ) {
                if (!showMessage) {
                    showMessage = true;
                    authorIdEdt.setBackgroundColor(getResources().getColor(R.color.error_red));
                    errorAlertmessage.setVisibility(View.VISIBLE);
                    errorAlertmessage.setText("* "+getString(R.string.accntid_cnm_title));

                }

            }
            if (index == 4014 ) {
                if (!showMessage) {
                    showMessage = true;
                    emailEdt.setBackgroundColor(getResources().getColor(R.color.error_red));
                    errorAlertmessage.setVisibility(View.VISIBLE);
                    errorAlertmessage.setText("* "+getString(R.string.enter_email_hint));

                }

            }

        }
        super.onResume();
    }

    @Override
    public void onPause() {
        errorAlertmessage.setVisibility(View.GONE);
        //errorAlertmessage.setText(errorMessage);
        super.onPause();
    }

    public void initializeViews(View view) {
        accountPersisit = getActivity().getSharedPreferences(prefName, getActivity().MODE_PRIVATE);
        accntRegEditor = accountPersisit.edit();
        userNameEdt = view.findViewById(R.id.per_name_edt);
        userNameEdt.setOnFocusChangeListener(this);
        authorIdEdt = view.findViewById(R.id.accnt_id_edt);
        authorIdEdt.setOnFocusChangeListener(this);
        passwordEdt = view.findViewById(R.id.accnt_password_edt);
        passwordEdt.setOnFocusChangeListener(this);
        emailEdt = view.findViewById(R.id.accnt_email_edt);
        emailEdt.setOnFocusChangeListener(this);
        nextOneBtn = view.findViewById(R.id.next_btn);
        nextOneBtn.setOnClickListener(this);
        nextOneBtn.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_click));
        //nextOneBtn.getBackground().setColorFilter(getResources().getDr(R.color.enabled_btn), PorterDuff.Mode.MULTIPLY);
        ImageView progresstracker = getActivity().findViewById(R.id.progress_tracker_img);
        progresstracker.setImageDrawable(getResources().getDrawable(R.drawable.progresstracker_1));
        errorAlertmessage = getActivity().findViewById(R.id.error_alert_message);
        errorAlertmessage.setVisibility(View.GONE);
        titleRegistration = getActivity().findViewById(R.id.title_reg);
        titleRegistration.setText(getResources().getString(R.string.reg_info_text));
        authorIdEdt.setFilters(new InputFilter[]{
                new InputFilter() {
                    public CharSequence filter(CharSequence src, int start,
                                               int end, Spanned dst, int dstart, int dend) {
                        if (src.equals(" ")) {
                            String underScore = src.toString().replace(" ", "_");
                            return underScore;
                        } else {

                            return src;

                        }


                    }
                }
        });
        passwordEdt.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (passwordEdt.getRight() - passwordEdt.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        if (!isShowPassword) {
                            isShowPassword = true;
                            passwordEdt.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.password_show, 0);
                            passwordEdt.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                        } else {
                            isShowPassword = false;
                            passwordEdt.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.password_hide, 0);
                            passwordEdt.setTransformationMethod(PasswordTransformationMethod.getInstance());
                        }
                        return true;
                    }

                }

                return false;
            }
        });
        getpersistDetails();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.next_btn:
                errorMessage();
                if (validateAccntReg() && (errorMessage() == "")) {
                    errorAlertmessage.setVisibility(View.GONE);
                    persisitAccntRegDetails();
                    Fragment newFragment = new RegWizardTwo();
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.replace(R.id.wizard_frag, newFragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                } else {
                    errorAlertmessage.setVisibility(View.VISIBLE);
                    errorAlertmessage.setText(errorMessage);
                }
                break;
        }
    }

    public boolean validateAccntReg() {
        boolean isComplete = true;
        if (!userNameEdt.getText().toString().equalsIgnoreCase("")) {
            personalName = userNameEdt.getText().toString();
            if (!authorIdEdt.getText().toString().equalsIgnoreCase("") && authorIdEdt.getText().toString().length() < 256) {

                authorID = authorIdEdt.getText().toString();
                if (new CommonFuncArea().validatePassword(passwordEdt.getText().toString())) {
                    password = passwordEdt.getText().toString();
                    if (!emailEdt.getText().toString().equalsIgnoreCase("") && new CommonFuncArea().isValidEmail(emailEdt.getText().toString())) {
                        emailId = emailEdt.getText().toString();

                    } else {
                        isComplete = false;
                        emailEdt.setBackgroundColor(getResources().getColor(R.color.error_red));
                    }
                } else {
                    isComplete = false;
                    passwordEdt.setBackgroundColor(getResources().getColor(R.color.error_red));
                }
            } else {
                isComplete = false;
                authorIdEdt.setBackgroundColor(getResources().getColor(R.color.error_red));
            }
        } else {
            isComplete = false;
            userNameEdt.setBackgroundColor(getResources().getColor(R.color.error_red));
        }
        if (userNameEdt.getText().toString().equalsIgnoreCase(""))
            userNameEdt.setBackgroundColor(getResources().getColor(R.color.error_red));
        if (authorIdEdt.getText().toString().equalsIgnoreCase(""))
            authorIdEdt.setBackgroundColor(getResources().getColor(R.color.error_red));
        if (!new CommonFuncArea().validatePassword(passwordEdt.getText().toString()))
            passwordEdt.setBackgroundColor(getResources().getColor(R.color.error_red));
        if (emailEdt.getText().toString().equalsIgnoreCase(""))
            emailEdt.setBackgroundColor(getResources().getColor(R.color.error_red));

        return isComplete;
    }

    public String errorMessage() {
        errorMessage = "";
        if (userNameEdt.getText().toString().equalsIgnoreCase("")) {

            errorMessage = "* "+getString(R.string.name_hint);
        }
        else
        {
            userNameEdt.setBackgroundColor(getResources().getColor(R.color.white));
        }
        if (authorIdEdt.getText().toString().equalsIgnoreCase("") || authorIdEdt.getText().toString().length() > 256) {
            if (errorMessage == "")
                errorMessage +="* "+getString(R.string.accntid_cnm_title);
            else
                errorMessage += "\n* "+getString(R.string.accntid_cnm_title);
        }
        else
        {
            authorIdEdt.setBackgroundColor(getResources().getColor(R.color.white));
        }
        if (!new CommonFuncArea().validatePassword(passwordEdt.getText().toString())) {
            if (errorMessage == "")
                errorMessage += "* "+getString(R.string.password_hint);
            else
                errorMessage += "\n* " +getString(R.string.password_hint);
        }
        else
        {
            passwordEdt.setBackgroundColor(getResources().getColor(R.color.white));
        }
        if (emailEdt.getText().toString().equalsIgnoreCase("") || !new CommonFuncArea().isValidEmail(emailEdt.getText().toString())) {
            if (errorMessage == "")
                errorMessage +="* "+getString(R.string.enter_email_hint);
            else
                errorMessage +="\n* "+getString(R.string.enter_email_hint);
        }
        else
        {
            emailEdt.setBackgroundColor(getResources().getColor(R.color.white));
        }
        return errorMessage;
    }

    public void showMessageAlert1(String Message, String Alert) {
        mAlertDialog = new AlertDialog.Builder(new ContextThemeWrapper(getActivity(), R.style.prefScreen));
        mAlertDialog.setTitle(Alert);
        mAlertDialog.setMessage(Message);
        mAlertDialog.setPositiveButton(getResources().getString(R.string.Dictate_Alert_Ok), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                userNameEdt.clearFocus();
                authorIdEdt.clearFocus();
                passwordEdt.clearFocus();
                emailEdt.clearFocus();
            }
        });
        mAlertDialog.show();
    }

    public void persisitAccntRegDetails() {
        accntRegEditor.putString(personalNameKey, userNameEdt.getText().toString());
        accntRegEditor.putString(authorIDKey, authorIdEdt.getText().toString());
        accntRegEditor.putString(passwordkey, passwordEdt.getText().toString());
        accntRegEditor.putString(emailIdkey, emailEdt.getText().toString());
        accntRegEditor.commit();
    }

    public void getpersistDetails() {
        personalName = accountPersisit.getString(personalNameKey, "");
        authorID = accountPersisit.getString(authorIDKey, "");
        password = accountPersisit.getString(passwordkey, "");
        emailId = accountPersisit.getString(emailIdkey, "");
        userNameEdt.setText(personalName);
        authorIdEdt.setText(authorID);
        passwordEdt.setText(password);
        emailEdt.setText(emailId);
    }


    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        switch (v.getId()) {
            case R.id.per_name_edt:
//                if (!userNameEdt.getText().toString().equalsIgnoreCase(""))
//
//                    userNameEdt.setBackgroundColor(getResources().getColor(R.color.white));


                if (authorIdEdt.getText().toString().equalsIgnoreCase(""))
                    authorIdEdt.setText(userNameEdt.getText().toString().replace(" ","_"));
                break;
            case R.id.accnt_id_edt:
                if (authorIdEdt.getText().toString().equalsIgnoreCase(""))
                    authorIdEdt.setText(userNameEdt.getText().toString().replace(" ","_"));
//                if (!authorIdEdt.getText().toString().equalsIgnoreCase(""))
//                    authorIdEdt.setBackgroundColor(getResources().getColor(R.color.white));
                //  authorIdEdt.setText(userNameEdt.getText().toString().replace(" ", "_"));
                break;
            case R.id.accnt_password_edt:
                if (authorIdEdt.getText().toString().equalsIgnoreCase(""))
                    authorIdEdt.setText(userNameEdt.getText().toString().replace(" ","_"));
//                if (!passwordEdt.getText().toString().equalsIgnoreCase(""))
//                    passwordEdt.setBackgroundColor(getResources().getColor(R.color.white));
                break;
            case R.id.accnt_email_edt:
                if (authorIdEdt.getText().toString().equalsIgnoreCase(""))
                    authorIdEdt.setText(userNameEdt.getText().toString().replace(" ","_"));
//                if (!emailEdt.getText().toString().equalsIgnoreCase(""))
//                    emailEdt.setBackgroundColor(getResources().getColor(R.color.white));
                break;
        }
    }

}
