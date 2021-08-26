package com.example.ecobrandlyapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class RegistrationOptionActivity extends AppCompatActivity {

    private Button btn_client,btn_enterprise;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_option);

        btn_client=findViewById(R.id.btn_client);
        btn_enterprise=findViewById(R.id.btn_enterprise);

        btn_client.setOnClickListener(new View.OnClickListener() {//일반회원 회원가입
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegistrationOptionActivity.this,RegisterActivity.class);
                //intent.putExtra("level",0);
                startActivity(intent);
                finish();
            }
        });

        btn_enterprise.setOnClickListener(new View.OnClickListener() { //기업 회원가입
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(RegistrationOptionActivity.this,RegisterEnterpriseActivity.class);
                //intent.putExtra("level",1);
                startActivity(intent);
                finish();
            }
        });


    }
}