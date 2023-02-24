package com.example.myapplication;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    EditText rollno, name, course, duration;
    ImageView img;
    Button browse, signup;
    Bitmap bitmap;
    InputStream is;
    Uri filepath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rollno = findViewById(R.id.rollno);
        name = findViewById(R.id.name);
        course = findViewById(R.id.course);
        duration = findViewById(R.id.duration);

        img = findViewById(R.id.imageView);

        browse = findViewById(R.id.browse);
        signup = findViewById(R.id.signup);

        browse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent,"Please select Image"),1);
            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {

            filepath = data.getData();
            try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),filepath);
                    img.setImageBitmap(bitmap);
            } catch (Exception exception) {
            }
        }

    }
}