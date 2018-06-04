package com.wizdanapril.assistantbag.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wizdanapril.assistantbag.R;
import com.wizdanapril.assistantbag.models.Catalog;

import java.util.List;

public class ActiveAdapter extends  RecyclerView.Adapter<ActiveAdapter.ViewHolder>  {

    private List<Catalog> catalogList;

    public ActiveAdapter(List<Catalog> catalogList) {
        this.catalogList = catalogList;
    }

    @Override
    public ActiveAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ActiveAdapter.ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_active, parent, false));
    }

    @Override
    public void onBindViewHolder(ActiveAdapter.ViewHolder holder, int position) {
        Catalog active = catalogList.get(position);
        holder.tagName.setText(active.name);
        holder.tagId.setText(active.id);
        holder.tagDate.setText(active.lastReadDate);
        holder.tagTime.setText(active.lastReadTime);
    }

    @Override
    public int getItemCount() {
        return catalogList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView tagName;
        TextView tagId;
        TextView tagDate;
        TextView tagTime;

        private ViewHolder(View itemView) {
            super(itemView);

            tagName = (TextView) itemView.findViewById(R.id.tv_name);
            tagId = (TextView) itemView.findViewById(R.id.tv_id);
            tagDate = (TextView) itemView.findViewById(R.id.tv_date);
            tagTime = (TextView) itemView.findViewById(R.id.tv_time);
        }
    }
}
