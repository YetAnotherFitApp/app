package com.example.yetanotherfitapp.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface ExerciseTitleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(ExerciseTitle exerciseTitle);

    @Query("SELECT * FROM exercisetitle order by id asc")
    LiveData<List<ExerciseTitle>> getAllTitles();

    @Query("SELECT * FROM exercisetitle WHERE isLoaded = 1")
    LiveData<List<ExerciseTitle>> getLocalTitles();

}
