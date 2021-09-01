package com.example.ecobrandlyapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.slider.Slider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class EnterPhoneNumberActivity extends AppCompatActivity {
    private Button btn[]=new Button[12];
    private EditText editText;
    private TableLayout tableLayout;
    private String current="";
    private String storeUid,storeName, userUid, userName;
    private String logTime = "0000-00-00 00:00:00";
    private int i=0, flag=1, cnt, level;
    private int userPoint;
    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference mDatabaseRef, eDatabaseRef;

    long now;
    Date date;
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public EnterPhoneNumberActivity() {
    }

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
        editText =(EditText)findViewById(R.id.inputNumber);
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
        
        for(i=0; i<10; i++) {
            btn[i].setOnClickListener(new Button.OnClickListener() {
                public void onClick(View v) {
                    current=editText.getText().toString();
                    Button btn = (Button) v;
                    /*전화번호 하이픈(-)추가 -> 전화번호 형식에 맞지않게 입력이 되면 강제 종료가 되는 오류가 있음..*/
                    if(current.length()>2)
                        editText.addTextChangedListener(new PhoneNumberFormattingTextWatcher()); //전화번호 "-" 추가
                    if(current.length() < 13) //수정h
                        editText.append(btn.getText().toString());
                    //editText.setText(textview.getText()+current);
                }
            });
        }

        btn[10].setOnClickListener(new Button.OnClickListener(){ // 입력값 지우기
            public void onClick(View v){
                current=editText.getText().toString();
                if(current.length()>3){//수정h : if(current.length()>0)
                    current=current.substring(0,current.length()-1);
                    //if(current.length()>2) //수정h
                    editText.addTextChangedListener(new PhoneNumberFormattingTextWatcher()); //전화번호 "-" 추가
                    editText.setText(current);
                }
            }
        });

        btn[11].setOnClickListener(new View.OnClickListener(){ // 입력
            public void onClick(View v){
                /* editText에 입력한 전화번호와 데이터베이스 정보 동일한 고객을 가져온다.
                 * 만약 DB에 저장된 전화번호가 없다면 홈으로 돌아간다.
                 * 있다면 "1 포인트가 증정되었습니다."
                 * */
                mDatabaseRef.child("userAccount").addListenerForSingleValueEvent(new ValueEventListener() {
                    /*addValueEvnetListener-> 값의 변화가 있을 때마다 불러온다. 따라서 포인트 적립의 경우 무한루프에 빠질 수 밖에 없음
                     * 정확한 해결방안 : 콜백함수를 이용해라..? ->인터페이스 호출 ??
                     * 내 해결방안 : addValueEventListener를 addListenerForSingleValueEvent로 수정 (리스너 한번만 호출되도록)
                     * */
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        //고객 정보 있는지 DB확인
                        for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                            //DB에는 전화번호에 -를 저장하고 있지 않기 때문에 제거하고 비교
                            if(snapshot.child("phoneNumber").getValue(String.class).equals(editText.getText().toString().replaceAll("-",""))){
                                userUid=snapshot.child("idToken").getValue(String.class);
                                userName=snapshot.child("alising").getValue(String.class);
                                userPoint=snapshot.child("point").getValue(Integer.class);
                                level=snapshot.child("level").getValue(Integer.class);
                                flag=0;
                            }
                        }
                        // 가게 정보 불러오기
                        UserAccount nowUser = dataSnapshot.child(user.getUid()).getValue(UserAccount.class);
                        storeUid = nowUser.getIdToken();
                        storeName = nowUser.getAlising();

                        mDatabaseRef.removeEventListener(this);


                        //최근 고객의 상점 로그기록 끌어오기 ,, .orderBykey --> 어떤게 더 최신일까..
                        mDatabaseRef.child("userLog").orderByKey().addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                                    /*시간순 역순 정렬방법이 있나..?*/
                                    if(userUid.equals(snapshot.child("userUid").getValue(String.class))&& storeUid.equals(snapshot.child("storeUid").getValue(String.class))){
                                        logTime = snapshot.child("timeStamp").getValue(String.class);
                                        //Toast.makeText(CafeQRScanActivity.this,snapshot.child("timeStamp").getValue(String.class)+logTime,Toast.LENGTH_SHORT).show();
                                        //break;
                                    }
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });




                        String time = getTime();

                        //저장된 고객이면 포인트 적립
                        if(flag==0 && level==1){
                            mDatabaseRef.child("userAccount").child(userUid).child("point").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DataSnapshot> task) {
                                    if(task.isSuccessful()){
                                        Date llogdate = new Date();
                                        Date ltime =new Date();
                                        try {
                                            llogdate = format.parse(logTime);
                                            ltime = format.parse(time);
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }

                                        if( llogdate.getTime() + (long)1000*60*10 > ltime.getTime()  ) {
                                            //Toast.makeText(CafeQRScanActivity.this, llogdate.getTime() + (long)1000*60*10+""+ltime, Toast.LENGTH_SHORT).show();
                                            //Toast.makeText(CafeQRScanActivity.this, llogdate+""+ltime, Toast.LENGTH_SHORT).show();
                                            Toast.makeText(EnterPhoneNumberActivity.this, "이미 증가된 포인트 입니다.", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(EnterPhoneNumberActivity.this, HomeActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                        else{
                                            //Toast.makeText(CafeQRScanActivity.this, "else 문 안 " + logTime+time, Toast.LENGTH_SHORT).show();
                                            mDatabaseRef.child("userAccount").child(user.getUid()).child("point").setValue(userPoint+1);
                                            //log에 데이터 저장..
                                            userLog log = new userLog();
                                            log.setStoreUid(storeUid);
                                            log.setStoreName(storeName);
                                            log.setUserUid(userUid);
                                            log.setUserName(userName);
                                            log.setIncreasePoint(1);
                                            log.setPoints(userPoint+1);
                                            log.setTimeStamp(time);
                                            mDatabaseRef.child("userLog").push().setValue(log);
                                        }
                                    }
                                    else{
                                        Log.e("firebase", "Error getting data", task.getException());
                                    }
                                }
                            });

                            Toast.makeText(EnterPhoneNumberActivity.this,"적립완료", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(EnterPhoneNumberActivity.this, HomeEnterpriseActivity.class);
                            startActivity(intent);
                            finish();
                        }
                        else {
                            Toast.makeText(EnterPhoneNumberActivity.this,"등록된 회원정보가 없습니다.", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(EnterPhoneNumberActivity.this, HomeEnterpriseActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

    }
}