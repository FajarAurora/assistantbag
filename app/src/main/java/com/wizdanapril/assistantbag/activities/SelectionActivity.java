package com.wizdanapril.assistantbag.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wizdanapril.assistantbag.R;
import com.wizdanapril.assistantbag.adapters.SelectionAdapter;
import com.wizdanapril.assistantbag.models.Catalog;
import com.wizdanapril.assistantbag.models.Constant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SelectionActivity extends AppCompatActivity implements View.OnLongClickListener {

    private Toolbar toolbar;

    private RecyclerView recyclerView;
    private SelectionAdapter selectionAdapter;

    private List<Catalog> catalogList, selectionList;
    private HashMap<String, Boolean> scheduleList;

    private DatabaseReference catalogReference, scheduleReference;

    private TextView emptyText;
    private TextView unselectedText;
    private TextView counterText;

    private int counter = 0;

    public boolean isInActionMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selection);

        SharedPreferences preferences = this.getSharedPreferences("LoggedAccount", MODE_PRIVATE);
        String userAccount = preferences.getString("userAccount", "error");
        String deviceId = preferences.getString("deviceId", "error");
        scheduleReference = FirebaseDatabase.getInstance().getReference(Constant.DATA)
                .child(userAccount).child(deviceId).child(Constant.SCHEDULE);
        catalogReference = FirebaseDatabase.getInstance().getReference(Constant.DATA)
                .child(userAccount).child(deviceId).child(Constant.CATALOG);

        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        toolbar.setTitle(R.string.unselected);
        emptyText = (TextView) findViewById(R.id.tv_no_data);

        Intent intent = getIntent();
        Bundle args = intent.getBundleExtra("BUNDLE");
        scheduleList = (HashMap<String, Boolean>) args.getSerializable("LINKEDMAP");
        String day = intent.getStringExtra("DAY");

        catalogList = new ArrayList<>();
        selectionList = new ArrayList<>();

        recyclerView = (RecyclerView) findViewById(R.id.rv_tag);
        recyclerView.setHasFixedSize(true);

        unselectedText = (TextView) findViewById(R.id.tv_unselected);
        counterText = (TextView) findViewById(R.id.tv_counter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        recyclerView.setLayoutManager(layoutManager);

        selectionAdapter = new SelectionAdapter(catalogList, this, day, scheduleReference, catalogReference);
        recyclerView.setAdapter(selectionAdapter);

        updateList();
    }

    private void updateList() {

        catalogReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot children : dataSnapshot.getChildren()) {

                    Catalog catalog = children.getValue(Catalog.class);

                        if (catalog != null && !scheduleList.containsKey(catalog.id)) {
                            catalogList.add(catalog);
                            selectionAdapter.notifyDataSetChanged();
                        }

                    checkIfEmpty();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void checkIfEmpty() {
        if (catalogList.size() == 0) {
            recyclerView.setVisibility(View.INVISIBLE);
            emptyText.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyText.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                clearActionMode();
                selectionAdapter.notifyDataSetChanged();
                Intent intent = NavUtils.getParentActivityIntent(this);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                NavUtils.navigateUpTo(this, intent);
//                this.overridePendingTransition(R.anim.exit_current, R.anim.exit_new);
                break;

            case R.id.menu_item_add:
                isInActionMode = false;
                selectionAdapter.updateAdapter(selectionList);
                finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (isInActionMode) {
            clearActionMode();
            selectionAdapter.notifyDataSetChanged();
        } else {
            super.onBackPressed();
//            overridePendingTransition(R.anim.exit_current, R.anim.exit_new);
        }
    }

    @Override
    public boolean onLongClick(View view) {
        toolbar.getMenu().clear();
        toolbar.inflateMenu(R.menu.action_menu_add);
        toolbar.setTitle(null);
        unselectedText.setVisibility(View.GONE);
        counterText.setVisibility(View.VISIBLE);
        isInActionMode = true;
        selectionAdapter.notifyDataSetChanged();

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        return true;
    }

    public void prepareSelection(View view, int position) {
        if (((CheckBox) view).isChecked()) {
            selectionList.add(catalogList.get(position));
            counter = counter + 1;
            updateCounter(counter);

        } else {
            selectionList.remove(catalogList.get(position));
            counter = counter - 1;
            updateCounter(counter);
        }

    }

    public void updateCounter(int counter) {
        String numSelected;
        if (counter == 0) {
            numSelected = getString(R.string.zero_selected);
            counterText.setText(numSelected);
        } else {
            numSelected = counter + " " + getString(R.string.selected);
            counterText.setText(numSelected);
        }
    }

    public void clearActionMode() {
        isInActionMode = false;
        toolbar.getMenu().clear();
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        counterText.setVisibility(View.GONE);
        unselectedText.setVisibility(View.VISIBLE);
        counterText.setText(getString(R.string.zero_selected));
        counter = 0;
        selectionList.clear();
    }

}
