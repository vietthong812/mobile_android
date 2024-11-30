package tdtu.EStudy_App.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;


import tdtu.EStudy_App.R;
import tdtu.EStudy_App.activities.AddTopic;
import tdtu.EStudy_App.activities.TopicDetail;
import tdtu.EStudy_App.adapters.OnTopicClickListener;
import tdtu.EStudy_App.adapters.TopicListAdapter;
import tdtu.EStudy_App.models.Topic;
import tdtu.EStudy_App.viewmodels.TopicViewModel;


public class Tag2Fragment extends Fragment implements OnTopicClickListener {
    private static final int REQUEST_CODE_REMOVE= 1;// Define REQUEST_CODE_REMOVE as a constant
    private static final int REQUEST_CODE_ADD= 2;// Define REQUEST_CODE_REMOVE as a constant

    LinearLayout btnAddTopic;
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
        init(view);
        if (user != null) {
            String userId = user.getUid();
            topicViewModel.loadTopics(userId);
        }

        topicViewModel.getTopics().observe(getViewLifecycleOwner(), topics -> {
            topicListAdapter.updateTopics(topics);
        });
        btnAddTopic.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), AddTopic.class);
            startActivityForResult(intent, REQUEST_CODE_ADD);
        });
        return view;
    }
    @Override
    public void onTopicClick(Topic topic) {
        Intent intent = new Intent(getContext(), TopicDetail.class);
        intent.putExtra("topicID", topic.getId());
        startActivityForResult(intent, REQUEST_CODE_REMOVE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_REMOVE && resultCode == Activity.RESULT_OK) {
            if (user != null) {
                String userId = user.getUid();
                topicViewModel.loadTopics(userId);
            }
        }
        if (requestCode == REQUEST_CODE_ADD && resultCode == Activity.RESULT_OK) {
            if (user != null) {
                String userId = user.getUid();
                topicViewModel.loadTopics(userId);
            }
        }
    }

    private void init(View view) {
        // Initialize View
        btnAddTopic = view.findViewById(R.id.btnAddTopic);
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        // Initialize RecyclerView
        recyclerViewTopic = view.findViewById(R.id.recyclerViewTopic);
        recyclerViewTopic.setLayoutManager(new LinearLayoutManager(getContext()));
        topicViewModel = new ViewModelProvider(this).get(TopicViewModel.class);
        topicListAdapter = new TopicListAdapter(getContext(), new ArrayList<>(), this);
        recyclerViewTopic.setAdapter(topicListAdapter);
    }
}