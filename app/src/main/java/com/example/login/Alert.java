package com.example.login;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class Alert extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert);

        getSupportActionBar().setTitle("My Alert");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}