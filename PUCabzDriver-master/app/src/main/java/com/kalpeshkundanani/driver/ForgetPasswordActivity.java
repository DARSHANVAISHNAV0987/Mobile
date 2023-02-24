package com.kalpeshkundanani.driver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class ForgetPasswordActivity extends AppCompatActivity {

    Button submitBtn;
    FirebaseFirestore db;
    String email;
    EditText emailEdit;
    FirebaseAuth fAuth;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        init();

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = emailEdit.getText().toString().trim().toLowerCase();
                if (email.equals("")) {
                    Toast.makeText(ForgetPasswordActivity.this, "pls Enter Valid Email", Toast.LENGTH_SHORT).show();
                } else {
                    /*Send password reset mail  logic*/
                    fAuth.sendPasswordResetEmail(email).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(ForgetPasswordActivity.this, "Reset Link Sent your Email : "+email, Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ForgetPasswordActivity.this, "Error! Reset Link not sent : "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    private void init() {
        emailEdit = findViewById(R.id.emailEdt);
        submitBtn = findViewById(R.id.submitBtn);
        db = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
    }
}