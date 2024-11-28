package tdtu.EStudy_App;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
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


import tdtu.EStudy_App.activities.TopicDetail;
import tdtu.EStudy_App.adapters.OnTopicClickListener;
import tdtu.EStudy_App.adapters.TopicListAdapter;
import tdtu.EStudy_App.models.Topic;
import tdtu.EStudy_App.utils.ToastUtils;
import tdtu.EStudy_App.viewmodels.TopicViewModel;


public class Tag2Fragment extends Fragment implements OnTopicClickListener {
    private static final int REQUEST_CODE = 1; // Define REQUEST_CODE as a constant

    RecyclerView recyclerViewTopic;
    TopicListAdapter topicListAdapter;
    TopicViewModel topicViewModel;
    FirebaseAuth mAuth;
    FirebaseUser user;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tag2, container, false);
        recyclerViewTopic = view.findViewById(R.id.recyclerViewTopic);
        recyclerViewTopic.setLayoutManager(new LinearLayoutManager(getContext()));

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        topicViewModel = new ViewModelProvider(this).get(TopicViewModel.class);
        topicListAdapter = new TopicListAdapter(getContext(), new ArrayList<>(), this);
        recyclerViewTopic.setAdapter(topicListAdapter);
        if (user != null) {
            String userId = user.getUid();
            topicViewModel.loadTopics(userId);
        }

        topicViewModel.getTopics().observe(getViewLifecycleOwner(), topics -> {
            topicListAdapter.updateTopics(topics);
        });
        return view;
    }
    @Override
    public void onTopicClick(Topic topic) {
        Intent intent = new Intent(getContext(), TopicDetail.class);
        intent.putExtra("topicID", topic.getId());
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            String topicId = data.getStringExtra("topicID");
            if (topicId != null) {
                topicListAdapter.removeTopicById(topicId);
            }
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        if (user != null) {
            String userId = user.getUid();
            topicViewModel.loadTopics(userId);
        }
    }
}