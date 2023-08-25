package com.ruet.ruetians;

public class SearchModule {

    private String userfullname, roll, series, phonenumber, aboutuser, email, profileimage;

    public SearchModule() {
    }

    public SearchModule(String userfullname, String roll, String series, String phonenumber, String aboutuser, String email, String profileimage) {
        this.userfullname = userfullname;
        this.roll = roll;
        this.series = series;
        this.phonenumber = phonenumber;
        this.aboutuser = aboutuser;
        this.email = email;
        this.profileimage = profileimage;
    }

    public String getUserfullname() {
        return userfullname;
    }

    public void setUserfullname(String userfullname) {
        this.userfullname = userfullname;
    }

    public String getRoll() {
        return roll;
    }

    public void setRoll(String roll) {
        this.roll = roll;
    }

    public String getSeries() {
        return series;
    }

    public void setSeries(String series) {
        this.series = series;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    public String getAboutuser() {
        return aboutuser;
    }

    public void setAboutuser(String aboutuser) {
        this.aboutuser = aboutuser;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfileimage() {
        return profileimage;
    }

    public void setProfileimage(String profileimage) {
        this.profileimage = profileimage;
    }
}
