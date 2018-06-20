package com.wizdanapril.assistantbag.models;

import java.util.Map;

public class Catalog {

    public String id, name, status, lastReadDate, lastReadTime, lastReadDay;
    public Map<String, Boolean> schedule;

    public Catalog() {

    }

    public Catalog(String id, String name, String status, String lastReadDate,
                   String lastReadTime, String lastReadDay,
                   Map<String, Boolean> schedule
    ) {

        this.id = id;
        this.name = name;
        this.status = status;
        this.lastReadDate = lastReadDate;
        this.lastReadTime = lastReadTime;
        this.lastReadDay = lastReadDay;
        this.schedule = schedule;

    }

}