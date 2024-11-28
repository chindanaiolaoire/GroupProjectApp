package com.example.groupprojectapp2;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import android.widget.EditText;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class CryptoPage extends Fragment {

    private static final String TAG = "CryptoPage";
    private FirebaseFirestore dbRoot;
    Model model = Model.getInstance();


    public CryptoPage() {

    }

    public static CryptoPage newInstance(String param1, String param2) {
        CryptoPage fragment = new CryptoPage();
        Bundle args = new Bundle();
        args.putString("param1", param1);
        args.putString("param2", param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbRoot = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_crypto_page, container, false);

        // Set onClickListener for adding coins to Firestore
        view.findViewById(R.id.btn_btc).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                // Update local array
                updateCryptosInFirestore("btc"); // Update Firestore array
            }
        });
        view.findViewById(R.id.btn_bnb).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                // Update local array
                updateCryptosInFirestore("bnb"); // Update Firestore array
            }
        });
        view.findViewById(R.id.btn_eth).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                // Update local array
                updateCryptosInFirestore("eth"); // Update Firestore array
            }
        });
        view.findViewById(R.id.btn_sol).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                // Update local array
                EditText editTextInput = view.findViewById(R.id.edit_text_input);
                String inputText = editTextInput.getText().toString(); // Get the text from the EditText
                Log.i("Firestore", "CurrentUserID: " + inputText);

                updateCryptosInFirestore("sol,"+inputText); // Update Firestore array
            }
        });
        view.findViewById(R.id.btn_xrp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                // Update local array
                updateCryptosInFirestore("xrp"); // Update Firestore array
            }
        });

        //Nav buttons
        view.findViewById(R.id.btn_alert).setOnClickListener(v -> Navigation.findNavController(view).navigate(R.id.action_cryptoPage_to_alertPage2));
        view.findViewById(R.id.btn_ratings).setOnClickListener(v -> Navigation.findNavController(view).navigate(R.id.action_cryptoPage_to_profilePage));
        view.findViewById(R.id.btn_help).setOnClickListener(v -> Navigation.findNavController(view).navigate(R.id.action_cryptoPage_to_helpPage));
        view.findViewById(R.id.btn_ai).setOnClickListener(v -> Navigation.findNavController(view).navigate(R.id.action_cryptoPage_to_aiPage));

        return view;
    }
    private void BuyAsset() {





    }

    private void updateCryptosInFirestore(String cryptoName) {
        //get current logged in user
        String currentUserId = model.getCurrentUser();
        Log.i("Firestore", "CurrentUserID: " + currentUserId);

        // Query database using email
        Query query = dbRoot.collection("registrations").whereEqualTo("Email", currentUserId);

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful() && !task.getResult().isEmpty()) {

                    DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                    DocumentReference userDoc = documentSnapshot.getReference();

                    //add to the crypto array
                    Map<String, Object> data = new HashMap<>();
                    data.put("cryptosOwned", FieldValue.arrayUnion(cryptoName));

                    // check if  document exists/ update it
                    userDoc.update(data)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.i("Firestore", "Cryptos added successfully");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.e("Firestore", "Error updating cryptos: " + e.getMessage());
                                }
                            });
                } else {
                    Log.e("Firestore", "No document found for email: " + currentUserId);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("Firestore", "Error fetching document: " + e.getMessage());
            }
        });
    }



}
