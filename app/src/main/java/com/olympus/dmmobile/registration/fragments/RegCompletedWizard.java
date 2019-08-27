package com.olympus.dmmobile.registration.fragments;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.method.PasswordTransformationMethod;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.olympus.dmmobile.R;
import com.olympus.dmmobile.registration.interfaces.RegistrationInterface;
import com.olympus.dmmobile.settings.TermsOfUseActivity;
import com.olympus.dmmobile.webservice.AccountRegistration;

import java.util.Objects;

public class RegCompletedWizard extends Fragment implements View.OnClickListener, RegistrationInterface {
    TextView confirmName, confirmAccountID, confirmEmailAddress, confirmAccountPassword, confirmAuthorID1, confirmAuthor1EmailAddress, confirmTypistEmailAddress, confirmDictationServicePassword, termLinkText;
    CheckBox checkBoxAgree;
    String nameText, accountIDText, emailAddressText, accountPasswordText, authorID1Text, author1EmailAddressText, typist1EmailAddresstext, dictationServiceText;
    SharedPreferences accountPersisit;
    String prefName = "AccountRegistration";
    SharedPreferences.Editor accntRegEditor;
    String personalNameKey = "personal_name";
    String authorIDKey = "author_ID";
    String passwordkey = "password";
    String emailIdkey = "emailID";
    String authEmailKey = "authEmail";
    String auth_idKey = "authid";
    String dest_accntkey = "destAccount";
    String authMailKey = "authMail3";
    Button editBtn, registerBtn;
    private TextView titleRegistration;
    private ProgressDialog mProgressDialog;
    boolean isClickedTermsOfUse = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.wizard_confirmation, container, false);
        initView(view);
        return view;
    }


    public void initView(View view) {
        accountPersisit = getActivity().getSharedPreferences(prefName, getActivity().MODE_PRIVATE);
        mProgressDialog = new ProgressDialog(getActivity(), R.style.progressDialogStyle);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage(getResources()
                .getString(R.string.Dictate_Loading));
        Objects.requireNonNull(mProgressDialog.getWindow()).setGravity(Gravity.CENTER);

        accntRegEditor = accountPersisit.edit();
        confirmName = view.findViewById(R.id.name_cnm);
        confirmAccountID = view.findViewById(R.id.accntid_cnm);
        confirmEmailAddress = view.findViewById(R.id.emailid_cnm);
        confirmAccountPassword = view.findViewById(R.id.acc_password_cnm);
        confirmAuthorID1 = view.findViewById(R.id.author1_cnm);
        confirmAuthor1EmailAddress = view.findViewById(R.id.authorid1_cnm);
        confirmTypistEmailAddress = view.findViewById(R.id.auth_emailid_cnm);
        confirmDictationServicePassword = view.findViewById(R.id.password_cnm);
        termLinkText = view.findViewById(R.id.term_link_txt);

     String nameTermOfUse = getString(R.string.term_first) + " <a href=''  >" + getResources().getString(R.string.terms_of_use_title) + "</a> " + getString(R.string.term_third);
     //  String nameTermOfUse=getString(R.string.terms_of_use_confirmation);

        //   Log.d("LinkName", "name " + name);


//        int i1 = nameTermOfUse.indexOf("the");
//        int i2 = nameTermOfUse.indexOf("for");
//        termLinkText.setMovementMethod(LinkMovementMethod.getInstance());
//        termLinkText.setText(nameTermOfUse, TextView.BufferType.SPANNABLE);
//        Spannable mySpannable = (Spannable) termLinkText.getText();
//        ClickableSpan myClickableSpan = new ClickableSpan() {
//            @Override
//            public void onClick(View widget) {
//                Intent intent = new Intent(getActivity(), TermsOfUseActivity.class);
//                startActivity(intent);
//            }
//        };
//        mySpannable.setSpan(myClickableSpan, i1, i2 + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);


        termLinkText.setText(Html.fromHtml(nameTermOfUse));

        termLinkText.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                accntRegEditor.putBoolean("termsofuse", true);
                                                accntRegEditor.commit();
                                                isClickedTermsOfUse = true;
                                                 Intent intent = new Intent(getActivity(), TermsOfUseActivity.class);
              startActivity(intent);
                                            }
                                        }
        );

        editBtn = view.findViewById(R.id.edit_btn);
        editBtn.setOnClickListener(this);
        registerBtn = view.findViewById(R.id.reg_btn);
        registerBtn.getBackground().setColorFilter(getResources().getColor(R.color.disabled_btn), PorterDuff.Mode.MULTIPLY);
        registerBtn.setOnClickListener(this);
        checkBoxAgree = view.findViewById(R.id.term_use_chkbx);
        titleRegistration = getActivity().findViewById(R.id.title_reg);
        titleRegistration.setText(getResources().getString(R.string.confirmation_text));
        checkBoxAgree.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    checkBoxAgree.setButtonDrawable(getResources().getDrawable(R.drawable.checked_acc));

                    registerBtn.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_click));
                    registerBtn.setEnabled(true);
                } else {
                    checkBoxAgree.setButtonDrawable(getResources().getDrawable(R.drawable.unchecked));
                    registerBtn.setBackgroundColor(registerBtn.getContext().getResources().getColor(R.color.disabled_btn));
                    // registerBtn.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_click));

                    registerBtn.setEnabled(false);
                }
            }
        });
    }

    @Override
    public void onResume() {
        getpersistDetails();
        //  mProgressDialog = new ProgressDialog(getActivity(), R.style.progressDialogStyle);
        ImageView progresstracker = getActivity().findViewById(R.id.progress_tracker_img);
        progresstracker.setImageDrawable(getResources().getDrawable(R.drawable.progresstracker_4));
        titleRegistration.setText(getResources().getString(R.string.confirmation_text));
        super.onResume();
    }

    public void getpersistDetails() {
        isClickedTermsOfUse = accountPersisit.getBoolean("termsofuse", false);
        nameText = accountPersisit.getString(personalNameKey, "");
        accountIDText = accountPersisit.getString(authorIDKey, "");
        emailAddressText = accountPersisit.getString(emailIdkey, "");
        accountPasswordText = accountPersisit.getString(passwordkey, "");
        authorID1Text = accountPersisit.getString(auth_idKey, "");
        author1EmailAddressText = accountPersisit.getString(authEmailKey, "");
        typist1EmailAddresstext = accountPersisit.getString(dest_accntkey, "");
        dictationServiceText = accountPersisit.getString(authMailKey, "");


        confirmName.setText(nameText);
        confirmAccountID.setText(accountIDText);
        confirmEmailAddress.setText(emailAddressText);
        confirmAccountPassword.setText(accountPasswordText);
        confirmAccountPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
        confirmAuthorID1.setText(authorID1Text);
        confirmAuthor1EmailAddress.setText(author1EmailAddressText);
        confirmTypistEmailAddress.setText(typist1EmailAddresstext);
        confirmDictationServicePassword.setText(dictationServiceText);
        confirmDictationServicePassword.setTransformationMethod(PasswordTransformationMethod.getInstance());

        if (isClickedTermsOfUse) {
            checkBoxAgree.setEnabled(true);
            checkBoxAgree.setButtonDrawable(getResources().getDrawable(R.drawable.unchecked));

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.edit_btn:
                Fragment newFragment = new RegWizardOne();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.wizard_frag, newFragment);

                transaction.commit();

                break;
            case R.id.reg_btn:

                mProgressDialog.show();
                mProgressDialog.getWindow().setLayout(560, 275);
                new AccountRegistration("1.0.0", nameText, accountIDText, accountPasswordText, emailAddressText, authorID1Text, author1EmailAddressText, dictationServiceText, "1", getActivity(), typist1EmailAddresstext, mProgressDialog).execute();

                break;
        }
    }

    @Override
    public void getResult(Boolean result) {

    }

    @Override
    public void getErrorCode(int errorCode) {
        if (errorCode != 4013 || errorCode != 4014 || errorCode != 4015) {
            //  new AccountRegistration("1.0.0", nameText, accountIDText, accountPasswordText, emailAddressText, authorID1Text, author1EmailAddressText, dictationServiceText, "1", getActivity(), mProgressDialog).execute();

        }
    }

//    @Override
//    public void getResult(Boolean result) {
//      //  mProgressDialog.dismiss();
////        if (result) {
//
//
//      //  }
//
//    }


}

