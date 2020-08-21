package com.example.cameraapi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class ShowActivity extends AppCompatActivity {

    ImageView show;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);

        setTitle("Image");

        show=findViewById(R.id.show_img);

        //Getting all information from the GalleryActivity for a particular image,when it is clicked
        Intent i = getIntent();
        Uri uri = Uri.parse(i.getStringExtra("image"));

        //Setting image to show on the big screen
        Picasso.get().load(uri).fit().placeholder(R.drawable.loading).into(show);
    }
}
