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

import static com.example.bhavya.places.ui.activity.SignUpActivity.PREF_FILE_NAME;

/**
 * Created by bhavya on 9/8/16.
 *
 * Checks if the email ID entered is already existing in shared preference. If yes, the
 * corresponding passcode is read and checked if it matches with the entered passcode.
 * If match is found, Password reset successful, else not successful.
 */
public class ResetPasswordActivity extends AppCompatActivity {

    public String mEmail;
    public String mFirstname;
    public String mLastname;
    public String mPhone;
    public String mPassword;
    public String mPasscode;
    public String mNewPassword;

    private SharedPreferences mPrefs;
    private SharedPreferences.Editor mPrefsEditor;

    private Toolbar mToolbar;
    private TextInputLayout mInputLayoutEmailId;
    private TextInputLayout mInputLayoutPswd;
    private TextInputLayout mInputLayoutConfirmPswd;
    private TextInputLayout mInputLayoutPasscode;
    private TextInputEditText mEditTextInputEmail;
    private TextInputEditText mEditTextInputPassword;
    private TextInputEditText mEditTextInputPasscode;
    private TextInputEditText mEditTextInputConfirmPassword;
    private Button mBtnResetPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        setCustomActionBar();
        setViewProperties();
    }

    TextWatcher enableDisableSignupTW = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (s.length() > 0) {
                mBtnResetPassword.setEnabled(true);
            } else {
                mBtnResetPassword.setEnabled(false);
            }
        }
    };

    View.OnClickListener resetPasswordBtnClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mNewPassword = mEditTextInputPassword.getText().toString();

            if (!isValidInputs() && checkIfEmailAlreadyExists()) {
                passwordResetUnsuccessful();
            } else if (!checkIfEmailAlreadyExists()) {
                emailDoesnotExist();
            } else if (!checkIfPasscodeExists()) {
                emailIDandPasscodeDoesNotMatch();
            } else if (checkIfEmailAlreadyExists() && checkIfPasscodeExists()) {
                passwordReset();
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
     * Initializing view properties and adding button clicks and text change listeners
     */
    private void setViewProperties() {
        // Assigning View References
        mBtnResetPassword = (Button) findViewById(R.id.btn_passwordReset);
        mInputLayoutEmailId = (TextInputLayout) findViewById(R.id.input_layout_email);
        mInputLayoutPswd = (TextInputLayout) findViewById(R.id.input_layout_password);
        mInputLayoutConfirmPswd = (TextInputLayout) findViewById(R.id.input_layout_confirm_password);
        mInputLayoutPasscode = (TextInputLayout) findViewById(R.id.input_layout_passcode);

        mEditTextInputPasscode = (TextInputEditText) findViewById(R.id.edittext_input_passcode);
        mEditTextInputEmail = (TextInputEditText) findViewById(R.id.edittext_input_email);
        mEditTextInputPassword = (TextInputEditText) findViewById(R.id.edittext_input_password);
        mEditTextInputConfirmPassword = (TextInputEditText) findViewById(R.id.edittext_input_confirm_password);

        // Adding Text change listeners
        mEditTextInputPasscode.addTextChangedListener(enableDisableSignupTW);
        mEditTextInputEmail.addTextChangedListener(enableDisableSignupTW);
        mEditTextInputPassword.addTextChangedListener(enableDisableSignupTW);
        mEditTextInputConfirmPassword.addTextChangedListener(enableDisableSignupTW);

        // Adding button Clicks
        mBtnResetPassword.setOnClickListener(resetPasswordBtnClicked);
    }

    /**
     * checks if any field is invalid
     *
     */
    private boolean isValidInputs() {
        boolean isValidInput = true;
        final String email = mEditTextInputEmail.getText().toString();
        final String passcode = mEditTextInputPasscode.getText().toString();
        final String password = mEditTextInputPassword.getText().toString();
        final String confirmpass = mEditTextInputConfirmPassword.getText().toString();

        mInputLayoutEmailId.setError(null);
        mInputLayoutPasscode.setError(null);
        mInputLayoutPswd.setError(null);
        mInputLayoutConfirmPswd.setError(null);

        if (!ValidationClass.isValidEmail(email)) {
            mInputLayoutEmailId.setError(getString(R.string.invalidEmail));
            isValidInput = false;
        }
        if (!ValidationClass.isValidPasscode(passcode)) {
            mInputLayoutPasscode.setError(getString(R.string.invalidPasscode));
            isValidInput = false;
        }
        if (!ValidationClass.isValidPassword(password)) {
            mInputLayoutPswd.setError(getString(R.string.invalidPswd));
            isValidInput = false;
        }
        if (!isValidConfirmPass(confirmpass)) {
            mInputLayoutConfirmPswd.setError(getString(R.string.pswdsDonotMatch));
            isValidInput = false;
        }

        return isValidInput;
    }

    /**
     * check if email id entered already exists
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
     * Alert dialogue box appears when entered email id does not exist
     */
    private void emailDoesnotExist() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ResetPasswordActivity.this);
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
     * check if passcode exists and check if entered passcode is equal to obtained passcode
     */
    private boolean checkIfPasscodeExists() {
        /*gets all the details of the user corresponding to the entered email id from shared
         preferences*/
        getDataFromSharedPref();
        if (mPasscode.equals(mEditTextInputPasscode.getText().toString())) {
            return true;
        }
        return false;
    }

    /**
     * Alert dialogue box appears when the entered Passcode does not exist
     */
    private void emailIDandPasscodeDoesNotMatch() {
        //Alert Dialogue Box
        AlertDialog.Builder builder = new AlertDialog.Builder(ResetPasswordActivity.this);
        builder.setMessage(getString(R.string.emailPasscodeDoesNotMatch))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    /**
     * Show alert dialog if Password could not be reset
     */
    private void passwordResetUnsuccessful() {
        //Alert Dialogue Box
        AlertDialog.Builder builder = new AlertDialog.Builder(ResetPasswordActivity.this);
        builder.setMessage(getString(R.string.pswdResetUnsucessful))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    /**
     * If Email id and Passcode exists,then reset password
     */
    private void passwordReset() {
        writeDataToSharedPref();
        final Intent intent = new Intent(getBaseContext(), LoginActivity.class);

        AlertDialog.Builder builder = new AlertDialog.Builder(ResetPasswordActivity.this);
        builder.setMessage(getString(R.string.passwordResetSuccessful))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        startActivity(intent);
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    /**
     * validating confirm password
     */
    private boolean isValidConfirmPass(String confirmpass) {

        if (confirmpass.length() > 0 && mEditTextInputConfirmPassword.getText().toString().equals
                (mEditTextInputPassword.getText().toString())) {
            return true;
        }
        return false;
    }

    /**
     * Retrieve the stored details of the user corresponding to the entered email id
     */
    private void getDataFromSharedPref() {
        mPrefs = getSharedPreferences(PREF_FILE_NAME, MODE_PRIVATE);
        String obj = mPrefs.getString(mEditTextInputEmail.getText().toString(), "");
        Gson gson = new Gson();
        UserDetails newUser = gson.fromJson(obj, UserDetails.class);
        mEmail = newUser.getEmail();
        mFirstname = newUser.getFirstname();
        mLastname = newUser.getLastname();
        mPhone = newUser.getPhone();
        mPassword = newUser.getPassword();
        mPasscode = newUser.getPasscode();
    }

    /**
     * Store the new password of the user into shared preferences
     */
    private void writeDataToSharedPref() {
        mPrefs = getSharedPreferences(PREF_FILE_NAME, MODE_PRIVATE);
        String obj = mPrefs.getString(mEditTextInputEmail.getText().toString(), "");
        Gson gson = new Gson();
        UserDetails newUser = new UserDetails();
        newUser.setFirstname(mFirstname);
        newUser.setLastname(mLastname);
        newUser.setPhone(mPhone);
        newUser.setEmail(mEmail);
        newUser.setPassword(mNewPassword);

        mPrefsEditor = mPrefs.edit();
        String jsonNewUserObj = gson.toJson(newUser);
        mPrefsEditor.putString(mEmail, jsonNewUserObj);
        mPrefsEditor.commit();
    }
}
