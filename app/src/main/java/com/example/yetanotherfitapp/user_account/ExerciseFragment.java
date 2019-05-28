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

import java.io.File;

public class ExerciseFragment extends Fragment {

    private final static String KEY_EXERCISE_ID = "exercise_id";

    private ExerciseListFragment.OnExListStateChangedListener mOnExListChangedListener;
    private String mId;
    private ExercisesViewModel mExercisesViewModel;

    public static ExerciseFragment newInstance(String id) {
        ExerciseFragment exerciseFragment = new ExerciseFragment();
        Bundle argument = new Bundle();
        argument.putString(KEY_EXERCISE_ID, id);
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

        mExercisesViewModel = ViewModelProviders.of(this).get(ExercisesViewModel.class);
        mExercisesViewModel.getExerciseById(mId).observe(this, new Observer<Exercise>() {
            @Override
            public void onChanged(@Nullable Exercise exercise) {
                if (exercise == null) {
                    mExercisesViewModel.getExerciseFromCloud(mId, new ExercisesRepo.LoadProgress() {
                        @Override
                        public void onLoadEnd(Exercise exercise) {
                            exerciseIsLoaded(view, exercise);
                        }

                        @Override
                        public void onFailed(String errorMsg) {
                            mOnExListChangedListener.showFail(errorMsg);
                        }
                    });
                    exerciseNotLoaded(view);
                } else {
                    exerciseIsLoaded(view, exercise);
                }
            }
        });
        mExercisesViewModel.getErrorMessage().observe(this, new Observer<String>() {
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

    //TODO Добавить обработку нажатий на кнопки меню
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return super.onOptionsItemSelected(item);
    }

    //TODO: обработать кейс с повторной записью в бд
    private void exerciseNotLoaded(final View view) {
        LinearLayout mainLayout = view.findViewById(R.id.ex_main_layout);
        mainLayout.setGravity(Gravity.CENTER);

        TextView title = view.findViewById(R.id.ex_title);
        title.setVisibility(View.GONE);

        final ProgressBar downloadProgress = view.findViewById(R.id.ex_progress);
        downloadProgress.setVisibility(View.VISIBLE);

        ScrollView exerciseScroll = view.findViewById(R.id.ex_scroll_view);
        exerciseScroll.setVisibility(View.GONE);

//        final ImageButton downloadBtn = view.findViewById(R.id.ex_download_btn);
//        downloadBtn.setVisibility(View.VISIBLE);
//        downloadBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                downloadBtn.setVisibility(View.GONE);
//                downloadProgress.setVisibility(View.VISIBLE);
//                mExercisesViewModel.downloadExercise(mId);
//            }
//        });
    }

    private void exerciseIsLoaded(final View view, final Exercise exercise) {
        final LinearLayout mainLayout = view.findViewById(R.id.ex_main_layout);
        mainLayout.setGravity(Gravity.CENTER | Gravity.TOP);

        final TextView title = view.findViewById(R.id.ex_title);
        title.setVisibility(View.VISIBLE);
        title.setText(exercise.title);

        final ProgressBar downloadProgress = view.findViewById(R.id.ex_progress);
        downloadProgress.setVisibility(View.GONE);

        final ScrollView exerciseScroll = view.findViewById(R.id.ex_scroll_view);
        exerciseScroll.setVisibility(View.VISIBLE);

        TextView description = view.findViewById(R.id.ex_description);
        description.setText(exercise.description);

        getImage(exercise.imageName, (ImageView) view.findViewById(R.id.ex_image));

//        final ImageButton deleteBtn = view.findViewById(R.id.ex_delete);
//        deleteBtn.setVisibility(View.VISIBLE);
//        deleteBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mainLayout.setGravity(Gravity.CENTER);
//                title.setVisibility(View.GONE);
//                downloadProgress.setVisibility(View.VISIBLE);
//                exerciseScroll.setVisibility(View.GONE);
//                deleteBtn.setVisibility(View.GONE);
//                mExercisesViewModel.deleteExercise(exercise);
//            }
//        });
    }

    private void getImage(final String name, final ImageView imageView) {
        File imageFile = mExercisesViewModel.getFileByName(name);
        Glide.with(this).load(imageFile).into(imageView);
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
