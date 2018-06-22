package com.wizdanapril.assistantbag.adapters;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.wizdanapril.assistantbag.R;
import com.wizdanapril.assistantbag.activities.SelectionActivity;
import com.wizdanapril.assistantbag.models.Catalog;
import com.wizdanapril.assistantbag.models.Constant;

import java.util.List;

public class SelectionAdapter extends RecyclerView.Adapter<SelectionAdapter.ViewHolder> {

    private List<Catalog> catalogList;
    private String day;
    private SelectionActivity selectionActivity;
    private DatabaseReference scheduleReference, catalogReference;


    public SelectionAdapter(List<Catalog> catalogList, SelectionActivity selectionActivity,
                            String day,
                            DatabaseReference scheduleReference,
                            DatabaseReference catalogReference) {
        this.catalogList = catalogList;
        this.selectionActivity = selectionActivity;
        this.day = day;
        this.scheduleReference = scheduleReference;
        this.catalogReference = catalogReference;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_selection, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Catalog catalog = catalogList.get(position);

        holder.tagName.setText(catalog.name);
        holder.tagId.setText(catalog.id);

        if (!selectionActivity.isInActionMode) {
            holder.itemCheckBox.setVisibility(View.INVISIBLE);
        } else {
            holder.itemCheckBox.setVisibility(View.VISIBLE);
            holder.itemCheckBox.setChecked(false);
        }
    }

    @Override
    public int getItemCount() {
        return catalogList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tagName;
        TextView tagId;
        CheckBox itemCheckBox;
        CardView itemCardView;

        private ViewHolder(View itemView) {
            super(itemView);

            tagName = (TextView) itemView.findViewById(R.id.tv_name);
            tagId = (TextView) itemView.findViewById(R.id.tv_id);
            itemCheckBox = (CheckBox) itemView.findViewById(R.id.check_box);
            itemCardView = (CardView) itemView.findViewById(R.id.card_view);

            itemCardView.setOnLongClickListener(selectionActivity);
            itemCheckBox.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            selectionActivity.prepareSelection(view, getAdapterPosition());
        }
    }

    public void updateAdapter(List<Catalog> selectionList) {

        for (Catalog catalog : selectionList) {
            catalogReference.child(catalog.id).child("schedule").child(day).setValue(true);
            scheduleReference.child(day).child("member").child(catalog.id).setValue(true);
        }
        notifyDataSetChanged();
    }
}