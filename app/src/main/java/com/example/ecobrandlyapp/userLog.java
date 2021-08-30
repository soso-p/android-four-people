package com.example.ecobrandlyapp;


public class userLog {
    private String StoreName;
    private String StoreUid;
    private String UserName;
    private String UserUid;
    private String TimeStamp;
    private int IncreasePoint;



    private int points;

    public userLog(){}


    public String getStoreName() {
        return StoreName;
    }

    public void setStoreName(String storeName) {
        StoreName = storeName;
    }

    public String getStoreUid() {
        return StoreUid;
    }

    public void setStoreUid(String storeUid) {
        StoreUid = storeUid;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getUserUid() {
        return UserUid;
    }

    public void setUserUid(String userUid) {
        UserUid = userUid;
    }

    public String getTimeStamp() {
        return TimeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        TimeStamp = timeStamp;
    }

    public int getIncreasePoint() {
        return IncreasePoint;
    }

    public void setIncreasePoint(int increasePoint) {
        IncreasePoint = increasePoint;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }



}
