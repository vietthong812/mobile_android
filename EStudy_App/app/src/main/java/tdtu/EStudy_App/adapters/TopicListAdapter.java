package tdtu.EStudy_App.adapters;
import androidx.fragment.app.Fragment;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import tdtu.EStudy_App.R;
import tdtu.EStudy_App.activities.TopicDetail;
import tdtu.EStudy_App.models.Folder;
import tdtu.EStudy_App.models.Topic;

public class TopicListAdapter extends RecyclerView.Adapter<TopicListAdapter.TopicViewHolder> {
    private Context context;
    private List<Topic> topicList;
    private List<Topic> topicListFilter;
    private OnTopicClickListener listener;

    public TopicListAdapter(Context context, List<Topic> topicList, OnTopicClickListener listener) {
        this.context = context;
        this.topicList = topicList;
        this.listener = listener;
        this.topicListFilter = new ArrayList<>(topicList);
    }
    public void removeTopicById(String topicId) {
        for (int i = 0; i < topicList.size(); i++) {
            if (topicList.get(i).getId().equals(topicId)) {
                topicList.remove(i);
                notifyItemRemoved(i);
                break;
            }
        }
    }
    public void updateTopics(List<Topic> newTopics) {
        this.topicList.clear();
        this.topicList.addAll(newTopics);
        this.topicListFilter.clear();
        this.topicListFilter.addAll(newTopics);
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public TopicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.topic_item_mini, parent, false);
        return new TopicViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TopicViewHolder holder, int position) {
        Topic topic = topicList.get(position);
        holder.tvNameTopic.setText(topic.getName());
        holder.tvNumWord.setText("Số từ: " + topic.getNumWord());
        holder.tvDate.setText("Ngày tạo: " + topic.convertTimestampToString(topic.getCreateTime()));

        holder.itemView.setTag(topic);
        holder.itemView.setOnClickListener(v -> listener.onTopicClick(topic));
    }

    @Override
    public int getItemCount() {
        return topicList.size();
    }

    public static class TopicViewHolder extends RecyclerView.ViewHolder  {
        TextView tvNameTopic;
        TextView tvNumWord;
        TextView tvDate;

        public TopicViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNameTopic = itemView.findViewById(R.id.tvNameTopic);
            tvNumWord = itemView.findViewById(R.id.tvNumWord);
            tvDate = itemView.findViewById(R.id.tvDate);
        }

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
        notifyDataSetChanged(); // Cập nhật lại giao diện
    }
}