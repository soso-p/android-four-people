package com.example.ecobrandlyapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
    private DatabaseReference mDatabaseRef; //실시간 데이터 베이스
    private EditText userId, changeAliasing, changePassword, changeRePassword, changePhoneNumber, changeBusinessReg;
    private TextView changePasswordChecker;
    private Button btn_modify;
    private String Aliasing, Pwd, RePwd, PhoneNumber;
    private Object current;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabaseRef= FirebaseDatabase.getInstance().getReference("fourpeople");
        FirebaseUser user = mFirebaseAuth.getCurrentUser();

        if(mDatabaseRef.child("userAccount").child(user.getUid()).child("level").equals(1)) {//고객이라면
            setContentView(R.layout.activity_modify_information);
            current=HomeActivity.class;
        }
        else{
            setContentView(R.layout.activity_modify_information);
            current=HomeEnterpriseActivity.class;
        }

        //layout 초기화
        userId=findViewById(R.id.userId);
        changeAliasing=findViewById(R.id.changeAliasing);
        changePassword=findViewById(R.id.changePassword);
        changeRePassword=findViewById(R.id.changeRePassword);
        changePhoneNumber=findViewById(R.id.changePhoneNumber);
        changeBusinessReg=findViewById(R.id.changeBusinessReg);
        changePasswordChecker=findViewById(R.id.changePasswordChecker);
        btn_modify=findViewById(R.id.btn_modify);

        mDatabaseRef.child("userAccount").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userId.setText(snapshot.child("id").getValue(String.class));
                changeAliasing.setText(snapshot.child("alising").getValue(String.class));
                changePhoneNumber.setText(snapshot.child("phoneNumber").getValue(String.class));
                if(snapshot.child("level").equals(2)) changeBusinessReg.setText(snapshot.child("businessReg").getValue(String.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

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
                Pwd = changePassword.getText().toString();
                RePwd =changeRePassword.getText().toString();
                PhoneNumber = changePhoneNumber.getText().toString();

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
                        Toast.makeText(ModifyInformationActivity.this,"수정되었습니다!.",Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(ModifyInformationActivity.this, (Class<?>) current);
                        startActivity(intent);
                        finish();
                    }
                })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(ModifyInformationActivity.this,"수정 취소.",Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        });

            }
        });
    }
}