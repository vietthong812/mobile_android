package tdtu.EStudy_App.fragments;



import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import tdtu.EStudy_App.R;
import tdtu.EStudy_App.activities.TopicDetail;
import tdtu.EStudy_App.adapters.TopicBrowseAdapter;
import tdtu.EStudy_App.adapters.TopicHomeAdapter;
import tdtu.EStudy_App.models.Topic;
import tdtu.EStudy_App.utils.ToastUtils;

public class share2Fragment extends Fragment implements TopicBrowseAdapter.OnTopicClickListener {


    private static final int REQUEST_CODE_TOPIC_DETAIL = 1;
    RecyclerView recyclerView;
    FirebaseFirestore db;
    FirebaseUser user;
    TopicBrowseAdapter topicBrowseAdapter;
    public share2Fragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_share2, container, false);
        init(view);
        loadYourTopicsShared();
        return view;
    }

    protected void init(View view) {
        recyclerView = view.findViewById(R.id.recyclerViewYourTopicShare);
        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        topicBrowseAdapter = new TopicBrowseAdapter(getContext(), new ArrayList<>(), this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(topicBrowseAdapter);
    }

    private void loadYourTopicsShared() {
        if (user != null) {
            String userId = user.getUid();
            db.collection("topics")
                    .whereEqualTo("userCreate", db.collection("users").document(userId))
                    .whereEqualTo("status", "public")
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult() != null) {
                            List<Topic> loadedTopics = new ArrayList<>();
                            for (DocumentSnapshot document : task.getResult()) {
                                String id = document.getId();
                                String name = document.getString("name");
                                Timestamp createTime = document.getTimestamp("createTime");
                                long numWord = document.getLong("numWord");
                                String status = document.getString("status");

                                Topic topic = new Topic(id, name, status, userId, createTime, (int) numWord);
                                loadedTopics.add(topic);
                            }

                            topicBrowseAdapter.updateTopics(loadedTopics);
                        } else {
                            Toast.makeText(getContext(), "Error getting topics: " + (task.getException() != null ? task.getException().getMessage() : "Unknown error"), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e("share2Fragment", "Error getting topics: ", e);
                        Toast.makeText(getContext(), "Failed to load topics: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else {
            ToastUtils.showLongToast(getContext(), "User is not logged in");
        }
    }

    @Override
    public void onTopicClick(String topicId) {
        Intent intent = new Intent(getContext(), TopicDetail.class);
        intent.putExtra("topicID", topicId);
        startActivityForResult(intent, REQUEST_CODE_TOPIC_DETAIL);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_TOPIC_DETAIL && resultCode == getActivity().RESULT_OK) {
            loadYourTopicsShared();
        }
    }


}