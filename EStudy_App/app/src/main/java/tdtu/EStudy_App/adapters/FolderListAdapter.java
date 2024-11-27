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
import tdtu.EStudy_App.activities.FolderDetail;
import tdtu.EStudy_App.models.Folder;

public class FolderListAdapter extends RecyclerView.Adapter<FolderListAdapter.FolderViewHolder> {
    private Context context;
    private List<Folder> folderList;
    public FolderListAdapter(Context context, List<Folder> folderList) {
        this.context = context;
        this.folderList = folderList;
    }
    @NonNull
    @Override
    public FolderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.folder_item, parent, false);
        return new FolderViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull FolderViewHolder holder, int position) {
        Folder folder = folderList.get(position);
        holder.tvNameFolder.setText("Tên folder: " + folder.getName());
        holder.itemView.setTag(folder);
        holder.itemView.setOnClickListener(holder);
    }
    @Override
    public int getItemCount() {
        return folderList.size();
    }
    public static class FolderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvNameFolder;
        public FolderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNameFolder = itemView.findViewById(R.id.tvFolderName);
        }
        @Override
        public void onClick(View v) {
            Folder folder = (Folder) v.getTag();
            if (folder != null) {
                Intent intent = new Intent(v.getContext(), FolderDetail.class);
                intent.putExtra("folderId", folder.getId());
                v.getContext().startActivity(intent);
            }
        }
    }
}
