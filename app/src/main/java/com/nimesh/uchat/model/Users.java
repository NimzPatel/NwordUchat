package com.nimesh.uchat.model;

public class Users {

    private String email, name, profileImage, uid, userName,StaffOrStudent;

    public Users() {
    }

    public Users(String email, String name, String profileImage, String uid, String userName, String StaffOrStudent) {
        this.email = email;
        this.name = name;
        this.profileImage = profileImage;
        this.uid = uid;
        this.userName = userName;
        this.StaffOrStudent =StaffOrStudent;
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

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getStaffOrStudent() {
        return StaffOrStudent;
    }

    public void setStaffOrStudent(String StaffOrStudent) {
        this.userName = StaffOrStudent;
    }


}
