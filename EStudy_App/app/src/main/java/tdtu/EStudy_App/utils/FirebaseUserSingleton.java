package tdtu.EStudy_App.utils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class FirebaseUserSingleton {
    private static FirebaseUser instance;

    private FirebaseUserSingleton() {
        // Private constructor to prevent instantiation
    }

    public static FirebaseUser getInstance() {
        if (instance == null) {
            instance = FirebaseAuth.getInstance().getCurrentUser();
        }
        return instance;
    }
}