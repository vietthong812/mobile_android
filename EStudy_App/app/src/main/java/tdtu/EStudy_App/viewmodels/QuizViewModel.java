// QuizViewModel.java
package tdtu.EStudy_App.viewmodels;

import android.util.Log;

import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import tdtu.EStudy_App.models.Word;

public class QuizViewModel extends ViewModel {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

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
}