package com.nimesh.uchat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class SettingActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private ImageButton backButton;
    private Button logoutButton;
    private Switch modeSwitch;
    private AppCompatDelegate delegate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        mAuth = FirebaseAuth.getInstance();
        logoutButton = findViewById(R.id.logout);
        backButton = findViewById(R.id.backBtn);
        modeSwitch = findViewById(R.id.mode);
        delegate = AppCompatDelegate.create(this, null);
        delegate.applyDayNight();

        if (delegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            modeSwitch.setChecked(true);
        }

        modeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    delegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                }else {
                    delegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                }
            }
        });

        logoutButton.setOnClickListener(v -> {
            mAuth.signOut();
            startActivity(new Intent(SettingActivity.this, SplashActivity.class));
            finish();
        });

        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(SettingActivity.this, MainActivity.class);
            intent.putExtra("tab", 4);
            startActivity(intent);
        });
    }
}
