package com.olympus.dmmobile.settings;


import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.olympus.dmmobile.DMApplication;
import com.olympus.dmmobile.R;
import com.olympus.dmmobile.log.ExceptionReporter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

/**
 * Class to add and delete worktypes for email options and server options
 *
 * @version 1.0.1
 */
public class

WorktypeActivity extends AppCompatActivity {

    private ExceptionReporter mReporter; // Error Logger

    private ListView mListView;
    private ArrayList<String> mListValues;
    private ArrayList<String> mCheckvalues;
    private WorktypeCustomAdapter mAdapter;
    private String mNewWorktypeVal;
    private static final int SWIPE_MIN_DISTANCE = 60;
    private static final int SWIPE_MAX_OFF_PATH = 250;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;
    private String mServerworktype;
    private String mMailworktype;
    private String mCheckServermail;
    private GestureDetector gestureDetector;
    private View.OnTouchListener gestureListener;
    private AlertDialog.Builder mAlertDialog;
    private Locale mLocale;
    private int flag = 0;
    private SharedPreferences.Editor editor;
    private ArrayList<String> WorktypeList;
    private int tempPos = -1;
    private AlertDialog.Builder mAlertBuilder;
    private Button delete;
    private DMApplication dmApplication = null;
    private int showFlag = 0;
    private EditText edit;
    private String[] mCheckWorktype;
    private boolean isKeyboardShown = false;
    FloatingActionButton floatingActionButton;
    private String mSettingsConfig;
    private SharedPreferences pref;
    private String mActivation;
    public static final String PREFS_NAME = "Config";
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        mReporter = ExceptionReporter.register(this);
        super.onCreate(savedInstanceState);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        setContentView(R.layout.custom_list_pref);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setTitle(getResources().getString(
                R.string.worktype_list_title));
        floatingActionButton = (FloatingActionButton) findViewById(R.id.fab_work);
        setTitle(getResources().getString(R.string.worktype_list_title));
        dmApplication = (DMApplication) getApplication();
        dmApplication.setContext(this);
        dmApplication.worktypeSwiped = null;
        setCurrentLanguage(dmApplication.getCurrentLanguage());
        getSendStatus();
        getWorktype();
        mListView = (ListView) findViewById(R.id.custom_list_pref);
        mListView.setSelector(android.R.color.transparent);
        mListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        pref = WorktypeActivity.this.getSharedPreferences(PREFS_NAME, 0);


        mSettingsConfig = pref.getString("Activation", mActivation);
        if (mSettingsConfig != null &&!mSettingsConfig.equalsIgnoreCase("Not Activated") ) {
            //    if (mCheckServermail.equalsIgnoreCase("2")) {
            floatingActionButton.setVisibility(View.GONE);
            if (WorktypeList != null) {

                mAdapter = new WorktypeCustomAdapter(this, WorktypeList);
            }
        } else {
            floatingActionButton.setVisibility(View.VISIBLE);
            initValues();
        }
//        } else if (mCheckServermail.equalsIgnoreCase("1")) {

//        }
        mListView.setAdapter(mAdapter);
        gestureDetector = new GestureDetector(new CustomGestureDetector());
        gestureListener = new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        };

        //  if (!mCheckServermail.equalsIgnoreCase("2")) {
        mListView.setOnTouchListener(gestureListener);
        // }

        final View activityRootView = findViewById(R.id.lin_worktype);

        activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(
                new OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        Rect r = new Rect();
                        // r will be populated with the coordinates of your view
                        // that area still visible.
                        activityRootView.getWindowVisibleDisplayFrame(r);

                        int heightDiff = activityRootView.getRootView()
                                .getHeight() - (r.bottom - r.top);
                        if (heightDiff > 100) { // if more than 100 pixels, its
                            // probably a keyboard...
                            isKeyboardShown = true;
                        } else {
                            if (isKeyboardShown) {
                                //mEditSearch.clearFocus();
                                isKeyboardShown = false;
                            }
                        }
                    }
                });

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (delete != null && delete.getVisibility() == View.VISIBLE)
                    delete.setVisibility(View.INVISIBLE);
                showAddWorktypeAlert();
                dmApplication.worktypeSwiped = "";
            }
        });
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //    if (!mCheckServermail.equalsIgnoreCase("2")) {
        MenuInflater mMenuInflater = getMenuInflater();
        mMenuInflater.inflate(R.menu.worktype_menu, menu);
        dmApplication.worktypeSwiped = null;

        //   }
        return true;

    }

    @Override
    protected void onResume() {
        super.onResume();
        setCurrentLanguage(dmApplication.getCurrentLanguage());
        //System.out.println("showFlag----"+showFlag);
        if (showFlag == 1) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            //imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
            imm.showSoftInput(edit, InputMethodManager.SHOW_FORCED);
        }

    }

    /**
     * Overriding method to show option menu for add worktype
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // if (!mCheckServermail.equalsIgnoreCase("2")) {

        switch (item.getItemId()) {
            case R.id.add_worktype:
                if (delete != null && delete.getVisibility() == View.VISIBLE)
                    delete.setVisibility(View.INVISIBLE);
                showAddWorktypeAlert();
                dmApplication.worktypeSwiped = "";
                break;
        }
        //  }
        return super.onOptionsItemSelected(item);
    }

    /**
     * method to show add worktype dialog
     */
    public void showAddWorktypeAlert() {
        LayoutInflater inflater = (LayoutInflater) this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mAlertBuilder = new AlertDialog.Builder(this);
        View layout = inflater.inflate(R.layout.add_worktype_dialog,
                (ViewGroup) this.findViewById(R.id.relAddWorktype));
//		mAlertBuilder.setTitle(getResources().getString(
//				R.string.Settings_Add_Worktype));
        // mAlertBuilder.setCancelable(false);
        edit = (EditText) layout
                .findViewById(R.id.edt_addworktype);
        edit.setImeOptions(EditorInfo.IME_ACTION_DONE);
        edit.requestFocus();
        int maxLength = 16;
        InputFilter[] fArray = new InputFilter[1];
        fArray[0] = new InputFilter.LengthFilter(maxLength);
        edit.setFilters(fArray);
        Button btnAdd1 = (Button) layout.findViewById(R.id.btnAdd1);

        Button btnAdd2 = (Button) layout.findViewById(R.id.btnAdd2);
        //   final AlertDialog dialog = mAlertBuilder.create();
        mAlertBuilder.setView(layout);
        final AlertDialog alertDialog = mAlertBuilder.create();
        btnAdd1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mNewWorktypeVal = edit.getText().toString();
                if (!mNewWorktypeVal.equals("")) {
                    checkWorktype();
                    if (flag == 1) {
                        setEmailWorktype();

                    } else
                        showMessageAlert(getResources().getString(
                                R.string.Alert), getResources().getString(
                                R.string.worktypeexist));
                    //showFlag=1;
                } else {
                    showMessageAlert(getResources().getString(
                            R.string.Alert), getResources().getString(
                            R.string.Settings_Enter_Worktype));
                    //showFlag=1;
                }
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(edit.getWindowToken(), 0);
                alertDialog.dismiss();
            }
        });

        btnAdd2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(edit.getWindowToken(), 0);
                showFlag = 0;
                alertDialog.dismiss();

            }
        });

        alertDialog.show();

        showFlag = 1;
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    /**
     * set Email worktypes to the list
     */
    public void initValues() {
        mListValues = new ArrayList<String>();
        SharedPreferences sharedPref = PreferenceManager
                .getDefaultSharedPreferences(this);
        mMailworktype = sharedPref.getString(
                getResources().getString(R.string.Worktype_Email_Key), "");
        if (mMailworktype.contains(":")) {
            mListValues = new ArrayList<String>(Arrays.asList(mMailworktype
                    .split(":")));
        } else if (mMailworktype.equalsIgnoreCase("")) {
            mListValues = null;
        } else if (!mMailworktype.equalsIgnoreCase("")
                && !mMailworktype.contains(":")) {
            mListValues.add(mMailworktype);
        }
        if (mListValues != null) {
            mAdapter = new WorktypeCustomAdapter(this, mListValues);
        } else {
            mAdapter = new WorktypeCustomAdapter(this, new ArrayList<String>());
        }

    }

    private int previousPostion = -1;

    class CustomGestureDetector extends SimpleOnGestureListener {

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                               float velocityY) {

            try {
                if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
                    return false;
                // right to left swipe
                if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE
                        && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {

                } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
                        && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    int x = Math.round(e1.getX());
                    int y = Math.round(e1.getY());
                    if (tempPos < mListView.getCount()) {
                        delete = (Button) mListView.findViewWithTag(mListView.getItemAtPosition(tempPos));
                        //	delete.setBackgroundColor(getResources().getColor(R.color.fab_color));

                        if (delete != null) {
                            if (delete.getVisibility() == View.VISIBLE)
                                delete.setVisibility(View.INVISIBLE);
                        }
                    }
                    tempPos = mListView.pointToPosition(x, y);
                    delete = (Button) mListView.findViewWithTag(mListView.getItemAtPosition(tempPos));
                    if (delete != null) {
                        if (delete.getVisibility() == View.INVISIBLE)
                            delete.setVisibility(View.VISIBLE);
                        else if (delete.getVisibility() == View.VISIBLE)
                            delete.setVisibility(View.INVISIBLE);
                    }
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }

    }

    /**
     * set server worktypes into list
     */
    public void getWorktype() {
        SharedPreferences sharedPref = PreferenceManager
                .getDefaultSharedPreferences(this);

        mServerworktype = sharedPref.getString(
                getResources().getString(R.string.Worktype_Server_key), "");

        if (TextUtils.isEmpty(mServerworktype)) {
            WorktypeList = null;
            //floatingActionButton.setVisibility(View.GONE);
        } else {
            //	floatingActionButton.setVisibility(View.VISIBLE);
            WorktypeList = new ArrayList<String>(Arrays.asList(mServerworktype
                    .split(":")));
        }
		
		/*if (mServerworktype.contains(":")) {
			WorktypeList = new ArrayList<String>(Arrays.asList(mServerworktype
					.split(":")));
		} else if (mServerworktype.equalsIgnoreCase("")) {
			WorktypeList = null;
		} else if (!mServerworktype.equalsIgnoreCase("")
				&& !mServerworktype.contains(":")) {
			WorktypeList.add(mServerworktype);
		}*/

    }

    /**
     * to check the status whether it is server or email is used
     */
    public void getSendStatus() {
        SharedPreferences sharedPref = PreferenceManager
                .getDefaultSharedPreferences(this);
//        mCheckServermail = sharedPref.getString(
//                getResources().getString(R.string.send_key), "");
    }

    /**
     * Add email worktypes to shared preference
     */
    public void setEmailWorktype() {
        mListValues = new ArrayList<String>();
        SharedPreferences sharedPref = PreferenceManager
                .getDefaultSharedPreferences(this);
        mMailworktype = sharedPref.getString(
                getResources().getString(R.string.Worktype_Email_Key), "");

        if (mMailworktype.equalsIgnoreCase("")) {
            editor = sharedPref.edit();
            editor.putString(
                    getResources().getString(R.string.Worktype_Email_Key),
                    mNewWorktypeVal.toUpperCase());
            editor.commit();
        } else {
            editor = sharedPref.edit();
            String work = mMailworktype + ":" + mNewWorktypeVal.toUpperCase();
            String[] Workarray = work.split(":");
            Arrays.sort(Workarray);
            String MailWorktype = null;
            for (int i = 0; i < Workarray.length; i++) {
                if (MailWorktype == null) {
                    MailWorktype = changeCaps(Workarray[i]);
                } else {
                    MailWorktype = MailWorktype + ":" + changeCaps(Workarray[i]);
                }
            }
            editor.putString(
                    getResources().getString(R.string.Worktype_Email_Key),
                    MailWorktype);
            editor.commit();
        }
        mMailworktype = sharedPref.getString(
                getResources().getString(R.string.Worktype_Email_Key), "");
        if (mMailworktype.contains(":")) {
            mListValues = new ArrayList<String>(Arrays.asList(mMailworktype
                    .split(":")));
        } else if (mMailworktype.equalsIgnoreCase("")) {
            mListValues = null;
        } else if (!mMailworktype.equalsIgnoreCase("")
                && !mMailworktype.contains(":")) {
            mListValues.add(mMailworktype);
        }
        if (mListValues.size() > 0)
            mAdapter.setList(mListValues);

        mAdapter.notifyDataSetChanged();
    }

    /**
     * method to check whether worktype is already present in the list
     */
    public void checkWorktype() {
        SharedPreferences sharedPref = PreferenceManager
                .getDefaultSharedPreferences(this);
        mMailworktype = sharedPref.getString(
                getResources().getString(R.string.Worktype_Email_Key), "");
        mCheckvalues = new ArrayList<String>();
        if (mMailworktype.contains(":")) {
            mCheckWorktype = mMailworktype.split(":");
            for (int i = 0; i < mCheckWorktype.length; i++) {
                if (mNewWorktypeVal.equalsIgnoreCase(mCheckWorktype[i])) {
                    flag = 0;
                    break;
                } else {
                    flag = 1;
                    continue;
                }
            }

        } else if (!mMailworktype.contains(":")
                && !mMailworktype.equalsIgnoreCase("")) {
            if (mMailworktype.equalsIgnoreCase(mNewWorktypeVal))
                flag = 0;
            else
                flag = 1;
        } else if (mMailworktype.equalsIgnoreCase("")) {
            flag = 1;
        }
    }

    /**
     * method to find the locale from sharedpreference
     *
     * @param value is the value in shared preference used to get locale
     */
    public void setCurrentLanguage(int value) {
        int Val = 1;
        Val = value;
        if (Val == 1)
            setLocale("en");
        else if (Val == 2)
            setLocale("de");
        else if (Val == 3)
            setLocale("fr");
        else if (Val == 4)
            setLocale("es");
        else if (Val == 5)
            setLocale("sv");
        else if (Val == 6)
            setLocale("cs");
    }

    /**
     * method used to set the language for the current activity
     *
     * @param lang is the locale get from sharedpreference
     */
    public void setLocale(String lang) {

        mLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = mLocale;
        res.updateConfiguration(conf, dm);

    }

    /**
     * method used to unbind all the widgets from layout
     *
     * @param view widgets used in layout
     */
    private void unbindDrawables(View view) {
        if (view.getBackground() != null) {
            view.getBackground().setCallback(null);
        }
        if (view instanceof ViewGroup && !(view instanceof AdapterView)) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                unbindDrawables(((ViewGroup) view).getChildAt(i));
            }
            ((ViewGroup) view).removeAllViews();
        }
    }

    /**
     * overriding function called on destroying the activity
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();

        unbindDrawables(findViewById(R.id.custom_list_pref));

        System.gc();
    }

    /**
     * method to show alert message
     *
     * @param title   is the title used for alert
     * @param Message is the message given for alert
     */
    public void showMessageAlert(String title, String Message) {
        //  mAlertDialog = new AlertDialog.Builder(WorktypeActivity.this);
        LayoutInflater inflater = (LayoutInflater) this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        AlertDialog.Builder alertDialogB = new AlertDialog.Builder(this);
        View layout = inflater.inflate(R.layout.alert_error_message,
                (ViewGroup) this.findViewById(R.id.alert_err_lot));
        //    mAlertDialog.setTitle(title);
        // mAlertDialog.setMessage(Message);
        TextView textView = (TextView) layout.findViewById(R.id.txt_error_Msg);
        TextView textViewtitle = (TextView) layout.findViewById(R.id.txt_err_title);
        Button button = (Button) layout.findViewById(R.id.btn_err);
        alertDialogB.setView(layout);
        textView.setText(Message);
        textViewtitle.setText(title);
        final AlertDialog alertDialog = alertDialogB.create();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddWorktypeAlert();
                alertDialog.dismiss();
            }
        });
        alertDialog.show();

    }

    /**
     * method to convert worktype to capital letter
     *
     * @param Worktype is the worktype that converted to capital letter
     * @return processed String
     */
    public String changeCaps(String Worktype) {
        StringBuilder sb = new StringBuilder(Worktype);
        for (int index = 0; index < sb.length(); index++) {
            char c = sb.charAt(index);
            if (Character.isLowerCase(c)) {
                sb.setCharAt(index, Character.toUpperCase(c));
            }
        }
        return sb.toString();
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        super.onBackPressed();
        if (showFlag == 1)
            showFlag = 0;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isKeyboardShown) {
            try {
                InputMethodManager imm = (InputMethodManager) getSystemService(
                        Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(edit.getWindowToken(), 0);
            } catch (NullPointerException e) {
                System.out.print("Null pointerException " + e.toString());
            }

        }
    }
}
