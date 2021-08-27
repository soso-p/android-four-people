package com.example.ecobrandlyapp;

import android.app.Activity;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CafeQRScanActivity extends AppCompatActivity{


    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference mDatabaseRef; //실시간 데이터 베이스
    private TextView storeId, timestamp, phoneNumber;
    private Button btn_back;
    private IntentIntegrator qrScan;
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

        storeId = (TextView) findViewById(R.id.storeId);
        timestamp = (TextView) findViewById(R.id.timestamp);
        phoneNumber = (TextView)  findViewById(R.id.phoneNumber);
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
            timestamp.setText(getTime());
            phoneNumber.setText(resultDataArray[1]);



            /*추가 부분 -juhee*/

            /*현재 고객 연동*/
            mFirebaseAuth = FirebaseAuth.getInstance();
            mDatabaseRef= FirebaseDatabase.getInstance().getReference("fourpeople");
            FirebaseUser user = mFirebaseAuth.getCurrentUser();


            String storeNumber=resultDataArray[1];


            //있는 회사인지 확인 --> 아직 테스팅 못해봄..
            mDatabaseRef.child("userAccount").child(String.valueOf(storeNumber)).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if(!task.isSuccessful()){
                        //Toast.makeText(CafeQRScanActivity.this,String.valueOf(storeNumber), Toast.LENGTH_SHORT).show();
                        Toast.makeText(CafeQRScanActivity.this,"가입된 기업이 아닙니다.", Toast.LENGTH_SHORT).show();

                        Log.e("firebase", "Error getting data", task.getException());
                        //뭔가 되는듯 안되는듯..

                        //Intent intent = new Intent(CafeQRScanActivity.this, HomeActivity.class);
                        //startActivity(intent);

                        //storeId.setText("");
                        //timestamp.setText("");
                        //phoneNumber.setText("");

                    }
                    else{
                        mDatabaseRef.child("userAccount").child(user.getUid()).child("point").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DataSnapshot> task) {
                                if(!task.isSuccessful()){
                                    Log.e("firebase", "Error getting data", task.getException());
                                }
                                else{
                                    int userPoint = Integer.parseInt(String.valueOf(task.getResult().getValue()));
                                    mDatabaseRef.child("userAccount").child(user.getUid()).child("point").setValue(userPoint+1);
                                }
                            }
                        });
                    }
                }
            });


            /*고민사항
            * 아예 처음에 인식했을때 없는 회사라면 그냥 바로 돌려버릴지 아니면 다시 qr을 찍게 할지..?
            * 회사전화번호로 확인했는데 고유키 같은걸로 확인할지-- 고유키로 하려면 qr에 내용 더 추가해야함
            * */





            /*추가부분 끝 -juhee*/


            }

        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }

    }


}
