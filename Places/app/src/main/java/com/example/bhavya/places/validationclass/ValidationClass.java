package com.example.bhavya.places.validationclass;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by bhavya on 9/8/16.
 *
 * Used for validating Email ID, first name, last name, Mobile number, and Password
 */

public class ValidationClass {

    /**
     * validating First name
     */
    public static boolean isValidFirstName(String firstname) {
        if (firstname.length() > 0) {
            return true;
        }
        return false;
    }

    /**
     * validating Last name
     */
    public static boolean isValidLastName(String lastname) {
        if (lastname.length() > 0) {
            return true;
        }
        return false;
    }

    /**
     * validating email id
     */
    public static boolean isValidEmail(String email) {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    /**
     * validating password
     */
    public static boolean isValidPassword(String password) {
        String PASSWORD_PATTERN = "^[a-zA-z](?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{7,16}$";
        Pattern pattern = Pattern.compile(PASSWORD_PATTERN);
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }

    /**
     * validating phone number
     */
    public static boolean isValidPhone(String phone) {
        if (phone.length() > 0 && phone.length() == 10) {
            return true;
        }
        return false;
    }

    /**
     * validating password length
     */
    public static boolean checkLengthOfPassword(String password) {

        if (password.length() > 0) {
            return true;
        }
        return false;

    }

    public static boolean isValidPasscode(String passcode) {
        if (passcode.length() > 0) {
            return true;
        }
        return false;
    }


    /**
     * validating radius
     */
    public static boolean isValidRadius(Integer radius) {
        if (radius>500 && radius<50000) {
            return true;
        }
        return false;
    }


}
