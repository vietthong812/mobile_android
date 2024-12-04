package tdtu.EStudy_App.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import tdtu.EStudy_App.R;

public class RankAdapter extends RecyclerView.Adapter<RankAdapter.RankViewHolder> {
    private Context context;
    private List<String> userNames;
    private List<String> finishTimes;
    private List<String> avatarUris;

    public RankAdapter(Context context, List<String> userNames, List<String> finishTimes, List<String> avatarUris) {
        this.context = context;
        this.userNames = userNames;
        this.finishTimes = finishTimes;
        this.avatarUris = avatarUris;
    }

    @NonNull
    @Override
    public RankViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.rank_item, parent, false);
        return new RankViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RankViewHolder holder, int position) {
        String userName = userNames.get(position);
        String finishTime = finishTimes.get(position);
        String avatarUri = avatarUris.get(position);

        holder.tvNameRank.setText(userName);
        holder.tvFinishTimeRank.setText(finishTime);

        Glide.with(context).load(avatarUri).into(holder.avtRank);

        if (position == 0) {
            holder.badgeIcon.setImageResource(R.drawable.badge1_icon);
            holder.badgeIcon.setVisibility(View.VISIBLE);
            holder.tvRank.setVisibility(View.GONE);
            holder.tvNameRank.setTextColor(context.getResources().getColor(R.color.orange));
            holder.tvFinishTimeRank.setTextColor(context.getResources().getColor(R.color.orange));
        } else if (position == 1) {
            holder.badgeIcon.setImageResource(R.drawable.badge2);
            holder.badgeIcon.setVisibility(View.VISIBLE);
            holder.tvRank.setVisibility(View.GONE);
            holder.tvNameRank.setTextColor(context.getResources().getColor(R.color.blue));
            holder.tvFinishTimeRank.setTextColor(context.getResources().getColor(R.color.blue));
        } else if (position == 2) {
            holder.badgeIcon.setImageResource(R.drawable.badge3);
            holder.badgeIcon.setVisibility(View.VISIBLE);
            holder.tvRank.setVisibility(View.GONE);
            holder.tvNameRank.setTextColor(context.getResources().getColor(R.color.brown));
            holder.tvFinishTimeRank.setTextColor(context.getResources().getColor(R.color.brown));
        } else {
            holder.tvRank.setText(String.valueOf(position + 1));
            holder.tvRank.setVisibility(View.VISIBLE);
            holder.badgeIcon.setVisibility(View.GONE);
            holder.tvNameRank.setTextColor(context.getResources().getColor(R.color.deepblue));
            holder.tvFinishTimeRank.setTextColor(context.getResources().getColor(R.color.deepblue));
        }
    }

    @Override
    public int getItemCount() {
        return userNames.size();
    }

    public static class RankViewHolder extends RecyclerView.ViewHolder {
        ImageView avtRank, badgeIcon;
        TextView tvNameRank, tvFinishTimeRank, tvRank;

        public RankViewHolder(@NonNull View itemView) {
            super(itemView);
            avtRank = itemView.findViewById(R.id.avtRank);
            tvNameRank = itemView.findViewById(R.id.tvNameRank);
            tvFinishTimeRank = itemView.findViewById(R.id.finishTimeRank);
            tvRank = itemView.findViewById(R.id.tvRank);
            badgeIcon = itemView.findViewById(R.id.badgeIcon);
        }
    }
}