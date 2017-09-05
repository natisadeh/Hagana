package com.example.natis.hagana.Model;

public class ClientUser {
    private String userId;
    private String lastName;
    private String firstName;
    private String email;
    private String password;
    private String gender;
    private String notes;
    public ClientUser(){
    }

    public String getlastName() {
        return lastName;
    }

    public void setlastName(String lastName) {
        this.lastName = lastName;
    }

    public String getfirstName() {
        return firstName;
    }

    public void setfirstName(String fName) {
        this.firstName = fName;
    }

    public String getUserId() {return userId;}

    public void setUserId(String userId) {this.userId = userId;}

    public String getEmail() {return email;}

    public void setEmail(String email) {this.email = email;}

    public String getPassword() {return password;}

    public void setPassword(String password) {this.password = password;}

    public String getGender() {return gender;}

    public void setGender(String gender) {this.gender = gender;}

    public String getNotes() {return notes;}

    public void setNotes(String notes) {this.notes = notes;}
}
