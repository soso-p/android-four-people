package com.example.ecobrandlyapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;

public class EnterPhoneNumberActivity extends AppCompatActivity {
    private Button btn[]=new Button[12];
    private TextView textview;
    private TableLayout tableLayout;
    private String current="";
    private String storeUid,storeName, userUid, userName;
    private int i=0, flag=1;
    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference mDatabaseRef, eDatabaseRef;
    long now;
    Date date;
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

    private String getTime(){
        now = System.currentTimeMillis();
        date = new Date(now);
        return format.format(date);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_phone_number);

        //데이터베이스
        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabaseRef= FirebaseDatabase.getInstance().getReference("fourpeople");
        FirebaseUser user = mFirebaseAuth.getCurrentUser();

        //layout 초기화
        tableLayout = (TableLayout)findViewById(R.id.ePhoneLayout);
        textview =(TextView)findViewById(R.id.inputNumber);
        btn[0] =(Button)findViewById(R.id.number0);
        btn[1] =(Button)findViewById(R.id.number1);
        btn[2] =(Button)findViewById(R.id.number2);
        btn[3] =(Button)findViewById(R.id.number3);
        btn[4] =(Button)findViewById(R.id.number4);
        btn[5] =(Button)findViewById(R.id.number5);
        btn[6] =(Button)findViewById(R.id.number6);
        btn[7] =(Button)findViewById(R.id.number7);
        btn[8] =(Button)findViewById(R.id.number8);
        btn[9] =(Button)findViewById(R.id.number9);

        btn[10] =(Button)findViewById(R.id.delete);
        btn[11] =(Button)findViewById(R.id.input);

        current=textview.getText().toString();

        for(i=0; i<10; i++) {
            btn[i].setOnClickListener(new Button.OnClickListener() {
                public void onClick(View v) {
                    textview.append(btn[i].getText().toString());
                }
            });
        }

        btn[10].setOnClickListener(new Button.OnClickListener(){ // 지우기
            public void onClick(View v){
                if(current.length()>0){
                    current=current.substring(0,current.length()-1);
                    textview.setText(current);
                }
            }
        });

        btn[11].setOnClickListener(new View.OnClickListener(){ // 입력
            public void onClick(View v){
                /*textview에 입력한 전화번호와 데이터베이스 정보 동일한 고객을 가져온다.
                * 만약 DB에 저장된 전화번호가 없다면 홈으로 돌아간다.
                * 있다면 "1 포인트가 증정되었습니다."
                * */

                mDatabaseRef.child("userAccount").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        //DB에 전화번호 있는지 확인
                        for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                            if(snapshot.child("phoneNumber").equals(textview.getText().toString())) {
                                userUid=snapshot.child("idToken").getValue(String.class);
                                userName=snapshot.child("alising").getValue(String.class);
                                flag=0;
                            }
                        }
                        // 가게 정보 불러오기
                        UserAccount nowUser = dataSnapshot.getValue(UserAccount.class);
                        storeUid = nowUser.getIdToken();
                        storeName = nowUser.getAlising();

                        String time = getTime();

                        /* 포인트 추가 */
                        mDatabaseRef.child("userAccount").child(userUid).child("point").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DataSnapshot> task) {
                                if(task.isSuccessful() && flag==0){
                                    int userPoint = Integer.parseInt(String.valueOf(task.getResult().getValue()));
                                    mDatabaseRef.child("userAccount").child(userUid).child("point").setValue(userPoint+1);

                                    userLog log = new userLog();
                                    log.setStoreUid(storeUid);
                                    log.setStoreName(storeName);
                                    log.setUserUid(userUid);
                                    log.setUserName(userName);
                                    log.setIncreasePoint(1);
                                    log.setPoints(userPoint+1);
                                    log.setTimeStamp(time);
                                    mDatabaseRef.child("userLog").push().setValue(log);
                                    
                                    Toast.makeText(EnterPhoneNumberActivity.this,"적립완료", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                                else if(!task.isSuccessful() || flag==1 ){
                                    Toast.makeText(EnterPhoneNumberActivity.this,"등록된 전화번호가 없습니다.", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(EnterPhoneNumberActivity.this, HomeActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                                else{
                                    Log.e("firebase", "Error getting data", task.getException());
                                }
                            }
                        });

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


            }
        });
    }
}