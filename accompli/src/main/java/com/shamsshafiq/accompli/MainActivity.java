package com.shamsshafiq.accompli;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;

import com.shamsshafiq.accompli.events.SubmitEvent;
import com.viewpagerindicator.TabPageIndicator;

import de.greenrobot.event.EventBus;

public class MainActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        ViewPager pager = (ViewPager) findViewById(R.id.view_pager);
        pager.setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int i) {
                if (i == 0) {
                    return new WebViewFragment();
                } else {
                    return new ListViewFragment();
                }
            }

            @Override
            public int getCount() {
                return 2;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                if (position == 0) {
                    return getApplicationContext().getResources().getString(R.string.string_web_view_title);
                } else {
                    return getApplicationContext().getResources().getString(R.string.string_list_view_title);
                }
            }
        });

        TabPageIndicator tpi = (TabPageIndicator) findViewById(R.id.title_page_indicator);
        tpi.setViewPager(pager);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        EventBus.getDefault().post(new SubmitEvent());
        super.onNewIntent(intent);
    }
}
