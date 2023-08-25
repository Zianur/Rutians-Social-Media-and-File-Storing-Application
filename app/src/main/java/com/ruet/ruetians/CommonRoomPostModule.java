package com.ruet.ruetians;

import com.google.firebase.database.Exclude;

public class CommonRoomPostModule {


    private String date, time, description, postimage, profileimage, userfullname, email, uid;

    private String postKey;


    public CommonRoomPostModule() {

    }

    public CommonRoomPostModule(String date, String time, String description, String postimage, String profileimage, String userfullname, String email, String uid) {
        this.date = date;
        this.time = time;
        this.description = description;
        this.postimage = postimage;
        this.profileimage = profileimage;
        this.userfullname = userfullname;
        this.email = email;
        this.uid = uid;
    }

    @Exclude
    public String getPostKey() {
        return postKey;
    }

    @Exclude
    public void setPostKey(String postKey) {
        this.postKey = postKey;
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

    public String getPostimage() {
        return postimage;
    }

    public void setPostimage(String postimage) {
        this.postimage = postimage;
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


}
