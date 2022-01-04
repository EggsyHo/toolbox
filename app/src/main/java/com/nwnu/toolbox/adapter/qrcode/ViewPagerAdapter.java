package com.nwnu.toolbox.adapter.qrcode;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.nwnu.toolbox.fragment.qrcodecard.DIYCodeFragment;
import com.nwnu.toolbox.fragment.qrcodecard.VisitingCardFragment;

public class ViewPagerAdapter extends FragmentPagerAdapter {

    private Fragment[] fragments = new Fragment[]{new VisitingCardFragment(), new DIYCodeFragment()};

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return fragments[position];
    }

    @Override
    public int getCount() {
        return fragments.length;
    }
}
