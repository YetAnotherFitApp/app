package com.example.yetanotherfitapp.database;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity
public class ExerciseTitle {
    @PrimaryKey
    @NonNull
    public String id;
    public String title;

    public ExerciseTitle(@NonNull String id, String title) {
        this.id = id;
        this.title = title;
    }
}
