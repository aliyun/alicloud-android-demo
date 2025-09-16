package com.alibaba.openim.feedbackdemo;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.alibaba.sdk.android.feedback.impl.FeedbackAPI;

import java.util.concurrent.Callable;

/**
 * Created by wuer on 16/9/21.
 */
public class DemoFragmentActivity extends FragmentActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);

        FeedbackAPI.activity = this; // 设置activity
        //support-v4包中的Fragment
        FragmentManager fm = getSupportFragmentManager();
        final FragmentTransaction transaction = fm.beginTransaction();
        final Fragment feedback = FeedbackAPI.getFeedbackFragment();
        // must be called
        FeedbackAPI.setFeedbackFragment(new Callable() {
            @Override
            public Object call() throws Exception {
                transaction.replace(R.id.content, feedback);
                transaction.commit();
                return null;
            }
        }, null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FeedbackAPI.cleanFeedbackFragment();
        FeedbackAPI.cleanActivity();
    }
}
