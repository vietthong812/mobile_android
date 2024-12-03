package tdtu.EStudy_App.adapters;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import tdtu.EStudy_App.R;
import tdtu.EStudy_App.models.Word;

public class WordListAdapter extends RecyclerView.Adapter<WordListAdapter.WordViewHolder> {
    private Context context;
    private List<Word> wordList;
    private TextToSpeech textToSpeech;
    private OnWordMarkedListener onWordMarkedListener;
    private Map<String, Integer> learnedWordsMap = new HashMap<>();
    private Map<String, Integer> learningWordsMap = new HashMap<>();
    private Map<String, Integer> unlearnWordsMap = new HashMap<>();

    public WordListAdapter(Context context, List<Word> wordList, OnWordMarkedListener onWordMarkedListener) {
        this.context = context;
        this.wordList = wordList;
        this.onWordMarkedListener = onWordMarkedListener;

        textToSpeech = new TextToSpeech(context, status -> {
            if (status != TextToSpeech.ERROR) {
                textToSpeech.setLanguage(Locale.US);
            } else {
                Toast.makeText(context, "Error initializing TextToSpeech", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void updateWordList(List<Word> newWordList) {
        this.wordList.clear();
        this.wordList.addAll(newWordList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public WordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.word_item, parent, false);
        return new WordViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WordViewHolder holder, int position) {
        Word word = wordList.get(position);
        holder.edtWordName.setText(word.getName());
        holder.edtWordMean.setText(word.getMeaning());

        if (learnedWordsMap.containsKey(word.getId())) {
            holder.statusWord.setText("Đã học");
            holder.statusWord.setTextColor(context.getResources().getColor(R.color.green));
        } else if (learningWordsMap.containsKey(word.getId())) {
            holder.statusWord.setText("Đang học");
            holder.statusWord.setTextColor(context.getResources().getColor(R.color.orange));
        } else if (unlearnWordsMap.containsKey(word.getId())) {
            holder.statusWord.setText("Chưa học");
            holder.statusWord.setTextColor(context.getResources().getColor(R.color.red));
        }

        holder.btnSound.setOnClickListener(v -> {
            holder.btnSound.setBackgroundResource(R.drawable.sound_icon_selected);
            String text = word.getName();

            String utteranceId = String.valueOf(System.currentTimeMillis());
            textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId);

            textToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                @Override
                public void onStart(String utteranceId) {
                }

                @Override
                public void onDone(String utteranceId) {
                    holder.btnSound.post(() -> holder.btnSound.setBackgroundResource(R.drawable.mic_icon));
                }

                @Override
                public void onError(String utteranceId) {
                    holder.btnSound.post(() -> holder.btnSound.setBackgroundResource(R.drawable.mic_icon));
                    Toast.makeText(context, "Pronunciation error", Toast.LENGTH_SHORT).show();
                }
            });
        });

        holder.btnSave.setOnClickListener(v -> {
            boolean isMarked = word.isMarked();
            word.setMarked(!isMarked);
            if (onWordMarkedListener != null) {
                onWordMarkedListener.onWordMarked(word, word.isMarked());
            }
            holder.btnSave.setBackgroundResource(word.isMarked() ? R.drawable.star_icon_selected : R.drawable.star_icon);
        });

        holder.btnSave.setBackgroundResource(word.isMarked() ? R.drawable.star_icon_selected : R.drawable.star_icon);
    }

    @Override
    public int getItemCount() {
        return wordList.size();
    }

    public static class WordViewHolder extends RecyclerView.ViewHolder {
        TextView edtWordName, edtWordMean, statusWord;
        ImageButton btnSound, btnSave;

        public WordViewHolder(@NonNull View itemView) {
            super(itemView);
            edtWordName = itemView.findViewById(R.id.edtWordName);
            edtWordMean = itemView.findViewById(R.id.edtWordMean);
            btnSound = itemView.findViewById(R.id.btnSoundWordList);
            btnSave = itemView.findViewById(R.id.btnSaveWordList);
            statusWord = itemView.findViewById(R.id.statusWord);
        }
    }

    public void setProgressMaps(Map<String, Integer> learnedWordsMap, Map<String, Integer> learningWordsMap, Map<String, Integer> unlearnWordsMap) {
        this.learnedWordsMap = learnedWordsMap != null ? learnedWordsMap : new HashMap<>();
        this.learningWordsMap = learningWordsMap != null ? learningWordsMap : new HashMap<>();
        this.unlearnWordsMap = unlearnWordsMap != null ? unlearnWordsMap : new HashMap<>();
    }
}