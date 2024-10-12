package com.mobi.demo;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.mobi.sdk.MobiCopilot;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Map;

public class CopilotFragment extends Fragment {

    private final BottomNavigationView bottomNavigationView;

    public CopilotFragment(BottomNavigationView bottomNavigationView) {
        this.bottomNavigationView = bottomNavigationView;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_copilot, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        WebView webView = view.findViewById(R.id.webView);
        try {
            MobiCopilot mobiCopilot = new MobiCopilot.Builder()
                    .setUrl("https://runtime-stage.mobiapp.cloud/workspace171343386479015/apps/172854875547671/")
                    .setAuthToken("mockedAuthToken-123")
                    .setWebView(webView)
                    .setCustomRouter(new MobiCopilot.MobiCopilotRouter() {
                        @Override
                        public void navigateTo(String routerKey, Map<String, Object> routerParams) {
                            MainActivity mainActivity = (MainActivity) getActivity();
                            if (bottomNavigationView != null && mainActivity != null) {
                                if (routerKey.equals("leave")) {
                                    Log.d("navigateTo", "navigateTo\nrouterKey:" + routerKey + ", routerParams:" + routerParams);
                                    mainActivity.setLeaveData(routerParams);
                                    bottomNavigationView.setSelectedItemId(R.id.nav_leave);
                                } else if (routerKey.equals("meeting")) {
                                    mainActivity.setMeetingData(routerParams);
                                    bottomNavigationView.setSelectedItemId(R.id.nav_meeting);
                                } else {
                                    Toast.makeText(getContext(), "navigateTo\nrouterKey:" + routerKey, Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                        @Override
                        public void onNavigateError(String routerKey, MobiCopilot.MobiError error) {
                            Toast.makeText(getContext(), "onNavigateError\nrouterKey:" + routerKey + "\nCode: " + error.getCode() + "\nMessage: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .build();
            mobiCopilot.loadCopilot(new MobiCopilot.MobiErrorListener() {
                @Override
                public void onError(MobiCopilot.MobiError error) {
                    Toast.makeText(getContext(), "onError\nCode: " + error.getCode() + "\nMessage: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}