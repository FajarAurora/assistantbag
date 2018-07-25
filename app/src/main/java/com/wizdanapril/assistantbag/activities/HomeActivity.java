package com.wizdanapril.assistantbag.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wizdanapril.assistantbag.R;
import com.wizdanapril.assistantbag.fragments.ActiveFragment;
import com.wizdanapril.assistantbag.fragments.HistoryFragment;
import com.wizdanapril.assistantbag.models.Catalog;
import com.wizdanapril.assistantbag.utils.Constant;
import com.wizdanapril.assistantbag.models.User;
import com.wizdanapril.assistantbag.utils.CustomViewPager;

import java.util.ArrayList;
import java.util.Locale;

public class HomeActivity extends AppCompatActivity {

    private int[] tabsIcons = {
            R.drawable.ic_home,
            R.drawable.history
    };

    public CustomViewPager viewPager;
    private FloatingActionButton speechButton;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    public DrawerLayout drawer;
    public NavigationView navigationView;

    private DatabaseReference catalogReference;
    private TextToSpeech t1;
    private StringBuilder stringBuilder;
    private String dayName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Setting up Drawer
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer, R.string.openDrawer, R.string.closeDrawer) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                viewPager.setPagingEnabled(true);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                viewPager.setPagingEnabled(false);
            }
        };
        drawer.addDrawerListener(actionBarDrawerToggle);
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        actionBarDrawerToggle.syncState();
        actionBarDrawerToggle.setDrawerIndicatorEnabled(false);
        getFragmentManager().invalidateOptionsMenu();

        // Shared preferences
        SharedPreferences preferences = this.getSharedPreferences("LoggedAccount", MODE_PRIVATE);
        final String userAccount = preferences.getString("userAccount", "error");
        final String deviceId = preferences.getString("deviceId", "error");
        String userEmail = preferences.getString("userEmail", "error");
        catalogReference = FirebaseDatabase.getInstance().getReference(Constant.DATA)
                .child(userAccount).child(deviceId).child(Constant.CATALOG);

        t1=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    t1.setLanguage(new Locale("id","ID"));
                }
            }
        });

        stringBuilder = new StringBuilder();
        
        // Setting up drawer menu items
//        final TextView menuUserName = (TextView) findViewById(R.id.drawer_app_name);
        TextView menuUserEmail = (TextView) findViewById(R.id.drawer_user_email);
        LinearLayout menuSchedule = (LinearLayout) findViewById(R.id.drawer_menu_schedule);
        LinearLayout menuCatalog = (LinearLayout) findViewById(R.id.drawer_menu_catalog);
        LinearLayout menuNetwork = (LinearLayout) findViewById(R.id.drawer_menu_network);
        LinearLayout menuCopyright = (LinearLayout) findViewById(R.id.drawer_menu_copyright);
        LinearLayout menuLogout = (LinearLayout) findViewById(R.id.drawer_menu_logout);


//        if (!userAccount.equals("error")) {
//            userReference.child(userAccount).addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot) {
//                        User user = dataSnapshot.getValue(User.class);
//                        menuUserName.setText(user.name);
//                }
//
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//
//                }
//            });
//        }

        menuUserEmail.setText(userEmail);
        menuSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, ScheduleActivity.class));
                drawer.closeDrawer(navigationView);
            }
        });

        menuCatalog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, CatalogActivity.class));
                drawer.closeDrawer(navigationView);
            }
        });


        menuNetwork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, NetworkActivity.class));
                drawer.closeDrawer(navigationView);
            }
        });


        menuCopyright.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, AboutusActivity.class));
                drawer.closeDrawer(navigationView);
            }
        });

        menuLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                FirebaseDatabase.getInstance().getReference(Constant.DEVICE).child(deviceId)
                        .child(Constant.CURRENT_USER).setValue("none");
                startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                drawer.closeDrawer(navigationView);
                finish();
            }
        });

        // Setup the viewPager
        viewPager = (CustomViewPager) findViewById(R.id.view_pager);
        PagerAdapter PagerAdapter = new PagerAdapter(getSupportFragmentManager());
        if (viewPager != null)
            viewPager.setAdapter(PagerAdapter);

        TabLayout mTabLayout = (TabLayout) findViewById(R.id.tab_layout);
        if (mTabLayout != null) {
            mTabLayout.setupWithViewPager(viewPager);

            for (int i = 0; i < mTabLayout.getTabCount(); i++) {
                TabLayout.Tab tab = mTabLayout.getTabAt(i);
                if (tab != null)
                    tab.setCustomView(PagerAdapter.getTabView(i));
            }

            mTabLayout.getTabAt(0).getCustomView().setSelected(true);
        }

        // Speech Recognition
        speechButton = (FloatingActionButton) findViewById(R.id.fab_speech_recognition);
//        speechButton.bringToFront();
        speechButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(intent, 10);
                } else {
                    Toast.makeText(HomeActivity.this, "Maaf, perangkat Anda tidak mendukung speech recognition", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

//    @Override
//    public void setDrawerEnabled(boolean enabled) {
//        int lockMode = enabled ? DrawerLayout.LOCK_MODE_UNLOCKED :
//                DrawerLayout.LOCK_MODE_LOCKED_CLOSED;
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        drawer.setDrawerLockMode(lockMode);
//    }

    private void speechAction() {
        catalogReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                stringBuilder.append(getResources().getString(R.string.speech_initial));
                for (DataSnapshot children : dataSnapshot.getChildren()) {
                    Catalog catalog = children.getValue(Catalog.class);
                    if (catalog != null && catalog.schedule.containsKey(dayName)) {
//                                        nameList.add(catalog.name);
                        stringBuilder.append(catalog.name);
                        stringBuilder.append(". ");
                    }
                }

                t1.speak(stringBuilder.toString(), TextToSpeech.QUEUE_FLUSH, null);
                stringBuilder.setLength(0);

                startActivity(new Intent(HomeActivity.this,
                        ScheduleMondayActivity.class));

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 10:
                if (resultCode == RESULT_OK && data != null) {
                    final ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    switch (result.get(0)) {
                        case "Senin":
                            dayName = "monday";
                            speechAction();
                            startActivity(new Intent(HomeActivity.this,
                                    ScheduleMondayActivity.class));
                            break;
                        case "Selasa":
                            dayName = "tuesday";
                            speechAction();
                            startActivity(new Intent(HomeActivity.this,
                                ScheduleTuesdayActivity.class));
                            break;
                        case "Rabu":
                            dayName = "wednesday";
                            speechAction();
                            startActivity(new Intent(HomeActivity.this,
                                ScheduleWednesdayActivity.class));
                            break;
                        case "Kamis":
                            dayName = "thurday"; //a typo
                            speechAction();
                            startActivity(new Intent(HomeActivity.this,
                                ScheduleThursdayActivity.class));
                            break;
                        case "Jumat":
                            dayName = "friday";
                            speechAction();
                            startActivity(new Intent(HomeActivity.this,
                                ScheduleFridayActivity.class));
                            break;
                        case "Sabtu":
                            dayName = "saturday";
                            speechAction();
                            startActivity(new Intent(HomeActivity.this,
                                ScheduleSaturdayActivity.class));
                            break;
                        case "Minggu":
                            dayName = "sunday";
                            String activityName = "ScheduleTuesdayActivity";
                            speechAction();
                            startActivity(new Intent(HomeActivity.this,
                                ScheduleSundayActivity.class));
                            break;
                        default:
                            t1.speak("Maaf, bukan nama hari", TextToSpeech.QUEUE_FLUSH, null);
                            Toast.makeText(this, "Bukan nama hari", Toast.LENGTH_SHORT).show();
                            break;
                    }
                }
                break;
        }

    }

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        actionBarDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles
        actionBarDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // toggle nav drawer on selecting action bar app icon/title
        return actionBarDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    private class PagerAdapter extends FragmentPagerAdapter {

        public final int PAGE_COUNT = 2;

        private final String[] mTabsTitle = {"HomeActivity", "Riwayat"};

        public PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public View getTabView(int position) {
            // Inflate the custom tab
            View view = LayoutInflater.from(HomeActivity.this).inflate(R.layout.snippet_custom_tab, null);
            ImageView icon = (ImageView) view.findViewById(R.id.icon);
            icon.setImageResource(tabsIcons[position]);
            return view;
        }

        @Override
        public Fragment getItem(int pos) {
            switch (pos) {
                case 0:
                    return ActiveFragment.newInstance(1);

                case 1:
                    return HistoryFragment.newInstance(2);
            }
            return null;
        }

        @Override
        public int getCount() {
            return PAGE_COUNT;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTabsTitle[position];
        }
    }



    @Override
    public void onBackPressed() {
        int count = getFragmentManager().getBackStackEntryCount();

        if (count == 0) {
//            super.onBackPressed();
            // Avoid
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            startActivity(intent);

            // Code to close drawer
        } else {
            getFragmentManager().popBackStack();
        }

    }
}
