// CardGoTuAdapter.java
package tdtu.EStudy_App.adapters;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import tdtu.EStudy_App.models.Word;
import tdtu.EStudy_App.R;

public class CardGoTuAdapter extends RecyclerView.Adapter<CardGoTuAdapter.WordViewHolder> {
    private Context context;
    private List<Word> wordList;
    private TextToSpeech textToSpeech;
    private boolean isEnglishFront; //CHỗ này
    private OnWordMarkedListener onWordMarkedListener;
    private Set<Word> learnedWords = new HashSet<>();
    private Set<Word> wrongWordsList = new HashSet<>();
    private String option;
    private ViewPager2 viewPagerCardGT;

    public CardGoTuAdapter(Context context, List<Word> wordList, String option, OnWordMarkedListener onWordMarkedListener, ViewPager2 viewPagerCardGT) {
        this.context = context;
        this.wordList = wordList;
        this.option = option;
        this.onWordMarkedListener = onWordMarkedListener;
        this.viewPagerCardGT = viewPagerCardGT;

        textToSpeech = new TextToSpeech(context, status -> {
            if (status != TextToSpeech.ERROR) {
                textToSpeech.setLanguage(Locale.US);
            } else {
                Toast.makeText(context, "Xuất hiện lỗi trong quá trình khởi tạo TextToSpeech", Toast.LENGTH_SHORT).show();
            }
        });

        if (option == null || option.equals("AutoPronunciation") || option.equals("ShowAnswer")) {
            isEnglishFront = true;
        } else if (option.equals("Reverse")) {
            isEnglishFront = false;
        }
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


        if (isEnglishFront) {
            holder.tvName.setText(word.getName());
            holder.tvMeaning.setText(word.getMeaning());
            holder.tvPronunciation.setVisibility(View.VISIBLE);
            holder.btnSound.setVisibility(View.VISIBLE);
            holder.cardView.setBackgroundResource(R.drawable.mattruoc);
        } else {
            holder.tvName.setText(word.getMeaning());
            holder.tvMeaning.setText(word.getName());
            holder.tvPronunciation.setVisibility(View.GONE);
            holder.btnSound.setVisibility(View.GONE);
            holder.cardView.setBackgroundResource(R.drawable.matsau);
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

        holder.btnSave.setBackgroundResource(word.isMarked() ? R.drawable.star_icon_selected : R.drawable.star_icon);

        holder.editGoTu.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN)) {
                String inputText = holder.editGoTu.getText().toString().trim().toLowerCase();
                String correctAnswer = isEnglishFront ? word.getMeaning().toLowerCase() : word.getName().toLowerCase();

                if (inputText.equals(correctAnswer)) {
                    learnedWords.add(word);
                    wrongWordsList.remove(word);
                    if (option != null && option.equals("ShowAnswer")) {
                        holder.cardViewAnswer.setCardBackgroundColor(context.getResources().getColor(R.color.green));
                        holder.editGoTu.setTextColor(context.getResources().getColor(R.color.white));
                    }
                } else {
                    wrongWordsList.add(word);
                    learnedWords.remove(word);
                    if (option != null && option.equals("ShowAnswer")) {
                        holder.cardViewAnswer.setCardBackgroundColor(context.getResources().getColor(R.color.red));
                        holder.editGoTu.setTextColor(context.getResources().getColor(R.color.white));
                    }
                }

                if (option != null && option.equals("ShowAnswer")) {
                    holder.editGoTu.setEnabled(false);

                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
                        int currentItem = viewPagerCardGT.getCurrentItem();
                        if (currentItem < wordList.size() - 1) {
                            viewPagerCardGT.setCurrentItem(currentItem + 1);
                        }
                    }, 2000);
                }

                return true;
            }
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return wordList.size();
    }

    public Set<Word> getLearnedWords() {
        return learnedWords;
    }

    public Set<Word> getWrongWordsList() {
        return wrongWordsList;
    }

    public static class WordViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvMeaning, tvPronunciation;
        ImageButton btnSound, btnSave;
        EditText editGoTu;
        CardView cardView, cardViewAnswer;

        public WordViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvMatTruocGT);
            tvMeaning = itemView.findViewById(R.id.tvMatSauGT);
            tvPronunciation = itemView.findViewById(R.id.tvPhatAmGT);
            btnSound = itemView.findViewById(R.id.btnSoundGT);
            btnSave = itemView.findViewById(R.id.btnSaveGT);
            editGoTu = itemView.findViewById(R.id.editGoTu);
            cardView = itemView.findViewById(R.id.currentCardGT);
            cardViewAnswer = itemView.findViewById(R.id.cardViewAnswer);
        }
    }
}