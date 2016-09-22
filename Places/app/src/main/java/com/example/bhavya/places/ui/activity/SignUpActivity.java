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
import com.example.bhavya.places.sharedpreferences.LoggedInUserSharedPreference;
import com.example.bhavya.places.validationclass.ValidationClass;
import com.google.gson.Gson;

/**
 * Created by bhavya on 9/8/16.
 *
 * Checks if the email ID entered is already existing in the shared preference. If yes, Sign Up
 * not possible, else, Sign Up successful.
 * If Sign Up successful, the details are stored into the shared preference with email as key.
 */

public class SignUpActivity extends AppCompatActivity {

    public static String PREF_FILE_NAME = "UserDetailsStorage";
    public static String PREF_LOGIN ="CurrentLogin";

    private SharedPreferences mSharedPreferences;
    private SharedPreferences mPrefs;
    private SharedPreferences.Editor mEditor;
    private SharedPreferences.Editor mPrefsEditor;

    private Toolbar mToolbar;
    private TextInputLayout mInputLayoutPhoneno;
    private TextInputLayout mInputLayoutEmailID;
    private TextInputLayout mInputLayoutPswd;
    private TextInputLayout mInputLayoutConfirmPswd;
    private TextInputLayout mInputLayoutFirstName;
    private TextInputLayout mInputLayoutLastName;
    private TextInputEditText mEditTextInputPhone;
    private TextInputEditText mEditTextInputEmail;
    private TextInputEditText mEditTextInputPassword;
    private TextInputEditText mEditTextInputFirstName;
    private TextInputEditText mEditTextInputLastName;
    private TextInputEditText mEditTextInputConfirmPassword;
    private Button mSignUpButton;

    UserDetails newUser = new UserDetails();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

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
                mSignUpButton.setEnabled(true);
            } else {
                mSignUpButton.setEnabled(false);
            }
        }
    };

    View.OnClickListener signUpButtonClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if (isValidInputs() && !checkIfEmailAlreadyExists())   //to get Signed up
            {
                writeToUserDetails();
                saveIntoSharedPreference();
                setLoginStatus();
                final Intent i = new Intent(getBaseContext(), DrawerActivity.class);
                startActivity(i);
                finish();
            } else if (checkIfEmailAlreadyExists()) {  //email already exists
                AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
                builder.setMessage(getString(R.string.emailAlreadyExists))
                        .setCancelable(false)
                        .setPositiveButton(getString(R.string.ok), new DialogInterface
                                .OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            } else {          //not signed up
                AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
                builder.setMessage(getString(R.string.signupNotSuccessful))
                        .setCancelable(false)
                        .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
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
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    /**
     * Initializing view properties and adding button clicks and text change listeners
     */
    private void setViewProperties() {
        // Assigning View References
        mSignUpButton = (Button) findViewById(R.id.btn_new_sign_up);
        mInputLayoutPhoneno = (TextInputLayout) findViewById(R.id.input_layout_phone);
        mInputLayoutEmailID = (TextInputLayout) findViewById(R.id.input_layout_email);
        mInputLayoutPswd = (TextInputLayout) findViewById(R.id.input_layout_password);
        mInputLayoutConfirmPswd = (TextInputLayout) findViewById(R.id.input_layout_confirm_password);
        mInputLayoutFirstName = (TextInputLayout) findViewById(R.id.input_layout_firstname);
        mInputLayoutLastName = (TextInputLayout) findViewById(R.id.input_layout_lastname);

        mEditTextInputFirstName = (TextInputEditText) findViewById(R.id.edittext_input_firstname);
        mEditTextInputLastName = (TextInputEditText) findViewById(R.id.edittext_input_lastname);
        mEditTextInputPhone = (TextInputEditText) findViewById(R.id.edittext_input_phone);
        mEditTextInputEmail = (TextInputEditText) findViewById(R.id.edittext_input_email);
        mEditTextInputPassword = (TextInputEditText) findViewById(R.id.edittext_input_password);
        mEditTextInputConfirmPassword = (TextInputEditText) findViewById(R.id.edittext_input_confirm_password);

        // Adding Text change listeners
        mEditTextInputFirstName.addTextChangedListener(enableDisableSignupTW);
        mEditTextInputLastName.addTextChangedListener(enableDisableSignupTW);
        mEditTextInputPhone.addTextChangedListener(enableDisableSignupTW);
        mEditTextInputEmail.addTextChangedListener(enableDisableSignupTW);
        mEditTextInputPassword.addTextChangedListener(enableDisableSignupTW);
        mEditTextInputConfirmPassword.addTextChangedListener(enableDisableSignupTW);

        // Adding button Clicks
        mSignUpButton.setOnClickListener(signUpButtonClicked);
    }

    /**
     * checks if any field is invalid
     *
     */
    private boolean isValidInputs() {

        boolean isValidInput = true;

        final String firstname = mEditTextInputFirstName.getText().toString();
        final String lastname = mEditTextInputLastName.getText().toString();
        final String phone = mEditTextInputPhone.getText().toString();
        final String email = mEditTextInputEmail.getText().toString();
        final String password = mEditTextInputPassword.getText().toString();
        final String confirmpass = mEditTextInputConfirmPassword.getText().toString();

        mInputLayoutFirstName.setError(null);
        mInputLayoutLastName.setError(null);
        mInputLayoutPhoneno.setError(null);
        mInputLayoutEmailID.setError(null);
        mInputLayoutPswd.setError(null);
        mInputLayoutConfirmPswd.setError(null);

        if (!ValidationClass.isValidFirstName(firstname)) {
            mInputLayoutFirstName.setError(getString(R.string.invalidFirstName));
            isValidInput = false;
        }
        if (!ValidationClass.isValidLastName(lastname)) {
            mInputLayoutLastName.setError(getString(R.string.invalidLastName));
            isValidInput = false;
        }
        if (!ValidationClass.isValidPhone(phone)) {
            mInputLayoutPhoneno.setError(getString(R.string.invalidPhone));
            isValidInput = false;
        }
        if (!ValidationClass.isValidEmail(email)) {
            mInputLayoutEmailID.setError(getString(R.string.invalidEmail));
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
     * Write user details into userdetails POJO class
     */
    private UserDetails writeToUserDetails() {

        String firstname = mEditTextInputFirstName.getText().toString();
        String lastname = mEditTextInputLastName.getText().toString();
        String phone = mEditTextInputPhone.getText().toString();
        String email = mEditTextInputEmail.getText().toString();
        String password = mEditTextInputPassword.getText().toString();

        newUser.setFirstname(firstname);
        newUser.setLastname(lastname);
        newUser.setEmail(email);
        newUser.setPhone(phone);
        newUser.setPassword(password);
        newUser.setPasscode("");
        newUser.setRadius(String.valueOf(500));
        newUser.setImageURI("");
        return newUser;
    }

    /**
     * convert the pojo user object into json format and save into shared preference
     */
    private void saveIntoSharedPreference() {
        String email = mEditTextInputEmail.getText().toString();
        mPrefs = getSharedPreferences(PREF_FILE_NAME, MODE_PRIVATE);
        mPrefsEditor = mPrefs.edit();
        Gson gson = new Gson();
        String jsonNewUserObj = gson.toJson(newUser);
        mPrefsEditor.putString(email, jsonNewUserObj);
        mPrefsEditor.commit();
    }

    /**
     * Set user as logged in
     */
    private void setLoginStatus() {
        mSharedPreferences = getSharedPreferences(PREF_FILE_NAME, MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();
        mEditor.putString(PREF_LOGIN, mEditTextInputEmail.getText().toString());
        mEditor.commit();
        LoggedInUserSharedPreference.setUserName(getBaseContext(), PREF_FILE_NAME);
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
     * validating confirm password
     */
    private boolean isValidConfirmPass(String confirmpass) {
        if (confirmpass.length() > 0 && mEditTextInputConfirmPassword.getText().toString().equals
                (mEditTextInputPassword.getText().toString())) {
            return true;
        }
        return false;
    }
}
