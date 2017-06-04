package com.ysy.mindmap.models.listeners;

import android.view.View;

public interface OnRecyclerViewItemClickListener {
    void onItemClick(View view, int position);

    void onItemLongClick(View view, int position);
}