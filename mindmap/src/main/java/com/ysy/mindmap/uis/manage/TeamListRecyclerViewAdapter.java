package com.ysy.mindmap.uis.manage;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ysy.mindmap.R;
import com.ysy.mindmap.bases.BaseRecyclerViewAdapter;
import com.ysy.mindmap.models.datas.DataTeam;

/**
 * Created by Administrator on 2017/6/4.
 */

public class TeamListRecyclerViewAdapter extends BaseRecyclerViewAdapter<DataTeam> {

    private Context mContext;

    public TeamListRecyclerViewAdapter(Context context) {
        mContext = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_team_list, parent, false);
        return new RecyclerViewHolder(view);
    }

    private class RecyclerViewHolder extends RecyclerView.ViewHolder {
        TextView teamnameTv;
        TextView createTimeTV;
        TextView introTv;

        RecyclerViewHolder(View itemView) {
            super(itemView);
            teamnameTv = (TextView) itemView.findViewById(R.id.item_team_teamname_tv);
            createTimeTV = (TextView) itemView.findViewById(R.id.item_team_create_time_tv);
            introTv = (TextView) itemView.findViewById(R.id.item_team_intro_tv);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        final RecyclerViewHolder holder = (RecyclerViewHolder) viewHolder;
        DataTeam team = getItem(position);
        if (team != null) {
            if (!TextUtils.isEmpty(team.getIntro()))
                holder.introTv.setText(team.getIntro());
            if (!TextUtils.isEmpty(team.getTeamname()))
                holder.teamnameTv.setText(team.getTeamname());
            if (!TextUtils.isEmpty(team.getCreateTime()))
                holder.createTimeTV.setText("成立时间：" + team.getCreateTime().substring(0, 19));
        }

        super.onBindViewHolder(holder, position);
    }
}
