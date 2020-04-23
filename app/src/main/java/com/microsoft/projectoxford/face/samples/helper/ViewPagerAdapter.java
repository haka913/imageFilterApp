package com.microsoft.projectoxford.face.samples.helper;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.microsoft.projectoxford.face.samples.helper.PageFragment;

import java.util.List;
/**
 * Created by paul on 2018-05-27.
 */

public class ViewPagerAdapter extends FragmentStatePagerAdapter {
    private List<Integer> images;

    public ViewPagerAdapter(FragmentManager fm, List<Integer> imagesList) {
        super(fm);
        this.images = imagesList;
    }

    @Override
    public Fragment getItem(int position) {
        return PageFragment.getInstance(images.get(position));
    }

    @Override
    public int getCount() {
        return images.size();
    }

}
