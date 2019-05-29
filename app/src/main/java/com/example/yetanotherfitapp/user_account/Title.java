package com.example.yetanotherfitapp.user_account;


import android.os.Parcel;
import android.os.Parcelable;

public class Title implements Parcelable {
    public final String name;

    public Title(String name){
        this.name = name;
    }

    protected Title(Parcel in) {
        name = in.readString();
    }

    public static final Creator<Title> CREATOR = new Creator<Title>() {
        @Override
        public Title createFromParcel(Parcel in) {
            return new Title(in);
        }

        @Override
        public Title[] newArray(int size) {
            return new Title[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
    }
}

