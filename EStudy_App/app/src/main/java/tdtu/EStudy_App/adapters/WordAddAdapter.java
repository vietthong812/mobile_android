package tdtu.EStudy_App.adapters;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import tdtu.EStudy_App.R;
import tdtu.EStudy_App.models.Word;

public class WordAddAdapter extends RecyclerView.Adapter<WordAddAdapter.WordAddViewHolder> {
    private Context context;
    private ArrayList<Word> wordList;
    private OnWordDeleteClickListener listener;
    public WordAddAdapter(Context context, ArrayList<Word> wordList, OnWordDeleteClickListener listener) {
        this.context = context;
        this.wordList = wordList;
        this.listener = listener;
    }
    public ArrayList<Word> getWordList() {
        return wordList;
    }
    public void updateWordList(ArrayList<Word> newWordList) {
        this.wordList.clear();
        this.wordList.addAll(newWordList);
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public WordAddViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.word_item_add, parent, false);
        return new WordAddViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WordAddViewHolder holder, int position) {
        Word word = wordList.get(position);
        EditText tuVung = holder.tuVung;
        EditText nghia = holder.nghia;
        tuVung.setText(word.getName());
        nghia.setText(word.getMeaning());
        tuVung.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                word.setName(tuVung.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        nghia.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                word.setMeaning(nghia.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        holder.btnDeleteWord.setOnClickListener(v -> listener.onWordDeleteClick(word));

    }

    @Override
    public int getItemCount() {
        return wordList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public static class WordAddViewHolder extends RecyclerView.ViewHolder {
        EditText tuVung, nghia;
        AppCompatButton btnDeleteWord;
        public WordAddViewHolder(@NonNull View itemView) {
            super(itemView);
            tuVung = itemView.findViewById(R.id.tuVung);
            nghia = itemView.findViewById(R.id.nghia);
            btnDeleteWord = itemView.findViewById(R.id.xoaTu);
        }
    }

}
