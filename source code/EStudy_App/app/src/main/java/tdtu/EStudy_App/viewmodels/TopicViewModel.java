package tdtu.EStudy_App.viewmodels;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public void loadAllTopics() {
        topics.setValue(new ArrayList<>()); // Clear the current data
        db.collection("topics")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Topic> topicList = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String name = document.getString("name");
                            String status = document.getString("status");
                            String id = document.getId();
                            int numWord = document.getLong("numWord").intValue();
                            String userId = ((DocumentReference) document.get("userCreate")).getId();
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

    public void getTopicProgress(String topicId, String userId, OnProgressLoadedListener callback) {
        db.collection("topics").document(topicId).collection("progress").document(userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && task.getResult().exists()) {
                        DocumentSnapshot document = task.getResult();
                        Map<String, Integer> learnedWordsMap = (Map<String, Integer>) document.get("learnedWords");
                        Map<String, Integer> learningWordsMap = (Map<String, Integer>) document.get("learningWords");
                        Map<String, Integer> unlearnWordsMap = (Map<String, Integer>) document.get("unlearnWords");

                        callback.onProgressLoaded(learnedWordsMap, learningWordsMap, unlearnWordsMap);
                    } else {
                        initializeProgress(topicId, userId, callback);
                    }
                });
    }

    private void initializeProgress(String topicId, String userId, OnProgressLoadedListener callback) {
        db.collection("topics").document(topicId).collection("words")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Map<String, Integer> learnedWordsMap = new HashMap<>();
                        Map<String, Integer> learningWordsMap = new HashMap<>();
                        Map<String, Integer> unlearnWordsMap = new HashMap<>();

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String wordId = document.getId();
                            unlearnWordsMap.put(wordId, 0);
                        }

                        Map<String, Object> initialData = new HashMap<>();
                        initialData.put("learnedWords", learnedWordsMap);
                        initialData.put("learningWords", learningWordsMap);
                        initialData.put("unlearnWords", unlearnWordsMap);

                        db.collection("topics").document(topicId)
                                .collection("progress").document(userId)
                                .set(initialData)
                                .addOnSuccessListener(aVoid -> {
                                    callback.onProgressLoaded(learnedWordsMap, learningWordsMap, unlearnWordsMap);
                                })
                                .addOnFailureListener(e -> {
                                    Log.w("initializeProgress", "Error creating progress document", e);
                                });
                    } else {
                        Log.w("initializeProgress", "Error getting words", task.getException());
                    }
                });
    }

    public interface OnProgressLoadedListener {
        void onProgressLoaded(Map<String, Integer> learnedWordsMap, Map<String, Integer> learningWordsMap, Map<String, Integer> unlearnWordsMap);
    }

    public void loadTopicForAddFolder(String userId, String folderId) {
        List<Topic> allUserTopics = new ArrayList<>();
        List<Topic> savedTopics = new ArrayList<>();

        db.collection("topics")
                .whereEqualTo("userCreate", db.document("users/" + userId))
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String name = document.getString("name");
                            String status = document.getString("status");
                            String id = document.getId();
                            int numWord = document.getLong("numWord").intValue();
                            Topic topic = new Topic(id, name, status, userId, document.getTimestamp("createTime"), numWord);
                            allUserTopics.add(topic);
                        }

                        // Fetch topics saved by the user
                        db.collection("users").document(userId).get().addOnCompleteListener(savedTask -> {
                            if (savedTask.isSuccessful()) {
                                DocumentSnapshot document = savedTask.getResult();
                                if (document.exists()) {
                                    List<DocumentReference> loadSavedTopic = (List<DocumentReference>) document.get("saveTopic");
                                    if (loadSavedTopic != null) {
                                        for (DocumentReference topicRef : loadSavedTopic) {
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
                                                        savedTopics.add(topic);


                                                    }
                                                }
                                            });
                                        }
                                    }
                                    combineAndFilterTopics(allUserTopics, savedTopics, folderId);
                                }
                            }
                        });
                    }
                });
    }

    private void combineAndFilterTopics(List<Topic> allUserTopics, List<Topic> savedTopics, String folderId) {
        List<Topic> combinedTopics = new ArrayList<>(allUserTopics);
        if (savedTopics != null){
            combinedTopics.addAll(savedTopics);

            db.collection("folders").document(folderId).get().addOnCompleteListener(folderTask -> {
                if (folderTask.isSuccessful()) {
                    DocumentSnapshot folderDocument = folderTask.getResult();
                    List<DocumentReference> savedTopicRefs = (List<DocumentReference>) folderDocument.get("topics");
                    List<String> savedTopicIds = new ArrayList<>();
                    if (savedTopicRefs != null) {
                        for (DocumentReference ref : savedTopicRefs) {
                            savedTopicIds.add(ref.getId());
                        }
                    }

                    List<Topic> topicsToAdd = new ArrayList<>();
                    for (Topic topic : combinedTopics) {
                        if (!savedTopicIds.contains(topic.getId())) {
                            topicsToAdd.add(topic);
                        }
                    }

                    topics.setValue(topicsToAdd);
                }
            });
        }
        else {
            topics.setValue(allUserTopics);
        }
    }
}
