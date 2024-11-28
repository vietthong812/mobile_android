package tdtu.EStudy_App.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import tdtu.EStudy_App.R;
import tdtu.EStudy_App.activities.TopicDetail;
import tdtu.EStudy_App.models.Topic;

public class TopicHomeAdapter extends RecyclerView.Adapter<TopicHomeAdapter.TopicViewHolder> {

    private Context context;
    private List<Topic> topicList;

    public TopicHomeAdapter(Context context, List<Topic> topicList) {
        this.context = context;
        this.topicList = topicList;
    }

    @NonNull
    @Override
    public TopicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.topic_item, parent, false);
        return new TopicViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TopicViewHolder holder, int position) {
        Topic topic = topicList.get(position);
        holder.tvBigName.setText(topic.getName());
        holder.tvBigNumWord.setText("Số từ: " + topic.getNumWord());

        // Xử lý thời gian từ Firebase Timestamp
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        Timestamp createTime = topic.getCreateTime(); // Giả sử kiểu Timestamp
        if (createTime != null) {
            String formattedDate = sdf.format(createTime.toDate());
            holder.tvNBigDate.setText(formattedDate);
        } else {
            holder.tvNBigDate.setText("N/A");
        }

        holder.itemView.setTag(topic);
        holder.itemView.setOnClickListener(holder);
    }

    @Override
    public int getItemCount() {
        return topicList.size();
    }

    public static class TopicViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvBigName;
        TextView tvBigNumWord;
        TextView tvNBigDate;

        public TopicViewHolder(@NonNull View itemView) {
            super(itemView);
            tvBigName = itemView.findViewById(R.id.tvBigName);
            tvBigNumWord = itemView.findViewById(R.id.tvBigNumWord);
            tvNBigDate = itemView.findViewById(R.id.tvBigDate);
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