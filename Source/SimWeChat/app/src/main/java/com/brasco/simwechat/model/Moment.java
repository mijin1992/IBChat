package com.brasco.simwechat.model;

/**
 * Created by Administrator on 12/23/2016.
 */

public class Moment {
    public String authorUid;
    public String authorName;

    public Moment(String uid, String author, String time, String comment, String url) {
        this.authorUid = uid;
        this.authorName = author;
//        this.starCount = 0;
    }
}
