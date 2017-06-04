package com.ysy.mindmap.uis.manage;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ysy.mindmap.R;
import com.ysy.mindmap.bases.BaseRecyclerViewAdapter;
import com.ysy.mindmap.models.datas.DataTeamMindMap;

/**
 * Created by Administrator on 2017/6/4.
 */

public class UserMapListRecyclerViewAdapter extends BaseRecyclerViewAdapter<DataTeamMindMap> {

    private Context mContext;

    public UserMapListRecyclerViewAdapter(Context context) {
        mContext = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_map_list, parent, false);
        return new RecyclerViewHolder(view);
    }

    private class RecyclerViewHolder extends RecyclerView.ViewHolder {
        TextView mapnameTv;
        TextView updateTimeTV;
        TextView introTv;
        TextView ownTeamTv;

        RecyclerViewHolder(View itemView) {
            super(itemView);
            mapnameTv = (TextView) itemView.findViewById(R.id.item_user_map_name_tv);
            updateTimeTV = (TextView) itemView.findViewById(R.id.item_user_map_update_time_tv);
            introTv = (TextView) itemView.findViewById(R.id.item_user_map_intro_tv);
            ownTeamTv = (TextView) itemView.findViewById(R.id.item_user_map_own_team_tv);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        final RecyclerViewHolder holder = (RecyclerViewHolder) viewHolder;
        DataTeamMindMap map = getItem(position);
        if (map != null) {
            if (!TextUtils.isEmpty(map.getIntro()))
                holder.introTv.setText(map.getIntro());
            if (!TextUtils.isEmpty(map.getMapname()))
                holder.mapnameTv.setText(map.getMapname());
            if (!TextUtils.isEmpty(map.getUpdateTime()))
                holder.updateTimeTV.setText("更新时间：" + map.getUpdateTime().substring(0, 19));
            if (!TextUtils.isEmpty(map.getOwnTeam()))
                holder.ownTeamTv.setText("所属团队：" + map.getOwnTeam());
        }

        super.onBindViewHolder(holder, position);
    }
}
