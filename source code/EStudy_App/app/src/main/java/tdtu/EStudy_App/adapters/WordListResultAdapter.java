package tdtu.EStudy_App.adapters;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
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

public class WordListResultAdapter extends RecyclerView.Adapter<WordListResultAdapter.WordViewHolder> {
    private Context context;
    private List<Word> wordList;
    private Boolean isFalse;

    public WordListResultAdapter(Context context, List<Word> wordList, Boolean isFalse) {
        this.context = context;
        this.wordList = wordList;
        this.isFalse = isFalse;
    }

    @NonNull
    @Override
    public WordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.word_item_result, parent, false);
        return new WordViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WordViewHolder holder, int position) {
        Word word = wordList.get(position);
        holder.wordResult.setText(word.getName());
        holder.wordMeanResult.setText(word.getMeaning());
        if (isFalse) {
            holder.backgroundWordResult.setBackgroundResource(R.drawable.word_wrong_many_time);
        } else {
            holder.backgroundWordResult.setBackgroundResource(R.drawable.word_correct_many_time);
        }
    }

    @Override
    public int getItemCount() {
        return wordList.size();
    }

    public static class WordViewHolder extends RecyclerView.ViewHolder {
        TextView wordResult, wordMeanResult;
        LinearLayout backgroundWordResult;

        public WordViewHolder(@NonNull View itemView) {
            super(itemView);
            wordResult = itemView.findViewById(R.id.wordResult);
            wordMeanResult = itemView.findViewById(R.id.wordMeanResult);
            backgroundWordResult = itemView.findViewById(R.id.backgroundForWordResult);

        }
    }


}