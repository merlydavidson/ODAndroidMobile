package com.olympus.dmmobile.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.olympus.dmmobile.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommonFuncArea {
    Context context;
    String language="";

    public CommonFuncArea(Context context) {
        this.context = context;
    }

    public CommonFuncArea() {
    }


    public boolean isValidEmail(String email) {
        boolean isValidEmail = false;
        String emailExpression = "^(?!.*\\.{2})[A-Z0-9a-z._%+-]+@[A-Za-z0-9]+[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}";
        //String emailExpression = "^(?!.*\\.{2})[A-Z0-9a-z._%+-]+@[A-Za-z0-9]+[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}";
        //String emailExpression = "^[A-Z0-9a-z_+-]+([&%]?+\\.[A-Z0-9a-z_-]+)?@[A-Za-z0-9]+[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}$";
        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(emailExpression,
                Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            isValidEmail = true;
        }
        return isValidEmail;
    }

    public boolean validatePassword(final String password) {
        Pattern pattern;
        Matcher matcher;
        String PASSWORD_PATTERN =
                "((?=.*\\d)(?=.*[A-Za-z])(?=.*[@#$!%]).{8,25})";
        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);
        return matcher.matches();

    }
    public String getUrl() {
        String url = "";
        String language = getlanguage();
        if(language.equalsIgnoreCase("en"))
            url="https://cs.olympus-imaging.jp/en/support/imsg/digicamera/download/policy/olympusdictation/eulaen.cfm";
        else if(language.equalsIgnoreCase("de"))
            url="https://cs.olympus-imaging.jp/en/support/imsg/digicamera/download/policy/olympusdictation/eulade.cfm";

        else if(language.equalsIgnoreCase("fr"))
            url="https://cs.olympus-imaging.jp/en/support/imsg/digicamera/download/policy/olympusdictation/eulafr.cfm";
        else if(language.equalsIgnoreCase("es"))
            url="https://cs.olympus-imaging.jp/en/support/imsg/digicamera/download/policy/olympusdictation/eulaes.cfm";
        else if(language.equalsIgnoreCase("sv"))
            url="https://cs.olympus-imaging.jp/en/support/imsg/digicamera/download/policy/olympusdictation/eulasv.cfm";
        else if(language.equalsIgnoreCase("cs"))
            url="https://cs.olympus-imaging.jp/en/support/imsg/digicamera/download/policy/olympusdictation/eulacs.cfm";


        return url;

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
}
