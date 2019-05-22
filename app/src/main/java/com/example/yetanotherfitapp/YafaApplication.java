package com.example.yetanotherfitapp;

import android.app.Application;
import android.arch.persistence.room.Room;
import android.content.Context;

import com.example.yetanotherfitapp.database.AppDatabase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

public class YafaApplication extends Application {

    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirebaseFirestore;
    private FirebaseStorage mFirebaseStorage;
    private AppDatabase mDataBase;

    @Override
    public void onCreate() {
        super.onCreate();
        mAuth = FirebaseAuth.getInstance();
        mFirebaseFirestore = FirebaseFirestore.getInstance();
        mFirebaseStorage = FirebaseStorage.getInstance();
        mDataBase = Room.databaseBuilder(this, AppDatabase.class, "main_db").build();
    }

    public FirebaseAuth getAuth() {
        return mAuth;
    }

    public FirebaseFirestore getFirestore() {
        return mFirebaseFirestore;
    }

    public FirebaseStorage getStorage() {
        return mFirebaseStorage;
    }

    public AppDatabase getDataBase() {
        return mDataBase;
    }

    public static YafaApplication from(Context context) {
        return (YafaApplication) context.getApplicationContext();
    }

}
