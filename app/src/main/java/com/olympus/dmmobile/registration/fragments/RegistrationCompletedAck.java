package com.olympus.dmmobile.registration.fragments;

import android.app.Fragment;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.olympus.dmmobile.R;

import static android.text.Layout.JUSTIFICATION_MODE_INTER_WORD;

public class RegistrationCompletedAck extends Fragment {
    View view;
    TextView bodyAboutTrial, bodyAnnualTrial;
    Button completeBtn;
    private TextView titleRegistration;
    LinearLayout prgs_tracker_layout;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.reg_completed_ack, container, false);
        initializeViews(view);
        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void initializeViews(View view) {
        try {
            bodyAboutTrial = view.findViewById(R.id.txt_abt_trl);
            bodyAnnualTrial = view.findViewById(R.id.txt_annl_lcns);
            bodyAboutTrial.setText(Html.fromHtml(getResources().getString(R.string.body_about_trial)));
            if (android.os.Build.VERSION.SDK_INT >25)
                bodyAboutTrial.setJustificationMode(JUSTIFICATION_MODE_INTER_WORD);
            bodyAnnualTrial.setText(Html.fromHtml(getResources().getString(R.string.body_annual_licens)));
            if (android.os.Build.VERSION.SDK_INT >25)
                bodyAnnualTrial.setJustificationMode(JUSTIFICATION_MODE_INTER_WORD);
            completeBtn = view.findViewById(R.id.btn_complete);
            completeBtn.setBackgroundDrawable(getResources().getDrawable(R.drawable.button_click));
            completeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getActivity().finish();
                }
            });
        } catch (Exception e) {

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        prgs_tracker_layout = getActivity().findViewById(R.id.rep_layout);
        prgs_tracker_layout.setVisibility(View.GONE);
        titleRegistration = getActivity().findViewById(R.id.title_reg);
        titleRegistration.setText(getResources().getString(R.string.reg_Completed_title));

    }


}

