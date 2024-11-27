package tdtu.EStudy_App.activities;

import android.os.Bundle;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import tdtu.EStudy_App.R;

public class HomeFragment extends Fragment {

    private boolean isFront = true;
    private CardView currentCard;
    private TextView tvMatTruoc, tvMatSau;
    private ImageButton btnSound, btnSave;
    private CardView cardThuVien, cardUser , cardBrowse;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        initializeViews(view);
        setOnClickListeners();
        return view;
    }

    private void initializeViews(View view) {
        currentCard = view.findViewById(R.id.currentCard);
        tvMatTruoc = view.findViewById(R.id.tvMatTruoc);
        tvMatSau = view.findViewById(R.id.tvMatSau);
        btnSound = view.findViewById(R.id.btnSound);
        btnSave = view.findViewById(R.id.btnSave);
        cardThuVien = view.findViewById(R.id.cardThuVien);
        cardUser  = view.findViewById(R.id.CardUser );
        cardBrowse = view.findViewById(R.id.cardBrowse);
    }

    private void setOnClickListeners() {
        currentCard.setOnClickListener(view -> flipCard(currentCard, tvMatTruoc, tvMatSau));

        cardThuVien.setOnClickListener(view -> {
            highlightBottomNavItem(R.id.topic);
            replaceFragment(new BrowseFragment());
        });

        cardUser .setOnClickListener(view -> {
            highlightBottomNavItem(R.id.user);
            replaceFragment(new UserFragment());
        });

        cardBrowse.setOnClickListener(view -> {
            highlightBottomNavItem(R.id.browse);
            replaceFragment(new TopicFragment());
        });
    }

    private void highlightBottomNavItem(int itemId) {
        BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(itemId);
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void flipCard(CardView cardView, TextView textFront, TextView textBack) {
        cardView.setCameraDistance(getResources().getDisplayMetrics().density * 15000);
        cardView.setPivotX((float) cardView.getWidth() / 2);
        cardView.setPivotY((float) cardView.getHeight() / 2);

        if (isFront) {
            cardView.animate()
                    .rotationY(90)
                    .setDuration(100)
                    .withEndAction(() -> {
                        textFront.setVisibility(View.GONE);
                        btnSound.setVisibility(View.INVISIBLE);
                        textBack.setVisibility(View.VISIBLE);
                        cardView.setRotationY(-90); // Reset for the next half
                        cardView.animate()
                                .rotationY(0)
                                .setDuration(100)
                                .start();
                    })
                    .start();
        } else {
            cardView.animate()
                    .rotationY(-90)
                    .setDuration(100)
                    .withEndAction(() -> {
                        textBack.setVisibility(View.GONE);
                        textFront.setVisibility(View.VISIBLE);                        btnSound.setVisibility(View.VISIBLE);
                        cardView.setRotationY(90); // Reset for the next half
                        cardView.animate()
                                .rotationY(0)
                                .setDuration(100)
                                .start();
                    })
                    .start();
        }

        isFront = !isFront; // Toggle state
    }
}