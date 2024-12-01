package tdtu.EStudy_App.adapters;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
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
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Locale;

import tdtu.EStudy_App.models.Word;
import tdtu.EStudy_App.R;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.WordViewHolder> {
    private Context context;
    private List<Word> wordList;
    private TextToSpeech textToSpeech;

    public CardAdapter(Context context, List<Word> wordList) {
        this.context = context;
        this.wordList = wordList;

        textToSpeech = new TextToSpeech(context, status -> {
            if (status != TextToSpeech.ERROR) {
                textToSpeech.setLanguage(Locale.US);
            } else {
                Toast.makeText(context, "Xuất hiện lỗi trong quá trình khởi tạo TextToSpeech", Toast.LENGTH_SHORT).show();
            }
        });
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
            holder.btnSound.setBackgroundResource(R.drawable.sound_icon_selected); // Thay đổi icon khi bắt đầu phát âm
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
                    Toast.makeText(context, "Lỗi phát âm", Toast.LENGTH_SHORT).show();
                }
            });
        });

        holder.cardView.setBackgroundResource(R.drawable.mattruoc);
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

        float cameraDistance = 15000 * holder.itemView.getResources().getDisplayMetrics().density;
        cardView.setCameraDistance(cameraDistance);

        boolean isFrontVisible = frontCard.getVisibility() == View.VISIBLE;

        ObjectAnimator flipOut = ObjectAnimator.ofFloat(cardView, "rotationY", 0f, 90f);
        ObjectAnimator flipIn = ObjectAnimator.ofFloat(cardView, "rotationY", -90f, 0f);

        flipOut.setDuration(150);
        flipIn.setDuration(150);

        flipOut.addListener(new android.animation.AnimatorListenerAdapter() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onAnimationEnd(android.animation.Animator animation) {
                if (isFrontVisible) {
                    frontCard.setVisibility(View.GONE);
                    backCard.setVisibility(View.VISIBLE);
                    tvPhatAm.setVisibility(View.INVISIBLE);
                    btnSound.setVisibility(View.INVISIBLE);
                    cardView.setBackgroundResource(R.drawable.matsau);
                } else {
                    frontCard.setVisibility(View.VISIBLE);
                    backCard.setVisibility(View.GONE);
                    tvPhatAm.setVisibility(View.VISIBLE);
                    btnSound.setVisibility(View.VISIBLE);
                    cardView.setBackgroundResource(R.drawable.mattruoc);
                }
                flipIn.start();
            }
        });

        flipOut.start();
    }
}