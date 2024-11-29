package tdtu.EStudy_App.adapters;

import android.content.Context;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import tdtu.EStudy_App.R;
import tdtu.EStudy_App.models.Word;

public class CardTracNghiemAdapter extends RecyclerView.Adapter<CardTracNghiemAdapter.WordViewHolder> {
    private Context context;
    private List<Word> wordList;
    private SparseIntArray selectedPositions = new SparseIntArray(); // Lưu đáp án đã chọn
    private SparseArray<List<String>> randomizedAnswers = new SparseArray<>(); // Lưu câu trả lời đã được xáo trộn
    private Random random = new Random();

    public CardTracNghiemAdapter(Context context, List<Word> wordList) {
        this.context = context;
        this.wordList = wordList;
    }

    @NonNull
    @Override
    public WordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.card_item_tracnghiem, parent, false);
        return new WordViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WordViewHolder holder, int position) {
        Word word = wordList.get(position);

        holder.tvName.setText(word.getName());
        holder.tvPronunciation.setText(word.getPronunciation());

        holder.btnSound.setOnClickListener(v -> {
            // Play pronunciation or do some action when clicked
        });

        holder.btnSave.setOnClickListener(v -> {
            boolean isMarked = word.isMarked();
            word.setMarked(!isMarked);
            holder.btnSave.setBackgroundResource(isMarked ? R.drawable.star_icon : R.drawable.star_icon_selected);
        });

        holder.btnSave.setBackgroundResource(word.isMarked() ? R.drawable.star_icon_selected : R.drawable.star_icon);

        // Get or generate randomized meanings
        List<String> meanings = randomizedAnswers.get(position);
        if (meanings == null) {
            meanings = getRandomMeanings(word.getMeaning());
            randomizedAnswers.put(position, meanings);
        }

        holder.daA.setText(meanings.get(0));
        holder.daB.setText(meanings.get(1));
        holder.daC.setText(meanings.get(2));
        holder.daD.setText(meanings.get(3));

        // Handle card selection logic
        holder.cardA.setOnClickListener(v -> selectCard(holder, position, 0));
        holder.cardB.setOnClickListener(v -> selectCard(holder, position, 1));
        holder.cardC.setOnClickListener(v -> selectCard(holder, position, 2));
        holder.cardD.setOnClickListener(v -> selectCard(holder, position, 3));

        updateCardBackground(holder, position);
    }

    @Override
    public int getItemCount() {
        return wordList.size();
    }

    private void selectCard(WordViewHolder holder, int position, int cardIndex) {
        selectedPositions.put(position, cardIndex);
        notifyItemChanged(position);
    }

    private void updateCardBackground(WordViewHolder holder, int position) {
        int selectedCardIndex = selectedPositions.get(position, -1);
        CardView[] cardViews = {holder.cardA, holder.cardB, holder.cardC, holder.cardD};
        TextView[] answerTexts = {holder.daA, holder.daB, holder.daC, holder.daD};

        for (int i = 0; i < cardViews.length; i++) {
            int backgroundColor = (selectedCardIndex == i) ? context.getResources().getColor(R.color.deepblue) : context.getResources().getColor(R.color.white);
            int textColor = (selectedCardIndex == i) ? context.getResources().getColor(R.color.white) : context.getResources().getColor(R.color.black);
            cardViews[i].setCardBackgroundColor(backgroundColor);
            answerTexts[i].setTextColor(textColor);
        }
    }

    private List<String> getRandomMeanings(String correctMeaning) {
        List<String> meanings = new ArrayList<>();
        meanings.add(correctMeaning);

        while (meanings.size() < 4) {
            String randomMeaning = wordList.get(random.nextInt(wordList.size())).getMeaning();
            if (!meanings.contains(randomMeaning)) {
                meanings.add(randomMeaning);
            }
        }

        Collections.shuffle(meanings);
        return meanings;
    }

    public static class WordViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvPronunciation, daA, daB, daC, daD;
        ImageButton btnSound, btnSave;
        CardView cardA, cardB, cardC, cardD;

        public WordViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvMatTruocTN);
            tvPronunciation = itemView.findViewById(R.id.tvPhatAmTN);
            daA = itemView.findViewById(R.id.daA);
            daB = itemView.findViewById(R.id.daB);
            daC = itemView.findViewById(R.id.daC);
            daD = itemView.findViewById(R.id.daD);
            btnSound = itemView.findViewById(R.id.btnSoundTN);
            btnSave = itemView.findViewById(R.id.btnSaveTN);
            cardA = itemView.findViewById(R.id.cardA);
            cardB = itemView.findViewById(R.id.cardB);
            cardC = itemView.findViewById(R.id.cardC);
            cardD = itemView.findViewById(R.id.cardD);
        }
    }
}