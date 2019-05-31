package com.example.yetanotherfitapp.user_account;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.yetanotherfitapp.R;

class ExerciseViewHolder extends RecyclerView.ViewHolder {

    TextView listElementTitle;

    ExerciseViewHolder(@NonNull View itemView) {
        super(itemView);
        listElementTitle = itemView.findViewById(R.id.list_element_title);
    }
}
