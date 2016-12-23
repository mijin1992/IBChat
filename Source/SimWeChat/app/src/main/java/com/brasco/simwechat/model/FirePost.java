package com.brasco.simwechat.model;

/**
 * Created by Administrator on 12/23/2016.
 */
import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

// [START firePost_class]
@IgnoreExtraProperties
public class FirePost {
    private String authorUid;
    private String authorQbId;
    private String authorName;
    private String time;
//    public String title = "";
    private String comment;
    private String imageUrl;
//    public int starCount = 0;

    public FirePost() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public FirePost(String uid, String authorname, String time, String comment, String url, String qbId) {
        this.authorUid = uid;
        this.authorName = authorname;
        this.authorQbId = qbId;
        this.time = time;
        this.comment = comment;
        this.imageUrl = url;
    }

    public void setAuthorUid(String id){
        this.authorUid = id;
    }
    public void setAuthorName(String name){
        this.authorName = name;
    }
    public void setTime(String time){
        this.time = time;
    }
    public void setComment(String value){
        this.comment = value;
    }
    public void setImageUrl(String url){
        this.imageUrl = url;
    }
    public void setAuthorQbId(String qbId) {this.authorQbId = qbId;}
    public String getAuthorUid(){
        return this.authorUid;
    }
    public String getAuthorName(){
        return this.authorName;
    }
    public String getTime(){
        return  this.time ;
    }
    public String getComment(){
        return  this.comment;
    }
    public String getImageUrl(){
        return  this.imageUrl;
    }
    public String getAuthorQbId() { return this.authorQbId; }
    // [START firePost_to_map]
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("authorUid", authorUid);
        result.put("authorName", authorName);
        result.put("time", time);
        result.put("authorQbId", authorQbId);
        result.put("comment", comment);
        result.put("imageUrl", imageUrl);
//        result.put("starCount", starCount);

        return result;
    }
    // [END firePost_to_map]
}
// [END firePost_class]
