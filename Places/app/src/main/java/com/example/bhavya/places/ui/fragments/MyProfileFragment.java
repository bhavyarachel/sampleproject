package com.example.bhavya.places.ui.fragments;


import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.bhavya.places.R;
import com.example.bhavya.places.pojoclass.userdetailspojoclass.UserDetails;
import com.example.bhavya.places.ui.activity.DrawerActivity;
import com.example.bhavya.places.validationclass.ValidationClass;
import com.google.gson.Gson;

import org.w3c.dom.Text;

import static android.content.Context.MODE_PRIVATE;
import static com.example.bhavya.places.R.id.emaildisplay_textview;
import static com.example.bhavya.places.ui.activity.SignUpActivity.PREF_FILE_NAME;
import static com.example.bhavya.places.ui.activity.SignUpActivity.PREF_LOGIN;

/**
 * A simple {@link Fragment} subclass.
 * Reads the user details from shared preferences and displays it.
 * On Updating the profile page, the details get updated in the shared preferences.
 */
public class MyProfileFragment extends Fragment {

    public String mUserEmail;
    public String mPassword;
    private SharedPreferences mPrefs;
    private SharedPreferences sharedpreferences ;
    private SharedPreferences.Editor prefsEditor;

    private Button mButtonEditDetails;
    private TextInputLayout mTextInputLayoutFirstName;
    private TextInputLayout mTextInputLayoutLastName;
    private TextView mTextInputLayoutEmail;
    private TextInputLayout mTextInputLayoutPhone;
    private TextInputLayout mTextInputLayoutRadius;
    private TextInputEditText mEditTextFirstName;
    private TextInputEditText mEditTextLastName;
    private TextInputEditText mEditTextPhoneNumber;
    private TextInputEditText mEditTextRadius;
    private View mView;
    private String imageURI;

    UserDetails newUser = new UserDetails();

    public MyProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_profile, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mView = view;
        setViewProperties();
        getDataFromPreferences();
    }

    TextWatcher enableDisableEditDetailsButtonTW = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (s.length() > 0) {
                mButtonEditDetails.setEnabled(true);
            } else {
                mButtonEditDetails.setEnabled(false);
            }
        }
    };

    View.OnClickListener EditDetailsButtonClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (isValidInputs()) {
                updateIntoSharedPreference();
                final Intent i = new Intent(getContext(), DrawerActivity.class);
                startActivity(i);
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage(getString(R.string.updationFailed))
                        .setCancelable(false)
                        .setPositiveButton(getString(R.string.ok), new DialogInterface
                                .OnClickListener
                                () {
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        }
    };

    /**
     * Initializing view properties
     */
    private void setViewProperties() {

        mButtonEditDetails = (Button) mView.findViewById(R.id.btn_edit_details);
        mEditTextFirstName = (TextInputEditText) mView.findViewById(R.id.edittext_firstname);
        mEditTextLastName = (TextInputEditText) mView.findViewById(R.id.edittext_lastname);
        mEditTextPhoneNumber = (TextInputEditText) mView.findViewById(R.id.edittext_phone);
        mEditTextRadius = (TextInputEditText) mView.findViewById(R.id.edittext_radius);
        mTextInputLayoutFirstName = (TextInputLayout) mView.findViewById(R.id.layout_firstname);
        mTextInputLayoutLastName = (TextInputLayout) mView.findViewById(R.id.layout_lastname);
        mTextInputLayoutPhone = (TextInputLayout) mView.findViewById(R.id.layout_phone);
        mTextInputLayoutEmail = (TextView) mView.findViewById(emaildisplay_textview);
        mTextInputLayoutRadius = (TextInputLayout) mView.findViewById(R.id.layout_radius);
        mEditTextFirstName.addTextChangedListener(enableDisableEditDetailsButtonTW);
        mEditTextLastName.addTextChangedListener(enableDisableEditDetailsButtonTW);
        mEditTextPhoneNumber.addTextChangedListener(enableDisableEditDetailsButtonTW);

        mButtonEditDetails.setOnClickListener(EditDetailsButtonClicked);
    }

    /**
     * Retrieve the stored data about logged in user from shared preferences and display on my
     * profile screen
     */
    private void getDataFromPreferences(){
        mPrefs= getContext().getSharedPreferences(PREF_FILE_NAME, MODE_PRIVATE);
        sharedpreferences = getContext().getSharedPreferences(PREF_FILE_NAME,MODE_PRIVATE);
        String email = sharedpreferences.getString(PREF_LOGIN, "");
        String obj= mPrefs.getString(email, "");
        Gson gson = new Gson();
        UserDetails newuser = gson.fromJson(obj, UserDetails.class);
        String Firstname= newuser.getFirstname();
        String Lastname= newuser.getLastname();
        String Phone= newuser.getPhone();
        mUserEmail=newuser.getEmail();
        mPassword = newuser.getPassword();
        String CurrentRadius = newuser.getRadius();
        imageURI = newuser.getImageURI();

        //Displaying retrieved data
        mEditTextFirstName.setText(Firstname);
        mEditTextLastName.setText(Lastname);
        mTextInputLayoutEmail.setText(mUserEmail);
        mEditTextPhoneNumber.setText(Phone);
        mEditTextRadius.setText(CurrentRadius);
    }

    /**
     * checks if any field is invalid
     *
     */
    private boolean isValidInputs() {

        boolean isValidInput = true;

        final String firstname = mEditTextFirstName.getText().toString();
        final String lastname = mEditTextLastName.getText().toString();
        final String phone = mEditTextPhoneNumber.getText().toString();
        final Integer radius = Integer.valueOf(mEditTextRadius.getText().toString());

        mTextInputLayoutFirstName.setError(null);
        mTextInputLayoutLastName.setError(null);
        mTextInputLayoutPhone.setError(null);

        if (!ValidationClass.isValidFirstName(firstname)) {
            mTextInputLayoutFirstName.setError(getString(R.string.invalidFirstName));
            isValidInput = false;
        }
        if (!ValidationClass.isValidLastName(lastname)) {
            mTextInputLayoutLastName.setError(getString(R.string.invalidLastName));
            isValidInput = false;
        }
        if (!ValidationClass.isValidPhone(phone)) {
            mTextInputLayoutPhone.setError(getString(R.string.invalidPhone));
            isValidInput = false;
        }
        if (!ValidationClass.isValidRadius(radius)) {
            mTextInputLayoutRadius.setError(getString(R.string.invalidRadius));
            isValidInput = false;
        }
        return isValidInput;
    }

    /**
     * Update the details about the user into shared preferences on clicking edit details button
     */
    private void updateIntoSharedPreference(){

        String firstname = mEditTextFirstName.getText().toString();
        String lastname = mEditTextLastName.getText().toString();
        String phone = mEditTextPhoneNumber.getText().toString();
        String radius = mEditTextRadius.getText().toString();

        newUser.setFirstname(firstname);
        newUser.setLastname(lastname);
        newUser.setPhone(phone);
        newUser.setPassword(mPassword);
        newUser.setEmail(mUserEmail);
        newUser.setRadius(radius);
        newUser.setImageURI(imageURI);

        //to get the current user email
        sharedpreferences = getContext().getSharedPreferences(PREF_FILE_NAME, MODE_PRIVATE);
        String email= sharedpreferences.getString(PREF_LOGIN,"");

        // to update the shared preference
        mPrefs = getContext().getSharedPreferences(PREF_FILE_NAME, MODE_PRIVATE);
        prefsEditor = mPrefs.edit();
        Gson gson = new Gson();
        String jsonNewUserObj = gson.toJson(newUser);
        prefsEditor.putString(email, jsonNewUserObj);
        prefsEditor.commit();
    }



}
