// CardGoTuAdapter.java
package tdtu.EStudy_App.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import tdtu.EStudy_App.models.Word;
import tdtu.EStudy_App.R;

public class CardGoTuAdapter extends RecyclerView.Adapter<CardGoTuAdapter.WordViewHolder> {
    private Context context;
    private List<Word> wordList;

    public CardGoTuAdapter(Context context, List<Word> wordList) {
        this.context = context;
        this.wordList = wordList;
    }

    @NonNull
    @Override
    public WordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.card_item_gotu, parent, false);
        return new WordViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WordViewHolder holder, int position) {
        Word word = wordList.get(position);

        holder.tvName.setText(word.getName());
        holder.tvMeaning.setText(word.getMeaning());
        holder.tvPronunciation.setText(word.getPronunciation());

        holder.btnSound.setOnClickListener(v -> {
            // Play pronunciation or do some action when clicked
        });

        holder.btnSave.setOnClickListener(v -> {
            boolean isMarked = word.isMarked();
            word.setMarked(!isMarked);

            if (word.isMarked()) {
                holder.btnSave.setBackgroundResource(R.drawable.star_icon_selected);
            } else {
                holder.btnSave.setBackgroundResource(R.drawable.star_icon);
            }
        });

        if (word.isMarked()) {
            holder.btnSave.setBackgroundResource(R.drawable.star_icon_selected);
        } else {
            holder.btnSave.setBackgroundResource(R.drawable.star_icon);
        }
    }

    @Override
    public int getItemCount() {
        return wordList.size();
    }

    public static class WordViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvMeaning, tvPronunciation;
        ImageButton btnSound, btnSave;
        EditText editGoTu;
        CardView cardView;

        public WordViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvMatTruocGT);
            tvMeaning = itemView.findViewById(R.id.tvMatSauGT);
            tvPronunciation = itemView.findViewById(R.id.tvPhatAmGT);
            btnSound = itemView.findViewById(R.id.btnSoundGT);
            btnSave = itemView.findViewById(R.id.btnSaveGT);
            editGoTu = itemView.findViewById(R.id.editGoTu);
            cardView = itemView.findViewById(R.id.currentCardGT);
        }
    }
}