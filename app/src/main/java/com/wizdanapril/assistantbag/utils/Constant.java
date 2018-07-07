package com.wizdanapril.assistantbag.utils;


import android.content.Context;
import android.content.SharedPreferences;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.wizdanapril.assistantbag.activities.LoginActivity;

public class Constant {

    public static final String USER = "user";
    public static final String DEVICE = "device";
    public static final String DATA = "data";
    public static final String CATALOG = "catalog";
    public static final String HISTORY = "history";
    public static final String SCHEDULE = "schedule";

}
