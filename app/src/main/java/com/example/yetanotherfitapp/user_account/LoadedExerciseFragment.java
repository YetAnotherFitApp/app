package com.example.yetanotherfitapp.user_account;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.yetanotherfitapp.R;
import com.example.yetanotherfitapp.database.ExerciseTitle;

import java.util.ArrayList;
import java.util.List;

public class LoadedExerciseFragment extends Fragment {

    private ExerciseListFragment.OnExListStateChangedListener mOnExListChangedListener;
    private ExercisesViewModel mExercisesViewModel;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mOnExListChangedListener = (ExerciseListFragment.OnExListStateChangedListener) context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_exercise_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView recyclerView = view.findViewById(R.id.exercise_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        final ExerciseAdapter exerciseAdapter = new ExerciseAdapter();
        recyclerView.setAdapter(exerciseAdapter);

        mExercisesViewModel = ViewModelProviders.of(this).get(ExercisesViewModel.class);
        mExercisesViewModel.getLocalTitles().observe(this, new Observer<List<ExerciseTitle>>() {
            @Override
            public void onChanged(@Nullable List<ExerciseTitle> exerciseTitles) {
                if (exerciseTitles != null) {
                    exerciseAdapter.setTitles(exerciseTitles);
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

    private class ExerciseAdapter extends RecyclerView.Adapter<ExerciseViewHolder> {

        private List<ExerciseTitle> mExerciseTitles = new ArrayList<>();

        void setTitles(List<ExerciseTitle> titles) {
            mExerciseTitles = titles;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ExerciseViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
            View view = inflater.inflate(R.layout.exercise_list_element, viewGroup, false);
            return new ExerciseViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ExerciseViewHolder exerciseViewHolder, int i) {
            final ExerciseTitle currentTitle = mExerciseTitles.get(i);
            exerciseViewHolder.listElementTitle.setText(currentTitle.title);
            exerciseViewHolder.listElementTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnExListChangedListener.goToExercise(currentTitle.id);
                    mExercisesViewModel.incrementNumOfDone(currentTitle.id);
                }
            });
        }

        @Override
        public int getItemCount() {
            return mExerciseTitles.size();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mOnExListChangedListener = null;
    }

}
