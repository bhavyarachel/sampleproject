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
import android.widget.TextView;

import com.example.bhavya.places.sharedpreferences.LoggedInUserSharedPreference;
import com.example.bhavya.places.pojoclass.userdetailspojoclass.UserDetails;
import com.example.bhavya.places.R;
import com.example.bhavya.places.validationclass.ValidationClass;
import com.google.gson.Gson;

import static com.example.bhavya.places.ui.activity.SignUpActivity.PREF_FILE_NAME;
import static com.example.bhavya.places.ui.activity.SignUpActivity.PREF_LOGIN;

/**
 * Created by bhavya on 9/8/16.
 *
 * For Login, user can enter the Email ID and Password. If the Email ID & corresponding password
 * exists in shared preference, user can log in, else log in not successful.
 * Forgot Password link is for resetting a password for an email ID.
 * If user is new to Places, Sign Up.
 */

public class LoginActivity extends AppCompatActivity {

    private SharedPreferences mSharedPreferences;
    private SharedPreferences mPrefs;
    private SharedPreferences.Editor mEditor;

    private Toolbar mToolbar;
    private TextInputLayout mInputLayoutPassword;
    private TextInputLayout mInputLayoutEmail;
    private TextInputEditText mEditTextInputEmail;
    private TextInputEditText mEditTextInputPassword;
    private TextView mForgotPassword;
    private Button mBtnLogin;
    private Button mBtnSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        setCustomActionBar();
        setViewProperties();
    }

    View.OnClickListener Onclick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_login:
                    if (isValidInputs() && getDataFromPreferences()) {
                        setUserLoginStatus();
                    } else if (!checkIfEmailAlreadyExists()) {
                        emailDoesnotExist();
                    } else if (!(checkIfPasswordExist() && checkIfEmailAlreadyExists())) {
                        emailAndPasswordDoesnotMatch();
                    }
                    break;
                case R.id.btn_signup:
                    final Intent i = new Intent(getBaseContext(), SignUpActivity.class);
                    startActivity(i);
                    break;
                case R.id.forgot_password:
                    Intent intent = new Intent(getBaseContext(), ForgotPasswordActivity.class);
                    startActivity(intent);
                    break;
            }
        }
    };

    /**
     * if username and password is empty then set login as disabled
     */
    TextWatcher enableDisableLoginTW = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (s.length() > 0) {
                mBtnLogin.setEnabled(true);
            } else {
                mBtnLogin.setEnabled(false);
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
     * Initializing view properties and adding text change listeners and button, text clicks
     */
    private void setViewProperties() {
        // Assigning View References
        mBtnLogin = (Button) findViewById(R.id.btn_login);
        mEditTextInputEmail = (TextInputEditText) findViewById(R.id.edittext_input_email);
        mEditTextInputPassword = (TextInputEditText) findViewById(R.id.edittext_input_password);
        mInputLayoutPassword = (TextInputLayout) findViewById(R.id.input_layout_password);
        mInputLayoutEmail = (TextInputLayout) findViewById(R.id.input_layout_email);
        mBtnSignUp = (Button) findViewById(R.id.btn_signup);
        mForgotPassword = (TextView) findViewById(R.id.forgot_password);

        // Adding Text change listeners
        mEditTextInputEmail.addTextChangedListener(enableDisableLoginTW);
        mEditTextInputPassword.addTextChangedListener(enableDisableLoginTW);

        // Adding button Clicks
        mBtnLogin.setOnClickListener(Onclick);
        mBtnSignUp.setOnClickListener(Onclick);

        //Adding textView clicks
        mForgotPassword.setOnClickListener(Onclick);

    }

    /**
     * On successful login, set the user as logged in in shared preferences until logout
     */
    private void setUserLoginStatus() {
        mSharedPreferences = getSharedPreferences(PREF_FILE_NAME, MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();
        mEditor.putString(PREF_LOGIN, mEditTextInputEmail.getText().toString());
        mEditor.commit();
        LoggedInUserSharedPreference.setUserName(getBaseContext(), PREF_FILE_NAME);
        Intent i = new Intent(getBaseContext(), DrawerActivity.class);
        startActivity(i);
    }

    /**
     * show alert dialogue box when email id does not exist and exit
     */
    private void emailDoesnotExist() {
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setMessage(getString(R.string.emailIDDoesNotExist))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                })
                .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        finish();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    /**
     * show alert box when email id and password does not match
     */
    private void emailAndPasswordDoesnotMatch() {
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setMessage(getString(R.string.emailPswdNotMatching))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                })
                .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        finish();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();

    }

    /**
     * to get saved information of the user based on email id as key
     */
    private boolean getDataFromPreferences() {
        if (checkIfEmailAlreadyExists()) {
            mPrefs = getSharedPreferences(PREF_FILE_NAME, MODE_PRIVATE);
            String obj = mPrefs.getString(mEditTextInputEmail.getText().toString(), "");
            Gson gson = new Gson();
            UserDetails newuser = gson.fromJson(obj, UserDetails.class);
            if (newuser.getEmail().equals(mEditTextInputEmail.getText().toString()) &&
                    newuser.getPassword().equals(mEditTextInputPassword.getText().toString()))
                return true;
        }

        return false;
    }

    /**
     * check if email exists
     */
    private boolean checkIfEmailAlreadyExists() {
        mPrefs = getSharedPreferences(PREF_FILE_NAME, MODE_PRIVATE);
        boolean hasEmail = mPrefs.contains(mEditTextInputEmail.getText().toString());
        if (hasEmail)
            return true;
        else
            return false;
    }

    /**
     * check if password exists
     */
    private boolean checkIfPasswordExist() {
        mPrefs = getSharedPreferences(PREF_FILE_NAME, MODE_PRIVATE);
        boolean hasPassword = mPrefs.contains(mEditTextInputPassword.getText().toString());
        if (hasPassword)
            return true;
        else
            return false;
    }

    /**
     * check if any field is invalid
     */
    private boolean isValidInputs() {
        final String email = mEditTextInputEmail.getText().toString();
        final String password = mEditTextInputPassword.getText().toString();
        boolean isValidInput = true;
        mInputLayoutEmail.setError(null);
        mInputLayoutPassword.setError(null);

        if (!ValidationClass.isValidEmail(email)) {
            mInputLayoutEmail.setError(getString(R.string.invalidEmail));
            isValidInput = false;
        }
        if (!ValidationClass.checkLengthOfPassword(password)) {
            mInputLayoutPassword.setError(getString(R.string.invalidPswd));
            isValidInput = false;
        }
        return isValidInput;
    }

}
