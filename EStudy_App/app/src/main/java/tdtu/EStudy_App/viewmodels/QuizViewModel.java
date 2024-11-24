package tdtu.EStudy_App.viewmodels;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Map;

import tdtu.EStudy_App.activities.MainActivity;
import tdtu.EStudy_App.activities.Register;
import tdtu.EStudy_App.models.Word;
import tdtu.EStudy_App.utils.ToastUtils;

public class QuizViewModel {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public ArrayList<Word> getWordList(String topicID) {
        ArrayList<Word> words = new ArrayList<>();
        db.collection("topics").document(topicID)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Map<String, Object> wordsMap = (Map<String, Object>) document.get("words");
                            if (wordsMap != null) {
                                for (Map.Entry<String, Object> entry : wordsMap.entrySet()) {
                                    Word word = new Word();
                                    word.setName(entry.getKey());

                                    Map<String, String> wordInfo = (Map<String, String>) entry.getValue();
                                    for(Map.Entry<String, String> info : wordInfo.entrySet()) {
                                        switch (info.getKey()) {
                                            case "meaning":
                                                word.setMeaning(info.getValue());
                                                break;
                                            case "pronunciation":
                                                word.setPronunciation(info.getValue());
                                                break;
                                            case "state":
                                                word.setState(info.getValue());
                                                break;
                                            case "marked":
                                                word.setMarked(Boolean.parseBoolean(info.getValue()));
                                                break;
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        Exception exception = task.getException();
                        if (exception != null) {
                            String errorMessage = exception.getMessage();
                            Log.e("getDataFlashCardError", errorMessage);
                        }
                    }
                });
        return words;
    }
}
