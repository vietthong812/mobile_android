package tdtu.EStudy_App.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import tdtu.EStudy_App.fragments.share1Fragment;
import tdtu.EStudy_App.fragments.share2Fragment;
import tdtu.EStudy_App.fragments.share3Fragment;

public class TagTopicSharedAdapter extends FragmentStatePagerAdapter {
    public TagTopicSharedAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new share1Fragment();
            case 1:
                return new share2Fragment();
            case 2:
                return new share3Fragment();
            default:
                return new share1Fragment();
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
                return "Browse";
            case 1:
                return "Đã chia sẻ";
            case 2:
                return "Đã lưu";
            default:
                return "Browse";
        }
    }
}
