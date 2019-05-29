package com.example.yetanotherfitapp.user_account;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.yetanotherfitapp.R;
import com.thoughtbot.expandablerecyclerview.ExpandableRecyclerViewAdapter;
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import java.util.List;

public class SpecialAdapter extends ExpandableRecyclerViewAdapter<DescriptionViewHolder, TitleViewHolder> {
    public SpecialAdapter(List<? extends ExpandableGroup> groups) {
        super(groups);
    }

    @Override
    public DescriptionViewHolder onCreateGroupViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.expanable_recycleview_title,parent,false);
        return new DescriptionViewHolder(v);
    }

    @Override
    public TitleViewHolder onCreateChildViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.expanable_recyclerview_description,parent,false);
        return new TitleViewHolder(v);
    }

    @Override
    public void onBindChildViewHolder(TitleViewHolder holder, int flatPosition, ExpandableGroup group, int childIndex) {
        final Title product = (Title) group.getItems().get(childIndex);
        holder.bind(product);
    }

    @Override
    public void onBindGroupViewHolder(DescriptionViewHolder holder, int flatPosition, ExpandableGroup group) {
        final Description company = (Description) group;
        holder.bind(company);
    }
}

