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
import com.example.yetanotherfitapp.YafaApplication;
import com.example.yetanotherfitapp.database.ExerciseTitle;

import java.util.ArrayList;
import java.util.List;

public class ExerciseListFragment extends Fragment {

    private OnExListStateChangedListener mOnExListChangedListener;
    private ExercisesViewModel mExercisesViewModel;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mOnExListChangedListener = (OnExListStateChangedListener) context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_exercise_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mOnExListChangedListener.setActionBarTitle("Список упражнений");

        RecyclerView recyclerView = view.findViewById(R.id.exercise_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        final ExerciseAdapter exerciseAdapter = new ExerciseAdapter();
        recyclerView.setAdapter(exerciseAdapter);

        mExercisesViewModel = ViewModelProviders.of(this).get(ExercisesViewModel.class);
        mExercisesViewModel.clearMessage();
        mExercisesViewModel.getNetworkState().observe(this, new Observer<YafaApplication.NetworkState>() {
            @Override
            public void onChanged(@Nullable YafaApplication.NetworkState networkState) {
                if (networkState == YafaApplication.NetworkState.AVAILABLE) {
                    observeGlobalData(exerciseAdapter);
                } else {
                    observeLocalData(exerciseAdapter);
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

    private void observeGlobalData(final ExerciseAdapter exerciseAdapter) {
        mExercisesViewModel.getListTitles().observe(this, new Observer<List<ExerciseTitle>>() {
            @Override
            public void onChanged(@Nullable List<ExerciseTitle> exerciseTitles) {
                if (exerciseTitles != null) {
                    if (exerciseTitles.isEmpty()) {
                        mExercisesViewModel.downloadTitles();
                    } else {
                        exerciseAdapter.setTitles(exerciseTitles);
                    }
                }
            }
        });
    }

    private void observeLocalData(final ExerciseAdapter exerciseAdapter) {
        mExercisesViewModel.getLocalTitles().observe(this, new Observer<List<ExerciseTitle>>() {
            @Override
            public void onChanged(@Nullable List<ExerciseTitle> exerciseTitles) {
                if (exerciseTitles != null) {
                    exerciseAdapter.setTitles(exerciseTitles);
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
        public void onBindViewHolder(@NonNull final ExerciseViewHolder exerciseViewHolder, int i) {
            final ExerciseTitle currentTitle = mExerciseTitles.get(i);
            exerciseViewHolder.listElementTitle.setText(currentTitle.title);
            exerciseViewHolder.listElementTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnExListChangedListener.goToExercise(currentTitle.id, currentTitle.title);
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

    public interface OnExListStateChangedListener {
        void goToExercise(String exerciseId, String title);

        void goToStatistic();

        void setActionBarTitle(String title);

        void showFail(String errMsg);
    }

}
