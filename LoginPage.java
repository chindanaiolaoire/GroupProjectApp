package com.example.groupprojectapp2;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class LoginPage extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private FirebaseFirestore dbRoot;
    private EditText emailInput;
    private EditText passwordInput;
    Model model = Model.getInstance();
    public LoginPage() {
        // Required empty public constructor
    }

    public static LoginPage newInstance(String param1, String param2) {
        LoginPage fragment = new LoginPage();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        dbRoot = FirebaseFirestore.getInstance();
        Log.i("Firestore", "firebase instance init");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login_page, container, false);

        emailInput = view.findViewById(R.id.email_input);
        passwordInput = view.findViewById(R.id.password_input);
        Button loginButton = view.findViewById(R.id.login_btn);


        loginButton.setOnClickListener(v -> {
            String email = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();
            Log.i("Firestore", "clicked login button");

            if (!email.isEmpty() && !password.isEmpty()) {
                validateUser(email, password);
            } else {
                Toast.makeText(getActivity(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            }
        });


        view.findViewById(R.id.register_text).setOnClickListener(v ->
                Navigation.findNavController(view).navigate(R.id.action_loginPage_to_registerPage)
        );

        return view;
    }
//validate user loggin in/making sure they exist
    private void validateUser(String email, String password) {
        dbRoot.collection("registrations")
                .whereEqualTo("Email", email)
                .whereEqualTo("password", password)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && !task.getResult().isEmpty()) {
                            Log.i("Firestore", "Login successful");
                            model.setCurrentUser(email);
                            Log.i("Firestore", "CurrentUserID :"+model.getCurrentUser());
                            Log.i("Firestore", email);
                            Toast.makeText(getActivity(), "Login successful", Toast.LENGTH_SHORT).show();
                            // Navigate to the next page
                            Navigation.findNavController(getView()).navigate(R.id.action_loginPage_to_cryptoPage);
                        } else {
                            Log.i("Firestore", "Invalid email or password");
                            Toast.makeText(getActivity(), "Invalid email or password", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
