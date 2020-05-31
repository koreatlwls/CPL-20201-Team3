package com.giggle.prototype;

public class members {
    private String name;
    private String sex;
    private String position;
    private Integer age;
    private String uid;
    private String phonenumber;

    public members(String name, Integer age, String sex, String position,  String phonenumber){
        this.name = name;
        this.age = age;
        this.sex = sex;
        this.position = position;
        this.phonenumber = phonenumber;
    }

    public members(String name, Integer age, String sex, String position, String uid, String phonenumber){
        this.name = name;
        this.age = age;
        this.sex = sex;
        this.position = position;
        this.uid = uid;
        this.phonenumber = phonenumber;
    }

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
