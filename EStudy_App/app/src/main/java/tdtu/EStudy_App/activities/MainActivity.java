package tdtu.EStudy_App.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import tdtu.EStudy_App.R;
import tdtu.EStudy_App.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private FloatingActionButton fabAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        // Check login state from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);

        if (!isLoggedIn) {
            // User is not logged in, redirect to SignIn
            Intent intent = new Intent(MainActivity.this, SignIn.class);
            startActivity(intent);
            finish();
        }

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        fabAdd = binding.fabAdd;
        fabAdd.setOnClickListener(v -> showBottomSheetDialog());
        replaceFragment(new HomeFragment());
        binding.bottomNavigationView.setBackground(null);


        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;

            if (item.getItemId() == R.id.home) {
                selectedFragment = new HomeFragment();
            } else if (item.getItemId() == R.id.browse) {
                selectedFragment = new BrowseFragment();
            } else if (item.getItemId() == R.id.topic) {
                selectedFragment = new TopicFragment();
            } else if (item.getItemId() == R.id.user) {
                selectedFragment = new UserFragment();
            } else {
                return false;
            }

            if (selectedFragment != null) {
                replaceFragment(selectedFragment);
                return true;
            }

            return false;
        });

    }

    private void replaceFragment(@NonNull Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }


    private void showBottomSheetDialog() {
        // Create BottomSheetDialog
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View sheetView = LayoutInflater.from(this).inflate(R.layout.bottom_sheet_menu, null);
        CardView createTopic = sheetView.findViewById(R.id.option_create_topic);
        CardView createFolder = sheetView.findViewById(R.id.option_create_folder);
        createTopic.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
            // Handle "Create Topic"
            Intent intent = new Intent(this, MainActivity.class); //Nữa thay activity add Topic
            startActivity(intent);
        });

        createFolder.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
            // Handle "Create Folder"
            Intent intent = new Intent(this, MainActivity.class); //Nữa thay activity add Topic
            startActivity(intent);
        });

        // Show dialog
        bottomSheetDialog.setContentView(sheetView);
        bottomSheetDialog.show();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Nullify binding reference to avoid memory leaks
        binding = null;
    }
}
