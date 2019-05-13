package com.example.yetanotherfitapp.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.yetanotherfitapp.R;
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

public class ExerciseFragment extends Fragment {

    private FirebaseFirestore mFirebaseFirestore;
    private TextView mTitle;
    private ImageView mImage;
    private TextView mDescription;
    private Button mGetInfoBtn;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFirebaseFirestore = FirebaseFirestore.getInstance();
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
        mFirebaseFirestore.collection("exercises").
                document("exercise1").
                get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    mTitle.setText(document.getString("title"));
                    mDescription.setText(document.getString("description"));
                    getImage(document.getString("image"));
                } else {
                    Toast.makeText(getActivity(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void getImage(final String name) {
        File[] files = getActivity().getFilesDir().listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String n) {
                return n.equals(name);
            }
        });
        if (files.length != 0) {
            Glide.with(this).load(files[0]).into(mImage);
        } else {
            final File imageFile = new File(getActivity().getApplicationContext().getFilesDir(), name);
            final Fragment fragment = this;
            FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
            StorageReference imageRef = firebaseStorage.getReference().child("exercise_pictures/ex1.png");
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
