package com.example.stalkernet.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.stalkernet.Globals;
import com.example.stalkernet.R;
import com.example.stalkernet.fragments.childTabs.ChatChildFragment;
import com.example.stalkernet.fragments.childTabs.CreedChildFragment;
import com.example.stalkernet.fragments.childTabs.DatabaseChildFragment;
import com.example.stalkernet.fragments.childTabs.QuestChildFragment;
import com.example.stalkernet.fragments.childTabs.UserChildFragment;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

public class HubFragment extends Fragment {

    Globals globals;

    private HubFragment.SectionsPagerAdapterMap mSectionsPagerAdapterChild;
    private ViewPager mViewPagerChild;
    private TabLayout mTabLayoutChild;

    public HubFragment(Globals globals){
        this.globals = globals;
    }

    public View onCreateView(@NonNull LayoutInflater layoutInflater, @Nullable ViewGroup viewGroup, @Nullable Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.fragment_hub, viewGroup, false);
        // запускаем ChildFragment
        /*mSectionsPagerAdapterChild = new HubFragment.SectionsPagerAdapterMap(getChildFragmentManager());
        mTabLayoutChild = inflate.findViewById(R.id.tabs3);
        mViewPagerChild = inflate.findViewById(R.id.container_hub);
        mViewPagerChild.setAdapter(mSectionsPagerAdapterChild);
        mViewPagerChild.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayoutChild));
        mTabLayoutChild.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPagerChild));*/

        return inflate;
    }

    public class SectionsPagerAdapterMap extends FragmentPagerAdapter {
        public int getCount() {
            return 5;
        }

        public SectionsPagerAdapterMap(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        public Fragment getItem(int i) {
            switch (i) {
                case 0:
                    return new UserChildFragment(globals);
                case 1:
                    return new CreedChildFragment();
                case 2:
                    return new QuestChildFragment();
                case 3:
                    return new DatabaseChildFragment();
                case 4:
                    return new QRTab(globals);
                case 5:
                    return new ChatChildFragment(globals);
                default:
                    return null;
            }
        }
    }
}
