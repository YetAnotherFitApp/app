package com.example.yetanotherfitapp.user_account;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.yetanotherfitapp.R;
import com.example.yetanotherfitapp.database.Exercise;
import com.example.yetanotherfitapp.database.ExerciseTitle;
import com.google.firebase.storage.StorageReference;

import java.io.File;

public class ExerciseFragment extends Fragment {

    private final static String KEY_EXERCISE_ID = "exercise_id";
    private final static String KEY_TITLE = "title";

    private ExerciseListFragment.OnExListStateChangedListener mOnExListChangedListener;
    private String mId;
    private String mTitle;
    private Boolean mIsLoaded;
    private Boolean mIsFavourite;
    private Exercise mExercise;
    private ExercisesViewModel mExercisesViewModel;

    public static ExerciseFragment newInstance(String id, String title) {
        ExerciseFragment exerciseFragment = new ExerciseFragment();
        Bundle argument = new Bundle();
        argument.putString(KEY_EXERCISE_ID, id);
        argument.putString(KEY_TITLE, title);
        exerciseFragment.setArguments(argument);
        return exerciseFragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mOnExListChangedListener = (ExerciseListFragment.OnExListStateChangedListener) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mId = getArguments().getString(KEY_EXERCISE_ID);
        mTitle = getArguments().getString(KEY_TITLE);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_exercise, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mOnExListChangedListener.setActionBarTitle(mTitle);
        mIsLoaded = null;
        mExercise = null;
        mIsFavourite = null;

        mExercisesViewModel = ViewModelProviders.of(this).get(ExercisesViewModel.class);
        mExercisesViewModel.clearMessage();
        mExercisesViewModel.getFavouriteTitleById(mId).observe(this, new Observer<ExerciseTitle>() {
            @Override
            public void onChanged(@Nullable ExerciseTitle exerciseTitle) {
                mIsFavourite = (exerciseTitle != null);
            }
        });
        mExercisesViewModel.getExerciseById(mId).observe(this, new Observer<Exercise>() {
            @Override
            public void onChanged(@Nullable Exercise exercise) {
                if (exercise == null) {
                    mExercisesViewModel.getExerciseFromCloud(mId, new ExercisesRepo.LoadProgress() {
                        @Override
                        public void onLoadEnd(Exercise exercise, StorageReference imageRef) {
                            exerciseIsLoaded(view, exercise, imageRef);
                        }

                        @Override
                        public void onFailed(String errorMsg) {
                            mOnExListChangedListener.showFail(errorMsg);
                        }
                    });
                    mIsLoaded = false;
                    mExercise = null;
                    exerciseNotLoaded(view);
                } else {
                    mIsLoaded = true;
                    mExercise = exercise;
                    exerciseIsLoaded(view, exercise, null);
                }
            }
        });
        mExercisesViewModel.getMessage().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                if (s != null) {
                    mOnExListChangedListener.showFail(s);
                }
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.exercise_fragment_app_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_favourite:
                if (mIsLoaded != null && mIsFavourite != null) {
                    if (mIsFavourite) {
                        mIsFavourite = null;
                        mExercisesViewModel.deleteFromFavourite(mExercise);
                    } else {
                        mIsFavourite = null;
                        mExercisesViewModel.addToFavourite(mIsLoaded, mId, mExercise);
                    }
                }
                return true;
            case R.id.action_download:
                if (mIsLoaded != null) {
                    if (!mIsLoaded) {
                        mIsLoaded = null;
                        mExercisesViewModel.downloadExercise(mId);
                    } else {
                        mOnExListChangedListener.showFail("Упражнение уже есть на Вашем устройстве");
                    }
                }
                return true;
            case R.id.action_delete:
                if (mIsLoaded != null) {
                    if (mIsLoaded) {
                        mIsLoaded = null;
                        mExercisesViewModel.deleteExercise(mExercise);
                    } else {
                        mOnExListChangedListener.showFail("Упражнение на загружено на Ваше устройство");
                    }
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void exerciseNotLoaded(final View view) {
        LinearLayout mainLayout = view.findViewById(R.id.ex_main_layout);
        mainLayout.setGravity(Gravity.CENTER);

        final ProgressBar downloadProgress = view.findViewById(R.id.ex_progress);
        downloadProgress.setVisibility(View.VISIBLE);

        ScrollView exerciseScroll = view.findViewById(R.id.ex_scroll_view);
        exerciseScroll.setVisibility(View.GONE);
    }

    private void exerciseIsLoaded(final View view, final Exercise exercise, StorageReference imageRef) {
        final LinearLayout mainLayout = view.findViewById(R.id.ex_main_layout);
        mainLayout.setGravity(Gravity.CENTER | Gravity.TOP);

        final ProgressBar downloadProgress = view.findViewById(R.id.ex_progress);
        downloadProgress.setVisibility(View.GONE);

        final ScrollView exerciseScroll = view.findViewById(R.id.ex_scroll_view);
        exerciseScroll.setVisibility(View.VISIBLE);

        TextView description = view.findViewById(R.id.ex_description);
        description.setText(exercise.description);

        if (imageRef == null) {
            getImage(exercise.imageName, (ImageView) view.findViewById(R.id.ex_image));
        } else {
            getImage((ImageView) view.findViewById(R.id.ex_image), imageRef);
        }
    }

    private void getImage(final String name, final ImageView imageView) {
        File imageFile = mExercisesViewModel.getFileByName(name);
        if (getActivity() != null) {
            Glide.with(this).load(imageFile).into(imageView);
        }
    }

    private void getImage(ImageView imageView, StorageReference imageRef) {
        if (getActivity() != null) {
            GlideApp.with(this).load(imageRef).into(imageView);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mOnExListChangedListener = null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

}
