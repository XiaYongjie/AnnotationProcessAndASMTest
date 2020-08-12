package com.example.myapplication;


import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MyLayoutManager extends RecyclerView.LayoutManager {

    @Override
    public void measureChild(@NonNull View child, int widthUsed, int heightUsed) {
        super.measureChild(child, widthUsed, heightUsed);
    }

    @Override
    public void measureChildWithMargins(@NonNull View child, int widthUsed, int heightUsed) {
        super.measureChildWithMargins(child, widthUsed, heightUsed);
    }

    @Override
    public int getDecoratedMeasuredWidth(@NonNull View child) {
        return super.getDecoratedMeasuredWidth(child);
    }

    @Override
    public int getDecoratedMeasuredHeight(@NonNull View child) {
        return super.getDecoratedMeasuredHeight(child);
    }

    @Override
    public void layoutDecorated(@NonNull View child, int left, int top, int right, int bottom) {
        super.layoutDecorated(child, left, top, right, bottom);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        super.onLayoutChildren(recycler, state);

    }

    @Override
    public void layoutDecoratedWithMargins(@NonNull View child, int left, int top, int right, int bottom) {
        super.layoutDecoratedWithMargins(child, left, top, right, bottom);
    }

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return null;
    }
}
