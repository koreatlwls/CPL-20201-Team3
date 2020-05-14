package com.giggle.prototype;

public class JobAd {
    private String shopname;
    private String shopposition;
    private String businessinfo;
    private String priorityreq;
    private String sex;
    private Integer hourlypay;
    private Integer age1;
    private Integer age2;
    private Integer date;
    private String sttime;
    private String fntime;

    public JobAd(String shopname, String shopposition, String businessinfo, String priorityreq, Integer hourlypay, Integer age1, Integer age2, String sex,Integer date,String sttime,String fntime){
        this.shopname = shopname;
        this.shopposition = shopposition;
        this.businessinfo = businessinfo;
        this.priorityreq = priorityreq;
        this.sex = sex;
        this.hourlypay = hourlypay;
        this.age1 = age1;
        this.age2 = age2;
        this.date = date;
        this.sttime = sttime;
        this.fntime = fntime;
    }

    public String getShopname(){
        return this.shopname;
    }
    public void setShopname(String shopname){
        this.shopname = shopname;
    }
    public String getShopposition(){
        return this.shopposition;
    }
    public void setShopposition(String shopposition){
        this.shopposition = shopposition;
    }
    public String getBusinessinfo(){
        return this.businessinfo;
    }
    public void setBusinessinfo(String businessinfo){
        this.businessinfo = businessinfo;
    }
    public String getPriorityreq(){
        return this.priorityreq;
    }
    public void setPriorityreq(String priorityreq){
        this.priorityreq = priorityreq;
    }
    public Integer getHourlypay(){
        return this.hourlypay;
    }
    public void setHourlypay(Integer hourlypay){
        this.hourlypay = hourlypay;
    }
    public Integer getAge1(){
        return this.age1;
    }
    public void setAge1(Integer age1){
        this.age1 = age1;
    }
    public Integer getAge2(){
        return this.age2;
    }
    public void setAge2(Integer age2){
        this.age2 = age2;
    }
    public String getSex(){
        return this.sex;
    }
    public void setSex(){
        this.sex = sex;
    }
    public Integer getDate(){
        return this.date;
    }
    public void setDate(){
        this.date=date;
    }
    public String getstTime(){
        return this.sttime;
    }
    public void setstTime(){
        this.sttime=sttime;
    }
    public String getfnTime(){
        return this.fntime;
    }
    public void setfnTime(){
        this.sttime=fntime;
    }
}
