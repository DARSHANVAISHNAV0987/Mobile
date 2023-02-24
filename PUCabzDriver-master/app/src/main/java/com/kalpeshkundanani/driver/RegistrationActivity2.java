package com.kalpeshkundanani.driver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

/*New firestore Registration Code Logic part 2*/
public class RegistrationActivity2 extends AppCompatActivity {

    FirebaseFirestore db;
    EditText firstName, lastName, phone, email, PUID ;
    MaterialButton registrationBtn;
    TextView emailTV;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration2);

        init();

        Intent intent = getIntent();
        final String USER_ID = intent.getStringExtra("Unique ID");
        final String EMAIL_ID = intent.getStringExtra("Email ID");
        emailTV.setText(EMAIL_ID);

        registrationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Firstname = firstName.getText().toString().trim();
                String Lastname = lastName.getText().toString().trim();
                String PhoneNo = phone.getText().toString().trim();
                String pu_id = PUID.getText().toString().trim();
                Map<String, Object> userData = new HashMap<>();
                userData.put("Email ID", EMAIL_ID);
                userData.put("First Name", Firstname);
                userData.put("Last Name", Lastname);
                userData.put("Phone Number", PhoneNo);
                userData.put("PU ID", pu_id);
                DocumentReference userRef = db.collection("drivers").document(USER_ID);
                userRef.set(userData)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Log.d("Driver","Profile created for " + USER_ID);
                                Intent i = new Intent(RegistrationActivity2.this, LoginActivity.class);
                                startActivity(i);
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d("FireStore", e.getMessage());
                                Toast.makeText(RegistrationActivity2.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

    }

    private void init() {
        emailTV = findViewById(R.id.emailTV);
        email = findViewById(R.id.email);
        firstName = findViewById(R.id.firstName);
        lastName = findViewById(R.id.lastName);
        phone = findViewById(R.id.phone);
        registrationBtn = findViewById(R.id.btnRegister);
        db = FirebaseFirestore.getInstance();
        PUID=findViewById(R.id.PU_ID);
    }
}