package com.example.yetanotherfitapp.fragments;

import android.annotation.SuppressLint;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.SharedPreferences;
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
import com.example.yetanotherfitapp.database.AppDatabase;
import com.example.yetanotherfitapp.database.Exercise;
import com.example.yetanotherfitapp.database.ExerciseDao;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ExerciseListFragment extends Fragment {

    private static final String PREFS_NAME = "exercisePrefs";
    private static final String EXERCISE_NAMES = "exerciseNames";
    private static final String DBG = "DBG_TAG";
    private static final int EXERCISES_COUNT = 9;

    private FirebaseStorage mFirebaseStorage;
    private FirebaseFirestore mFirebaseFirestore;
    private OnExListStateChangedListener mOnExListChangedListener;
    private RecyclerView mRecyclerView;
    private ArrayList<String> mExerciseNames;
    private ExerciseDao mExerciseDao;
    private HashMap<String, Exercise> mExerciseHashMap;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mOnExListChangedListener = (OnExListStateChangedListener) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFirebaseFirestore = FirebaseFirestore.getInstance();
        mFirebaseStorage = FirebaseStorage.getInstance();

        SharedPreferences exercisePrefs = getActivity().getSharedPreferences(PREFS_NAME, 0);
        Set<String> names = exercisePrefs.getStringSet(EXERCISE_NAMES, null);
        if (names == null) {
            Log.d(DBG, "EXERCISE_NAMES doesn't exist");
            names = makeNames();
            exercisePrefs.edit().putStringSet(EXERCISE_NAMES, names).apply();
        }
        mExerciseNames = new ArrayList<>(names);

        mExerciseDao = Room.databaseBuilder(mOnExListChangedListener.getAppContext(), AppDatabase.class, "main_db").
                build().getExerciseDao();
    }

    private HashSet<String> makeNames() {
        HashSet<String> names = new HashSet<>(EXERCISES_COUNT);
        names.add("Письмо носом");
        names.add("Пальминг");
        names.add("Сквозь пальцы");
        names.add("Движения глазами в стороны");
        names.add("Большой круг");
        names.add("Восьмёрка");
        names.add("Напряжение взгляда");
        names.add("Взгляд в окно");
        names.add("Изменение фокусного расстояния");
        return names;
    }

    private int getDocNum(String exerciseName) {
        return exerciseName.equals("Письмо носом") ? 1 :
                exerciseName.equals("Пальминг") ? 2 :
                        exerciseName.equals("Сквозь пальцы") ? 3 :
                                exerciseName.equals("Движения глазами в стороны") ? 4 :
                                        exerciseName.equals("Большой круг") ? 5 :
                                                exerciseName.equals("Восьмёрка") ? 6 :
                                                        exerciseName.equals("Напряжение взгляда") ? 7 :
                                                                exerciseName.equals("Взгляд в окно") ? 8 :
                                                                        exerciseName.equals("Изменение фокусного расстояния") ? 9 : -1;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_exercise_list, container, false);
        mRecyclerView = view.findViewById(R.id.exercise_list);
        return view;
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

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
            final String exerciseName = mExerciseNames.get(i);
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
                @SuppressLint("StaticFieldLeak")
                @Override
                public void onClick(View v) {
                    if (exerciseViewHolder.isLoaded) {
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
                    } else {
                        exerciseViewHolder.listElementStateImage.setVisibility(View.GONE);
                        exerciseViewHolder.listElementProgress.setVisibility(View.VISIBLE);

                        mFirebaseFirestore.collection("exercises").
                                document("exercise" + getDocNum(exerciseName)).
                                get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @SuppressLint("StaticFieldLeak")
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    final Exercise exercise = new Exercise(document.getString("title"),
                                            document.getString("image"),
                                            document.getString("description"));

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
                                } else {
                                    mOnExListChangedListener.showFail(task.getException().getMessage());
                                }
                            }
                        });
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return mExerciseNames.size();
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
