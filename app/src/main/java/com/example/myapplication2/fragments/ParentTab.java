package com.example.myapplication2.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.example.myapplication2.AnomalyTypeDialog;
import com.example.myapplication2.CodesQRAndText;
import com.example.myapplication2.Globals;
import com.example.myapplication2.R;
import com.example.myapplication2.fragments.childTabs.ChatChildFragment;
import com.example.myapplication2.fragments.childTabs.CreedChildFragment;
import com.example.myapplication2.fragments.childTabs.DatabaseChildFragment;
import com.example.myapplication2.fragments.childTabs.QuestChildFragment;
import com.example.myapplication2.fragments.childTabs.UserChildFragment;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

public class ParentTab extends Fragment {

    Globals globals;

    private SectionsPagerAdapterChild mSectionsPagerAdapterChild;
    private ViewPager mViewPagerChild;
    private TabLayout mTabLayoutChild;


    public ParentTab(Globals globals) {
        this.globals = globals;
    }

    public View onCreateView(@NonNull LayoutInflater layoutInflater, @Nullable ViewGroup viewGroup, @Nullable Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.fragment_chat, viewGroup, false);
        final EditText editText = inflate.findViewById(R.id.CommandLine);
        TextView txtView = inflate.findViewById(R.id.txtViewChat);

        // запускаем ChildFragment
        mSectionsPagerAdapterChild = new SectionsPagerAdapterChild(getChildFragmentManager());
        mTabLayoutChild = inflate.findViewById(R.id.tabs2);
        mViewPagerChild = inflate.findViewById(R.id.container_child);
        mViewPagerChild.setAdapter(mSectionsPagerAdapterChild);
        mViewPagerChild.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayoutChild));
        mTabLayoutChild.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPagerChild));

        return inflate;
    }

    public class SectionsPagerAdapterChild extends FragmentPagerAdapter {
        public int getCount() {
            return 5;
        }

        public SectionsPagerAdapterChild(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        public Fragment getItem(int i) {
            switch (i) {
                case 0:
                    return new UserChildFragment();
                case 1:
                    return new CreedChildFragment();
                case 2:
                    return new QuestChildFragment();
                case 3:
                    return new DatabaseChildFragment();
                case 4:
                    return new ChatChildFragment(globals);
                default:
                    return null;
            }
        }
    }


}
