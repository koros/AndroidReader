package com.korosmatick.androidreader;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;


public class MyPagerAdapter extends FragmentPagerAdapter {
	
	int pages;
	
	public MyPagerAdapter(FragmentManager fragmentManager, int totalPages) {
		 super(fragmentManager);
		 this.pages = totalPages;
	}

	@Override
    public Fragment getItem(int position) {
        return PageContentsFragmentBase.create(position);
    }
	
    @Override
    public int getCount() {
        return pages;
    }

    public void incrementPageCount(){
        pages+=1;
        notifyDataSetChanged();
    }
}
