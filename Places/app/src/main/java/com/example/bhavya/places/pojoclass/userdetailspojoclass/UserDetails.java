package com.example.bhavya.places.pojoclass.userdetailspojoclass;

/**
 * Created by bhavya on 9/8/16.
 *
 * Contains the details of the user which is stored in shared preference. The get methods reads the
 * details from the shared preference. The set methods writes the details into shared preference.
 */

public class UserDetails {

    private String firstname;
    private String lastname;
    private String emailid;
    private String password;
    private String phone;
    private String passcode;
    private String radius;
    private String imageURI;

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public String getEmail() {
        return emailid;
    }

    public String getPassword() {
        return password;
    }

    public String getPhone() {
        return phone;
    }

    public String getPasscode() {
        return passcode;
    }

    public String getRadius(){
        return radius;
    }

    public String getImageURI() {
        return imageURI;
    }



    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public void setEmail(String emailid) {
        this.emailid = emailid;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setPasscode(String passcode) {
        this.passcode = passcode;
    }

    public void setRadius(String radius){
        this.radius = radius;
    }

    public void setImageURI(String imageURI) {
        this.imageURI = imageURI;
    }
}
