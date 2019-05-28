package com.example.yetanotherfitapp.user_account;

import android.annotation.SuppressLint;
import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.yetanotherfitapp.YafaApplication;
import com.example.yetanotherfitapp.database.Exercise;
import com.example.yetanotherfitapp.database.ExerciseDao;
import com.example.yetanotherfitapp.database.ExerciseTitle;
import com.example.yetanotherfitapp.database.ExerciseTitleDao;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
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

    private final String EXERCISE_COLLECTION_NAME = "exercises";
    private final String PICTURES_COLLECTION_NAME = "exercise_pictures";
    private final String USERS_COLLECTION_NAME = "users";
    private final String EX_INFO_COLLECTION_NAME = "exercisesInfo";
    private final String NUM_OF_DONE = "NumOfDone";
    private final String ID_FIELD_NAME = "image";
    private final String TITLE_FIELD_NAME = "title";
    private final String DESCRIPTION_FIELD_NAME = "description";

    private Context mContext;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseFirestore mFirebaseFirestore;
    private FirebaseStorage mFirebaseStorage;
    private ExerciseDao mExerciseDao;
    private ExerciseTitleDao mExerciseTitleDao;

    ExercisesRepo(Context context) {
        mContext = context;
        mFirebaseAuth = YafaApplication.from(context).getAuth();
        mFirebaseFirestore = YafaApplication.from(context).getFirestore();
        mFirebaseStorage = YafaApplication.from(context).getStorage();
        mExerciseDao = YafaApplication.from(context).getDataBase().getExerciseDao();
        mExerciseTitleDao = YafaApplication.from(context).getDataBase().getTitleDao();
    }

    LiveData<List<ExerciseTitle>> getExercisesTitles() {
        return mExerciseTitleDao.getAllTitles();
    }

    LiveData<List<ExerciseTitle>> getLocalTitles() {
        return mExerciseTitleDao.getLocalTitles();
    }

    LiveData<Exercise> getExerciseById(String id) {
        return mExerciseDao.getExerciseById(id);
    }

    void getExerciseFromCloud(String id, final LoadProgress progress) {
        mFirebaseFirestore.collection(EXERCISE_COLLECTION_NAME)
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

                            StorageReference imageRef = mFirebaseStorage.getReference().child(PICTURES_COLLECTION_NAME + "/" + exercise.imageName + ".png");
                            imageRef.getFile(createFile(exercise.imageName))
                                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                            progress.onLoadEnd(exercise);
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            progress.onFailed(e.getMessage());
                                        }
                                    });
                        } else {
                            progress.onFailed(task.getException().getMessage());
                        }
                    }
                });
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
        mFirebaseFirestore.collection(EXERCISE_COLLECTION_NAME)
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
                mExerciseTitleDao.insert(new ExerciseTitle(id, title, false));
            }
        });
    }

    void downloadExercise(String id, final LoadProgress progress) {
        mFirebaseFirestore.collection(EXERCISE_COLLECTION_NAME)
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

    private File createFile(String name) {
        File file = getFileByName(name);
        if (file != null) {
            return file;
        } else {
            return new File(mContext.getFilesDir(), name);
        }
    }

    private void insertExercise(final Exercise exercise, final LoadProgress progress) {
        StorageReference imageRef = mFirebaseStorage.getReference().child(PICTURES_COLLECTION_NAME + "/" + exercise.imageName + ".png");
        imageRef.getFile(createFile(exercise.imageName))
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

    @SuppressLint("StaticFieldLeak")
    void deleteExercise(final Exercise exercise, final LoadProgress progress) {
        deleteFileByName(exercise.imageName);

        new AsyncTask<Exercise, Void, Void>() {
            @Override
            protected Void doInBackground(Exercise... exercises) {
                mExerciseDao.delete(exercises[0]);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                progress.onLoadEnd(exercise);
            }
        };
    }

    void incrementNumOfDone(final String exerciseId, final LoadProgress progress) {
        final DocumentReference docNumOfDone = mFirebaseFirestore
                .collection(USERS_COLLECTION_NAME)
                .document(mFirebaseAuth.getUid())
                .collection(EX_INFO_COLLECTION_NAME)
                .document(NUM_OF_DONE);

        docNumOfDone.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        long numOfDone = (long) documentSnapshot.get(exerciseId);
                        Log.d("smth", String.valueOf(numOfDone));
                        docNumOfDone.update(exerciseId, ++numOfDone)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d("smth", "Done");
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        progress.onFailed(e.getMessage());
                                    }
                                });
                        Log.d("smth", String.valueOf(numOfDone));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progress.onFailed(e.getMessage());
                    }
                });
    }

    public interface LoadProgress {
        void onLoadEnd(Exercise exercise);

        void onFailed(String errorMsg);
    }

}
