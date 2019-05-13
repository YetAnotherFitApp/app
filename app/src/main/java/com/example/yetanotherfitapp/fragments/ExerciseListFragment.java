package com.example.yetanotherfitapp.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.yetanotherfitapp.R;

public class ExerciseListFragment extends Fragment {

    private OnExListChangedListener mOnExListChangedListener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mOnExListChangedListener = (OnExListChangedListener) getActivity();
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_exercise_list, container, false);

        view.findViewById(R.id.goExBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnExListChangedListener.goToExercise();
            }
        });

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.user_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.signOut) {
            mOnExListChangedListener.signOut();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public interface OnExListChangedListener {
        void signOut();

        void goToExercise();
    }
}
