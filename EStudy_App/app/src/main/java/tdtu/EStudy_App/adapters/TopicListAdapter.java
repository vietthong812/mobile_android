package tdtu.EStudy_App.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import tdtu.EStudy_App.R;
import tdtu.EStudy_App.activities.TopicDetail;
import tdtu.EStudy_App.models.Topic;

public class TopicListAdapter extends RecyclerView.Adapter<TopicListAdapter.TopicViewHolder> {

    private Context context;
    private List<Topic> topicList;

    public TopicListAdapter(Context context, List<Topic> topicList) {
        this.context = context;
        this.topicList = topicList;
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
        holder.tvNameTopic.setText("Tên topic: " +topic.getName());
        holder.tvNumWord.setText("Số từ: " + topic.getNumWord());

        holder.itemView.setTag(topic);
        holder.itemView.setOnClickListener(holder);
    }

    @Override
    public int getItemCount() {
        return topicList.size();
    }

    public static class TopicViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvNameTopic;
        TextView tvNumWord;

        public TopicViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNameTopic = itemView.findViewById(R.id.tvNameTopic);
            tvNumWord = itemView.findViewById(R.id.tvNumWord);
        }
        @Override
        public void onClick(View v) {
            Topic topic = (Topic) v.getTag();
            if (topic != null) {
                // Create an Intent to start the TopicDetailActivity
                Intent intent = new Intent(v.getContext(), TopicDetail.class);
                intent.putExtra("topicID", topic.getId());
                v.getContext().startActivity(intent);
            }
        }
    }
}