package tdtu.EStudy_App.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import tdtu.EStudy_App.Tag1Fragment;
import tdtu.EStudy_App.Tag2Fragment;
import tdtu.EStudy_App.Tag3Fragment;

public class TagTopicAdapter extends FragmentStatePagerAdapter {
    public TagTopicAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new Tag1Fragment();
            case 1:
                return new Tag2Fragment();
            case 2:
                return new Tag3Fragment();
            default:
                return new Tag1Fragment();
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    @NonNull
    @Override
    public CharSequence getPageTitle(int position) {
        // return super.getPageTitle(position);
        switch (position){
            case 0:
                return "Folder";
            case 1:
                return "Topic";
            case 2:
                return "Từ vựng";
            default:
                return "Folder";
        }
    }
}
