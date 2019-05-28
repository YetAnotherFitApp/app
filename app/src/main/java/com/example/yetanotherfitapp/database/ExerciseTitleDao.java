package com.example.yetanotherfitapp.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface ExerciseTitleDao {

    @Insert
    void insert(ExerciseTitle exerciseTitle);

    @Update
    void update(ExerciseTitle exerciseTitle);

    @Query("SELECT * FROM exercisetitle order by id asc")
    LiveData<List<ExerciseTitle>> getAllTitles();

    @Query("SELECT * FROM exercisetitle WHERE isLoaded = 1 order by id asc")
    LiveData<List<ExerciseTitle>> getLocalTitles();

}
