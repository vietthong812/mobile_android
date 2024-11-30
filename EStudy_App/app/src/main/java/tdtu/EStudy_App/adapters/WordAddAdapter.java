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

import java.util.List;

import tdtu.EStudy_App.R;
import tdtu.EStudy_App.models.Word;

public class WordAddAdapter extends RecyclerView.Adapter<WordAddAdapter.WordAddViewHolder> {
    private Context context;
    private List<Word> wordList;

    public WordAddAdapter(Context context, List<Word> wordList) {
        this.context = context;
        this.wordList = wordList;
    }
    public List<Word> getWordList() {
        return wordList;
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
        holder.tuVung.setText(word.getName());
        holder.nghia.setText(word.getMeaning());
        // Cập nhật dữ liệu vào wordList khi người dùng thay đổi
        // Lắng nghe sự thay đổi văn bản trong EditText tuVung (từ)
        holder.tuVung.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
                // Có thể xử lý khi văn bản trước khi thay đổi
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                // Lưu dữ liệu khi người dùng đang nhập vào EditText tuVung
                word.setName(charSequence.toString());  // Cập nhật dữ liệu trong đối tượng Word
            }
            @Override
            public void afterTextChanged(Editable editable) {
                // Có thể xử lý sau khi văn bản đã thay đổi
            }
        });

        // Lắng nghe sự thay đổi văn bản trong EditText nghia (nghĩa)
        holder.nghia.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
                // Có thể xử lý khi văn bản trước khi thay đổi
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                // Lưu dữ liệu khi người dùng đang nhập vào EditText nghia
                word.setMeaning(charSequence.toString());  // Cập nhật dữ liệu trong đối tượng Word
            }
            @Override
            public void afterTextChanged(Editable editable) {
                // Có thể xử lý sau khi văn bản đã thay đổi
            }
        });

        holder.btnDeleteWord.setOnClickListener(v -> {
            // Xóa phần tử tại vị trí hiện tại
            wordList.remove(position);
            // Thông báo xóa phần tử
            notifyItemRemoved(position);
            // Thông báo cập nhật dữ liệu cho các phần tử còn lại
            notifyItemRangeChanged(position, wordList.size() - position);
        });

    }

    @Override
    public int getItemCount() {
        return wordList.size();
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
