package com.example.yetanotherfitapp.database;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity
public class Exercise {
    @PrimaryKey @NonNull public String title;
    public String imageName;
    public String description;

    public Exercise(String title, String imageName, String description) {
        this.title = title;
        this.imageName = imageName;
        this.description = description;
    }
}
