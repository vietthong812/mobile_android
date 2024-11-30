package tdtu.EStudy_App.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import tdtu.EStudy_App.R;
import tdtu.EStudy_App.models.Topic;

public class Topic_folderAdapter extends RecyclerView.Adapter<Topic_folderAdapter.Topic_folderViewHolder> {
    private Context context;
    private List<Topic> topicList;
    private OnTopicDeleteClickListener listener;

    public Topic_folderAdapter(Context context, List<Topic> topicList, OnTopicDeleteClickListener listener) {
        this.context = context;
        this.topicList = topicList;
        this.listener = listener;
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
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public Topic_folderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.topic_item_mini_delete, parent, false);
        return new Topic_folderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Topic_folderViewHolder holder, int position) {
        Topic topic = topicList.get(position);
        holder.tvNameTopic.setText(topic.getName());
        holder.tvNumWord.setText("Số từ: " + topic.getNumWord());
        holder.tvDate.setText("Ngày tạo: " + topic.convertTimestampToString(topic.getCreateTime()));


        holder.itemView.setTag(topic);
        holder.itemView.setOnClickListener(v -> listener.onTopicClick(topic));
        holder.btnDeleteTopic.setOnClickListener(v -> listener.onDeleteTopicClick(topic));
    }
    @Override
    public int getItemCount() {
        return topicList.size();
    }
    public static class Topic_folderViewHolder extends RecyclerView.ViewHolder {
        TextView tvNameTopic;
        TextView tvNumWord;
        TextView tvDate;
        ImageButton btnDeleteTopic;
        public Topic_folderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNameTopic = itemView.findViewById(R.id.tvNameTopicDel);
            tvNumWord = itemView.findViewById(R.id.tvNumWordDel);
            tvDate = itemView.findViewById(R.id.tvDateAddDel);
            btnDeleteTopic = itemView.findViewById(R.id.btnDeleteTopicDel);
        }

    }
}
