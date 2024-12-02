package tdtu.EStudy_App.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;

import tdtu.EStudy_App.R;
import tdtu.EStudy_App.adapters.CardAdapter;
import tdtu.EStudy_App.adapters.CardGoTuAdapter;
import tdtu.EStudy_App.adapters.OnWordMarkedListener;
import tdtu.EStudy_App.models.Word;
import tdtu.EStudy_App.utils.ToastUtils;
import tdtu.EStudy_App.viewmodels.QuizViewModel;

public class HocGoTu extends AppCompatActivity implements OnWordMarkedListener {

    private AppCompatButton btnCancleGoTu, btnNextGoTu, btnPreviousGoTu;
    private CardGoTuAdapter cardGoTuAdapter;
    private List<Word> wordList;
    private TextView countNumGoTu;
    private ViewPager2 viewPagerCardGoTu;
    private TextView titleGoTu;
    private CardView cardViewNopBaiGoTu;
    private String topicId;
    private String userId;
    private FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.study_gotu);

        init();
        btnCancleGoTu.setOnClickListener(v -> {
            finish();
        });

        Intent intent = getIntent();
        String topicName = intent.getStringExtra("topicName");
        titleGoTu.setText("Gõ từ - " + topicName);

        wordList = intent.getParcelableArrayListExtra("wordList");
        if (wordList == null || wordList.isEmpty()) {
            ToastUtils.showShortToast(this, "Không có từ vựng nào được lưu trữ");
            finish();
        }

        cardGoTuAdapter = new CardGoTuAdapter(this, wordList, false, HocGoTu.this); //Chỗ na
        viewPagerCardGoTu.setAdapter(cardGoTuAdapter);
        countNumGoTu.setText("1/" + wordList.size());

        btnNextGoTu.setOnClickListener(v -> {
            int currentItem = viewPagerCardGoTu.getCurrentItem();
            if (currentItem < wordList.size() - 1) {
                viewPagerCardGoTu.setCurrentItem(currentItem + 1);
            }
        });

        btnPreviousGoTu.setOnClickListener(v -> {
            int currentItem = viewPagerCardGoTu.getCurrentItem();
            if (currentItem > 0) {
                viewPagerCardGoTu.setCurrentItem(currentItem - 1);
            }
        });

        viewPagerCardGoTu.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                countNumGoTu.setText((position + 1) + "/" + wordList.size());
            }
        });

        cardViewNopBaiGoTu.setOnClickListener(v -> {
            Intent intent1 = new Intent(HocGoTu.this, KetQuaHocTap.class);
            intent1.putExtra("topicID", topicId);
            intent1.putExtra("topicName", topicName);
            startActivity(intent1);
            finish();
        });


    }


    protected void init(){
        btnCancleGoTu = findViewById(R.id.btnCancleGT);
        btnNextGoTu = findViewById(R.id.btnNextGT);
        btnPreviousGoTu = findViewById(R.id.btnPreviousGT);
        countNumGoTu = findViewById(R.id.countNumGT);
        viewPagerCardGoTu = findViewById(R.id.viewPagerCardGT);
        titleGoTu = findViewById(R.id.titleGT);
        cardViewNopBaiGoTu = findViewById(R.id.cardViewNopBaiGT);

        db = FirebaseFirestore.getInstance();
        topicId = getIntent().getStringExtra("topicID");
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();


    }

    @Override
    public void onWordMarked(Word word, boolean isMarked) {
        if (isMarked) {
            db.collection("topics").document(topicId)
                    .collection("markedList").document(userId)
                    .update("wordIds", FieldValue.arrayUnion(word.getId()))
                    .addOnFailureListener(e -> {
                        if (e.getMessage().contains("No document to update")) {
                            HashMap<String, Object> data = new HashMap<>();
                            data.put("wordIds", FieldValue.arrayUnion(word.getId()));
                            db.collection("topics").document(topicId)
                                    .collection("markedList").document(userId)
                                    .set(data);
                        } else {
                            Toast.makeText(this, "Marking failed", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            db.collection("topics").document(topicId)
                    .collection("markedList").document(userId)
                    .update("wordIds", FieldValue.arrayRemove(word.getId()));
        }
    }



}
