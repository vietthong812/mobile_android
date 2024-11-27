package tdtu.EStudy_App;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import tdtu.EStudy_App.adapters.FolderListAdapter;
import tdtu.EStudy_App.adapters.TopicListAdapter;
import tdtu.EStudy_App.models.Folder;
import tdtu.EStudy_App.models.Topic;
import tdtu.EStudy_App.utils.ToastUtils;


public class Tag1Fragment extends Fragment {
    RecyclerView recyclerViewFolder;
    FolderListAdapter folderListAdapter;
    List<Folder> topicFolder;
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tag1, container, false);
        topicFolder = new ArrayList<>();
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        folderListAdapter = new FolderListAdapter(getContext(), topicFolder);
        recyclerViewFolder = view.findViewById(R.id.recyclerViewFolder);
        recyclerViewFolder.setLayoutManager(new LinearLayoutManager(getContext()));
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            db.collection("folders")
                    .whereEqualTo("userCreate", db.document("users/" + userId))
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            topicFolder.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String name = document.getString("name");
                                String id = document.getId();
                                Folder folder = new Folder(id, name);
                                topicFolder.add(folder);
                            }
                            recyclerViewFolder.setAdapter(folderListAdapter);
                        } else {
                            ToastUtils.showShortToast(getContext(), "Error getting documents: " + task.getException());
                        }
                    });
        }
        return view;
    }
}