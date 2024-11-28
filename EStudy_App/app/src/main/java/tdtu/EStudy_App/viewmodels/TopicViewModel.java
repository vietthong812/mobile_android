package tdtu.EStudy_App.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import tdtu.EStudy_App.models.Topic;

public class TopicViewModel extends ViewModel {
    private final MutableLiveData<List<Topic>> topics = new MutableLiveData<>();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public LiveData<List<Topic>> getTopics() {
        return topics;
    }

    public void loadTopics(String userId) {
        topics.setValue(new ArrayList<>()); // Clear the current data
        db.collection("topics")
                .whereEqualTo("userCreate", db.document("users/" + userId))
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Topic> topicList = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String name = document.getString("name");
                            String status = document.getString("status");
                            String id = document.getId();
                            int numWord = document.getLong("numWord").intValue();
                            Topic topic = new Topic(id, name, status, userId, document.getTimestamp("createTime"), numWord);
                            topicList.add(topic);
                        }
                        topics.setValue(topicList);
                    }
                });
    }
    public void loadTopicsByFolderId(String folderId) {
        topics.setValue(new ArrayList<>()); // Clear the current data
        db.collection("folders").document(folderId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                ArrayList<DocumentReference> topicIds = (ArrayList<DocumentReference>) document.get("topics");
                if (topicIds != null) {
                    List<Topic> topicList = new ArrayList<>();
                    for (DocumentReference topicId : topicIds) {
                        topicId.get().addOnCompleteListener(topicTask -> {
                            if (topicTask.isSuccessful()) {
                                DocumentSnapshot topicDoc = topicTask.getResult();
                                String name = topicDoc.getString("name");
                                String status = topicDoc.getString("status");
                                String id = topicDoc.getId();
                                int numWord = topicDoc.getLong("numWord").intValue();
                                DocumentReference userCreate = (DocumentReference) topicDoc.get("userCreate");
                                String userId = userCreate.getId();
                                Topic topic = new Topic(id, name, status, userId, topicDoc.getTimestamp("createTime"), numWord);
                                topicList.add(topic);
                                topics.setValue(topicList);
                            }
                        });
                    }
                }
            }
        });
    }
}