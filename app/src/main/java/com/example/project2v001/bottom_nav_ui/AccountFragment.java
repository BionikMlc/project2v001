package com.example.project2v001.bottom_nav_ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.project2v001.R;
import com.example.project2v001.tab_ui.MyPostsFragment;
import com.example.project2v001.tab_ui.SavedFragment;
import com.example.project2v001.tab_ui.SentRequestsFragment;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

//import com.example.project2v001.tab_ui.SentRequestsFragment;


/**
 * A simple {@link Fragment} subclass.
 */
public class AccountFragment extends Fragment {

    private ViewPager viewPager;
    private TabLayout   accountTabLayout;
    private Fragment    myPostsFragment;
    private Fragment    sentRequestsFragment;
    private Fragment    savedPostsFragment;


    public AccountFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        myPostsFragment = new MyPostsFragment();
        sentRequestsFragment = new SentRequestsFragment();
        savedPostsFragment = new SavedFragment();
        viewPager = view.findViewById(R.id.account_view_pager);
        accountTabLayout = view.findViewById(R.id.account_tab_layout);

        accountTabLayout.setupWithViewPager(viewPager);
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager(),0);
        viewPagerAdapter.addFragment(myPostsFragment,"My Posts");
        viewPagerAdapter.addFragment(sentRequestsFragment,"Sent RequestsActivity");
        viewPagerAdapter.addFragment(savedPostsFragment,"Saved Posts");
        viewPager.setAdapter(viewPagerAdapter);

        // Inflate the layout for this fragment
        return view;
    }

    private class ViewPagerAdapter  extends FragmentPagerAdapter {

        private List<Fragment> fragmentList = new ArrayList<>();
        private List<String> fragmentTitle = new ArrayList<>();

        public void addFragment(Fragment fragment,String title){
            fragmentList.add(fragment);
            fragmentTitle.add(title);
        }

        public ViewPagerAdapter(@NonNull FragmentManager fm, int behavior) {
            super(fm, behavior);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }
        @Override
        public CharSequence getPageTitle(int position) {
            return fragmentTitle.get(position);
        }
    }
}
