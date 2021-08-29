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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterEnterpriseActivity extends AppCompatActivity {

    private FirebaseAuth eFirebaseAuth; //Firebase 인증처리
    private DatabaseReference eDatabaseRef; //실시간 데이터 베이스
    private EditText eetId,eEtPwd,eEtRePwd,eEtPhonenumber,eEtBusinessReg,eEtAliasing;
    private Button eBtnRegister;
    private TextView passMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_enterprise);

        eFirebaseAuth= FirebaseAuth.getInstance();
        eDatabaseRef= FirebaseDatabase.getInstance().getReference("fourpeople");

        eetId=findViewById(R.id.etId2);
        eEtPhonenumber=findViewById(R.id.etPhoneNumber2);
        eEtPwd=findViewById(R.id.etPassword2);
        eEtRePwd=findViewById(R.id.etRePassword2);
        eEtBusinessReg=findViewById(R.id.BusinessRegistration);
        eEtAliasing=findViewById(R.id.etAliasing2);
        passMessage=findViewById(R.id.etPasswordChecker2);


        eBtnRegister=findViewById(R.id.btn_registerCorp);

        //동일 비밀번호 확인
        eEtRePwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                String Pwd=eEtPwd.getText().toString();
                String RePwd=eEtRePwd.getText().toString();

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



        eBtnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String strId = eetId.getText().toString();
                String strPwd = eEtPwd.getText().toString();
                String strRePwd =eEtRePwd.getText().toString();//pwd->repwd로 수정
                String strPhoneNumber = eEtPhonenumber.getText().toString();
                String strBusinessReg=eEtBusinessReg.getText().toString();
                String strAlising=eEtAliasing.getText().toString();


                //이메일 입력 확인
                if (strId.length()==0){
                    Toast.makeText(RegisterEnterpriseActivity.this,"Email을 입력하세요",Toast.LENGTH_SHORT).show();
                    eetId.requestFocus();
                    return;
                }
                //비밀번호 입력 확인
                if (strPwd.length()==0){
                    Toast.makeText(RegisterEnterpriseActivity.this,"비밀번호을 입력하세요",Toast.LENGTH_SHORT).show();
                    eEtPwd.requestFocus();
                    return;
                }
                //비밀번호 입력 확인
                if (strRePwd.length()==0){
                    Toast.makeText(RegisterEnterpriseActivity.this,"비밀번호 재확인을 입력하세요",Toast.LENGTH_SHORT).show();
                    eEtRePwd.requestFocus();
                    return;
                }
                //동일 비밀번호 확인 -- 수정할수도 있음
                if(!strPwd.equals(strRePwd)){
                    Toast.makeText(RegisterEnterpriseActivity.this,"비밀번호가 일치하지 않습니다",Toast.LENGTH_SHORT).show();
                    eEtRePwd.setText("");
                    eEtPwd.setText("");
                    eEtRePwd.requestFocus();
                    return;
                }
                //연락처 확인
                if(strPhoneNumber.length()==0){
                    Toast.makeText(RegisterEnterpriseActivity.this,"연락처를 입력하세요",Toast.LENGTH_SHORT).show();
                    eEtPhonenumber.requestFocus();
                    return;
                }

                //별칭 확인
                if(strAlising.length()==0){
                    Toast.makeText(RegisterEnterpriseActivity.this,"이름 입력하세요",Toast.LENGTH_SHORT).show();
                    eEtAliasing.requestFocus();
                    return;
                }
                //사업자등록번호 입력 확인
                if(eEtBusinessReg.getText().toString().length()==0 ){
                    Toast.makeText(RegisterEnterpriseActivity.this,"사업자등록번호를 정확히 입력해주세요",Toast.LENGTH_SHORT).show();
                    eEtBusinessReg.requestFocus();
                    return;
                }

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
                            //point 설정 필요?
                            account.setLevel(2);//기업 level = 2
                            account.setAlising(strAlising);

                            eDatabaseRef.child("userAccount").child(firebaseUser.getUid()).setValue(account);

                            Toast.makeText(RegisterEnterpriseActivity.this,"회원가입 성공하셨습니다.",Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(RegisterEnterpriseActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }else{
                            Toast.makeText(RegisterEnterpriseActivity.this,"회원가입 실패하셨습니다.",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

    }
}
