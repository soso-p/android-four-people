package com.example.ecobrandlyapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

public class RegisterActivity extends AppCompatActivity
{

    private FirebaseAuth mFirebaseAuth; //Firebase 인증처리
    private DatabaseReference mDatabaseRef; //실시간 데이터 베이스
    private EditText metId,mEtPwd,mEtRePwd,mEtPhonenumber,mEtAliasing;

    private TextView passMessage;

    private Button mBtnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mFirebaseAuth=FirebaseAuth.getInstance();
        mDatabaseRef= FirebaseDatabase.getInstance().getReference("fourpeople");

        metId=findViewById(R.id.etId);
        mEtPhonenumber=findViewById(R.id.etPhoneNumber);
        mEtPwd=findViewById(R.id.etPassword);
        mEtRePwd=findViewById(R.id.etRePassword);
        mEtAliasing=findViewById(R.id.etAliasing);
        passMessage=findViewById(R.id.etPasswordChecker);

        //TextView passMessage = new TextView(getApplicationContext());


        //동일 비밀번호 확인
        mEtRePwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                String Pwd=mEtPwd.getText().toString();
                String RePwd=mEtRePwd.getText().toString();

                if(Pwd.equals(RePwd)){
                    passMessage.setHeight(30);
                    passMessage.setText("비밀번호가 일치합니다");
                    passMessage.setTypeface(null, Typeface.BOLD);
                    passMessage.setTextColor(Color.GREEN);

                }else {
                    passMessage.setHeight(30);
                    passMessage.setText("비밀번호가 일치하지 않습니다.");
                    passMessage.setTextColor(Color.RED);
                    passMessage.setTypeface(null, Typeface.BOLD);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mEtPwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                String Pwd=mEtPwd.getText().toString();
                String RePwd=mEtRePwd.getText().toString();

                if(Pwd.equals(RePwd)){
                    passMessage.setHeight(30);
                    passMessage.setText("비밀번호가 일치합니다");
                    passMessage.setTypeface(null, Typeface.BOLD);
                    passMessage.setTextColor(Color.GREEN);

                }else {
                    passMessage.setHeight(30);
                    passMessage.setText("비밀번호가 일치하지 않습니다.");
                    passMessage.setTextColor(Color.RED);
                    passMessage.setTypeface(null, Typeface.BOLD);

                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        mBtnRegister=findViewById(R.id.btn_register);
        mBtnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //회원가입 처리 시작

                String strId = metId.getText().toString();
                String strPwd = mEtPwd.getText().toString();
                String strRePwd =mEtPwd.getText().toString();
                String strPhoneNumber = mEtPhonenumber.getText().toString();
                String strAlising=mEtAliasing.getText().toString();

                //이메일 입력 확인
                if (strId.length()==0){
                    Toast.makeText(RegisterActivity.this,"Email을 입력하세요",Toast.LENGTH_SHORT).show();
                    metId.requestFocus();
                    return;
                }
                //비밀번호 입력 확인
                if (strPwd.length()==0){
                    Toast.makeText(RegisterActivity.this,"비밀번호을 입력하세요",Toast.LENGTH_SHORT).show();
                    mEtPwd.requestFocus();
                    return;
                }
                //비밀번호 입력 확인
                if (strRePwd.length()==0){
                    Toast.makeText(RegisterActivity.this,"비밀번호 재확인을 입력하세요",Toast.LENGTH_SHORT).show();
                    mEtRePwd.requestFocus();
                    return;
                }
                //동일 비밀번호 확인 -- 수정할수도 있음
                if(!strPwd.equals(strRePwd)){
                    Toast.makeText(RegisterActivity.this,"비밀번호가 일치하지 않습니다",Toast.LENGTH_SHORT).show();
                    mEtRePwd.setText("");
                    mEtPwd.setText("");
                    mEtRePwd.requestFocus();
                    return;
                }
                //연락처 확인
                if(strPhoneNumber.length()==0){
                    Toast.makeText(RegisterActivity.this,"연락처를 입력하세요",Toast.LENGTH_SHORT).show();
                    mEtPhonenumber.requestFocus();
                    return;
                }

                //별칭 확인
                if(strAlising.length()==0){
                    Toast.makeText(RegisterActivity.this,"이름 입력하세요",Toast.LENGTH_SHORT).show();
                    mEtAliasing.requestFocus();
                    return;
                }

                //Firebase auth 진행
                mFirebaseAuth.createUserWithEmailAndPassword(strId,strPwd).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            /*make account*/
                            FirebaseUser firebaseUser=mFirebaseAuth.getCurrentUser();
                            UserAccount account = new UserAccount();
                            account.setIdToken(firebaseUser.getUid());
                            account.setPhoneNumber(firebaseUser.getPhoneNumber());
                            account.setAlising(firebaseUser.getDisplayName());
                            account.setId(firebaseUser.getEmail());
                            account.setPwd(strPwd);
                            account.setPhoneNumber(strPhoneNumber);
                            account.setPoint(0);
                            account.setLevel(1);
                            account.setAlising(strAlising);

                            mDatabaseRef.child("userAccount").child(firebaseUser.getUid()).setValue(account);

                            Toast.makeText(RegisterActivity.this,"회원가입 성공하셨습니다.",Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }else{
                            Toast.makeText(RegisterActivity.this,"회원가입 실패하셨습니다.",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });


    }
}