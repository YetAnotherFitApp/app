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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.yetanotherfitapp.R;
import com.example.yetanotherfitapp.database.ExerciseTitle;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

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


        //RecyclerView recyclerView = view.findViewById(R.id.exercise_list);
        //recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        RecyclerView recyclerView = view.findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        //final ExerciseAdapter exerciseAdapter = new ExerciseAdapter();
        //recyclerView.setAdapter(exerciseAdapter);

        final ArrayList<Description> listt = new ArrayList<>();




        mExercisesViewModel = ViewModelProviders.of(this).get(ExercisesViewModel.class);
        mExercisesViewModel.getListTitles().observe(this, new Observer<List<ExerciseTitle>>() {
            @Override
            public void onChanged(@Nullable List<ExerciseTitle> exerciseTitles) {
                if (exerciseTitles != null) {
                    if (exerciseTitles.isEmpty()) {
                        mExercisesViewModel.downloadTitles();
                    } else {
                        ArrayList<Title> temp = new ArrayList<>();
                        temp.add(new Title(""));
                        Description google = new Description("Google", temp);
                        listt.add(google);

                    }
                }
            }
        });

        System.out.println(listt.size());


        mExercisesViewModel.getErrorMessage().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                if (s != null) {
                    mOnExListChangedListener.showFail(s);
                }
            }
        });

        SpecialAdapter adapter = new SpecialAdapter(listt);
        recyclerView.setAdapter(adapter);

       /* mExercisesViewModel = ViewModelProviders.of(this).get(ExercisesViewModel.class);
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
        mExercisesViewModel.getErrorMessage().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                if (s != null) {
                    mOnExListChangedListener.showFail(s);
                }
            }
        });*/
    }

    private class ExerciseViewHolder extends RecyclerView.ViewHolder {

        TextView listElementTitle;

        ExerciseViewHolder(@NonNull View itemView) {
            super(itemView);
            listElementTitle = itemView.findViewById(R.id.list_element_title);
        }
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
                    mOnExListChangedListener.goToExercise(currentTitle.id);
                    FirebaseFirestore
                            .getInstance()
                            .collection("users")
                            .document(FirebaseAuth.getInstance().getUid())
                            .collection("exercisesInfo")
                            .document("NumOfDone")
                            .get()
                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    long numOfDone = (long) documentSnapshot.get(currentTitle.id);
                                    Log.d("smth", String.valueOf(numOfDone));
                                    FirebaseFirestore
                                            .getInstance()
                                            .collection("users")
                                            .document(FirebaseAuth.getInstance().getUid())
                                            .collection("exercisesInfo")
                                            .document("NumOfDone")
                                            .update(currentTitle.id, ++numOfDone).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d("smth", "Done");
                                        }
                                    });
                                    Log.d("smth", String.valueOf(numOfDone));


                                }
                            });
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

        void goToExercise(String exerciseId);

        void showFail(String errMsg);
    }

}
