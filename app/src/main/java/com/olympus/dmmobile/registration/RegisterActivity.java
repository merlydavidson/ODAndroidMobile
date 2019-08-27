package com.olympus.dmmobile.registration;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.olympus.dmmobile.R;
import com.olympus.dmmobile.registration.fragments.RegWizardOne;
import com.olympus.dmmobile.registration.fragments.RegWizardTwo;
import com.olympus.dmmobile.registration.fragments.RegistrationCompletedAck;
import com.olympus.dmmobile.registration.interfaces.RegistrationInterface;

public class RegisterActivity extends AppCompatActivity implements RegistrationInterface {
    SharedPreferences accountPersisit;
    String prefName = "AccountRegistration";
    SharedPreferences.Editor accntRegEditor;
    RegistrationCompletedAck registrationCompletedAck;
    SharedPreferences.Editor popUpeditor;
    private SharedPreferences mSharedPref;
    public static final String mPREFERENCES = "Checkbox";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mSharedPref = getSharedPreferences(mPREFERENCES, Context.MODE_PRIVATE);
        popUpeditor = mSharedPref.edit();
        accountPersisit = getSharedPreferences(prefName, MODE_PRIVATE);
        accntRegEditor = accountPersisit.edit();
        Fragment newFragment = new RegWizardOne();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.wizard_frag, newFragment);

        transaction.commit();
    }

    @Override
    protected void onDestroy() {
        popUpeditor.putBoolean("popup", false);
        popUpeditor.commit();
        accntRegEditor.clear();
        accntRegEditor.commit();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        registrationCompletedAck = new RegistrationCompletedAck();
        try {
            registrationCompletedAck = (RegistrationCompletedAck) getFragmentManager().findFragmentByTag("completed");

        } catch (Exception e) {

        }
        if (registrationCompletedAck != null) {
            if (registrationCompletedAck.isVisible()) {

                this.finish();
            }
        }
        if (getFragmentManager().getBackStackEntryCount() == 0) {
            popUpeditor.putBoolean("popup", true);
            popUpeditor.commit();
            this.finish();
        } else {

            getFragmentManager().popBackStack();


        }


    }

    @Override
    public void getResult(Boolean result) {
        if (result) {
            Fragment newFragment = new RegistrationCompletedAck();
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.wizard_frag, newFragment, "completed");
            transaction.commit();
        }
    }

    @Override
    public void getErrorCode(int errorCode) {
        if (errorCode == 4013) {
            Fragment frgone = new RegWizardOne();
            Bundle args = new Bundle();
            args.putInt("errorcode", 4013);
            frgone.setArguments(args);
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.wizard_frag, frgone, "completed");
            transaction.commit();
        } else if (errorCode == 4014) {
            Fragment frgtwo = new RegWizardOne();
            Bundle args = new Bundle();
            args.putInt("errorcode", errorCode);
            frgtwo.setArguments(args);
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.wizard_frag, frgtwo, "completed");
            transaction.commit();
        } else if (errorCode == 4015) {
            Fragment frgtwo = new RegWizardTwo();
            Bundle args = new Bundle();
            args.putInt("errorcode", errorCode);
            frgtwo.setArguments(args);
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.wizard_frag, frgtwo, "completed");
            transaction.commit();
        }

    }
}
