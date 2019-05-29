package com.example.yetanotherfitapp.user_account;

import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import java.util.List;

public class Description extends ExpandableGroup<Title> {
    public Description(String title, List<Title> items) {
        super(title, items);
    }
}
