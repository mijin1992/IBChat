package com.brasco.simwechat.model;

/**
 * Created by Administrator on 12/22/2016.
 */
import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

// [START blog_user_class]
@IgnoreExtraProperties
public class FireUser {
    public String username;
    public String email;
    public String gender;
    public String country;
    public String comment;

    public FireUser() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public FireUser(String username, String email, String gender, String country, String comment) {
        this.username = username;
        this.email = email;
        this.gender = gender;
        this.country = country;
        this.comment = comment;
    }

    // [START post_to_map]
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("username", username);
        result.put("email", email);
        result.put("gender", gender);
        result.put("country", country);
        result.put("comment", comment);

        return result;
    }
    // [END post_to_map]

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
    public void setWhatsUp(String value){
        this.comment = value;
    }
    public String getWhatsUp(){
        return this.comment;
    }
}
// [END blog_user_class]