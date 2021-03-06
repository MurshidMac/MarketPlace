package com.example.gabbygiordano.marketplace.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.gabbygiordano.marketplace.Item;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.List;

/**
 * Created by tanvigupta on 7/17/17.
 */

public class ClothesTimelineFragment extends ItemsListFragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);

        // populateTimeline();
    }

    @Override
    public void populateTimeline() {
        ((ProgressListener) getActivity()).showProgressBar();
        ParseQuery<Item> query = ParseQuery.getQuery(Item.class);
        query.include("owner");
        query.include("image");
        query.whereEqualTo("type", "Clothes");
        query.orderByDescending("_created_at");
        query.setLimit(limit); // 20 items per page
        query.setSkip(page * limit); // skip first (page * 20) items
        query.findInBackground(new FindCallback<Item>() {
            public void done(List<Item> itemsList, ParseException e) {
                ((ProgressListener) getActivity()).hideProgressBar();
                if (e == null) {
                    if (itemsList != null && !itemsList.isEmpty()) {
                        addItems(itemsList);
                    }
                } else {
                    Log.d("ClothesFragment", e.getMessage());
                    scrollListener.resetState();
                }
            }
        });
    }

    @Override
    public void fetchTimelineAsync(int page) {
        ((ProgressListener) getActivity()).showProgressBar();
        ParseQuery<Item> query = ParseQuery.getQuery(Item.class);
        query.include("owner");
        query.include("image");
        query.whereEqualTo("type", "Clothes");
        query.orderByDescending("_created_at");
        query.setLimit(limit); // 20 items per page
        query.setSkip(page * limit); // skip first (page * 20) items
        query.findInBackground(new FindCallback<Item>() {
            public void done(List<Item> itemsList, ParseException e) {
                ((ProgressListener) getActivity()).hideProgressBar();
                if (e == null) {
                    if (itemsList != null && !itemsList.isEmpty()) {
                        refreshItems(itemsList);
                    }
                } else {
                    Log.d("ClothesFragment", e.getMessage());
                    swipeContainer.setRefreshing(false);
                    scrollListener.resetState();
                }
            }
        });
    }
}
