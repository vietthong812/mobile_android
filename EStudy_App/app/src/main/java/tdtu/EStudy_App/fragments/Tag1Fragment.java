package tdtu.EStudy_App.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import java.util.ArrayList;

import tdtu.EStudy_App.R;
import tdtu.EStudy_App.activities.AddFolder;
import tdtu.EStudy_App.adapters.FolderListAdapter;
import tdtu.EStudy_App.viewmodels.FolderViewModel;


public class Tag1Fragment extends Fragment {
    RecyclerView recyclerViewFolder;
    FolderListAdapter folderListAdapter;
    FolderViewModel folderViewModel;
    FirebaseUser user;
    FirebaseAuth mAuth;
    LinearLayout btnAdd;
    EditText searchFolder;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tag1, container, false);

        init(view);

        folderViewModel.getFolders().observe(getViewLifecycleOwner(), folders -> {
            folderListAdapter.updateFolders(folders);
        });

        btnAdd.setOnClickListener(v -> {
            // Open AddFolderActivity
            Intent intent = new Intent(getContext(), AddFolder.class);
            startActivity(intent);
        });

        searchFolder.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Do nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                folderListAdapter.filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Do nothing
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (user != null) {
            String userId = user.getUid();
            folderViewModel.loadFolders(userId);
        }
    }
    private void init(View view) {
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        // Initialize View
        btnAdd = view.findViewById(R.id.btnAdd);
        // Initialize RecyclerView
        recyclerViewFolder = view.findViewById(R.id.recyclerViewFolder);
        recyclerViewFolder.setLayoutManager(new LinearLayoutManager(getContext()));
        folderViewModel = new ViewModelProvider(this).get(FolderViewModel.class);
        folderListAdapter = new FolderListAdapter(getContext(), new ArrayList<>());
        recyclerViewFolder.setAdapter(folderListAdapter);
        searchFolder = view.findViewById(R.id.searchFolder);

    }
}