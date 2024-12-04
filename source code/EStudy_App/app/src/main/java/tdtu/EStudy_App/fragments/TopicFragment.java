package tdtu.EStudy_App.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;

import tdtu.EStudy_App.R;
import tdtu.EStudy_App.adapters.TagTopicAdapter;


public class TopicFragment extends Fragment {


    private ViewPager viewPager;
    private TabLayout tagLayout;
    private View getView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        getView =  inflater.inflate(R.layout.fragment_topic2, container, false);

        viewPager = getView.findViewById(R.id.viewPage);
        tagLayout = getView.findViewById(R.id.tagLayout);

        //Tạo adapter chuyển tag
        TagTopicAdapter tagTopicAdapter = new TagTopicAdapter(getChildFragmentManager());
        viewPager.setAdapter(tagTopicAdapter);
        tagLayout.setupWithViewPager(viewPager);

        return getView;
    }
}