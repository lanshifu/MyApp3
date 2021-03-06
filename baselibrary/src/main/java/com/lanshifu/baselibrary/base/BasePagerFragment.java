package com.lanshifu.baselibrary.base;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;

import com.lanshifu.baselibrary.R;
import com.lanshifu.baselibrary.adapter.BaseFragmentPagerAdapter;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Administrator on 2017/7/23.
 */

public abstract class BasePagerFragment extends BaseFragment {


    FragmentManager fm;
    List<Fragment> fragmentList = new ArrayList<>();
    List<String> titleList = new ArrayList<>();
    private TabLayout tablayout;
    private ViewPager viewpager;


    @Override
    public int getLayoutId() {
        return R.layout.layout_base_pager;
    }

    @Override
    protected void initView() {
        tablayout = mRootView.findViewById(R.id.tablayout);
        viewpager = mRootView.findViewById(R.id.viewpager);

        fm = getActivity().getSupportFragmentManager();

        viewpager.setAdapter(new BaseFragmentPagerAdapter(fm, initTitleList(), initFragmentList()));
        tablayout.setupWithViewPager(viewpager);

    }

    @Override
    protected void initPresenter() {

    }

    protected abstract List<Fragment> initFragmentList();

    protected abstract List<String> initTitleList();


}
