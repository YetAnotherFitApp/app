package com.example.yetanotherfitapp.user_account;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.yetanotherfitapp.R;
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
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.HashMap;
import java.util.List;

public class ExerciseListFragment extends Fragment {

    private static final String DBG = "DBG_TAG";

    private FirebaseStorage mFirebaseStorage;
    private FirebaseFirestore mFirebaseFirestore;
    private OnExListStateChangedListener mOnExListChangedListener;
    private RecyclerView mRecyclerView;
    private ExerciseDao mExerciseDao;
    private ExerciseTitleDao mExerciseTitleDao;
    private HashMap<String, Exercise> mExerciseHashMap;
    private HashMap<String, String> mExerciseTitlesMap;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mOnExListChangedListener = (OnExListStateChangedListener) context;
        mFirebaseFirestore = YafaApplication.from(context).getFirestore();
        mFirebaseStorage = YafaApplication.from(context).getStorage();
        mExerciseDao = YafaApplication.from(context).getDataBase().getExerciseDao();
        mExerciseTitleDao = YafaApplication.from(context).getDataBase().getTitleDao();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_exercise_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRecyclerView = view.findViewById(R.id.exercise_list);
        getExerciseTitles();
    }

    @SuppressLint("StaticFieldLeak")
    private void getExerciseTitles() {
        new AsyncTask<ExerciseTitleDao, Void, HashMap<String, String>>() {

            @Override
            protected HashMap<String, String> doInBackground(ExerciseTitleDao... exerciseTitleDaos) {
                List<ExerciseTitle> titleList = exerciseTitleDaos[0].getAllTitles();
                if (titleList.isEmpty()) {
                    insertTitles(exerciseTitleDaos[0]);
                    titleList = exerciseTitleDaos[0].getAllTitles();
                }

                HashMap<String, String> titleMap = new HashMap<>();
                for (ExerciseTitle exerciseTitle : titleList) {
                    titleMap.put(exerciseTitle.id, exerciseTitle.title);
                }

                return titleMap;
            }

            @Override
            protected void onPostExecute(HashMap<String, String> stringStringHashMap) {
                super.onPostExecute(stringStringHashMap);
                mExerciseTitlesMap = stringStringHashMap;
                getExistExercises();
            }
        }.execute(mExerciseTitleDao);
    }

    @SuppressLint("StaticFieldLeak")
    private void getExistExercises() {
        new AsyncTask<ExerciseDao, Void, List<Exercise>>() {

            @Override
            protected List<Exercise> doInBackground(ExerciseDao... exerciseDaos) {
                Log.d(DBG, Long.toString(Thread.currentThread().getId()));
                Log.d(DBG, "get all exercises");
                return exerciseDaos[0].getAllExercises();
            }

            @Override
            protected void onPostExecute(List<Exercise> exercises) {
                mExerciseHashMap = new HashMap<>(exercises.size());
                for (Exercise exercise : exercises) {
                    mExerciseHashMap.put(exercise.title, exercise);
                }

                mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                mRecyclerView.setAdapter(new ExerciseAdapter());
            }
        }.execute(mExerciseDao);
    }

    private void insertTitles(ExerciseTitleDao titleDao) {
        titleDao.insert(new ExerciseTitle("exercise1", "Письмо носом"));
        titleDao.insert(new ExerciseTitle("exercise2", "Пальминг"));
        titleDao.insert(new ExerciseTitle("exercise3", "Сквозь пальцы"));
        titleDao.insert(new ExerciseTitle("exercise4", "Движения глазами в стороны"));
        titleDao.insert(new ExerciseTitle("exercise5", "Большой круг"));
        titleDao.insert(new ExerciseTitle("exercise6", "Восьмёрка"));
        titleDao.insert(new ExerciseTitle("exercise7", "Напряжение взгляда"));
        titleDao.insert(new ExerciseTitle("exercise8", "Взгляд в окно"));
        titleDao.insert(new ExerciseTitle("exercise9", "Изменение фокусного расстояния"));
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mOnExListChangedListener = null;
    }

    private class ExerciseViewHolder extends RecyclerView.ViewHolder {

        TextView listElementTitle;
        ImageView listElementStateImage;
        ProgressBar listElementProgress;
        boolean isLoaded;

        ExerciseViewHolder(@NonNull View itemView) {
            super(itemView);
            listElementTitle = itemView.findViewById(R.id.list_element_title);
            listElementStateImage = itemView.findViewById(R.id.list_element_state_image);
            listElementProgress = itemView.findViewById(R.id.list_element_progress);
            isLoaded = false;
        }
    }

    private class ExerciseAdapter extends RecyclerView.Adapter<ExerciseViewHolder> {

        @NonNull
        @Override
        public ExerciseViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
            View view = inflater.inflate(R.layout.exercise_list_element, viewGroup, false);
            return new ExerciseViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final ExerciseViewHolder exerciseViewHolder, int i) {
            final String exerciseId = "exercise" + (i + 1);
            final String exerciseName = mExerciseTitlesMap.get(exerciseId);
            exerciseViewHolder.isLoaded = mExerciseHashMap.containsKey(exerciseName);
            exerciseViewHolder.listElementTitle.setText(exerciseName);
            int imageStateResource = exerciseViewHolder.isLoaded ? R.drawable.baseline_delete_black_18dp :
                    R.drawable.baseline_backup_black_18dp;
            exerciseViewHolder.listElementStateImage.setImageResource(imageStateResource);

            exerciseViewHolder.listElementTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (exerciseViewHolder.isLoaded) {
                        mOnExListChangedListener.goToExercise(mExerciseHashMap.get(exerciseName));
                    }
                }
            });
            exerciseViewHolder.listElementStateImage.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (exerciseViewHolder.isLoaded) {
                        deleteExercise(exerciseViewHolder, exerciseName);
                    } else {
                        downloadExercise(exerciseId, exerciseViewHolder);
                    }
                }
            });
        }

        @SuppressLint("StaticFieldLeak")
        private void deleteExercise(final ExerciseViewHolder exerciseViewHolder, final String exerciseName) {
            new AsyncTask<Exercise, Void, Void>() {
                @Override
                protected void onPreExecute() {
                    exerciseViewHolder.listElementStateImage.setVisibility(View.GONE);
                    exerciseViewHolder.listElementProgress.setVisibility(View.VISIBLE);
                }

                @Override
                protected Void doInBackground(Exercise... exercises) {
                    Log.d(DBG, Long.toString(Thread.currentThread().getId()));
                    Log.d(DBG, "delete image");
                    mOnExListChangedListener.deleteFileByName(exercises[0].imageName);
                    Log.d(DBG, "delete exercise");
                    mExerciseDao.delete(exercises[0]);
                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    mExerciseHashMap.remove(exerciseName);
                    exerciseViewHolder.isLoaded = false;
                    exerciseViewHolder.listElementStateImage.setImageResource(R.drawable.baseline_backup_black_18dp);
                    exerciseViewHolder.listElementProgress.setVisibility(View.GONE);
                    exerciseViewHolder.listElementStateImage.setVisibility(View.VISIBLE);
                }
            }.execute(mExerciseHashMap.get(exerciseName));
        }

        private void downloadExercise(String exerciseId, final ExerciseViewHolder exerciseViewHolder) {
            exerciseViewHolder.listElementStateImage.setVisibility(View.GONE);
            exerciseViewHolder.listElementProgress.setVisibility(View.VISIBLE);

            mFirebaseFirestore.collection("exercises").
                    document(exerciseId).
                    get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {

                @SuppressLint("StaticFieldLeak")
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        final Exercise exercise = new Exercise(document.getString("title"),
                                document.getString("image"),
                                document.getString("description"));
                        insertExercise(exercise, exerciseViewHolder);
                    } else {
                        mOnExListChangedListener.showFail(task.getException().getMessage());
                    }
                }
            });
        }

        @SuppressLint("StaticFieldLeak")
        private void insertExercise(final Exercise exercise, final ExerciseViewHolder exerciseViewHolder) {
            new AsyncTask<Exercise, Void, File>() {

                @Override
                protected File doInBackground(Exercise... exercises) {
                    Log.d(DBG, Long.toString(Thread.currentThread().getId()));
                    Log.d(DBG, "insert exercise");
                    mExerciseDao.insert(exercises[0]);
                    return new File(mOnExListChangedListener.getAppContext().getFilesDir(), exercises[0].imageName);
                }

                @Override
                protected void onPostExecute(File imageFile) {
                    Log.d(DBG, "download image");
                    StorageReference imageRef = mFirebaseStorage.getReference().child("exercise_pictures/" + exercise.imageName + ".png");
                    imageRef.getFile(imageFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            Log.d(DBG, "download success");
                            mExerciseHashMap.put(exercise.title, exercise);
                            exerciseViewHolder.isLoaded = true;
                            exerciseViewHolder.listElementStateImage.setImageResource(R.drawable.baseline_delete_black_18dp);
                            exerciseViewHolder.listElementProgress.setVisibility(View.GONE);
                            exerciseViewHolder.listElementStateImage.setVisibility(View.VISIBLE);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            mOnExListChangedListener.showFail(e.getMessage());
                        }
                    });
                }
            }.execute(exercise);
        }

        @Override
        public int getItemCount() {
            return mExerciseTitlesMap.size();
        }
    }

    public interface OnExListStateChangedListener {

        Context getAppContext();

        void goToExercise(Exercise exercise);

        File getFileByName(String name);

        void deleteFileByName(String name);

        void showFail(String errMsg);
    }
}
