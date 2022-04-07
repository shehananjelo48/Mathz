package com.sehan.mathz;

public class User {
    private String name;
    private String age;
    private String phone;
    private String imageUri;

    public User() {
    }

    public User(String name, String age, String phone, String imageUri) {
        this.name = name;
        this.age = age;
        this.phone = phone;
        this.imageUri = imageUri;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

}
