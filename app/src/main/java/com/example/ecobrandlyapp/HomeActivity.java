package com.example.ecobrandlyapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HomeActivity extends AppCompatActivity {

    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference mDatabaseRef; //실시간 데이터 베이스
    private TextView metId,mEtPoint;
    private int Point;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        System.out.println("ok");
        /*현재 고객 연동*/
        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabaseRef= FirebaseDatabase.getInstance().getReference("fourpeople");
        FirebaseUser user = mFirebaseAuth.getCurrentUser();

        metId=findViewById(R.id.tv_id);
        mEtPoint=findViewById(R.id.tv_point);
        metId.setText(user.getEmail());
       // mEtPoint.setText(userPoint);

        mDatabaseRef.child("userAccount").child(user.getUid()).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String userPoint = Integer.toString(snapshot.child("point").getValue(int.class));
                String userId = snapshot.child("id").getValue(String.class);
                mEtPoint.setText(userPoint);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //Log.e("MainActivity", String.valueOf(databaseError.toException())); // 에러문 출력
            }
        });





        Button btn_logout = findViewById(R.id.btn_logout);
        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //로그아웃
                mFirebaseAuth.signOut();
                Intent logoutIntent = new Intent(HomeActivity.this, MainActivity.class);
                startActivity(logoutIntent);
                finish();
            }
        });

        //탈퇴처리하는 구문 (파이어베이스의 회원 정보 삭제)
        //mFirebaseAuth.getCurrentUser().delete();


        Button btn_qr1 = findViewById(R.id.btn_qr1);
        btn_qr1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this,CafeQRScanActivity.class);
                startActivity(intent);
                //finish();
            }
        });
    }
}