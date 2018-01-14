package com.lanshifu.myapp_3.fragment;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.lanshifu.baselibrary.base.BaseFragment;
import com.lanshifu.myapp_3.R;

import butterknife.Bind;

/**
 * Created by lanxiaobin on 2018/1/3.
 */

public class MainFragment extends BaseFragment {
    @Bind(R.id.viewPager)
    ViewPager mViewPager;
    @Bind(R.id.tabLayout)
    TabLayout mTabLayout;

    private Class[] fragments = new Class[]{HomeFragment.class, DefaultFragment.class,
            DefaultFragment.class, DefaultFragment.class};

    private String[] mTitles = new String[]{"主页", "周边", "更多", "我的"};

    private int[] icons = new int[]{
            R.drawable.tab_home_selector, R.drawable.tab_around_selector,
            R.drawable.tab_me_selector, R.drawable.tab_more_selector};

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_main;
    }

    @Override
    protected void initView() {

        mViewPager.setAdapter(new FragmentPagerAdapter(getActivity().getSupportFragmentManager()) {

            @Override
            public Fragment getItem(int position) {
                if (position == 1) {
                    return new HomeFragment();
                } else if (position == 2) {
                    return new DefaultFragment();
                } else if (position == 3) {
                    return new DefaultFragment();
                }
                return new DefaultFragment();
            }

            @Override
            public int getCount() {
                return mTitles.length;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return mTitles[position];
            }

        });
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.getTabAt(0).setIcon(R.drawable.tab_home_selector);
        mTabLayout.getTabAt(1).setIcon(R.drawable.tab_around_selector);
        mTabLayout.getTabAt(2).setIcon(R.drawable.tab_more_selector);
        mTabLayout.getTabAt(3).setIcon(R.drawable.tab_me_selector);

    }

}
