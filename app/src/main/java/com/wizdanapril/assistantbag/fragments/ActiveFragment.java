package com.wizdanapril.assistantbag.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wizdanapril.assistantbag.R;
import com.wizdanapril.assistantbag.activities.CatalogActivity;
import com.wizdanapril.assistantbag.activities.HomeActivity;
import com.wizdanapril.assistantbag.activities.ScheduleActivity;
import com.wizdanapril.assistantbag.adapters.ActiveAdapter;
import com.wizdanapril.assistantbag.adapters.HistoryAdapter;
import com.wizdanapril.assistantbag.models.Constant;
import com.wizdanapril.assistantbag.utils.CustomViewPager;
import com.wizdanapril.assistantbag.models.Catalog;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;

public class ActiveFragment extends Fragment implements NavigationView.OnNavigationItemSelectedListener {

    public static final String ARG_PAGE = "ARG_PAGE";

    private View view;
    private HomeActivity homeActivity;

    private CustomViewPager viewPager;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private DrawerLayout drawer;
    private NavigationView navigationView;

    private RecyclerView recyclerView;
    private List<Catalog> activeList;
    private ActiveAdapter activeAdapter;
    private HistoryAdapter historyAdapter;

    private TextView emptyText, tagCounter;

    private DatabaseReference catalogReference, historyReference;
    public ActiveFragment() {
        // Required empty public constructor
    }

    public static ActiveFragment newInstance(int pageNo) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, pageNo);
        ActiveFragment fragment = new ActiveFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences preferences = getContext().getSharedPreferences("LoggedAccount", MODE_PRIVATE);
        String userAccount = preferences.getString("userAccount", "error");
        String deviceId = preferences.getString("deviceId", "error");
        catalogReference = FirebaseDatabase.getInstance().getReference(Constant.DATA)
                .child(userAccount).child(deviceId).child(Constant.CATALOG);
        historyReference = FirebaseDatabase.getInstance().getReference(Constant.DATA)
                .child(userAccount).child(deviceId).child(Constant.HISTORY);


        setHasOptionsMenu(true);
        updateList();
//        int mPageNo = getArguments().getInt(ARG_PAGE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_active, container, false);
        homeActivity = (HomeActivity) getActivity();
        // Setting up nav menu
//        drawer = (DrawerLayout) view.findViewById(R.id.drawer_layout);
//        navigationView = (NavigationView) view.findViewById(R.id.nav_view);
        viewPager = homeActivity.viewPager;
        drawer = homeActivity.drawer;
        navigationView = homeActivity.navigationView;
        doDrawerAction();

//        actionBarDrawerToggle = new ActionBarDrawerToggle(getActivity(), drawer, R.string.openDrawer, R.string.closeDrawer) {
//            @Override
//            public void onDrawerClosed(View drawerView) {
//                super.onDrawerClosed(drawerView);
//            }
//
//            @Override
//            public void onDrawerOpened(View drawerView) {
//                super.onDrawerOpened(drawerView);
//            }
//        };
//
//        // Setting the actionbarToggle to drawer layout
//        drawer.addDrawerListener(actionBarDrawerToggle);
//        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
//        // Calling sync state so menu icon will show up
//        actionBarDrawerToggle.syncState();
//        actionBarDrawerToggle.setDrawerIndicatorEnabled(false);
//
//        getActivity().invalidateOptionsMenu();


        // Popup menu
        final TextView verticalEllipsisIcon = (TextView) view.findViewById(R.id.tv_vertical_ellipsis);
        verticalEllipsisIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(getActivity(), verticalEllipsisIcon);
                popupMenu.inflate(R.menu.option_menu_active);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.menu_item_clear_active:
                                showClearActiveDialog();
                                break;
                            case R.id.menu_item_clear_history:
                                showClearHistoryDialog();
                                break;
                            default:
                                break;
                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });

        // Setting the crescento button address
        TextView toCatalog = (TextView) view.findViewById(R.id.to_catalog);
        toCatalog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), CatalogActivity.class);
                startActivity(intent);
            }
        });

        TextView toSchedule = (TextView) view.findViewById(R.id.to_schedule);
        toSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ScheduleActivity.class);
                startActivity(intent);
            }
        });

        // Setting up content
        emptyText = (TextView) view.findViewById(R.id.tv_no_data);
        tagCounter = (TextView) view.findViewById(R.id.tv_active_counter);

        activeList = new ArrayList<>();

        recyclerView = (RecyclerView) view.findViewById(R.id.rv_tag);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        activeAdapter = new ActiveAdapter(activeList);
        historyAdapter = new HistoryAdapter(null, getActivity(), catalogReference);
        recyclerView.setAdapter(activeAdapter);

        checkIfEmpty();
        return view;
    }

    public void doDrawerAction() {
        view.findViewById(R.id.nav_menu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!drawer.isDrawerOpen(navigationView)) {
                    drawer.openDrawer(navigationView);
                }
            }
        });
    }

    private void updateList() {
        catalogReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                activeList.clear();
                for (DataSnapshot children : dataSnapshot.getChildren()) {
                    Catalog catalog = children.getValue(Catalog.class);
                    if (catalog != null && Objects.equals(catalog.status, "in")) {
                        activeList.add(catalog);
                        tagCounter.setText(String.valueOf(activeList.size()));
                        activeAdapter.notifyDataSetChanged();
                        checkIfEmpty();
                    } else {
                        activeAdapter.notifyDataSetChanged();
                        checkIfEmpty();
                    }
                }
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

//                catalogReference.addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//                Catalog catalog = dataSnapshot.getValue(Catalog.class);
//                if (catalog != null && Objects.equals(catalog.status, "in")) {
//                    activeList.add(catalog);
//                    activeAdapter.notifyDataSetChanged();
//                    tagCounter.setText(String.valueOf(activeList.size()));
//                    checkIfEmpty();
//                }
//            }
//
//            @Override
//            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//                Catalog catalog = dataSnapshot.getValue(Catalog.class);
//                int index = getItemIndex(catalog);
//                if (catalog != null && Objects.equals(catalog.status, "in")) {
//                    activeList.set(index, catalog);
//                    activeAdapter.notifyItemChanged(index);
//                    tagCounter.setText(String.valueOf(activeList.size()));
//                }
//            }
//
//            @Override
//            public void onChildRemoved(DataSnapshot dataSnapshot) {
//                Catalog catalog = dataSnapshot.getValue(Catalog.class);
//                int index = getItemIndex(catalog);
//                if (catalog != null && Objects.equals(catalog.status, "in")) {
//                    activeList.remove(index);
//                    activeAdapter.notifyItemRemoved(index);
//                    tagCounter.setText(String.valueOf(activeList.size()));
//                }
//                checkIfEmpty();
//
//            }
//
//            @Override
//            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
//
//            }
////
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
        });
    }

    private int getItemIndex(Catalog catalog) {

        int index = -1;

        for (int i = 0; i < activeList.size(); i++) {
            if (activeList.get(i).id.equals(catalog.id)) {
                index = i;
                break;
            }
        }

        return index;
    }

    private void checkIfEmpty() {
        if (activeList.size() == 0) {
            recyclerView.setVisibility(View.INVISIBLE);
            emptyText.setVisibility(View.VISIBLE);
            tagCounter.setText("0");
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyText.setVisibility(View.INVISIBLE);
        }
    }

    private void showClearActiveDialog() throws Resources.NotFoundException {
        new AlertDialog.Builder(getContext())
                .setTitle(getResources().getString(R.string.clear_active))
                .setMessage(
                        getResources().getString(R.string.clear_active_question))
//                .setIcon(
//                        getResources().getDrawable(
//                                android.R.drawable.ic_dialog_alert))
                .setPositiveButton(
                        getResources().getString(R.string.positive_button),
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                clearActive();
                            }
                        })
                .setNegativeButton(
                        getResources().getString(R.string.negative_button), null)
                .show();
    }

    private void showClearHistoryDialog() throws Resources.NotFoundException {
        new AlertDialog.Builder(getContext())
                .setTitle(getResources().getString(R.string.clear_history))
                .setMessage(
                        getResources().getString(R.string.clear_history_question))
//                .setIcon(
//                        getResources().getDrawable(
//                                android.R.drawable.ic_dialog_alert))
                .setPositiveButton(
                        getResources().getString(R.string.positive_button),
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                historyReference.removeValue();
                                historyAdapter.notifyDataSetChanged();
                            }
                        })
                .setNegativeButton(
                        getResources().getString(R.string.negative_button), null)
                .show();
    }

    private void clearActive() {
        catalogReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String out = "out";
                for (DataSnapshot children : dataSnapshot.getChildren()) {
                    children.child("status").getRef().setValue(out);
                    activeList.clear();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();

    }

//    @Override
//    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
//        super.onActivityCreated(savedInstanceState);
//        // Sync the toggle state after onRestoreInstanceState has occurred.
//        actionBarDrawerToggle.syncState();
//    }
//
//    @Override
//    public void onConfigurationChanged(Configuration newConfig) {
//        super.onConfigurationChanged(newConfig);
//        // Pass any configuration change to the drawer toggles
//        actionBarDrawerToggle.onConfigurationChanged(newConfig);
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // toggle nav drawer on selecting action bar app icon/title
//        return actionBarDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
//    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        DrawerLayout drawer = (DrawerLayout) view.findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
