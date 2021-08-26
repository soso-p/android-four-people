package com.example.ecobrandlyapp;


/**
 * 사용자 계정 정보 모델 클래스
 */
public class UserAccount {



    private String idToken;  //Firebase Uid
    private String Id;
    private String Pwd;
    private String PhoneNumber;
    private String BusinessReg=" ";
    private int level = 0;


    public UserAccount() { }

    public String getIdToken() {
        return idToken;
    }

    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }
    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getPwd() {
        return Pwd;
    }

    public void setPwd(String pwd) {
        Pwd = pwd;
    }

    public String getPhoneNumber() {
        return PhoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        PhoneNumber = phoneNumber;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void setBusinessReg(String businessReg){ BusinessReg = businessReg; }

    public String getBusinessReg(){ return BusinessReg ; }







}
