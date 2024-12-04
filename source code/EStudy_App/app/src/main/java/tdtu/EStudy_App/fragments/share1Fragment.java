package tdtu.EStudy_App.fragments;


import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import tdtu.EStudy_App.R;
import tdtu.EStudy_App.activities.TopicDetail;
import tdtu.EStudy_App.adapters.TopicBrowseAdapter;
import tdtu.EStudy_App.models.Topic;

public class share1Fragment extends Fragment implements TopicBrowseAdapter.OnTopicClickListener {


    private static final int REQUEST_CODE_TOPIC_DETAIL = 1;
    RecyclerView recyclerView;
    FirebaseFirestore db;
    FirebaseUser user;
    TopicBrowseAdapter topicBrowseAdapter;
    EditText searchTopicShare;
    public share1Fragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_share1, container, false);
        init(view);
        loadAllPublicTopics();

        searchTopicShare.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Do nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                topicBrowseAdapter.filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Do nothing
            }
        });
        return view;
    }

    protected void init(View view){
        recyclerView = view.findViewById(R.id.recyclerViewAllPublicTopic);
        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        topicBrowseAdapter = new TopicBrowseAdapter(getContext(), new ArrayList<>(), this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(topicBrowseAdapter);
        searchTopicShare = view.findViewById(R.id.searchTopicShare);
    };

    private void loadAllPublicTopics() {
        if (user != null) {
            String userId = user.getUid();
            //CHỗ này lấy uid của người dùng hện tại
            db.collection("users").document(userId).get().addOnCompleteListener(userTask -> {
                if (userTask.isSuccessful() && userTask.getResult() != null) {

                    //Lấy reference của người dùng
                    DocumentReference userRef = userTask.getResult().getReference();
                    db.collection("topics")
                            .whereEqualTo("status", "public")
                            .get()
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful() && task.getResult() != null) {
                                    List<Topic> loadedTopics = new ArrayList<>();
                                    for (DocumentSnapshot document : task.getResult()) {
                                        DocumentReference topicUserRef = document.getDocumentReference("userCreate");

                                        // Xong rồi mới kiểm tra xem topic có phải của người dùng hiện tại không
                                        if (topicUserRef != null && !topicUserRef.equals(userRef)) {
                                            String id = document.getId();
                                            String name = document.getString("name");
                                            Timestamp createTime = document.getTimestamp("createTime");
                                            long numWord = document.getLong("numWord");
                                            String status = document.getString("status");

                                            Topic topic = new Topic(id, name, status, topicUserRef.getId(), createTime, (int) numWord);
                                            loadedTopics.add(topic);
                                        }
                                    }

                                    // Update the adapter with the loaded topics
                                    topicBrowseAdapter.updateTopics(loadedTopics);
                                } else {
                                    Toast.makeText(getContext(), "Error getting topics: " + (task.getException() != null ? task.getException().getMessage() : "Unknown error"), Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(e -> {
                                Log.e("share1Fragment", "Error getting topics: ", e);
                                Toast.makeText(getContext(), "Failed to load topics: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                } else {
                    Toast.makeText(getContext(), "Error getting user reference: " + (userTask.getException() != null ? userTask.getException().getMessage() : "Unknown error"), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(getContext(), "User  not logged in", Toast.LENGTH_SHORT).show();
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
            loadAllPublicTopics();
        }
    }


}