package com.mobi.demo;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private BottomNavigationView bottomNavigationView;

    private final MutableLiveData<Map<String, Object>> leaveData = new MutableLiveData<>();
    private final MutableLiveData<Map<String, Object>> meetingData = new MutableLiveData<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager = findViewById(R.id.view_pager);
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        ViewPagerFragmentAdapter adapter = new ViewPagerFragmentAdapter(this, bottomNavigationView);
        viewPager.setAdapter(adapter);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                Log.d("Mobidebug", "onNavigationItemSelected: "+itemId);
                if (itemId == R.id.nav_copilot) {
                    viewPager.setCurrentItem(0, false);
                    return true;
                } else if (itemId == R.id.nav_leave) {
                    viewPager.setCurrentItem(1, false);
                    return true;
                } else if (itemId == R.id.nav_meeting) {
                    viewPager.setCurrentItem(2, false);
                    return true;
                }
                return false;
            }
        });

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                Log.d("Mobidebug", "onPageSelected: "+position);
                switch (position) {
                    case 0:
                        bottomNavigationView.setSelectedItemId(R.id.nav_copilot);
                        break;
                    case 1:
                        bottomNavigationView.setSelectedItemId(R.id.nav_leave);
                        break;
                    case 2:
                        bottomNavigationView.setSelectedItemId(R.id.nav_meeting);
                        break;
            }}
        });
    }

    private static class ViewPagerFragmentAdapter extends FragmentStateAdapter {

        private final BottomNavigationView bottomNavigationView;

        public ViewPagerFragmentAdapter(@NonNull FragmentActivity fragmentActivity, BottomNavigationView bottomNavigationView) {
            super(fragmentActivity);
            this.bottomNavigationView = bottomNavigationView;
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            switch (position) {
                case 0:
                    return new CopilotFragment(bottomNavigationView);
                case 1:
                    return new LeaveFragment();
                default:
                    return new MeetingFragment();
            }
        }

        @Override
        public int getItemCount() {
            return 3;
        }
    }

    public LiveData<Map<String, Object>> getLeaveData() {
        return leaveData;
    }

    public void setLeaveData(Map<String, Object> newLeaveData) {
        leaveData.setValue(newLeaveData);
    }

    public LiveData<Map<String, Object>> getMeetingData() {
        return meetingData;
    }

    public void setMeetingData(Map<String, Object> newMeetingData) {
        meetingData.setValue(newMeetingData);
    }
}