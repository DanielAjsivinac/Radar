package com.example.microcontroladores;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    private List<Fragment> fragments = new ArrayList<>();
    private List<String> fragmentsTitles = new ArrayList<>();
    Bundle data_bundle;

    public  ViewPagerAdapter(FragmentManager fragmentManager, Bundle data){
        super(fragmentManager);
        data_bundle = data;


    }



    @NonNull
    @Override
    public Fragment getItem(int position) {
        Fragment f = fragments.get(position);
        f.setArguments(data_bundle);
        return f;
    }


    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public  CharSequence getPageTitle(int position){
        return fragmentsTitles.get(position);
    }



    public void addFragment(Fragment fragment, String title){
        fragments.add(fragment);
        fragmentsTitles.add(title);
    }

}
