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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentReference;

import java.util.HashMap;
import java.util.Map;

public class RegisterPage extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private FirebaseFirestore dbRoot;


    private EditText nameInput;
    private EditText emailInput;
    private EditText passwordInput;

    public RegisterPage() {

    }

    public static RegisterPage newInstance(String param1, String param2) {
        RegisterPage fragment = new RegisterPage();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register_page, container, false);


        nameInput = view.findViewById(R.id.name_input);
        emailInput = view.findViewById(R.id.email_input);
        passwordInput = view.findViewById(R.id.password_input);
        Button registerButton = view.findViewById(R.id.register_btn);

        registerButton.setOnClickListener(v -> {
            String name = nameInput.getText().toString().trim();
            String email = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();

            if (name.isEmpty() && !email.isEmpty() && !password.isEmpty()) {
                insertData(name, email, password);
            } else {
                Toast.makeText(getActivity(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            }
        });
        view.findViewById(R.id.register_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view).navigate(R.id.action_registerPage_to_cryptoPage);
            }
        });
        return view;
    }

    private void insertData(String name, String email, String password) {
        Map<String, String> items = new HashMap<>();
        items.put("Name", name);
        items.put("Email", email);
        items.put("password", password);

        dbRoot.collection("registrations").add(items)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if (task.isSuccessful()) {

                            nameInput.setText("");
                            emailInput.setText("");
                            passwordInput.setText("");
                            Log.i("Firestore", "Data successfully added");
                        } else {
                            Log.e("Firestore", "Error adding data", task.getException());
                        }
                    }
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Error: " + e.getMessage()));
    }
}
