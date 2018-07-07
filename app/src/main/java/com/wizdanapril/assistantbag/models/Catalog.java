package com.wizdanapril.assistantbag.models;

import android.net.Uri;

import java.util.Map;

public class Catalog {

    public String id, name, status, lastReadDate, lastReadTime, imageUri;
    public Map<String, Boolean> schedule;

    public Catalog() {

    }

    public Catalog(String id, String name, String status, String lastReadDate,
                   String lastReadTime, Map<String, Boolean> schedule, String imageUri
    ) {

        this.id = id;
        this.name = name;
        this.status = status;
        this.lastReadDate = lastReadDate;
        this.lastReadTime = lastReadTime;
        this.schedule = schedule;
        this.imageUri = imageUri;

    }

}