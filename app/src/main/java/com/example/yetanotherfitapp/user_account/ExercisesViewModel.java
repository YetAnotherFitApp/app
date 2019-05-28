package com.example.yetanotherfitapp.user_account;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.example.yetanotherfitapp.YafaApplication;
import com.example.yetanotherfitapp.database.Exercise;
import com.example.yetanotherfitapp.database.ExerciseTitle;

import java.io.File;
import java.util.List;

public class ExercisesViewModel extends AndroidViewModel {

    private ExercisesRepo mExercisesRepo;
    private LiveData<List<ExerciseTitle>> mListTitles;
    private LiveData<List<ExerciseTitle>> mLocalTitles;
    private LiveData<Exercise> mExercise;
    private MutableLiveData<String> mErrorMessage;

    public ExercisesViewModel(@NonNull Application application) {
        super(application);
        mExercisesRepo = new ExercisesRepo(getApplication());
        mListTitles = mExercisesRepo.getExercisesTitles();
        mLocalTitles = mExercisesRepo.getLocalTitles();
        mExercise = null;
        mErrorMessage = new MutableLiveData<>();
        mErrorMessage.setValue(null);
    }

    LiveData<YafaApplication.NetworkState> getNetworkState() {
        return YafaApplication.from(getApplication()).getNetworkState();
    }

    LiveData<List<ExerciseTitle>> getListTitles() {
        return mListTitles;
    }

    LiveData<List<ExerciseTitle>> getLocalTitles() {
        return mLocalTitles;
    }

    LiveData<Exercise> getExerciseById(String id) {
        mExercise = mExercisesRepo.getExerciseById(id);
        return mExercise;
    }

    LiveData<String> getErrorMessage() {
        return mErrorMessage;
    }

    File getFileByName(final String name) {
        return mExercisesRepo.getFileByName(name);
    }

    void downloadTitles() {
        mExercisesRepo.downloadTitles(new ExercisesRepo.LoadProgress() {
            @Override
            public void onFailed(String errorMsg) {
                mErrorMessage.postValue(errorMsg);
            }
        });
    }

    void downloadExercise(String id) {
        mExercisesRepo.downloadExercise(id, new ExercisesRepo.LoadProgress() {
            @Override
            public void onFailed(String errorMsg) {
                mErrorMessage.postValue(errorMsg);
            }
        });
    }

    void deleteExercise(Exercise exercise) {
        mExercisesRepo.deleteExercise(exercise);
    }

    void incrementNumOfDone(String exerciseId) {
        mExercisesRepo.incrementNumOfDone(exerciseId, new ExercisesRepo.LoadProgress() {
            @Override
            public void onFailed(String errorMsg) {
                mErrorMessage.postValue(errorMsg);
            }
        });
    }

}
