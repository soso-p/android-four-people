package com.example.ecobrandlyapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class ModifyInformationActivity extends AppCompatActivity {

    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference mDatabaseRef, Ref; //실시간 데이터 베이스
    private EditText userId, changeAliasing, currentPassword, changePassword, changeRePassword, changePhoneNumber, changeBusinessReg;
    private TextView changePasswordChecker;
    private Button btn_modify;
    private String Aliasing, cuPwd, Pwd, RePwd, PhoneNumber;
    private Object current;
    private int level;
    private String result="";

    private void readData(GetLevelListener levelListener) {
        Ref= FirebaseDatabase.getInstance().getReference("fourpeople");
        FirebaseUser user = mFirebaseAuth.getCurrentUser();

        Ref.child("userAccount").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                level = dataSnapshot.child("level").getValue(Integer.class);
                levelListener.onLevel(level);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }
    private void read(){
        readData(new GetLevelListener() {
            @Override
            public void onLevel(int value) {
                Log.d("##########",String.valueOf(value));
                level=value;
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabaseRef= FirebaseDatabase.getInstance().getReference("fourpeople");
        FirebaseUser user = mFirebaseAuth.getCurrentUser();

        /*콜백함수이용.. -> 코드를 전부 읽은 후에 나중에 읽어오게 되는듯?? -> 후에 더 공부필요
        read();
        Log.d("@@@@@@@@@@",String.valueOf(level));
        */

        mDatabaseRef.child("userAccount").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                level = dataSnapshot.child("level").getValue(Integer.class);
                if(level == 1) {//고객이라면
                    setContentView(R.layout.activity_modify_information);
                    current=HomeActivity.class;
                }
                else{
                    setContentView(R.layout.activity_modify_information2);
                    current=HomeEnterpriseActivity.class;
                }


                //layout 초기화
                userId=findViewById(R.id.userId);
                changeAliasing=findViewById(R.id.changeAliasing);
                currentPassword=findViewById(R.id.currentPassword);
                changePassword=findViewById(R.id.changePassword);
                changeRePassword=findViewById(R.id.changeRePassword);
                changePhoneNumber=findViewById(R.id.changePhoneNumber);
                changeBusinessReg=findViewById(R.id.changeBusinessReg);
                changePasswordChecker=findViewById(R.id.changePasswordChecker);
                btn_modify=findViewById(R.id.btn_modify);

                userId.setText(dataSnapshot.child("id").getValue(String.class));
                changeAliasing.setText(dataSnapshot.child("alising").getValue(String.class));
                changePhoneNumber.setText(dataSnapshot.child("phoneNumber").getValue(String.class));
                if(level == 2) changeBusinessReg.setText(dataSnapshot.child("businessReg").getValue(String.class));

                //동일 비밀번호 확인
                changeRePassword.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                        Pwd = changePassword.getText().toString();
                        RePwd =changeRePassword.getText().toString();

                        if(Pwd.equals(RePwd)){
                            changePasswordChecker.setHeight(30);
                            changePasswordChecker.setText("비밀번호가 일치합니다");
                            changePasswordChecker.setTypeface(null, Typeface.BOLD);
                            changePasswordChecker.setTextColor(Color.GREEN);

                        }else {
                            changePasswordChecker.setHeight(30);
                            changePasswordChecker.setText("비밀번호가 일치하지 않습니다.");
                            changePasswordChecker.setTextColor(Color.RED);
                            changePasswordChecker.setTypeface(null, Typeface.BOLD);
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });


                btn_modify.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Aliasing=changeAliasing.getText().toString();
                        cuPwd=currentPassword.getText().toString();
                        Pwd = changePassword.getText().toString();
                        RePwd =changeRePassword.getText().toString();
                        PhoneNumber = changePhoneNumber.getText().toString();

                        //현재 비밀번호 확인
                        if(!cuPwd.equals(dataSnapshot.child("pwd").getValue(String.class))){
                            Toast.makeText(ModifyInformationActivity.this,"현재 비밀번호를 다시 입력해주세요",Toast.LENGTH_SHORT).show();
                            currentPassword.requestFocus();
                            return;
                        }
                        //비밀번호 입력 확인
                        if (Pwd.length()==0){
                            Toast.makeText(ModifyInformationActivity.this,"비밀번호을 입력하세요",Toast.LENGTH_SHORT).show();
                            changePassword.requestFocus();
                            return;
                        }
                        //비밀번호 재입력 확인
                        if (RePwd.length()==0){
                            Toast.makeText(ModifyInformationActivity.this,"비밀번호 재확인을 입력하세요",Toast.LENGTH_SHORT).show();
                            changeRePassword.requestFocus();
                            return;
                        }
                        //동일 비밀번호 확인
                        if(!Pwd.equals(RePwd)){
                            Toast.makeText(ModifyInformationActivity.this,"비밀번호가 일치하지 않습니다",Toast.LENGTH_SHORT).show();
                            changeRePassword.requestFocus();
                            return;
                        }
                        //연락처 확인
                        if(PhoneNumber.length()==0){
                            Toast.makeText(ModifyInformationActivity.this,"연락처를 입력하세요",Toast.LENGTH_SHORT).show();
                            changePhoneNumber.requestFocus();
                            return;
                        }
                        //별칭 확인
                        if(Aliasing.length()==0){
                            Toast.makeText(ModifyInformationActivity.this,"이름 입력하세요",Toast.LENGTH_SHORT).show();
                            changeAliasing.requestFocus();
                            return;
                        }

                        //데이터베이스 수정
                        Map<String,Object> taskMap = new HashMap<String,Object>();
                        taskMap.put("pwd",Pwd);
                        taskMap.put("alising",Aliasing);
                        taskMap.put("PhoneNumber",PhoneNumber);

                        mDatabaseRef.child("userAccount").child(user.getUid()).updateChildren(taskMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(ModifyInformationActivity.this,"수정되었습니다!",Toast.LENGTH_SHORT).show();
                                /*Intent intent = new Intent(ModifyInformationActivity.this, (Class<?>) current);
                                startActivity(intent);*/
                                finish();
                            }
                        })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(ModifyInformationActivity.this,"수정 오류.",Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                });

                    }
                });


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

    }
}