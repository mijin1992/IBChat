package com.brasco.simwechat.model;

/**
 * Created by Administrator on 12/22/2016.
 */
import com.google.firebase.database.IgnoreExtraProperties;

// [START blog_user_class]
@IgnoreExtraProperties
public class User {
    public String username;
    public String email;
    public String gender;
    public String country;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String username, String email, String gender, String country) {
        this.username = username;
        this.email = email;
        this.gender = gender;
        this.country = country;
    }
    public void setGender(String gender){
        this.gender = gender;
    }
    public String getGender(){
        return  this.gender;
    }
    public void setCountry(String country){
        this.country = country;
    }
    public String getCountry(){
        return this.country;
    }
}
// [END blog_user_class]