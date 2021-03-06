package com.example.ecobrandlyapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

/*첫 화면 */

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mFirebaseAuth; //Firebase 인증처리
    private DatabaseReference mDatabaseRef, Ref; //실시간 데이터 베이스
    private EditText metId,mEtPwd;
    private Button btn_register,btn_login;
    private long level=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mFirebaseAuth=FirebaseAuth.getInstance(); //파이어베이스 접근 권한
        mDatabaseRef= FirebaseDatabase.getInstance().getReference("fourpeople");
        Ref=FirebaseDatabase.getInstance().getReference("fourpeople");
        FirebaseUser user2 = FirebaseAuth.getInstance().getCurrentUser();

        //로그인 인증 유지 => 로그아웃을 안한 경우!
        if(mFirebaseAuth.getCurrentUser() !=null) {

            Ref.child("userAccount").child(user2.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    level=snapshot.child("level").getValue(Integer.class);
                    Toast.makeText(MainActivity.this,"로그인 완료!",Toast.LENGTH_SHORT).show();
                    if(level==1){
                        Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    else if(level==2){
                        Intent intent = new Intent(MainActivity.this, HomeEnterpriseActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    else if(level==0){
                        Intent intent = new Intent(MainActivity.this, AdminActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        else { //로그인 유지가 안되어있는 경우 [로그아웃한 경우]
            setContentView(R.layout.activity_main);//화면에 무엇을 보여줄지 결정하는 메서드

            metId=findViewById(R.id.etId);
            mEtPwd=findViewById(R.id.etPassword);

            btn_login=findViewById(R.id.btn_login);
            btn_login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //로그인 요청버튼
                    String strId = metId.getText().toString().trim();
                    String strPwd = mEtPwd.getText().toString().trim();

                    //아이디 입력 확인
                    if (strId.length()==0){
                        Toast.makeText(MainActivity.this,"아이디를 입력하세요",Toast.LENGTH_SHORT).show();
                        metId.requestFocus();
                        return;
                    }
                    //비밀번호 입력 확인
                    if (strPwd.length()==0){
                        Toast.makeText(MainActivity.this,"비밀번호를 입력하세요",Toast.LENGTH_SHORT).show();
                        mEtPwd.requestFocus();
                        return;
                    }

                    mFirebaseAuth.signInWithEmailAndPassword(strId,strPwd).addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            /*210826 수정 [일반회원/기업 layout 구분]*/
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            if(task.isSuccessful()){
                                mDatabaseRef.child("userAccount").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        long value = snapshot.child("level").getValue(long.class);
                                        level = value;

                                        String userLevel = Long.toString(snapshot.child("level").getValue(long.class));
                                        String userId = snapshot.child("id").getValue(String.class);
                                        //Log.i("MainActivity","msg : "+level+userId);

                                        Toast.makeText(MainActivity.this,"로그인 완료!",Toast.LENGTH_SHORT).show();
                                        if(userId.equals(strId) && userLevel.equals("0")){
                                            Intent intent = new Intent(MainActivity.this, AdminActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }

                                        else if(level==1){//일반회원
                                            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }

                                        else if(level==2) {//기업
                                            Intent intent = new Intent(MainActivity.this, HomeEnterpriseActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        //Log.e("MainActivity", String.valueOf(databaseError.toException())); // 에러문 출력
                                    }
                                });

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


}
