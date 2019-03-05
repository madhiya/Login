package com.example.login;

public class UserDetails {

    String mail;
    //String Number;
    String Pincode;
    String Location;
    String Interests;
    String Name;


    public UserDetails() {
        //default constructor

    }



    public UserDetails(String EMAIL,  String PINCODE, String LOCATION, String INTEREST, String NAME)
    {
        this.mail = EMAIL;
       // this.Number = MOBILE;
        this.Pincode = PINCODE;
        this.Location = LOCATION;
        this.Interests = INTEREST;
        this.Name = NAME;
    }



}
