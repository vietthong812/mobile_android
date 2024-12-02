package tdtu.EStudy_App.adapters;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import tdtu.EStudy_App.R;
import tdtu.EStudy_App.models.Word;

public class CardTracNghiemAdapter extends RecyclerView.Adapter<CardTracNghiemAdapter.WordViewHolder> {
    private Context context;
    private List<Word> wordList;
    private SparseIntArray selectedPositions = new SparseIntArray(); // Lưu đáp án đã chọn
    private SparseArray<List<String>> randomizedAnswers = new SparseArray<>(); // Lưu câu trả lời đã được xáo trộn
    private Random random = new Random();
    private TextToSpeech textToSpeech;
    private boolean isEnglishFront;
    private OnWordMarkedListener onWordMarkedListener;

    public CardTracNghiemAdapter(Context context, List<Word> wordList, boolean isEnglishFront, OnWordMarkedListener onWordMarkedListener) {
        this.context = context;
        this.wordList = wordList;
        this.isEnglishFront = isEnglishFront;
        this.onWordMarkedListener = onWordMarkedListener;

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
        View view = LayoutInflater.from(context).inflate(R.layout.card_item_tracnghiem, parent, false);
        return new WordViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WordViewHolder holder, int position) {
        Word word = wordList.get(position);

        if (isEnglishFront) {
            holder.tvName.setText(word.getName());
            holder.tvPronunciation.setText(word.getPronunciation());
            holder.tvPronunciation.setVisibility(View.VISIBLE);
            holder.btnSound.setVisibility(View.VISIBLE);
            holder.currentCardTN.setBackgroundResource(R.drawable.mattruoc);
        } else {
            holder.tvName.setText(word.getMeaning());
            holder.tvPronunciation.setVisibility(View.GONE);
            holder.btnSound.setVisibility(View.GONE);
            holder.currentCardTN.setBackgroundResource(R.drawable.matsau);
        }

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

        // Get or generate randomized answers
        List<String> answers = randomizedAnswers.get(position);
        if (answers == null) {
            answers = getRandomAnswers(word);
            randomizedAnswers.put(position, answers);
        }

        holder.daA.setText(answers.get(0));
        holder.daB.setText(answers.get(1));
        holder.daC.setText(answers.get(2));
        holder.daD.setText(answers.get(3));

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

    private List<String> getRandomAnswers(Word word) {
        List<String> answers = new ArrayList<>();
        if (isEnglishFront) {
            answers.add(word.getMeaning());
            while (answers.size() < 4) {
                String randomMeaning = wordList.get(random.nextInt(wordList.size())).getMeaning();
                if (!answers.contains(randomMeaning)) {
                    answers.add(randomMeaning);
                }
            }
        } else {
            answers.add(word.getName());
            while (answers.size() < 4) {
                String randomName = wordList.get(random.nextInt(wordList.size())).getName();
                if (!answers.contains(randomName)) {
                    answers.add(randomName);
                }
            }
        }

        Collections.shuffle(answers);
        return answers;
    }

    public static class WordViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvPronunciation, daA, daB, daC, daD;
        ImageButton btnSound, btnSave;
        CardView cardA, cardB, cardC, cardD, currentCardTN;

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
            currentCardTN = itemView.findViewById(R.id.currentCardTN);
        }
    }
}