package com.example.yetanotherfitapp.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface ExerciseTitleDao {

    @Insert
    void insert(ExerciseTitle exerciseTitle);

    @Query("SELECT * FROM exercisetitle order by id asc")
    LiveData<List<ExerciseTitle>> getAllTitles();

}
