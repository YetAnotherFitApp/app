package com.example.yetanotherfitapp.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.yetanotherfitapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;

//TODO: add progress!!!

public class ExerciseFragment extends Fragment {

    private final String DBG = "DBG_TAG";
    private final static String KEY_TITLE = "Title";
    private final static String KEY_IMAGE_NAME = "ImageName";
    private final static String KEY_DESCRIPTION = "Description";

    private FirebaseStorage mFirebaseStorage;
    private ExerciseListFragment.OnExListChangedListener mOnExListChangedListener;
    private String mTitle;
    private String mImageName;
    private String mDescription;

    public static ExerciseFragment newInstance(String title, String imageName, String description) {
        ExerciseFragment exerciseFragment = new ExerciseFragment();
        Bundle argument = new Bundle();
        argument.putString(KEY_TITLE, title);
        argument.putString(KEY_IMAGE_NAME, imageName);
        argument.putString(KEY_DESCRIPTION, description);
        exerciseFragment.setArguments(argument);
        return exerciseFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTitle = getArguments().getString(KEY_TITLE);
        mImageName = getArguments().getString(KEY_IMAGE_NAME);
        mDescription = getArguments().getString(KEY_DESCRIPTION);
        mFirebaseStorage = FirebaseStorage.getInstance();
        mOnExListChangedListener = (ExerciseListFragment.OnExListChangedListener) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_exercise, container, false);

        TextView viewTitle = view.findViewById(R.id.ex_title);
        viewTitle.setText(mTitle);
        TextView viewDescription = view.findViewById(R.id.ex_description);
        viewDescription.setText(mDescription);
        getImage(mImageName, (ImageView) view.findViewById(R.id.ex_image));

        return view;
    }

    private void getImage(final String name, final ImageView imageView) {
        File imageFile = mOnExListChangedListener.getFileByName(name);

        if (imageFile == null) {
            Log.d(DBG, "image doesn't exist");
            imageFile = new File(mOnExListChangedListener.getAppContext().getFilesDir(), name);
            final Fragment fragment = this;
            final File finalImageFile = imageFile;
            StorageReference imageRef = mFirebaseStorage.getReference().child("exercise_pictures/" + name + ".png");
            imageRef.getFile(imageFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Glide.with(fragment).load(finalImageFile).into(imageView);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(fragment.getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        } else {
            Log.d(DBG, "image exists");
            Glide.with(this).load(imageFile).into(imageView);
        }
    }

}
