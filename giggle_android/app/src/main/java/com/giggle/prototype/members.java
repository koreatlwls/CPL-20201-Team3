package com.giggle.prototype;

public class members {
    private String name;
    private String sex;
    private String position;
    private Integer age;
    private String uid;
    private String phonenumber;
    private Double score;
    private Integer num_of_score;
    private double latitude;
    private double longtitude;
    public members(){
        score=0.0;
    }
    public members(String name, Integer age, String sex, String position,  String phonenumber){
        this.name = name;
        this.age = age;
        this.sex = sex;
        this.position = position;
        this.phonenumber = phonenumber;
    }

    public members(String name, Integer age, String sex, String position, String uid, String phonenumber,Double score){
        this.name = name;
        this.age = age;
        this.sex = sex;
        this.position = position;
        this.uid = uid;
        this.phonenumber = phonenumber;
        this.score=score;
    }
    public members(String name, Integer age, String sex, String position, String uid, String phonenumber,Double latitude,Double longtitude){
        this.name = name;
        this.age = age;
        this.sex = sex;
        this.position = position;
        this.uid = uid;
        this.phonenumber = phonenumber;
        this.score=0.0;
        this.num_of_score=0;
        this.latitude=latitude;
        this.longtitude=longtitude;
    }
    public double getLatitude() { return latitude; }
    public double getLongtitude() { return longtitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }
    public void setLongtitude(double longtitude) { this.longtitude = longtitude; }
    public void calScore(){
        score=score/num_of_score;
    }
    public Integer getNum_of_score(){return num_of_score;}
    public void setNum_of_score(Integer num_of_score){this.num_of_score=num_of_score; }
    public Double getScore(){ return score; }
    public void setScore(Double score){ this.score=score; }
    public String getPhonenumber(){
        return this.phonenumber;
    }
    public void setPhonenumber(String phonenumber){
        this.phonenumber = phonenumber;
    }
    public String getUid(){
        return this.uid;
    }
    public void setUid(String uid){
        this.uid = uid;
    }
    public String getName(){
        return this.name;
    }
    public void setName(String name){
        this.name = name;
    }
    public Integer getAge(){
        return this.age;
    }
    public void setAge(Integer age){
        this.age = age;
    }
    public String getSex(){
        return this.sex;
    }
    public void setSex(String sex){
        this.sex = sex;
    }
    public String getPosition(){
        return this.position;
    }
    public void setPosition(String position){
        this.position = position;
    }
}
