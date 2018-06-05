package com.wizdanapril.assistantbag.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.wizdanapril.assistantbag.R;
import com.wizdanapril.assistantbag.adapters.CatalogAdapter;
import com.wizdanapril.assistantbag.models.Catalog;
import com.wizdanapril.assistantbag.utils.Constant;

import java.util.ArrayList;
import java.util.List;

public class CatalogActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<Catalog> catalogList;
    private CatalogAdapter catalogAdapter;

    private DatabaseReference catalogReference = FirebaseDatabase.getInstance()
            .getReference(Constant.USER).child(Constant.CATALOG);

    private TextView emptyText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        toolbar.setTitle(R.string.catalog);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        emptyText = (TextView) findViewById(R.id.tv_no_data);

        catalogList = new ArrayList<>();

        recyclerView = (RecyclerView) findViewById(R.id.rv_tag);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        recyclerView.setLayoutManager(layoutManager);

        catalogAdapter = new CatalogAdapter(catalogList, this);
        recyclerView.setAdapter(catalogAdapter);

        updateList();
    }

    private void updateList() {

        catalogReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Catalog catalog = dataSnapshot.getValue(Catalog.class);
                catalogList.add(catalog);
//                Log.d("LIST", String.valueOf(catalogList.size()));
                catalogAdapter.notifyDataSetChanged();
                checkIfEmpty();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Catalog catalog = dataSnapshot.getValue(Catalog.class);
                int index = getItemIndex(catalog);
                catalogList.set(index, catalog);
                catalogAdapter.notifyItemChanged(index);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Catalog catalog = dataSnapshot.getValue(Catalog.class);
                int index = getItemIndex(catalog);
                catalogList.remove(index);
                catalogAdapter.notifyItemRemoved(index);
                checkIfEmpty();

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private int getItemIndex(Catalog catalog) {

        int index = -1;

        for (int i = 0; i < catalogList.size(); i++) {
            if (catalogList.get(i).id.equals(catalog.id)) {
                index = i;
                break;
            }
        }

        return index;
    }

    public void removeTag(int position) {
        catalogReference.child(catalogList.get(position).id).removeValue();
        Toast.makeText(this, getResources().getString(R.string.tag_deleted), Toast.LENGTH_LONG).show();
    }

    public void changeTag(int position) {
        final Catalog catalog = catalogList.get(position);
        showUpdateDialog(catalog);
    }

    private void showUpdateDialog(final Catalog catalog) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);

        LayoutInflater inflater = getLayoutInflater();

        final View dialogView = inflater.inflate(R.layout.dialog_update, null);

        dialogBuilder.setView(dialogView);

        final EditText editTextName = (EditText) dialogView.findViewById(R.id.et_name);
        final Button buttonUpdate = (Button) dialogView.findViewById(R.id.bt_update);

        dialogBuilder.setTitle(getResources().getString(R.string.update_tag));
        editTextName.setText(catalog.name);

        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newName = editTextName.getText().toString().trim();

                if (TextUtils.isEmpty(newName)) {
                    editTextName.setError(getResources().getString(R.string.error_empty_field));
                    return;
                }

                updateTag(catalog, newName);
                alertDialog.dismiss();
            }
        });
    }

    private void updateTag(Catalog catalog, String newName) {
        catalog.name = newName;

//        Map<String, Object> catalogModelValue = catalog.toMap();
//        Map<String, Object> newUser = new HashMap<>();
//
//        newUser.put(catalog.id, catalogModelValue);
//
//        catalogReference.updateChildren(newUser);

        catalogReference.child(catalog.id).child("name").setValue(newName);

        Toast.makeText(CatalogActivity.this, getResources().getString(R.string.tag_updated),
                Toast.LENGTH_LONG).show();
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
                Intent intent = NavUtils.getParentActivityIntent(this);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                NavUtils.navigateUpTo(this, intent);
//                this.overridePendingTransition(R.anim.exit_current, R.anim.exit_new);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        overridePendingTransition(R.anim.exit_current, R.anim.exit_new);
    }
}
