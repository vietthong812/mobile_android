package tdtu.EStudy_App;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;


import tdtu.EStudy_App.adapters.TopicListAdapter;
import tdtu.EStudy_App.models.Topic;
import tdtu.EStudy_App.utils.ToastUtils;


public class Tag2Fragment extends Fragment {
    RecyclerView recyclerViewTopic;
    TopicListAdapter topicListAdapter;
    List<Topic> topicList;
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tag2, container, false);
        recyclerViewTopic = view.findViewById(R.id.recyclerViewTopic);
        topicList = new ArrayList<>();

        recyclerViewTopic.setLayoutManager(new LinearLayoutManager(getContext()));
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            db.collection("topics")
                    .whereEqualTo("userCreate", db.document("users/" + userId))
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            topicList.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String name = document.getString("name");
                                String status = document.getString("status");
                                Timestamp createTime = document.getTimestamp("createTime");
                                String id = document.getId();
                                int numWord = document.getLong("numWord").intValue();
                                Topic topic = new Topic(id, name, status, userId, createTime, numWord);
                                topicList.add(topic);
                            }
                            topicListAdapter = new TopicListAdapter(getContext(), topicList);
                            recyclerViewTopic.setAdapter(topicListAdapter);
                        } else {
                            // Handle the error
                            ToastUtils.showShortToast(getContext(), "Error getting documents: " + task.getException());
                        }
                    });
        }

        return view;
    }

}