package com.example.bhavya.places.ui.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;

import com.example.bhavya.places.pojoclass.userdetailspojoclass.UserDetails;
import com.example.bhavya.places.R;
import com.example.bhavya.places.validationclass.ValidationClass;
import com.google.gson.Gson;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;

import java.util.Random;

import static com.example.bhavya.places.ui.activity.SignUpActivity.PREF_FILE_NAME;

/**
 * Created by bhavya on 9/8/16.
 *
 * When user enters an Email ID, a random alphanumeric passcode is created and saved into shared
 * preference for the corresponding email ID key.
 */
public class ForgotPasswordActivity extends AppCompatActivity {

    private String mEmail;
    private String mFirstname;
    private String mLastname;
    private String mPhone;
    private String mPassword;
    private String mPasscodeGenerated;

    private SharedPreferences mPrefs;
    private SharedPreferences.Editor mprefsEditor;

    private Toolbar mToolbar;
    private TextInputLayout mInputLayoutEmail;
    private TextInputEditText mEditTextEmail;
    private Button mBtnResetPswd;
    private View mNotificationView;
    private final String ALPHANUMERICARRAY =
            "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private Random random = new Random();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        setCustomActionBar();
        setBackArrowActionBar();
        setViewProperties();
    }

    View.OnClickListener ResetPswdBtnClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mInputLayoutEmail.setError(null);
            mNotificationView = v;
            final String email = mEditTextEmail.getText().toString();
            if (!ValidationClass.isValidEmail(email)) {
                mInputLayoutEmail.setError(getString(R.string.invalidEmail));
            } else if (checkIfEmailAlreadyExists()) {
                passwordReset();
            } else {
                emailDoesnotExist();
            }
        }
    };

    TextWatcher enableDisableResetPswdBtnTW = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (s.length() > 0) {
                mBtnResetPswd.setEnabled(true);
            } else {
                mBtnResetPswd.setEnabled(false);
            }
        }
    };

    /**
     * Customizing toolbar with no title
     */
    private void setCustomActionBar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        // to refer the toolbar
        setSupportActionBar(mToolbar);
        //to eliminate the app name from the toolbar
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mToolbar.setTitle("");
        mToolbar.setSubtitle("");
    }

    /**
     * Set back arrow on toolbar
     */
    private void setBackArrowActionBar() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    /**
     * Initializing view properties and adding button clicks and text change listeners
     */
    private void setViewProperties() {
        mBtnResetPswd = (Button) findViewById(R.id.btn_reset_password);
        mInputLayoutEmail = (TextInputLayout) findViewById(R.id.input_layout_email);
        mEditTextEmail = (TextInputEditText) findViewById(R.id.edittext_input_email);

        mBtnResetPswd.setOnClickListener(ResetPswdBtnClicked);

        mEditTextEmail.addTextChangedListener(enableDisableResetPswdBtnTW);
    }

    /**
     * If Email id exists,then reset password
     */
    private void passwordReset() {
        final Intent intent = new Intent(getBaseContext(), LoginActivity.class);
        //Alert Dialogue Box
        AlertDialog.Builder builder = new AlertDialog.Builder(ForgotPasswordActivity.this);
        builder.setMessage(getString(R.string.resetPasswordConfirmation))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mPasscodeGenerated = generatePasscode();
                        getDataFromSharedPref();
                        writeDataIntoSharedPref();
                        createNotification(mNotificationView);
                        finish();
                        startActivity(intent);
                    }
                })
                .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener
                        () {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        finish();
                        startActivity(intent);
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    /**
     * Alert dialogue box appears when entered email id does not exist
     */
    private void emailDoesnotExist() {
        //Alert Dialogue Box
        AlertDialog.Builder builder = new AlertDialog.Builder(ForgotPasswordActivity.this);
        builder.setMessage(getString(R.string.emailIDDoesNotExist))
                .setCancelable(false)
                .setNegativeButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    /**
     * check if email exists
     */
    private boolean checkIfEmailAlreadyExists() {
        mPrefs = getSharedPreferences(PREF_FILE_NAME, MODE_PRIVATE);
        boolean hasEmail = mPrefs.contains(mEditTextEmail.getText().toString());
        if (hasEmail)
            return true;
        else
            return false;
    }

    /**
     * To create a local push Notification to show the passcode generated
     */
    private void createNotification(View v) {
        // Prepare intent which is triggered, if the notification is selected
        Intent intent = new Intent(this, ResetPasswordActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, 0);
        // Build notification
        Notification noti = new Notification.Builder(this)
                .setContentTitle(getString(R.string.passcode)+ mPasscodeGenerated)
                .setContentText(getString(R.string.notificationMsg)).setSmallIcon(R.drawable
                        .ic_launcher)
                .setContentIntent(pIntent)
                .build();
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // hide the notification after its selected
        noti.flags = Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(0, noti);
    }

    /**
     * To generate a random alpha numeric passcode for resetting password
     */
    private String generatePasscode() {
        StringBuilder stringbuilder = new StringBuilder(5);
        for (int i = 0; i < 5; i++) {
            stringbuilder.append(ALPHANUMERICARRAY.charAt(random.nextInt(ALPHANUMERICARRAY.length())));
        }
        return stringbuilder.toString();
    }

    /**
     * Retrieve the stored details of the user corresponding to the entered email id
     */
    private void getDataFromSharedPref() {

        mPrefs = getSharedPreferences(PREF_FILE_NAME, MODE_PRIVATE);
        String obj = mPrefs.getString(mEditTextEmail.getText().toString(), "");
        Gson gson = new Gson();
        UserDetails newUser = gson.fromJson(obj, UserDetails.class);
        mEmail = newUser.getEmail();
        mFirstname = newUser.getFirstname();
        mLastname = newUser.getLastname();
        mPhone = newUser.getPhone();
        mPassword = newUser.getPassword();
    }

    /**
     * Store the passcode that has been generated into shared preferences
     */
    public void writeDataIntoSharedPref() {
        mPrefs = getSharedPreferences(PREF_FILE_NAME, MODE_PRIVATE);
        String obj = mPrefs.getString(mEditTextEmail.getText().toString(), "");
        Gson gson = new Gson();
        UserDetails newUser = new UserDetails();
        newUser.setFirstname(mFirstname);
        newUser.setLastname(mLastname);
        newUser.setPhone(mPhone);
        newUser.setEmail(mEmail);
        newUser.setPassword(mPassword);
        newUser.setPasscode(mPasscodeGenerated);

        // to update the details into shared preference
        mprefsEditor = mPrefs.edit();
        String jsonNewUserObj = gson.toJson(newUser);
        mprefsEditor.putString(mEmail, jsonNewUserObj);
        mprefsEditor.commit();
    }

}
