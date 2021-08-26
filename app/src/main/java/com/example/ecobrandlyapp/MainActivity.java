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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mFirebaseAuth; //Firebase 인증처리
    private DatabaseReference mDatabaseRef; //실시간 데이터 베이스
    private EditText metId,mEtPwd;
    private Button btn_register,btn_login;



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
                        if(task.isSuccessful()){
                            Intent intent = new Intent(MainActivity.this,HomeActivity.class);
                            startActivity(intent);
                            finish(); //현재 액티비티 파괴시키고 가기,, 다시 쓸일 없다고 생각
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
