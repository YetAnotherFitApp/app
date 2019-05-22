package com.example.yetanotherfitapp;

import android.app.Application;
import android.content.Context;

import com.google.firebase.auth.FirebaseAuth;

public class YafaApplication extends Application {

    private FirebaseAuth mAuth;

    @Override
    public void onCreate() {
        super.onCreate();
        mAuth = FirebaseAuth.getInstance();
    }

    public FirebaseAuth getAuth() {
        return mAuth;
    }

    public static YafaApplication from(Context context) {
        return (YafaApplication) context.getApplicationContext();
    }

}
