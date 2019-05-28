package com.example.yetanotherfitapp.user_account;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toolbar;

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
        View view = inflater.inflate(R.layout.fragment_exercise, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        mExercisesViewModel = ViewModelProviders.of(this).get(ExercisesViewModel.class);
        mExercisesViewModel.getExerciseById(mId).observe(this, new Observer<Exercise>() {
            @Override
            public void onChanged(@Nullable Exercise exercise) {
                if (exercise == null) {
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

    //TODO: обработать кейс с повторной записью в бд
    private void exerciseNotLoaded(final View view) {
        LinearLayout mainLayout = view.findViewById(R.id.ex_main_layout);
        mainLayout.setGravity(Gravity.CENTER);

        TextView title = view.findViewById(R.id.ex_title);
        title.setText(R.string.exercise_not_load);

        final ProgressBar downloadProgress = view.findViewById(R.id.ex_progress);
        downloadProgress.setVisibility(View.GONE);

        ScrollView exerciseScroll = view.findViewById(R.id.ex_scroll_view);
        exerciseScroll.setVisibility(View.GONE);

        ImageButton deleteBtn = view.findViewById(R.id.ex_delete);
        deleteBtn.setVisibility(View.GONE);

        final ImageButton downloadBtn = view.findViewById(R.id.ex_download_btn);
        downloadBtn.setVisibility(View.VISIBLE);
        downloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadBtn.setVisibility(View.GONE);
                downloadProgress.setVisibility(View.VISIBLE);
                mExercisesViewModel.downloadExercise(mId);
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

    private void exerciseIsLoaded(final View view, final Exercise exercise) {
        final LinearLayout mainLayout = view.findViewById(R.id.ex_main_layout);
        mainLayout.setGravity(Gravity.CENTER | Gravity.TOP);

        final TextView title = view.findViewById(R.id.ex_title);
        title.setText(exercise.title);

        ImageButton downloadBtn = view.findViewById(R.id.ex_download_btn);
        downloadBtn.setVisibility(View.GONE);

        final ProgressBar downloadProgress = view.findViewById(R.id.ex_progress);
        downloadProgress.setVisibility(View.GONE);

        final ScrollView exerciseScroll = view.findViewById(R.id.ex_scroll_view);
        exerciseScroll.setVisibility(View.VISIBLE);

        TextView description = view.findViewById(R.id.ex_description);
        description.setText(exercise.description);

        getImage(exercise.imageName, (ImageView) view.findViewById(R.id.ex_image));

        final ImageButton deleteBtn = view.findViewById(R.id.ex_delete);
        deleteBtn.setVisibility(View.VISIBLE);
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainLayout.setGravity(Gravity.CENTER);
                title.setVisibility(View.GONE);
                downloadProgress.setVisibility(View.VISIBLE);
                exerciseScroll.setVisibility(View.GONE);
                deleteBtn.setVisibility(View.GONE);
                mExercisesViewModel.deleteExercise(exercise);
            }
        });
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
