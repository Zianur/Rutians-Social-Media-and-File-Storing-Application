package com.ruet.ruetians;

public class LikesAndCommentsModule {

  private String date, time, comment, profileimage, userfullname, email, uid;

    public LikesAndCommentsModule() {

    }

    public LikesAndCommentsModule(String date, String time, String comment, String profileimage, String userfullname, String email, String uid) {
        this.date = date;
        this.time = time;
        this.comment = comment;
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

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
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
