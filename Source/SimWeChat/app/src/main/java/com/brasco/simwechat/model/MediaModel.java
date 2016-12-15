package com.brasco.simwechat.model;

/**
 * Created by Administrator on 12/14/2016.
 */
public class MediaModel {
    public static final String TAG = "MediaModel";

    public String id;
    public String artist;
    public String title;
    public String data;
    public String display_name;
    public long duration;

    public boolean selected;

    public MediaModel() {
        id = "";
        artist = "";
        title = "";
        data = "";
        display_name = "";
        duration = 0;

        selected = false;
    }

    public void setData(MediaModel model) {
        this.id = model.id;
        this.artist = model.artist;
        this.title = model.title;
        this.data = model.data;
        this.display_name = model.display_name;
        this.duration = model.duration;
    }

    public boolean equal(MediaModel model) {
        if (!this.id.equalsIgnoreCase(model.id))
            return false;
        if (!this.artist.equalsIgnoreCase(model.artist))
            return false;
        if (!this.title.equalsIgnoreCase(model.title))
            return false;
        if (!this.data.equalsIgnoreCase(model.data))
            return false;
        if (!this.display_name.equalsIgnoreCase(model.display_name))
            return false;
        if (this.duration != model.duration)
            return false;

        return true;
    }
}

