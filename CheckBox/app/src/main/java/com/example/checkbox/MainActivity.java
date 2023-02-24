package com.example.checkbox;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    CheckBox android, php, python, swift;
    Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn = findViewById(R.id.button);
        android = findViewById(R.id.android);
        php = findViewById(R.id.php);
        python = findViewById(R.id.python);
        swift = findViewById(R.id.swift);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String result = "Selected is: ";
                if (android.isChecked()){
                    result += android.getText().toString();
                    if (php.isChecked()){
                        result += " " + php.getText().toString();
                    }
                }

                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
            }
        });

    }
}