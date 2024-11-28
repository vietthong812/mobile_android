package tdtu.EStudy_App;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import java.util.ArrayList;
import tdtu.EStudy_App.adapters.FolderListAdapter;
import tdtu.EStudy_App.viewmodels.FolderViewModel;


public class Tag1Fragment extends Fragment {
    RecyclerView recyclerViewFolder;
    FolderListAdapter folderListAdapter;
    FolderViewModel folderViewModel;
    FirebaseUser user;
    FirebaseAuth mAuth;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tag1, container, false);
        recyclerViewFolder = view.findViewById(R.id.recyclerViewFolder);
        recyclerViewFolder.setLayoutManager(new LinearLayoutManager(getContext()));
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        folderListAdapter = new FolderListAdapter(getContext(), new ArrayList<>());
        recyclerViewFolder.setAdapter(folderListAdapter);
        folderViewModel = new ViewModelProvider(this).get(FolderViewModel.class);

        folderViewModel.getFolders().observe(getViewLifecycleOwner(), folders -> {
            folderListAdapter.updateFolders(folders);
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
}