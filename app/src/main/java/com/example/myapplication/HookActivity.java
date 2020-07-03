package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.test_annotation.MyRouter;
@MyRouter(router = "/two")
public class HookActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_two);

        findViewById(R.id.hello2).setOnClickListener(v->{
            startActivity(new Intent(HookActivity.this, HookActivity.class));
        });
    }
}
