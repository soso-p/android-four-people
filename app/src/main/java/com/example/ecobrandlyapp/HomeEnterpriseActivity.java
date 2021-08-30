package com.example.ecobrandlyapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

public class HomeEnterpriseActivity extends AppCompatActivity {

    private FirebaseAuth mFirebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_enterprise);

        mFirebaseAuth = FirebaseAuth.getInstance();

        Button btn_logout = findViewById(R.id.btn_logout);
        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //로그아웃
                mFirebaseAuth.signOut();
                Intent logoutIntent = new Intent(HomeEnterpriseActivity.this, MainActivity.class);
                startActivity(logoutIntent);
                finish();
            }
        });

        Button btn_qr = findViewById(R.id.btn_qr);
        btn_qr.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(HomeEnterpriseActivity.this,CreateQrActivity.class);
                startActivity(intent);
                //finish();
            }
        });

        Button phoneInput =findViewById(R.id.phoneInput);
        phoneInput.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(HomeEnterpriseActivity.this,EnterPhoneNumberActivity.class);
                startActivity(intent);
            }
        }));

    }
}