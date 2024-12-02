// QuizViewModel.java
package tdtu.EStudy_App.viewmodels;

import android.util.Log;

import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tdtu.EStudy_App.models.Word;
import tdtu.EStudy_App.utils.FirebaseUserUtils;

public class QuizViewModel extends ViewModel {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseUser user = FirebaseUserUtils.getInstance();

    public interface WordListCallback {
        void onWordListLoaded(List<Word> words);
        void onError(Exception e);
    }

    public void loadWordList(String topicID, WordListCallback callback) {
        db.collection("topics").document(topicID).collection("words")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Word> words = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Word word = new Word();
                            word.setId(document.getId());
                            word.setName(document.getString("name"));
                            word.setMeaning(document.getString("meaning"));
                            word.setPronunciation(document.getString("pronunciation"));
                            word.setState(document.getString("state"));
                            word.setMarked(document.getBoolean("marked"));
                            words.add(word);
                        }
                        callback.onWordListLoaded(words);
                    } else {
                        Exception exception = task.getException();
                        if (exception != null) {
                            Log.e("getDataFlashCardError", exception.getMessage());
                            callback.onError(exception);
                        }
                    }
                });
    }

    public void saveLearningResult(String topicID, List<Word> learnedWords, List<Word> topicWords) {
        db.collection("topics").document(topicID).collection("progress").document(user.getUid())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (!task.getResult().exists()) {
                            // Document does not exist, create it with initial maps
                            Map<String, Integer> learningWordsMap = new HashMap<>();
                            Map<String, Integer> unlearnWordsMap = new HashMap<>();
                            Map<String, Integer> learnedWordsMap = new HashMap<>();

                            for (Word word : learnedWords) {
                                learningWordsMap.put(word.getId(), 1);
                            }

                            for (Word word : topicWords) {
                                if (!learningWordsMap.containsKey(word.getId())) {
                                    unlearnWordsMap.put(word.getId(), 0);
                                }
                            }

                            Map<String, Object> initialData = new HashMap<>();
                            initialData.put("learnedWords", learnedWordsMap);
                            initialData.put("learningWords", learningWordsMap);
                            initialData.put("unlearnWords", unlearnWordsMap);

                            db.collection("topics").document(topicID).collection("progress").document(user.getUid())
                                    .set(initialData)
                                    .addOnSuccessListener(aVoid -> {
                                        Log.d("saveLearningResult", "Document successfully created!");
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.w("saveLearningResult", "Error creating document", e);
                                    });
                        } else {
                            // Document exists, update the maps
                            DocumentSnapshot document = task.getResult();
                            Map<String, Long> learnedWordsMap = (Map<String, Long>) document.get("learnedWords");
                            Map<String, Long> learningWordsMap = (Map<String, Long>) document.get("learningWords");
                            Map<String, Long> unlearnWordsMap = (Map<String, Long>) document.get("unlearnWords");

                            for (Word word : learnedWords) {
                                String wordId = word.getId();
                                if (unlearnWordsMap.containsKey(wordId)) {
                                    unlearnWordsMap.remove(wordId);
                                    learningWordsMap.put(wordId, 1l);
                                } else if (learningWordsMap.containsKey(wordId)) {
                                    Long count = learningWordsMap.get(wordId) + 1l;
                                    if (count > 5) {
                                        learningWordsMap.remove(wordId);
                                        learnedWordsMap.put(wordId, count);
                                    } else {
                                        learningWordsMap.put(wordId, count);
                                    }
                                }
                            }

                            Map<String, Object> updatedData = new HashMap<>();
                            updatedData.put("learnedWords", learnedWordsMap);
                            updatedData.put("learningWords", learningWordsMap);
                            updatedData.put("unlearnWords", unlearnWordsMap);

                            db.collection("topics").document(topicID).collection("progress").document(user.getUid())
                                    .update(updatedData)
                                    .addOnSuccessListener(aVoid -> {
                                        Log.d("saveLearningResult", "Document successfully updated!");
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.w("saveLearningResult", "Error updating document", e);
                                    });
                        }
                    } else {
                        Log.w("saveLearningResult", "Error getting document", task.getException());
                    }
                });
    }

    public void loadWordsAndMarkedWords(String topicId, String userId, WordListCallback callback) {
        db.collection("topics").document(topicId).collection("words")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Word> allWords = new ArrayList<>();
                        for (DocumentSnapshot document : task.getResult()) {
                            Word word = new Word();
                            word.setId(document.getId()); // This line sets the word ID
                            word.setName(document.getString("name"));
                            word.setMeaning(document.getString("meaning"));
                            word.setPronunciation(document.getString("pronunciation"));
                            word.setState(document.getString("state"));
                            word.setMarked(false); // Initialize as not marked
                            allWords.add(word);
                        }

                        db.collection("topics").document(topicId)
                                .collection("markedList").document(userId)
                                .get()
                                .addOnCompleteListener(markedTask -> {
                                    if (markedTask.isSuccessful() && markedTask.getResult() != null) {
                                        List<String> markedWordIds = (List<String>) markedTask.getResult().get("wordIds");
                                        if (markedWordIds != null) {
                                            for (Word word : allWords) {
                                                if (markedWordIds.contains(word.getId())) {
                                                    word.setMarked(true);
                                                }
                                            }
                                        }
                                        callback.onWordListLoaded(allWords);
                                    } else {
                                        callback.onError(markedTask.getException());
                                    }
                                });
                    } else {
                        callback.onError(task.getException());
                    }
                });
    }


}


