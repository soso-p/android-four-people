package com.example.ecobrandlyapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

public class HomeEnterpriseActivity extends AppCompatActivity {
    private TextView metId;
    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference mDatabaseRef; //실시간 데이터 베이스
    private String userUid;

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

        // 전화번호 입력 버튼
        Button phoneInput =findViewById(R.id.phoneInput);
        phoneInput.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(HomeEnterpriseActivity.this,EnterPhoneNumberActivity.class);
                startActivity(intent);
                finish();
            }
        }));

        //내 정보
        Button btn_information=findViewById(R.id.btn_information2);
        btn_information.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(HomeEnterpriseActivity.this,ModifyInformationActivity.class);
                startActivity(intent);
                //finish();
            }
        });

        /*현재 고객 연동*/
        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabaseRef= FirebaseDatabase.getInstance().getReference("fourpeople");
        FirebaseUser user = mFirebaseAuth.getCurrentUser();

        metId=findViewById(R.id.id);

        mDatabaseRef.child("userAccount").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userUid=snapshot.child("idToken").getValue(String.class);
                String userName = snapshot.child("alising").getValue(String.class);
                metId.setText("\""+userName+"\"님, 반갑습니다!");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //Log.e("MainActivity", String.valueOf(databaseError.toException())); // 에러문 출력
            }
        });

        Calendar cal = Calendar.getInstance();

        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int min = cal.get(Calendar.MINUTE);
        int resultDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH) - day;

        /*달의 말일 23:00:00 ~ 23:59:59동안 고객에게 포인트 제공 금지(: point 인정 안됨!)*/
        if((resultDay == 0 && hour == 23 && min>=0 && min<=59)){
            btn_qr.setEnabled(false);
            phoneInput.setEnabled(false);
        }

    }
}