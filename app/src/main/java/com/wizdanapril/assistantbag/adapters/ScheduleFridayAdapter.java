package com.wizdanapril.assistantbag.adapters;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.wizdanapril.assistantbag.R;
import com.wizdanapril.assistantbag.activities.ScheduleFridayActivity;
import com.wizdanapril.assistantbag.activities.ScheduleMondayActivity;
import com.wizdanapril.assistantbag.models.Catalog;
import com.wizdanapril.assistantbag.utils.LinkedMap;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

public class ScheduleFridayAdapter extends RecyclerView.Adapter<ScheduleFridayAdapter.ViewHolder>  {

    private LinkedMap<String, Boolean> scheduleList;
    private ScheduleFridayActivity scheduleActivity;
    private String day;
    private DatabaseReference scheduleReference, catalogReference;

    public ScheduleFridayAdapter(LinkedMap<String, Boolean> scheduleList,
                                 ScheduleFridayActivity scheduleActivity,
                                 String day,
                                 DatabaseReference scheduleReference,
                                 DatabaseReference catalogReference) {
        this.scheduleList = scheduleList;
        this.scheduleActivity = scheduleActivity;
        this.day = day;
        this.scheduleReference = scheduleReference;
        this.catalogReference = catalogReference;
    }

    @Override
    public ScheduleFridayAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ScheduleFridayAdapter.ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_schedule, parent, false));
    }

    @Override
    public void onBindViewHolder(final ScheduleFridayAdapter.ViewHolder holder, int position) {

        final String key = scheduleList.getKey(position);
        holder.tagId.setText(key);

        catalogReference.child(key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Catalog catalog = dataSnapshot.getValue(Catalog.class);
                if (catalog != null) {
                    String name = catalog.name;
                    holder.tagName.setText(name);
//                holder.tagName.setText(holder.currentDayString);
                    if (catalog.imageUri != null) {
                        Uri imageUri = Uri.parse(catalog.imageUri);
                        Picasso.with(scheduleActivity).load(imageUri).into(holder.tagImage);
                    }
                    if (day.equals(holder.currentDayString) && catalog.status.equals("in")) {
                        holder.itemCardView.setBackgroundColor(scheduleActivity
                                .getResources().getColor(R.color.material_light_green));
                        holder.tagId.setBackground(scheduleActivity
                                .getResources().getDrawable(R.drawable.roun_rect_lightgreen));
                        holder.tagId.setTextColor(Color.WHITE);
                        holder.tagName.setTextColor(Color.WHITE);
                    }

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        if (!scheduleActivity.isInActionMode) {
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

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tagName;
        TextView tagId;
        ImageView tagImage;
        CheckBox itemCheckBox;
        CardView itemCardView;

        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        int currentDayInt = calendar.get(Calendar.DAY_OF_WEEK);
        String currentDayString = (new SimpleDateFormat("EEEE", Locale.ENGLISH).format(date.getTime())).toLowerCase();

        private ViewHolder(View itemView) {
            super(itemView);

            tagName = (TextView) itemView.findViewById(R.id.tv_name);
            tagId = (TextView) itemView.findViewById(R.id.tv_id);
            tagImage = (ImageView) itemView.findViewById(R.id.iv_item);
            itemCheckBox = (CheckBox) itemView.findViewById(R.id.check_box);
            itemCardView = (CardView) itemView.findViewById(R.id.card_view);

            itemCardView.setOnLongClickListener(scheduleActivity);
            itemCheckBox.setOnClickListener(this);

            // Incorrect DAY_OF_WEEK, need + 1
//            switch (currentDayInt) {
//                case Calendar.SUNDAY:
//                    currentDayString = "monday";
//
//                case Calendar.MONDAY:
//                    currentDayString = "tuesday";
//
//                case Calendar.TUESDAY:
//                    currentDayString = "wednesday";
//
//                case Calendar.WEDNESDAY:
//                    currentDayString = "thursday";
//
//                case Calendar.THURSDAY:
//                    currentDayString = "friday";
//
//                case Calendar.FRIDAY:
//                    currentDayString = "saturday";
//
//                case Calendar.SATURDAY:
//                    currentDayString = "sunday";
//            }

        }

        @Override
        public void onClick(View view) {
            scheduleActivity.prepareSelection(view, getAdapterPosition());
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