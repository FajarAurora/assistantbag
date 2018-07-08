package com.wizdanapril.assistantbag.adapters;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.wizdanapril.assistantbag.R;
import com.wizdanapril.assistantbag.activities.HomeActivity;
import com.wizdanapril.assistantbag.models.Catalog;
import com.wizdanapril.assistantbag.utils.Constant;

import java.util.List;

public class ActiveAdapter extends  RecyclerView.Adapter<ActiveAdapter.ViewHolder>  {

    private List<Catalog> catalogList;
    private Context context;
    private StorageReference tagImageReference;

    public ActiveAdapter(List<Catalog> catalogList, Context context) {
        this.catalogList = catalogList;
        this.context = context;
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

        if (active.imageUri != null) {
            Uri imageUri = Uri.parse(active.imageUri);
            Picasso.with(context).load(imageUri).into(holder.tagImage);
        }
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
        ImageView tagImage;

        private ViewHolder(View itemView) {
            super(itemView);

            tagName = (TextView) itemView.findViewById(R.id.tv_name);
            tagId = (TextView) itemView.findViewById(R.id.tv_id);
            tagDate = (TextView) itemView.findViewById(R.id.tv_date);
            tagTime = (TextView) itemView.findViewById(R.id.tv_time);
            tagImage = (ImageView) itemView.findViewById(R.id.iv_item);
        }
    }
}
