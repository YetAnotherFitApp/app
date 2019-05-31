package com.example.yetanotherfitapp;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkRequest;

import com.example.yetanotherfitapp.database.AppDatabase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

public class YafaApplication extends Application {

    public enum NetworkState {
        AVAILABLE,
        UNAVAILABLE
    }

    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirebaseFirestore;
    private FirebaseStorage mFirebaseStorage;
    private AppDatabase mDataBase;
    private MutableLiveData<NetworkState> mNetworkState;

    @Override
    public void onCreate() {
        super.onCreate();
        mAuth = FirebaseAuth.getInstance();
        mFirebaseFirestore = FirebaseFirestore.getInstance();
        mFirebaseStorage = FirebaseStorage.getInstance();
        mDataBase = Room.databaseBuilder(this, AppDatabase.class, "main_db").build();
        mNetworkState = new MutableLiveData<>();
        mNetworkState.setValue(NetworkState.UNAVAILABLE);
        addNetworkListener();
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

    public LiveData<NetworkState> getNetworkState() {
        return mNetworkState;
    }

    private void addNetworkListener() {
        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        connectivityManager.registerNetworkCallback(new NetworkRequest.Builder().build(), new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(Network network) {
                super.onAvailable(network);
                mNetworkState.postValue(NetworkState.AVAILABLE);
            }

            @Override
            public void onLost(Network network) {
                super.onLost(network);
                mNetworkState.postValue(NetworkState.UNAVAILABLE);
            }
        });
    }

    public static YafaApplication from(Context context) {
        return (YafaApplication) context.getApplicationContext();
    }

}
