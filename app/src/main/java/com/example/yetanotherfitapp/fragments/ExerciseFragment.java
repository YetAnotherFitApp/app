package com.example.yetanotherfitapp.fragments;

import android.arch.persistence.room.Room;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
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
import java.io.FilenameFilter;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

//TODO: Добавить refresh()

public class ExerciseFragment extends Fragment {

    private final String DBG = "DBG_TAG";

    private FirebaseFirestore mFirebaseFirestore;
    private FirebaseStorage mFirebaseStorage;
    private ExerciseDao mExerciseDao;
    private List<Exercise> mExerciseList;
    private ExecutorService mExecutorService;
    private TextView mTitle;
    private ImageView mImage;
    private TextView mDescription;
    private Button mGetInfoBtn;

    private class GetExercisesTask extends AsyncTask<ExerciseDao, Void, List<Exercise>> {

        @Override
        protected List<Exercise> doInBackground(ExerciseDao... exerciseDaos) {
            Log.d(DBG, Long.toString(Thread.currentThread().getId()));
            return exerciseDaos[0].getAllExercises();
        }

        @Override
        protected void onPostExecute(List<Exercise> exercises) {
            mExerciseList = exercises;
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d(DBG, Long.toString(Thread.currentThread().getId()));
        super.onCreate(savedInstanceState);
        mFirebaseFirestore = FirebaseFirestore.getInstance();
        mFirebaseStorage = FirebaseStorage.getInstance();
        mExerciseDao = Room.databaseBuilder(getActivity().getApplicationContext(), AppDatabase.class, "main_db").
                build().getExerciseDao();
        mExecutorService = Executors.newSingleThreadExecutor();
        new GetExercisesTask().execute(mExerciseDao);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_exercise, container, false);

        mTitle = view.findViewById(R.id.exTitle);
        mImage = view.findViewById(R.id.exImage);
        mDescription = view.findViewById(R.id.exDescription);
        mGetInfoBtn = view.findViewById(R.id.exGetInfoBtn);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        mGetInfoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getInfo();
            }
        });
    }

    private void getInfo() {
        if (mExerciseList.isEmpty()) {
            Log.d(DBG, "database is empty");
            mFirebaseFirestore.collection("exercises").
                    document("exercise1").
                    get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        final Exercise exercise = new Exercise(document.getString("title"),
                                document.getString("image"),
                                document.getString("description"));
                        mExecutorService.execute(new Runnable() {
                            @Override
                            public void run() {
                                Log.d(DBG, Long.toString(Thread.currentThread().getId()));
                                mExerciseDao.insert(exercise);
                            }
                        });
                        mTitle.setText(exercise.title);
                        mDescription.setText(exercise.description);
                        getImage(exercise.imageName);
                    } else {
                        Toast.makeText(getActivity(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });
        } else {
            Log.d(DBG, "database is NOT empty");
            mTitle.setText(mExerciseList.get(0).title);
            mDescription.setText(mExerciseList.get(0).description);
            getImage(mExerciseList.get(0).imageName);
        }
    }

    private void getImage(final String name) {
        File[] files = getActivity().getFilesDir().listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String n) {
                return n.equals(name);
            }
        });
        if (files.length != 0) {
            Log.d(DBG, "image exist");
            Glide.with(this).load(files[0]).into(mImage);
        } else {
            Log.d(DBG, "image NOT exist");
            final File imageFile = new File(getActivity().getApplicationContext().getFilesDir(), name);
            final Fragment fragment = this;
            StorageReference imageRef = mFirebaseStorage.getReference().child("exercise_pictures/ex1.png");
            imageRef.getFile(imageFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Glide.with(fragment).load(imageFile).into(mImage);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(fragment.getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }

}
