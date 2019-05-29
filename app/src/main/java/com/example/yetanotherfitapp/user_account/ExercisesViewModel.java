package com.example.yetanotherfitapp.user_account;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.example.yetanotherfitapp.YafaApplication;
import com.example.yetanotherfitapp.database.Exercise;
import com.example.yetanotherfitapp.database.ExerciseTitle;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.List;

public class ExercisesViewModel extends AndroidViewModel {

    private ExercisesRepo mExercisesRepo;
    private LiveData<List<ExerciseTitle>> mListTitles;
    private LiveData<List<ExerciseTitle>> mLocalTitles;
    private LiveData<List<ExerciseTitle>> mFavouriteTitles;
    private LiveData<Exercise> mExercise;
    private MutableLiveData<String> mMessage;

    public ExercisesViewModel(@NonNull Application application) {
        super(application);
        mExercisesRepo = new ExercisesRepo(getApplication());
        mListTitles = mExercisesRepo.getExercisesTitles();
        mLocalTitles = mExercisesRepo.getLocalTitles();
        mFavouriteTitles = mExercisesRepo.getFavouriteTitles();
        mExercise = null;
        mMessage = new MutableLiveData<>();
        mMessage.setValue(null);
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

    LiveData<List<ExerciseTitle>> getFavouriteTitles() {
        return mFavouriteTitles;
    }

    LiveData<Exercise> getExerciseById(String id) {
        mExercise = mExercisesRepo.getExerciseById(id);
        return mExercise;
    }

    void getExerciseFromCloud(String id, final ExercisesRepo.LoadProgress progress) {
        mExercisesRepo.getExerciseFromCloud(id, new ExercisesRepo.LoadProgress() {
            @Override
            public void onLoadEnd(Exercise exercise, StorageReference imageRef) {
                progress.onLoadEnd(exercise, imageRef);
            }

            @Override
            public void onFailed(String errorMsg) {
                mMessage.postValue(errorMsg);
            }
        });
    }

    LiveData<String> getMessage() {
        return mMessage;
    }

    File getFileByName(final String name) {
        return mExercisesRepo.getFileByName(name);
    }

    void downloadTitles() {
        mExercisesRepo.downloadTitles(new ExercisesRepo.LoadProgress() {
            @Override
            public void onLoadEnd(Exercise exercise, StorageReference imageRef) {
            }

            @Override
            public void onFailed(String errorMsg) {
                mMessage.postValue(errorMsg);
            }
        });
    }

    void addToFavourite(Boolean isLoaded, String id, Exercise exercise) {
        if (isLoaded) {
            mExercisesRepo.addToFavourite(exercise, new ExercisesRepo.LoadProgress() {
                @Override
                public void onLoadEnd(Exercise exercise, StorageReference imageRef) {
                    mMessage.postValue("Упражнение добавлено в избранное");
                }

                @Override
                public void onFailed(String errorMsg) {
                    mMessage.postValue(errorMsg);
                }
            });
        } else {
            mExercisesRepo.downloadExercise(id, new ExercisesRepo.LoadProgress() {
                @Override
                public void onLoadEnd(Exercise exercise, StorageReference imageRef) {
                    mExercisesRepo.addToFavourite(exercise, new ExercisesRepo.LoadProgress() {
                        @Override
                        public void onLoadEnd(Exercise exercise, StorageReference imageRef) {
                            mMessage.postValue("Упражнение добавлено в избранное");
                        }

                        @Override
                        public void onFailed(String errorMsg) {
                            mMessage.postValue(errorMsg);
                        }
                    });
                }

                @Override
                public void onFailed(String errorMsg) {
                    mMessage.postValue(errorMsg);
                }
            });
        }
    }

    void downloadExercise(String id) {
        mExercisesRepo.downloadExercise(id, new ExercisesRepo.LoadProgress() {
            @Override
            public void onLoadEnd(Exercise exercise, StorageReference imageRef) {
                mMessage.postValue("Упражнение успешно загружено на Ваше устройство");
            }

            @Override
            public void onFailed(String errorMsg) {
                mMessage.postValue(errorMsg);
            }
        });
    }

    void deleteExercise(Exercise exercise) {
        mExercisesRepo.deleteExercise(exercise, new ExercisesRepo.LoadProgress() {
            @Override
            public void onLoadEnd(Exercise exercise, StorageReference imageRef) {
                mMessage.postValue("Упражнение успешно удалено с Вашего устройства");
            }

            @Override
            public void onFailed(String errorMsg) {
                mMessage.postValue(errorMsg);
            }
        });
    }

    void incrementNumOfDone(String exerciseId) {
        mExercisesRepo.incrementNumOfDone(exerciseId, new ExercisesRepo.LoadProgress() {
            @Override
            public void onLoadEnd(Exercise exercise, StorageReference imageRef) {
            }

            @Override
            public void onFailed(String errorMsg) {
                mMessage.postValue(errorMsg);
            }
        });
    }

}
