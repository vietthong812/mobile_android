package tdtu.EStudy_App.adapters;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.Locale;

import tdtu.EStudy_App.models.Word;
import tdtu.EStudy_App.R;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.WordViewHolder>{
    private Context context;
    private List<Word> wordList;
    private TextToSpeech textToSpeech;
    private String option; //CHỗ này
    private Boolean isEnglishFront;
    private OnWordMarkedListener onWordMarkedListener;
    private SparseArray<WordViewHolder> viewHolderCache = new SparseArray<>();

    public CardAdapter(Context context, List<Word> wordList, String option, OnWordMarkedListener onWordMarkedListener) {
        this.context = context;
        this.wordList = wordList;
        this.option = option;
        this.onWordMarkedListener = onWordMarkedListener;

        textToSpeech = new TextToSpeech(context, status -> {
            if (status != TextToSpeech.ERROR) {
                textToSpeech.setLanguage(Locale.US);
            } else {
                Toast.makeText(context, "Xuất hiện lỗi trong quá trình khởi tạo TextToSpeech", Toast.LENGTH_SHORT).show();
            }
        });

        if (option == null || option.equals("AutoPronunciation")) {
            isEnglishFront = true;
        } else if (option.equals("Reverse")) {
            isEnglishFront = false;
        }
    }

    @NonNull
    @Override
    public WordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.card_item, parent, false);
        return new WordViewHolder(view);
    }

    private void playWordSound(String text) {
        String utteranceId = String.valueOf(System.currentTimeMillis());
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId);
    }

    @Override
    public void onBindViewHolder(@NonNull WordViewHolder holder, int position) {
        Word word = wordList.get(position);
        viewHolderCache.put(position, holder);

        if (isEnglishFront) {
            holder.tvName.setText(word.getName());
            holder.tvPronunciation.setText(word.getPronunciation());
            holder.tvPronunciation.setVisibility(View.VISIBLE);
            holder.btnSound.setVisibility(View.VISIBLE);
            holder.cardView.setBackgroundResource(R.drawable.mattruoc);
        } else {
            holder.tvName.setText(word.getMeaning());
            holder.tvPronunciation.setVisibility(View.GONE);
            holder.btnSound.setVisibility(View.GONE);
            holder.cardView.setBackgroundResource(R.drawable.matsau);
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
                    Toast.makeText(context, "Lỗi phát âm", Toast.LENGTH_SHORT).show();
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

        // Khởi taạo marked tương ứng band dầu
        holder.btnSave.setBackgroundResource(word.isMarked() ? R.drawable.star_icon_selected : R.drawable.star_icon);

        holder.cardView.setOnClickListener(v -> flipCard(holder));
    }

    public void flipCard(WordViewHolder holder) {
        CardView cardView = holder.cardView;
        float cameraDistance = 15000 * holder.itemView.getResources().getDisplayMetrics().density;
        cardView.setCameraDistance(cameraDistance);

        ObjectAnimator flipOut = ObjectAnimator.ofFloat(cardView, "rotationY", 0f, 90f);
        ObjectAnimator flipIn = ObjectAnimator.ofFloat(cardView, "rotationY", -90f, 0f);

        flipOut.setDuration(150);
        flipIn.setDuration(150);


        flipOut.addListener(new android.animation.AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(android.animation.Animator animation) {
                // Lật mặt

                isEnglishFront = !isEnglishFront;
                notifyDataSetChanged();
                flipIn.start();
            }
        });

        flipOut.start();
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
    public void flipCardAtPosition(int position) {
        WordViewHolder holder = (WordViewHolder) viewHolderCache.get(position);
        if (holder != null) {
            flipCard(holder);
        }

    }

    public void setEnglishFront(boolean isEnglishFront) {
        this.isEnglishFront = isEnglishFront;
        notifyDataSetChanged();
    }


}