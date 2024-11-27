package tdtu.EStudy_App.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

import tdtu.EStudy_App.R;

public class UserFragment extends Fragment {


    Button btnLogout;
    FirebaseAuth mAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user, container, false);


        initialized(view);
        // Set an OnClickListener on the logout button
        btnLogout.setOnClickListener(v -> {
            // Sign out from FirebaseAuth
            mAuth.signOut();

            // Redirect to the SignIn activity
            Intent intent = new Intent(getActivity(), SignIn.class);
            startActivity(intent);

            // Finish the current activity
            getActivity().finish();
        });

        return view;
    }
    public void initialized(View view){
        btnLogout = view.findViewById(R.id.btnLogout);
        mAuth = FirebaseAuth.getInstance();
    }
}