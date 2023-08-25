package com.ruet.ruetians;

import com.google.firebase.database.Exclude;

public class MemePostModule {

    private String date, time, description, memeimage, profileimage, userfullname, email, uid;
    private String memeKey;


    public MemePostModule()
    {

    }


    public MemePostModule(String date, String time, String description, String memeimage, String profileimage, String userfullname, String email, String uid) {
        this.date = date;
        this.time = time;
        this.description = description;
        this.memeimage = memeimage;
        this.profileimage = profileimage;
        this.userfullname = userfullname;
        this.email = email;
        this.uid = uid;

    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMemeimage() {
        return memeimage;
    }

    public void setMemeimage(String memeimage) {
        this.memeimage = memeimage;
    }

    public String getProfileimage() {
        return profileimage;
    }

    public void setProfileimage(String profileimage) {
        this.profileimage = profileimage;
    }

    public String getUserfullname() {
        return userfullname;
    }

    public void setUserfullname(String userfullname) {
        this.userfullname = userfullname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    @Exclude
    public String getMemeKey() {
        return memeKey;
    }

    @Exclude
    public void setMemeKey(String memeKey) {
        this.memeKey = memeKey;
    }
}
