package com.xiayiye5.xiayiye5ad;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TTAdManagerHolder.get().requestPermissionIfNecessary(this);
    }
}
