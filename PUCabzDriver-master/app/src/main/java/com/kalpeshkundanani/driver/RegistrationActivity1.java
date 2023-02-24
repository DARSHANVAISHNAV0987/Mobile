package com.kalpeshkundanani.driver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegistrationActivity1 extends AppCompatActivity {

    Button submitBtn;
    EditText mEmail, mPassword;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;
    FirebaseFirestore db;
    private DatabaseReference driverDatabaseRef;
    private String onlineDriverID;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration1);

        init();

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = mEmail.getText().toString().trim().toLowerCase();
                final String password = mPassword.getText().toString();
                /*Here Implement before registration check if user with given email exists or not*/
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(RegistrationActivity1.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){

                                    onlineDriverID = mAuth.getCurrentUser().getUid();
                                    driverDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(onlineDriverID);
                                    driverDatabaseRef.setValue(true);

                                    String user_id = mAuth.getCurrentUser().getUid();
                                    Intent intent=new Intent(RegistrationActivity1.this, RegistrationActivity2.class);
                                    intent.putExtra("Unique ID", user_id);
                                    intent.putExtra("Email ID", email);
                                    startActivity(intent);
                                }
                            }
                        });
            }
        });

    }

    private void init() {
        submitBtn = findViewById(R.id.submitBtn);
        mEmail = findViewById(R.id.email);
        mPassword = findViewById(R.id.password);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }
}