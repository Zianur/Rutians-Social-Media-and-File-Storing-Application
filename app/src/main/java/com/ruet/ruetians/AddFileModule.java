package com.ruet.ruetians;

public class AddFileModule {

    private String userfullname, email, name, url;

    public AddFileModule() {

    }

    public AddFileModule(String userfullname, String email, String name, String url) {
        this.userfullname = userfullname;
        this.email = email;
        this.name = name;
        this.url = url;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
