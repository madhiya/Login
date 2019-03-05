package com.example.login;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class NewAlert extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_alert);

        getSupportActionBar().setTitle("New Alert");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
