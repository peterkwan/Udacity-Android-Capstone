package org.peterkwan.udacity.mysupermarket.ui;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.peterkwan.udacity.mysupermarket.R;

import butterknife.BindBool;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import lombok.NoArgsConstructor;

import static org.peterkwan.udacity.mysupermarket.util.AppConstants.REMOVE_FRAGMENT_ACTION;

/**
 * A simple {@link Fragment} subclass.
 */
@NoArgsConstructor
public class MainFragment extends BaseFragment {

    private static final int SHOPPING_CART_TAB = 1;

    @BindView(R.id.mainTabLayout)
    TabLayout tabLayout;

    @BindView(R.id.mainViewPager)
    ViewPager viewPager;

    @BindBool(R.bool.two_pane_layout)
    boolean isTwoPaneLayout;

    private Unbinder unbinder;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        unbinder = ButterKnife.bind(this, rootView);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                TabLayout.Tab tab = tabLayout.getTabAt(position);
                if (tab != null)
                    tab.select();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                if (isTwoPaneLayout && tab.getPosition() == SHOPPING_CART_TAB)
                    removeDetailFragment();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        viewPager.setAdapter(new MainTabViewPagerAdapter(getChildFragmentManager(), tabLayout.getTabCount()));

        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private void removeDetailFragment() {
        if (mCallbackListener != null)
            mCallbackListener.onCallback(REMOVE_FRAGMENT_ACTION);
    }
}
