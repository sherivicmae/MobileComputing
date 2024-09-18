package com.example.activity_information;

public class User {
    private String userId; // Unique ID for the user
    private String username;
    private String email;
    private String phoneNum;
    private String gender;
    private String birthdate;
    private String province;
    private String interests;


    // Default constructor required for calls to DataSnapshot.getValue(User.class)
    public User() {
        // No-argument constructor
    }

    // Constructor with all fields
    public User(String userId, String username, String email, String phoneNum, String gender, String birthdate, String province, String interests) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.phoneNum = phoneNum;
        this.gender = gender;
        this.birthdate = birthdate;
        this.province = province;
        this.interests = interests;
    }

    // Getters and setters for all fields
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return username;
    }

    public void setUserName(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getInterests() {
        return interests;
    }

    public void setInterests(String interests) {
        this.interests = interests;
    }
}
