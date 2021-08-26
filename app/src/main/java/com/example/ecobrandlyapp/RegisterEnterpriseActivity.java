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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterEnterpriseActivity extends AppCompatActivity {

    private FirebaseAuth eFirebaseAuth; //Firebase 인증처리
    private DatabaseReference eDatabaseRef; //실시간 데이터 베이스
    private EditText eetId,eEtPwd,eEtRePwd,eEtPhonenumber,eEtBusinessReg ;
    private Button eBtnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_enterprise);

        eFirebaseAuth= FirebaseAuth.getInstance();
        eDatabaseRef= FirebaseDatabase.getInstance().getReference("fourpeople");

        eetId=findViewById(R.id.etId);
        eEtPhonenumber=findViewById(R.id.etPhoneNumber);
        eEtPwd=findViewById(R.id.etPassword);
        eEtRePwd=findViewById(R.id.etRePassword);
        eEtBusinessReg=findViewById(R.id.BusinessRegistration);


        eBtnRegister=findViewById(R.id.btn_registerCorp);
        eBtnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String strId = eetId.getText().toString();
                String strPwd = eEtPwd.getText().toString();
                String strRePwd =eEtRePwd.getText().toString();//pwd->repwd로 수정
                String strPhoneNumber = eEtPhonenumber.getText().toString();
                String strBusinessReg=eEtBusinessReg.getText().toString();


                //Firebase auth 진행
                eFirebaseAuth.createUserWithEmailAndPassword(strId,strPwd).addOnCompleteListener(RegisterEnterpriseActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            FirebaseUser firebaseUser=eFirebaseAuth.getCurrentUser();
                            UserAccount account = new UserAccount();
                            account.setIdToken(firebaseUser.getUid());
                            account.setId(firebaseUser.getEmail());
                            account.setPwd(strPwd);
                            account.setPhoneNumber(strPhoneNumber);
                            account.setBusinessReg(strBusinessReg);
                            account.setLevel(1);//기업 level = 1

                            eDatabaseRef.child("userAccount").child(firebaseUser.getUid()).setValue(account);

                            Toast.makeText(RegisterEnterpriseActivity.this,"회원가입 성공하셨습니다.",Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(RegisterEnterpriseActivity.this, MainActivity.class);
                            startActivity(intent);
                        }else{
                            Toast.makeText(RegisterEnterpriseActivity.this,"회원가입 실패하셨습니다.",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

    }
}
