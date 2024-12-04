package tdtu.EStudy_App.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;

import tdtu.EStudy_App.R;
import tdtu.EStudy_App.adapters.TagTopicSharedAdapter;


public class BrowseFragment extends Fragment {

    private ViewPager viewPagerShare;
    private TabLayout tagLayoutShare;
    private View getView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        getView =  inflater.inflate(R.layout.fragment_browse, container, false);

        viewPagerShare = getView.findViewById(R.id.viewPageShare);
        tagLayoutShare = getView.findViewById(R.id.tagLayoutShare);

        //Tạo adapter chuyển tag
        TagTopicSharedAdapter tagTopicSharedAdapter = new TagTopicSharedAdapter(getChildFragmentManager());
        viewPagerShare.setAdapter(tagTopicSharedAdapter);
        tagLayoutShare.setupWithViewPager(viewPagerShare);

        return getView;
    }
}