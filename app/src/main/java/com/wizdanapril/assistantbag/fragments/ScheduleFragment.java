package com.wizdanapril.assistantbag.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wizdanapril.assistantbag.R;
import com.wizdanapril.assistantbag.activities.SelectionActivity;
import com.wizdanapril.assistantbag.adapters.ScheduleAdapter;
import com.wizdanapril.assistantbag.models.Schedule;
import com.wizdanapril.assistantbag.utils.Constant;
import com.wizdanapril.assistantbag.utils.LinkedMap;

import org.w3c.dom.Text;

import java.util.Map;


public class ScheduleFragment extends Fragment implements View.OnLongClickListener {

    private static final String ARG_PAGE = "arg_page";
    private String day[] = new String[] {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};

    private LinkedMap<String, Boolean> scheduleList, selectionList;

    private DatabaseReference scheduleReference = FirebaseDatabase.getInstance()
            .getReference(Constant.USER).child(Constant.SCHEDULE);

    private ScheduleAdapter scheduleAdapter;

    private RecyclerView recyclerView;
    private TextView emptyText, plusIcon;
    private ImageView deleteIcon;

    public boolean isInActionMode = false;

    public ScheduleFragment() {

    }

    public static ScheduleFragment newInstance(int pageNumber) {

        Bundle args = new Bundle();
        ScheduleFragment fragment = new ScheduleFragment();
        args.putInt(ARG_PAGE, pageNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Bundle args = getArguments();
        final int pageNumber = args.getInt(ARG_PAGE);

        View view = inflater.inflate(R.layout.fragment_schedule, container, false);

        plusIcon = (TextView) view.findViewById(R.id.tv_add_schedule);
        deleteIcon = (ImageView) view.findViewById(R.id.iv_delete_schedule);

        plusIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        deleteIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isInActionMode = false;
                scheduleAdapter.removeAdapter(selectionList, getActivity());
            }
        });

        emptyText = (TextView) view.findViewById(R.id.tv_no_data);
        recyclerView = (RecyclerView) view.findViewById(R.id.rv_tag);

        emptyText.setText(R.string.no_schedule);
        emptyText.setGravity(Gravity.CENTER);

        scheduleList = new LinkedMap<>();
        selectionList = new LinkedMap<>();

        recyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        recyclerView.setLayoutManager(layoutManager);

        scheduleAdapter = new ScheduleAdapter(scheduleList,this, day[pageNumber]);
        recyclerView.setAdapter(scheduleAdapter);

        updateList(pageNumber);

        TextView plusIcon = (TextView) view.findViewById(R.id.tv_add_schedule);
        plusIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), SelectionActivity.class);
                Bundle args = new Bundle();
                args.putSerializable("LINKEDMAP", scheduleList);
                intent.putExtra("BUNDLE", args);
                intent.putExtra("DAY", day[pageNumber]);

                startActivity(intent);
            }
        });


        return view;
    }

    private void updateList(final int pageNumber) {
        scheduleReference.child(day[pageNumber]).addValueEventListener(new ValueEventListener() {
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
}
