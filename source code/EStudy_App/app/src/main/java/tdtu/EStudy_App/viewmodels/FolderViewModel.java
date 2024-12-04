package tdtu.EStudy_App.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import tdtu.EStudy_App.models.Folder;

public class FolderViewModel extends ViewModel {
    private final MutableLiveData<List<Folder>> folders = new MutableLiveData<>();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    public LiveData<List<Folder>> getFolders() {
        return folders;
    }
    public void loadFolders(String userId) {
        folders.setValue(new ArrayList<>()); // Clear the current data
        db.collection("folders")
                .whereEqualTo("userCreate", db.document("users/" + userId))
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Folder> folderList = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String name = document.getString("name");
                            String id = document.getId();
                            Folder folder = new Folder(id, name);
                            folderList.add(folder);
                        }
                        folders.setValue(folderList);
                    }
                });
    }
}
