package tdtu.EStudy_App.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

public class share3Fragment extends Fragment implements TopicBrowseAdapter.OnTopicClickListener {

    RecyclerView recyclerView;
    FirebaseFirestore db;
    FirebaseUser user;
    TopicBrowseAdapter topicBrowseAdapter;
    EditText searchYourTopicSaved;
    private static final int REQUEST_CODE_TOPIC_DETAIL = 1;

    public share3Fragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_share3, container, false);
        init(view);
        loadSavedTopics();

        searchYourTopicSaved.addTextChangedListener(new android.text.TextWatcher() {
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

    protected void init(View view) {
        recyclerView = view.findViewById(R.id.recyclerViewTopicSaved);
        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        topicBrowseAdapter = new TopicBrowseAdapter(getContext(), new ArrayList<>(), this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(topicBrowseAdapter);
        searchYourTopicSaved = view.findViewById(R.id.searchYourTopicSaved);
    }

    private void loadSavedTopics() {
        if (user != null) {
            String userId = user.getUid();
            db.collection("users").document(userId).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        List<DocumentReference> savedTopics = (List<DocumentReference>) document.get("saveTopic");
                        if (savedTopics != null) {
                            List<Topic> loadedTopics = new ArrayList<>();
                            for (DocumentReference topicRef : savedTopics) {
                                topicRef.get().addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {
                                        DocumentSnapshot document1 = task1.getResult();
                                        if (document1.exists()) {
                                            String id = document1.getId();
                                            String name = document1.getString("name");
                                            Timestamp createTime = document1.getTimestamp("createTime");
                                            long numWord = document1.getLong("numWord");
                                            String status = document1.getString("status");
                                            DocumentReference userCreateRef = document1.getDocumentReference("userCreate");
                                            String userIdOfTopic = userCreateRef != null ? userCreateRef.getId() : null;

                                            Topic topic = new Topic(id, name, status, userIdOfTopic, createTime, (int) numWord);
                                            loadedTopics.add(topic);
                                        }
                                        topicBrowseAdapter.updateTopics(loadedTopics);
                                    }
                                });
                            }
                        }
                    }
                }
            });
        }
    }

    @Override
    public void onTopicClick(String topicId) {
        Intent intent = new Intent(getContext(), TopicDetail.class);
        intent.putExtra("topicID", topicId);
        startActivityForResult(intent, REQUEST_CODE_TOPIC_DETAIL);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_TOPIC_DETAIL && resultCode == getActivity().RESULT_OK) {
            loadSavedTopics();
        }
    }
}