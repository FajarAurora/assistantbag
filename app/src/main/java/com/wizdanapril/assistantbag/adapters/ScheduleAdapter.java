package com.wizdanapril.assistantbag.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wizdanapril.assistantbag.R;
import com.wizdanapril.assistantbag.fragments.ScheduleFragment;
import com.wizdanapril.assistantbag.models.Catalog;
import com.wizdanapril.assistantbag.models.Constant;
import com.wizdanapril.assistantbag.utils.LinkedMap;

import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ViewHolder>  {

    private LinkedMap<String, Boolean> scheduleList;
    private ScheduleFragment scheduleFragment;
    private String day;
    private DatabaseReference scheduleReference, catalogReference;

    public ScheduleAdapter(LinkedMap<String, Boolean> scheduleList,
                           ScheduleFragment scheduleFragment,
                           String day,
                           DatabaseReference scheduleReference,
                           DatabaseReference catalogReference) {
        this.scheduleList = scheduleList;
        this.scheduleFragment = scheduleFragment;
        this.day = day;
        this.scheduleReference = scheduleReference;
        this.catalogReference = catalogReference;
    }

    @Override
    public ScheduleAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ScheduleAdapter.ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_schedule, parent, false));
    }

    @Override
    public void onBindViewHolder(final ScheduleAdapter.ViewHolder holder, int position) {

        final String key = scheduleList.getKey(position);
        holder.tagId.setText(key);

        catalogReference.child(key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Catalog catalog = dataSnapshot.getValue(Catalog.class);
                String name = catalog != null ? catalog.name : null;

                holder.tagName.setText(name);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        if (!scheduleFragment.isInActionMode) {
            holder.itemCheckBox.setVisibility(View.INVISIBLE);
        } else {
            holder.itemCheckBox.setVisibility(View.VISIBLE);
            holder.itemCheckBox.setChecked(false);
        }

    }

    @Override
    public int getItemCount() {
        return scheduleList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

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

            itemCardView.setOnLongClickListener(scheduleFragment);
            itemCheckBox.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            scheduleFragment.prepareSelection(view, getAdapterPosition());
        }
    }


    public void removeAdapter(LinkedMap<String, Boolean> selectionList, Context context) {

        for (Map.Entry<String, Boolean> schedule : selectionList.entrySet()) {
            catalogReference.child(schedule.getKey()).child("schedule").child(day).removeValue();
            scheduleReference.child(day).child("member").child(schedule.getKey()).removeValue();
        }
        notifyDataSetChanged();
        Toast.makeText(context, context.getResources().getString(R.string.schedule_deleted), Toast.LENGTH_SHORT).show();
    }

}
