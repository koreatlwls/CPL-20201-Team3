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
    private String st;
    private String fn;
    private Integer numofperson;
    private String uid;
    private String photouri;


    public JobAd() {}

    public JobAd(String shopname, String shopposition) {
        this.shopname = shopname;
        this.shopposition = shopposition;
    }
    public JobAd(String shopname, String shopposition, String businessinfo, String priorityreq, Integer hourlypay, Integer age1, Integer age2, String sex,String st,String fn, Integer numofperson, String uid,String photouri){
        this.shopname = shopname;
        this.shopposition = shopposition;
        this.businessinfo = businessinfo;
        this.priorityreq = priorityreq;
        this.sex = sex;
        this.hourlypay = hourlypay;
        this.age1 = age1;
        this.age2 = age2;
        this.st = st;
        this.fn = fn;
        this.numofperson = numofperson;
        this.uid = uid;
        this.photouri = photouri;
    }
    public String getPhotouri(){
        return this.photouri;
    }
    public void setPhotouri(String photouri){
        this.photouri = photouri;
    }
    public String getUid(){
        return this.uid;
    }
    public void setUid(String uid){
        this.uid = uid;
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
    public String getSt(){
        return this.st;
    }
    public void setSt(){
        this.st= st;
    }
    public String getFn(){
        return this.fn;
    }
    public void setFn(){
        this.fn= fn;
    }
    public Integer getNumofperson(){
        return this.numofperson;
    }
    public void setNumofperson(){
        this.numofperson= numofperson;
    }
}
