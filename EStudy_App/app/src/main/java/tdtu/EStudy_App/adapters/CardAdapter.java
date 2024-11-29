 package tdtu.EStudy_App.adapters;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import tdtu.EStudy_App.models.Word;
import tdtu.EStudy_App.R;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.WordViewHolder> {
    private Context context;
    private List<Word> wordList;

    public CardAdapter(Context context, List<Word> wordList) {
        this.context = context;
        this.wordList = wordList;
    }

    @NonNull
    @Override
    public WordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.card_item, parent, false);
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

        holder.cardView.setOnClickListener(v -> {
            flipCard(holder);
        });

        holder.btnSave.setOnClickListener(v -> {
            boolean isMarked = word.isMarked();
            word.setMarked(!isMarked);  // Toggle the marked status

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
        CardView cardView;

        public WordViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvMatTruoc);
            tvMeaning = itemView.findViewById(R.id.tvMatSau);
            tvPronunciation = itemView.findViewById(R.id.tvPhatAm);
            btnSound = itemView.findViewById(R.id.btnSound);
            btnSave = itemView.findViewById(R.id.btnSave);
            cardView = itemView.findViewById(R.id.currentCard);
        }
    }

    private void flipCard(WordViewHolder holder) {
        CardView cardView = holder.cardView;
        View frontCard = holder.tvName;
        View backCard = holder.tvMeaning;
        View tvPhatAm = holder.tvPronunciation;
        View btnSound = holder.btnSound;

        //Chỗ này phóng ra xa ra để thấy card flip không bị cawts
        float cameraDistance = 15000 * holder.itemView.getResources().getDisplayMetrics().density;
        cardView.setCameraDistance(cameraDistance);

        boolean isFrontVisible = frontCard.getVisibility() == View.VISIBLE;

        ObjectAnimator flipOut = ObjectAnimator.ofFloat(cardView, "rotationY", 0f, 90f);
        ObjectAnimator flipIn = ObjectAnimator.ofFloat(cardView, "rotationY", -90f, 0f);

        flipOut.setDuration(150);
        flipIn.setDuration(150);

        flipOut.addListener(new android.animation.AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(android.animation.Animator animation) {
                if (isFrontVisible) {
                    // Hiện mặt sau
                    frontCard.setVisibility(View.GONE);
                    backCard.setVisibility(View.VISIBLE);
                    tvPhatAm.setVisibility(View.INVISIBLE);
                    btnSound.setVisibility(View.INVISIBLE);
                } else {
                    // Hiện mặt trước
                    frontCard.setVisibility(View.VISIBLE);
                    backCard.setVisibility(View.GONE);
                    tvPhatAm.setVisibility(View.VISIBLE);
                    btnSound.setVisibility(View.VISIBLE);
                }
                flipIn.start(); // Bắt đầu hiệu ứng lật vào
            }
        });

        flipOut.start(); // Bắt đầu hiệu ứng lật ra
    }

}