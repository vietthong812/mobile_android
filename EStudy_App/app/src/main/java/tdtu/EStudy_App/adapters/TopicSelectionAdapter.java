package tdtu.EStudy_App.adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import tdtu.EStudy_App.R;
import tdtu.EStudy_App.models.Topic;

public class TopicSelectionAdapter extends RecyclerView.Adapter<TopicSelectionAdapter.TopicViewHolder> {
    private Context context;
    private List<Topic> topicList;

    public TopicSelectionAdapter(Context context, List<Topic> topicList) {
        this.context = context;
        this.topicList = topicList;
    }
    public void updateTopics(List<Topic> newTopics) {
        this.topicList.clear();
        this.topicList.addAll(newTopics);
        notifyDataSetChanged();
    }
    public List<Topic> getTopicList() {
        return topicList;
    }

    @NonNull
    @Override
    public TopicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.topic_item_mini_selection, parent, false);
        return new TopicViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TopicViewHolder holder, int position) {
        Topic topic = topicList.get(position);
        holder.tvNameTopicSelect.setText(topic.getName());
        holder.tvNumWordSelect.setText("Số từ: " + topic.getNumWord());
        holder.tvDateAddSelect.setText("Ngày tạo: " + topic.convertTimestampToString(topic.getCreateTime()));
        holder.checkBoxSelectTopic.setOnCheckedChangeListener((buttonView, isChecked) -> {
            topic.setSelectedForFolder(holder.checkBoxSelectTopic.isChecked());
        });

    }

    @Override
    public int getItemCount() {
        return topicList.size();
    }

    public static class TopicViewHolder extends RecyclerView.ViewHolder  {
        TextView tvNameTopicSelect;
        TextView tvNumWordSelect;
        TextView tvDateAddSelect;
        CheckBox checkBoxSelectTopic;

        public TopicViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNameTopicSelect = itemView.findViewById(R.id.tvNameTopicSelect);
            tvNumWordSelect = itemView.findViewById(R.id.tvNumWordAddSelect);
            tvDateAddSelect = itemView.findViewById(R.id.tvDateAddSelect);
            checkBoxSelectTopic = itemView.findViewById(R.id.checkBoxSelectTopic);
        }

    }
}