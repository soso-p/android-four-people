package com.example.ecobrandlyapp;


/**
 * 사용자 계정 정보 모델 클래스
 */
public class UserAccount {



    private String idToken;  //Firebase Uid
    private String Id;
    private String Alising = "  "; //별칭
    private String Pwd;
    private String PhoneNumber;
    private String BusinessReg;

    private long Level = 0;
    private int Point;


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

    public long getLevel() {
        return Level;
    }

    public void setLevel(int level) {
        this.Level = level;
    }

    public void setBusinessReg(String businessReg){ BusinessReg = businessReg; }

    public String getBusinessReg(){ return BusinessReg ; }

    public int getPoint() {
        return Point;
    }

    public void setPoint(int point) {
        Point = point;
    }

    public String getAlising() {
        return Alising;
    }

    public void setAlising(String alising) {
        Alising = alising;
    }



}
