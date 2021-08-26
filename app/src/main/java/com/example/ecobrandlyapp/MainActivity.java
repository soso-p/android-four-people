package com.example.ecobrandlyapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mFirebaseAuth; //Firebase 인증처리
    private DatabaseReference mDatabaseRef; //실시간 데이터 베이스
    private EditText metId,mEtPwd;
    private Button btn_register,btn_login;
    private long level=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);//화면에 무엇을 보여줄지 결정하는 메서드

        mFirebaseAuth=FirebaseAuth.getInstance(); //파이어베이스 접근 권한
        mDatabaseRef= FirebaseDatabase.getInstance().getReference("fourpeople");

        metId=findViewById(R.id.etId);
        mEtPwd=findViewById(R.id.etPassword);

        btn_login=findViewById(R.id.btn_login);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //로그인 요청버튼
                String strId = metId.getText().toString();
                String strPwd = mEtPwd.getText().toString();

                mFirebaseAuth.signInWithEmailAndPassword(strId,strPwd).addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        /*210826 수정 [일반회원/기업 layout 구분]*/
                        if(task.isSuccessful()){
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            mDatabaseRef.child("userAccount").child(user.getUid()).child("level").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    long value = snapshot.getValue(long.class);
                                    level = value;
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    //Log.e("MainActivity", String.valueOf(databaseError.toException())); // 에러문 출력
                                }
                            });

                            if(level==2) {//기업
                                Intent intent = new Intent(MainActivity.this, HomeEnterpriseActivity.class);
                                startActivity(intent);
                                finish();
                            }
                            else if(level==1){//일반회원
                                Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                                startActivity(intent);
                                finish();
                            }

                        }else{
                            Toast.makeText(MainActivity.this," 로그인 실패",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });



        btn_register = findViewById(R.id.btn_register);
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,RegistrationOptionActivity.class);
                startActivity(intent); //액티비티 이동 구문
            }
        });
    }


}
