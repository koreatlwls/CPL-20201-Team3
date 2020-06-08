package com.giggle.prototype;

import java.util.List;

public class ProcessingAdDocument {
    public List<ProcessingAd> shop;
    public String uid;

    public ProcessingAdDocument() {

    }

    public ProcessingAdDocument(List<ProcessingAd> shop, String uid) {
        this.shop = shop;
        this.uid = uid;
    }
}