package com.wizdanapril.assistantbag.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.wizdanapril.assistantbag.R;
import com.wizdanapril.assistantbag.fragments.ScheduleFragment;

public class ScheduleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager());

        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        toolbar.setTitle(getResources().getString(R.string.schedule));
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        setSupportActionBar(toolbar);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        ViewPager pager = (ViewPager) findViewById(R.id.pager);

        pager.setAdapter(adapter);
        tabLayout.setupWithViewPager(pager);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/HomeActivity button
            case android.R.id.home:
                Intent intent = NavUtils.getParentActivityIntent(this);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                NavUtils.navigateUpTo(this, intent);
//                this.overridePendingTransition(R.anim.exit_current, R.anim.exit_new);
        }
        return super.onOptionsItemSelected(item);
    }
//
//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//        overridePendingTransition(R.anim.exit_current, R.anim.exit_new);
//    }

//    public void addSchedule(View view) {
//        Intent intent = new Intent(this, SelectionActivity.class);
//        startActivity(intent);
//    }

    private class PagerAdapter extends FragmentStatePagerAdapter {

        private String tabTitle[] = new String[] {
                getResources().getString(R.string.mon), getResources().getString(R.string.tue),
                getResources().getString(R.string.wed), getResources().getString(R.string.thu),
                getResources().getString(R.string.fri), getResources().getString(R.string.sat),
                getResources().getString(R.string.sun),
                };

        private PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return ScheduleFragment.newInstance(position);
        }

        @Override
        public int getCount() {
            return 7;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitle[position];
        }

    }
}
