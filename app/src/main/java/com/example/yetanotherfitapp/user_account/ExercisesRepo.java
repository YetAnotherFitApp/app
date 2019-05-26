package com.example.yetanotherfitapp.user_account;

import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.support.annotation.NonNull;

import com.example.yetanotherfitapp.YafaApplication;
import com.example.yetanotherfitapp.database.Exercise;
import com.example.yetanotherfitapp.database.ExerciseDao;
import com.example.yetanotherfitapp.database.ExerciseTitle;
import com.example.yetanotherfitapp.database.ExerciseTitleDao;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FilenameFilter;
import java.util.List;
import java.util.concurrent.Executors;

public class ExercisesRepo {

    private final String COLLECTION_NAME = "exercises";
    private final String ID_FIELD_NAME = "image";
    private final String TITLE_FIELD_NAME = "title";
    private final String DESCRIPTION_FIELD_NAME = "description";

    private Context mContext;
    private FirebaseFirestore mFirebaseFirestore;
    private FirebaseStorage mFirebaseStorage;
    private ExerciseDao mExerciseDao;
    private ExerciseTitleDao mExerciseTitleDao;

    ExercisesRepo(Context context) {
        mContext = context;
        mFirebaseFirestore = YafaApplication.from(context).getFirestore();
        mFirebaseStorage = YafaApplication.from(context).getStorage();
        mExerciseDao = YafaApplication.from(context).getDataBase().getExerciseDao();
        mExerciseTitleDao = YafaApplication.from(context).getDataBase().getTitleDao();
    }

    LiveData<List<ExerciseTitle>> getExercisesTitles() {
        return mExerciseTitleDao.getAllTitles();
    }

    LiveData<Exercise> getExerciseById(String id) {
        return mExerciseDao.getExerciseById(id);
    }

    File getFileByName(final String name) {
        File[] files = mContext.getFilesDir().listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String n) {
                return n.equals(name);
            }
        });
        return files.length == 0 ? null : files[0];
    }

    private void deleteFileByName(String name) {
        mContext.deleteFile(name);
    }

    void downloadTitles(final LoadProgress progress) {
        mFirebaseFirestore.collection(COLLECTION_NAME)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                insertTitles(document.getString(ID_FIELD_NAME), document.getString(TITLE_FIELD_NAME));
                            }
                        } else {
                            progress.onFailed(task.getException().getMessage());
                        }
                    }
                });
    }

    private void insertTitles(final String id, final String title) {
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                mExerciseTitleDao.insert(new ExerciseTitle(id, title));
            }
        });
    }

    void downloadExercise(String id, final LoadProgress progress) {
        mFirebaseFirestore.collection(COLLECTION_NAME)
                .document(id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            final Exercise exercise = new Exercise(document.getString(TITLE_FIELD_NAME),
                                    document.getString(ID_FIELD_NAME),
                                    document.getString(DESCRIPTION_FIELD_NAME));
                            insertExercise(exercise, progress);
                        } else {
                            progress.onFailed(task.getException().getMessage());
                        }
                    }
                });
    }

    private void insertExercise(final Exercise exercise, final LoadProgress progress) {
        StorageReference imageRef = mFirebaseStorage.getReference().child("exercise_pictures/" + exercise.imageName + ".png");
        imageRef.getFile(new File(mContext.getFilesDir(), exercise.imageName))
                .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        Executors.newSingleThreadExecutor().execute(new Runnable() {
                            @Override
                            public void run() {
                                mExerciseDao.insert(exercise);
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progress.onFailed(e.getMessage());
                    }
                });
    }

    void deleteExercise(final Exercise exercise) {
        deleteFileByName(exercise.imageName);
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                mExerciseDao.delete(exercise);
            }
        });
    }

    public interface LoadProgress {
        void onFailed(String errorMsg);
    }

}
