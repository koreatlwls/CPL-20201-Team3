package com.giggle.prototype;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class UserItem implements ClusterItem {

    private String name;
    private String sex;
    private LatLng position;
    public UserItem(String name,String sex,LatLng position){
        this.name=name;
        this.sex=sex;
        this.position=position;
    }
    @Override
    public LatLng getPosition() {
        return position;
    }
    @Override
    public String getTitle() {
        return name;
    }
    @Override
    public String getSnippet() {
        return sex;
    }
}
