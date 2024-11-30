package tdtu.EStudy_App.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import tdtu.EStudy_App.R;
import tdtu.EStudy_App.models.Word;

public class WordListAdapter extends RecyclerView.Adapter<WordListAdapter.WordViewHolder>{
    private Context context;
    private List<Word> wordList;

    public WordListAdapter(Context context, List<Word> wordList) {
        this.context = context;
        this.wordList = wordList;
    }

    @NonNull
    @Override
    public WordListAdapter.WordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.word_item, parent, false);
        return new WordListAdapter.WordViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WordListAdapter.WordViewHolder holder, int position) {
        Word word = wordList.get(position);
        holder.edtWordName.setText(word.getName());
        holder.edtWordMean.setText(word.getMeaning());
    }

    @Override
    public int getItemCount() {
        return wordList.size();
    }

    public static class WordViewHolder extends RecyclerView.ViewHolder {
        TextView edtWordName, edtWordMean;

        public WordViewHolder(@NonNull View itemView) {
            super(itemView);
            edtWordName = itemView.findViewById(R.id.edtWordName);
            edtWordMean = itemView.findViewById(R.id.edtWordMean);
        }
    }
}
