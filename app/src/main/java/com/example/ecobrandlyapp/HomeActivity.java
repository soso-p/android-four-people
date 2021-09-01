package com.example.ecobrandlyapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

public class HomeActivity extends AppCompatActivity {

    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference mDatabaseRef; //실시간 데이터 베이스
    private TextView metId,mEtPoint;
    private int Point;
    private String userUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        TableLayout tableLayout2 = (TableLayout)findViewById(R.id.tablelayout2);
        TableRow titleRow2 = (TableRow)findViewById(R.id.titleRow2);
        TableRow noDataComment = (TableRow)findViewById(R.id.noDataComment);

        /*현재 고객 연동*/
        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabaseRef= FirebaseDatabase.getInstance().getReference("fourpeople");
        FirebaseUser user = mFirebaseAuth.getCurrentUser();

        /*접근권한 설정*/
        if(user.getUid() == null){
            //로그아웃
            mFirebaseAuth.signOut();
            Intent logoutIntent = new Intent(HomeActivity.this, MainActivity.class);
            startActivity(logoutIntent);
            finish();
        }

        metId=findViewById(R.id.tv_id);
        mEtPoint=findViewById(R.id.tv_point);

        mDatabaseRef.child("userAccount").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userUid=snapshot.child("idToken").getValue(String.class);
                String userPoint = Integer.toString(snapshot.child("point").getValue(int.class));
                String userName = snapshot.child("alising").getValue(String.class);
                metId.setText(userName+"님의 포인트 현황");
                mEtPoint.setText(userPoint);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //Log.e("MainActivity", String.valueOf(databaseError.toException())); // 에러문 출력
            }
        });


        //*로그 출력부분*//

        mDatabaseRef.child("userLog").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                tableLayout2.removeAllViews();
                tableLayout2.addView(titleRow2);
                //String userUid= user.getIdToken(;

                int row_cnt = 0;
                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    if(userUid.equals(snapshot.child("userUid").getValue((String.class)))){
                        String storeName=snapshot.child("storeName").getValue(String.class);
                        String time=snapshot.child("timeStamp").getValue(String.class);
                        int point=snapshot.child("increasePoint").getValue(Integer.class);
                        int points=snapshot.child("points").getValue(Integer.class);
                        printPresentRow(tableLayout2, time, storeName, point,points);
                        row_cnt += 1;
                    }
                }
                if(row_cnt == 0){
                    tableLayout2.addView(noDataComment);
                }
                mDatabaseRef.removeEventListener(this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        Button btn_logout = findViewById(R.id.btn_logout);
        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //로그아웃
                mFirebaseAuth.signOut();
                Intent logoutIntent = new Intent(HomeActivity.this, MainActivity.class);
                startActivity(logoutIntent);
                finish();
            }
        });

        //탈퇴처리하는 구문 (파이어베이스의 회원 정보 삭제)
        //mFirebaseAuth.getCurrentUser().delete();


        Button btn_qr1 = findViewById(R.id.btn_qr1);
        btn_qr1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this,CafeQRScanActivity.class);
                startActivity(intent);
                finish();
            }
        });

        Calendar cal = Calendar.getInstance();

        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int min = cal.get(Calendar.MINUTE);

        int resultDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH) - day;

        /*달의 말일 23:00:00 ~ 23:59:59동안 QR Scan 금지(: point 인정 안됨!)*/
        if((resultDay == 0 && hour == 23 && min>=0 && min<=59))
            btn_qr1.setEnabled(false);
    }


    public void printPresentRow(TableLayout tableLayout, String time, String storename, Integer point,Integer points) {
        TableRow tableRow = new TableRow(this);
        tableRow.setLayoutParams(new TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT
        ));

        TextView textView1 = new TextView(this);
        textView1.setText(String.valueOf(time));
        textView1.setGravity(Gravity.CENTER);
        textView1.setTextSize(14);
        tableRow.addView(textView1);

        TextView textView2 = new TextView(this);
        textView2.setText(String.valueOf(storename));
        textView2.setGravity(Gravity.CENTER);
        textView2.setTextSize(14);
        tableRow.addView(textView2);

        TextView textView3 = new TextView(this);
        textView3.setText(String.valueOf(point));
        textView3.setGravity(Gravity.CENTER);
        textView3.setTextSize(14);
        tableRow.addView(textView3);

        TextView textView4= new TextView(this);
        textView4.setText(String.valueOf(points));
        textView4.setGravity(Gravity.CENTER);
        textView4.setTextSize(14);
        tableRow.addView(textView4);

        tableRow.setBackgroundColor(Color.WHITE);

        tableLayout.addView(tableRow);
    }


}