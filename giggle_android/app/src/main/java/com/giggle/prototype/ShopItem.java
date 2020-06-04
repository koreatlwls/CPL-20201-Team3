package com.giggle.prototype;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class ShopItem implements ClusterItem {
    private LatLng location;
    private Integer hourlypay;
    private String mTitle;
    private String mSnippet;
    public ShopItem(){}
    public ShopItem(LatLng location,Integer hourlypay,String mTitle,String mSnippet){
        this.hourlypay=hourlypay;
        this.location=location;
        this.mTitle=mTitle;
        this.mSnippet=mSnippet;
    }

    public ShopItem(LatLng location,String mTitle,String mSnippet){
        this.location=location;
        this.mTitle=mTitle;
        this.mSnippet=mSnippet;
    }

    public LatLng getLocation(){
        return location;
    }
    public void setLocation(LatLng location){
        this.location=location;
    }

    public Integer getHourlypay(){
        return hourlypay;
    }
    public void setHourlypay(Integer hourlypay){
        this.hourlypay=hourlypay;
    }
    public LatLng getposition(){
        return location;
    }

    @Override
    public LatLng getPosition() {
        return location;
    }

    @Override
    public String getTitle() {
        return mTitle;
    }

    @Override
    public String getSnippet() {
        return mSnippet;
    }
}
