package com.ysy.mindmap.bases;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.StringSignature;
import com.ysy.mindmap.models.listeners.NoDoubleViewClickListener;
import com.ysy.mindmap.models.listeners.OnRecyclerViewItemClickListener;
import com.ysy.mindmap.views.CircularImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * 全能RecyclerView适配器基类
 * Created by Sylvester on 17/4/19.
 */
public class BaseRecyclerViewAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    protected List<T> mDatas;
    protected boolean isScrolling = false;
    private static final int MAX_EXIST_COUNT = 9;

    private OnRecyclerViewItemClickListener mOnItemClickListener;

    public BaseRecyclerViewAdapter() {
        mDatas = new ArrayList<>();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public int getItemCount() {
        return mDatas == null ? 0 :
                (mDatas.size() <= maxExistCount ? mDatas.size() : mDatas.size() + 1);
    }

    public T getItem(int position) {
        return mDatas == null ? null : mDatas.get(position);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if (mOnItemClickListener != null) {
            holder.itemView.setOnClickListener(new NoDoubleViewClickListener() {
                @Override
                protected void onNoDoubleClick(View v) {
                    int pos = holder.getLayoutPosition();
                    mOnItemClickListener.onItemClick(holder.itemView, pos);
                }
            });

            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int pos = holder.getLayoutPosition();
                    mOnItemClickListener.onItemLongClick(holder.itemView, pos);
                    return false;
                }
            });
        }
    }

    public void addAll(List<T> mList) {
        if (mList != null) {
            mDatas.clear();
            mDatas.addAll(mList);
            setMaxExistCount(MAX_EXIST_COUNT);
            notifyDataSetChanged();
        }
    }

    public void addMore(List<T> mList) {
        if (mList != null) {
            if (mList.size() == 0)
                setMaxExistCount(getItemCount() - 1);
            else
                mDatas.addAll(mList);
            notifyDataSetChanged();
        }
    }

    protected int maxExistCount = 9;
    protected boolean isLoading = true;

    protected FootLoadCallBack loadCallBack;

    public interface FootLoadCallBack {
        void onLoad();
    }

    public void setOnItemClickListener(OnRecyclerViewItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    public void setScrolling(boolean scrolling) {
        isScrolling = scrolling;
    }

    public void setMaxExistCount(int maxExistCount) {
        this.maxExistCount = maxExistCount;
    }

    public void setFootLoadCallBack(FootLoadCallBack loadCallBack) {
        this.loadCallBack = loadCallBack;
    }

    public void setIsLoading(boolean isLoading) {
        this.isLoading = isLoading;
    }

    protected void downloadAvatar(Context context, String url, CircularImageView avatarImg, int defaultResId) {
        Glide.with(context).load(url)
                .asBitmap()
                .signature(new StringSignature("" + System.currentTimeMillis()))
                .placeholder(defaultResId)
                .error(defaultResId)
                .into(avatarImg);
    }
}
