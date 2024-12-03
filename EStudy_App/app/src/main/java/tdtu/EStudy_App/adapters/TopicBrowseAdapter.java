package tdtu.EStudy_App.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import tdtu.EStudy_App.R;
import tdtu.EStudy_App.models.Topic;

public class TopicBrowseAdapter extends RecyclerView.Adapter<TopicBrowseAdapter.TopicViewHolder> {
    private Context context;
    private List<Topic> topicList;
    private List<Topic> topicListFilter;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private OnTopicClickListener topicClickListener;
    private HashMap<String, String> authorCache = new HashMap<>(); // Dùng cái na để lưu trước tên tác giả

    public interface OnTopicClickListener {
        void onTopicClick(String topicId);
    }

    public TopicBrowseAdapter(Context context, List<Topic> topicList, OnTopicClickListener listener) {
        this.context = context;
        this.topicList = topicList;
        this.topicClickListener = listener;
        this.topicListFilter = new ArrayList<>(topicList);
    }

    @NonNull
    @Override
    public TopicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.topic_item_browse, parent, false);
        return new TopicViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TopicViewHolder holder, int position) {
        Topic topic = topicList.get(position);
        holder.tvNameTopicPublic.setText(topic.getName());
        holder.tvNumWordPublic.setText("Số từ: " + topic.getNumWord());
        holder.tvDateAddPublic.setText("Ngày tạo: " + topic.convertTimestampToString(topic.getCreateTime()));


        String userId = topic.getUserid();
        if (authorCache.containsKey(userId)) {
            holder.tvTacGia.setText("Tác giả: " + authorCache.get(userId));
        } else {
            fetchAuthorName(userId, holder.tvTacGia);
        }

        holder.itemView.setTag(topic);
        holder.itemView.setOnClickListener(v -> {
            if (topicClickListener != null) {
                topicClickListener.onTopicClick(topic.getId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return topicList.size();
    }

    private void fetchAuthorName(String userId, TextView textView) {
        db.collection("users").document(userId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                String authorName = task.getResult().getString("fullName");
                authorName = authorName != null ? authorName : "Unknown Author";
                authorCache.put(userId, authorName); // Lưu vào cache
                textView.setText("Tác giả: " + authorName);
            } else {
                textView.setText("Unknown Author");
            }
        });
    }

    public static class TopicViewHolder extends RecyclerView.ViewHolder {
        TextView tvNameTopicPublic;
        TextView tvNumWordPublic;
        TextView tvDateAddPublic;
        TextView tvTacGia;

        public TopicViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNameTopicPublic = itemView.findViewById(R.id.tvNameTopicPublic);
            tvNumWordPublic = itemView.findViewById(R.id.tvNumWordTopicPublic);
            tvDateAddPublic = itemView.findViewById(R.id.tvDateTopicPublic);
            tvTacGia = itemView.findViewById(R.id.tvTacGiaTopicPublic);
        }
    }

    public void updateTopics(List<Topic> newTopics) {
        this.topicList.clear();
        this.topicList.addAll(newTopics);
        this.topicListFilter.clear();
        this.topicListFilter.addAll(newTopics);
        notifyDataSetChanged();
    }

    public void filter(String text) {
        topicList.clear();
        if (text.isEmpty()) {
            topicList.addAll(topicListFilter);
        } else {
            text = text.toLowerCase();
            for (Topic topic : topicListFilter) {
                if (topic.getName().toLowerCase().contains(text)
                        || String.valueOf(topic.getNumWord()).contains(text)
                        || topic.convertTimestampToString(topic.getCreateTime()).contains(text)) {
                    topicList.add(topic);
                }
            }
        }
        notifyDataSetChanged();
    }
}
