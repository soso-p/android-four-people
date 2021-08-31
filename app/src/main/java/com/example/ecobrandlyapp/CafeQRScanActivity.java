package com.example.ecobrandlyapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CafeQRScanActivity extends AppCompatActivity{


    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference mDatabaseRef, eDatabaseRef; //실시간 데이터 베이스
    private TextView storeId, timestamp, phoneNumber;
    private String storeUid,storeName,userName,userUid;
    private Button btn_back;
    private IntentIntegrator qrScan;
    private int flag=0; //기업정보 있는지
    long now;
    Date date;
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cafeqrscan);

        qrScan = new IntentIntegrator(this);
        qrScan.setOrientationLocked(false); //세로모드로 고정!
        qrScan.setPrompt("박스 안에 QR 코드를 스캔하세요!");
        qrScan.initiateScan();

        storeId = (TextView) findViewById(R.id.storeUid);
        timestamp = (TextView) findViewById(R.id.timestamp);
        phoneNumber = (TextView)  findViewById(R.id.storeName);
        btn_back = (Button) findViewById(R.id.btn_back);


        btn_back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(CafeQRScanActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private String getTime(){
        now = System.currentTimeMillis();
        date = new Date(now);
        return format.format(date);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Intent intent = new Intent(CafeQRScanActivity.this, HomeActivity.class);
                startActivity(intent);
                Toast.makeText(CafeQRScanActivity.this, "스캔취소!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(CafeQRScanActivity.this, "스캔완료!", Toast.LENGTH_SHORT).show();
                /*json 형식으로 qr스캔 받을 경우*/
                    /*
                try {

                    JSONObject obj = new JSONObject(result.getContents ());
                    storeId.setText(obj.getString("storeId"));
                    timestamp.setText(obj.getString("timestamp"));
                    phoneNumber.setText(obj.getString("phoneNumber"));


                } catch (JSONException e) {
                    e.printStackTrace();
                }
                   */

            /*개행문자로 구분*/

            String resultDataArray[]=result.getContents().split("\n");
            storeId.setText(resultDataArray[0]);
            //시간 저장 위해서 수정
            String time = getTime();
            timestamp.setText(time);
            phoneNumber.setText(resultDataArray[1]);

            storeUid=resultDataArray[0];
            storeName=resultDataArray[1];

            //현재 고객 = qr 코드 스캔하고 있는
            mFirebaseAuth = FirebaseAuth.getInstance();
            mDatabaseRef= FirebaseDatabase.getInstance().getReference("fourpeople");
            FirebaseUser user = mFirebaseAuth.getCurrentUser();

                //user 정보 찾기
                mDatabaseRef.child("userAccount").child(user.getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        UserAccount nowUser = snapshot.getValue(UserAccount.class);//객체에 저장
                        userUid = nowUser.getIdToken();
                        userName = nowUser.getAlising();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        //Log.e("MainActivity", String.valueOf(databaseError.toException())); // 에러문 출력
                    }
                });

            /*
             * onDataChange(DataSnapshot snapshot) => 비동기적. firebase에서 데이터를 "다시" 가져올 때 호출된다.
             * */

            eDatabaseRef= FirebaseDatabase.getInstance().getReference("fourpeople");

            //기업이 존재하는지 check
            eDatabaseRef.child("userAccount").child(resultDataArray[0]).child("idToken").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.getValue()==null) flag=1;

                    /* 포인트 추가 */
                    mDatabaseRef.child("userAccount").child(user.getUid()).child("point").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            if(task.isSuccessful() && flag==0){
                                int userPoint = Integer.parseInt(String.valueOf(task.getResult().getValue()));
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
                            else if(!task.isSuccessful() || flag==1 ){
                                Toast.makeText(CafeQRScanActivity.this,"가입된 기업이 아닙니다.", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(CafeQRScanActivity.this, HomeActivity.class);
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

        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }

    }


}
