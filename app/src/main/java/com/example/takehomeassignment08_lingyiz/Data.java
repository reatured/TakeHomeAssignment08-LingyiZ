package com.example.takehomeassignment08_lingyiz;

public class Data {
    String massage;
    private String name;
    private String photoUrl;

    public Data(String massage, String name, String photoUrl) {
        this.massage = massage;
        this.name = name;
        this.photoUrl = photoUrl;
    }

    public Data() {
    }


    public String getMassage() {
        return massage;
    }

    public void setMassage(String massage) {
        this.massage = massage;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
}
