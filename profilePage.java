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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class profilePage extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private FirebaseFirestore db;
    private EditText depositInput;

    public profilePage() {
        // Required empty public constructor
    }

    public static profilePage newInstance(String param1, String param2) {
        profilePage fragment = new profilePage();
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
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_page, container, false);

        depositInput = view.findViewById(R.id.deposit_input);
        Button saveDepositButton = view.findViewById(R.id.deposit_btn);
        Button withdrawButton = view.findViewById(R.id.withdraw_btn);

        //handle save/deposit button
        saveDepositButton.setOnClickListener(v -> {
            String depositAmount = depositInput.getText().toString().trim();
            if (!depositAmount.isEmpty()) {
                updateDeposit(depositAmount);
            } else {
                Toast.makeText(getActivity(), "Please enter a deposit amount", Toast.LENGTH_SHORT).show();
            }
        });

        // handle withdraw button
        withdrawButton.setOnClickListener(v -> {
            String withdrawAmount = depositInput.getText().toString().trim();
            if (!withdrawAmount.isEmpty()) {
                withdrawAmount(withdrawAmount);
            } else {
                Toast.makeText(getActivity(), "Please enter a withdrawal amount", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private void updateDeposit(String depositAmount) {
        String documentId = "8aXzP13PO8Lj1tcoq0qG";
        DocumentReference docRef = db.collection("registrations").document(documentId);

        try {
            float newDepositAmount = Float.parseFloat(depositAmount);

            //First get the current deposit amount
            docRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult() != null) {
                    // Get the current deposit amount from Firestore dbs
                    Double currentDepositAmountDouble = task.getResult().getDouble("deposit");
                    float currentDepositAmount = (currentDepositAmountDouble != null) ? currentDepositAmountDouble.floatValue() : 0.0f;

                    //  calculate
                    float updatedDepositAmount = currentDepositAmount + newDepositAmount;


                    Map<String, Object> depositData = new HashMap<>();
                    depositData.put("deposit", updatedDepositAmount);


                    docRef.set(depositData, SetOptions.merge())
                            .addOnSuccessListener(aVoid -> {
                                Log.i("Firestore", "Deposit updated successfully");
                                Toast.makeText(getActivity(), "Deposit updated successfully", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> {
                                Log.e("Firestore", "Error updating deposit", e);
                                Toast.makeText(getActivity(), "Error updating deposit", Toast.LENGTH_SHORT).show();
                            });
                } else {
                    Log.e("Firestore", "Error fetching current deposit", task.getException());
                    Toast.makeText(getActivity(), "Error fetching current deposit", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (NumberFormatException e) {
            Toast.makeText(getActivity(), "Invalid deposit amount", Toast.LENGTH_SHORT).show();
        }
    }

    private void withdrawAmount(String withdrawAmount) {
        String documentId = "8aXzP13PO8Lj1tcoq0qG";
        DocumentReference docRef = db.collection("registrations").document(documentId);

        try {
            float withdrawalAmount = Float.parseFloat(withdrawAmount);


            docRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult() != null) {

                    Double currentDepositAmountDouble = task.getResult().getDouble("deposit");
                    float currentDepositAmount = (currentDepositAmountDouble != null) ? currentDepositAmountDouble.floatValue() : 0.0f;

                    // Check if the withdrawal amount is greater than the current balance
                    if (withdrawalAmount > currentDepositAmount) {
                        Toast.makeText(getActivity(), "Insufficient balance", Toast.LENGTH_SHORT).show();
                        return;
                    }


                    float updatedDepositAmount = currentDepositAmount - withdrawalAmount;


                    Map<String, Object> depositData = new HashMap<>();
                    depositData.put("deposit", updatedDepositAmount);


                    docRef.set(depositData, SetOptions.merge())
                            .addOnSuccessListener(aVoid -> {
                                Log.i("Firestore", "Deposit updated after withdrawal");
                                Toast.makeText(getActivity(), "Withdrawal successful", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> {
                                Log.e("Firestore", "Error updating deposit after withdrawal", e);
                                Toast.makeText(getActivity(), "Error updating deposit after withdrawal", Toast.LENGTH_SHORT).show();
                            });
                } else {
                    Log.e("Firestore", "Error fetching current deposit", task.getException());
                    Toast.makeText(getActivity(), "Error fetching current deposit", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (NumberFormatException e) {
            Toast.makeText(getActivity(), "Invalid withdrawal amount", Toast.LENGTH_SHORT).show();
        }
    }

}
