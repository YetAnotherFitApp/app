package com.example.yetanotherfitapp.user_account;

import android.view.View;
import android.widget.TextView;

import com.example.yetanotherfitapp.R;
import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder;

public class TitleViewHolder extends ChildViewHolder {
    private TextView mTextView;

    public TitleViewHolder(View itemView) {
        super(itemView);
        mTextView = itemView.findViewById(R.id.textView);
    }

    public void bind(Title product){

        mTextView.setText(product.name);
    }
}

