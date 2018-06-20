package com.wizdanapril.assistantbag.models;

import java.util.Map;

public class History {

    public String status, date, time;
    public Map<String, Boolean> reference;

    public History() {

    }

    public History(String status, String date, String time, Map<String, Boolean> reference) {
        this.status = status;
        this.date = date;
        this.time = time;
        this.reference = reference;
    }
    
}



