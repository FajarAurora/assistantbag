package com.wizdanapril.assistantbag.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wizdanapril.assistantbag.R;
import com.wizdanapril.assistantbag.adapters.ScheduleMondayAdapter;
import com.wizdanapril.assistantbag.models.Schedule;
import com.wizdanapril.assistantbag.utils.Constant;
import com.wizdanapril.assistantbag.utils.LinkedMap;

import java.util.Map;

public class ScheduleMondayActivity extends AppCompatActivity implements View.OnLongClickListener {

    private String day = "monday";

    private LinkedMap<String, Boolean> scheduleList, selectionList;

    private DatabaseReference scheduleReference, catalogReference;

    private ScheduleMondayAdapter scheduleAdapter;

    private RecyclerView recyclerView;
    private TextView emptyText, plusIcon;
    private ImageView deleteIcon;

    public boolean isInActionMode = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_monday);

        SharedPreferences preferences = this.getSharedPreferences("LoggedAccount", MODE_PRIVATE);
        String userAccount = preferences.getString("userAccount", "error");
        String deviceId = preferences.getString("deviceId", "error");
        scheduleReference = FirebaseDatabase.getInstance().getReference(Constant.DATA)
                .child(userAccount).child(deviceId).child(Constant.SCHEDULE);
        catalogReference = FirebaseDatabase.getInstance().getReference(Constant.DATA)
                .child(userAccount).child(deviceId).child(Constant.CATALOG);

        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        toolbar.setTitle(R.string.schedule);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        Button tuesdayButton = (Button) findViewById(R.id.bt_tuesday);
        Button wednesdayButton = (Button) findViewById(R.id.bt_wednesday);
        Button thursdayButton = (Button) findViewById(R.id.bt_thursday);
        Button fridayButton = (Button) findViewById(R.id.bt_friday);
        Button saturdayButton = (Button) findViewById(R.id.bt_saturday);
        Button sundayButton = (Button) findViewById(R.id.bt_sunday);

        tuesdayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ScheduleMondayActivity.this,
                        ScheduleTuesdayActivity.class));
                overridePendingTransition(0,0);
            }
        });

        wednesdayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ScheduleMondayActivity.this,
                        ScheduleWednesdayActivity.class));
                overridePendingTransition(0,0);
            }
        });

        thursdayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ScheduleMondayActivity.this,
                        ScheduleThursdayActivity.class));
                overridePendingTransition(0,0);
            }
        });

        fridayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ScheduleMondayActivity.this,
                        ScheduleFridayActivity.class));
                overridePendingTransition(0,0);
            }
        });

        saturdayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ScheduleMondayActivity.this,
                        ScheduleSaturdayActivity.class));
                overridePendingTransition(0,0);
            }
        });

        sundayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ScheduleMondayActivity.this,
                        ScheduleSundayActivity.class));
                overridePendingTransition(0,0);
            }
        });

        plusIcon = (TextView) findViewById(R.id.tv_add_schedule);
        deleteIcon = (ImageView) findViewById(R.id.iv_delete_schedule);

        plusIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        deleteIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isInActionMode = false;
                scheduleAdapter.removeAdapter(selectionList, ScheduleMondayActivity.this);
                clearActionMode();
            }
        });

        emptyText = (TextView) findViewById(R.id.tv_no_data);
        recyclerView = (RecyclerView) findViewById(R.id.rv_tag);

        emptyText.setText(R.string.no_schedule);
        emptyText.setGravity(Gravity.CENTER);

        scheduleList = new LinkedMap<>();
        selectionList = new LinkedMap<>();

        recyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        recyclerView.setLayoutManager(layoutManager);

        scheduleAdapter = new ScheduleMondayAdapter(scheduleList, this,
                day, scheduleReference, catalogReference);
        recyclerView.setAdapter(scheduleAdapter);

        updateList();

        TextView plusIcon = (TextView) findViewById(R.id.tv_add_schedule);
        plusIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ScheduleMondayActivity.this, SelectionActivity.class);
                Bundle args = new Bundle();
                args.putSerializable("LINKEDMAP", scheduleList);
                intent.putExtra("BUNDLE", args);
                intent.putExtra("DAY", day);

                startActivity(intent);
            }
        });
    }


    private void updateList() {
        scheduleReference.child(day).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                scheduleList.clear();
                Schedule schedule = dataSnapshot.getValue(Schedule.class);
                if (schedule != null) {
                    for (Map.Entry<String, Boolean> entry : schedule.member.entrySet()) {
                        String key = entry.getKey();
                        scheduleList.put(key, true);
                    }
                    scheduleAdapter.notifyDataSetChanged();

                }
                checkIfEmpty();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void checkIfEmpty() {
        if (scheduleList.size() == 0) {
            recyclerView.setVisibility(View.INVISIBLE);
            emptyText.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyText.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public boolean onLongClick(View view) {
        isInActionMode = true;
        plusIcon.setVisibility(View.INVISIBLE);
        deleteIcon.setVisibility(View.VISIBLE);
        scheduleAdapter.notifyDataSetChanged();

        return false;
    }

    public void prepareSelection(View view, int position) {
        if (((CheckBox) view).isChecked()) {
            selectionList.put(scheduleList.getKey(position), scheduleList.getValue(position));
        } else {
            selectionList.remove(scheduleList.getKey(position));
        }
    }

    public void clearActionMode() {
        isInActionMode = false;
        plusIcon.setVisibility(View.VISIBLE);
        deleteIcon.setVisibility(View.INVISIBLE);
        selectionList.clear();
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(ScheduleMondayActivity.this, HomeActivity.class));
    }
}
