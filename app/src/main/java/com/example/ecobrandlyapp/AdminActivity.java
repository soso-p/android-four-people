package com.example.ecobrandlyapp;


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
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class AdminActivity extends AppCompatActivity {

    private FirebaseAuth mFirebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        mFirebaseAuth = FirebaseAuth.getInstance();
        TableLayout tableLayout = (TableLayout)findViewById(R.id.tablelayout);
        TableLayout tableLayout2 = (TableLayout)findViewById(R.id.tablelayout2);
        TableRow titleRow1 = (TableRow)findViewById(R.id.titleRow1);
        TableRow titleRow2 = (TableRow)findViewById(R.id.titleRow2);
        TableRow noDataComment = (TableRow)findViewById(R.id.noDataComment);
        Button btn_complete = (Button)findViewById(R.id.btn_complete);
        Button btn_reset = (Button)findViewById(R.id.btn_reset);

        Button btn_logout = findViewById(R.id.btn_logout);
        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //로그아웃
                mFirebaseAuth.signOut();
                Intent logoutIntent = new Intent(AdminActivity.this, MainActivity.class);
                startActivity(logoutIntent);
                finish();
            }
        });

        TextView date = (TextView)findViewById(R.id.date);
        Calendar cal = Calendar.getInstance();
        System.out.println(cal);

        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int min = cal.get(Calendar.MINUTE);
        int sec = cal.get(Calendar.SECOND);
        date.setText("Today : " + year + "/ " + month + "/ " + day);
        date.setTextSize(10);

        int resultDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH) - day;
        TextView dday = (TextView)findViewById(R.id.Dday);
        dday.setText("혜택 발송 D - "+ Integer.toString(resultDay));
        dday.setTextColor(Color.RED);
        dday.setTextSize(12);

        /*달의 말일 23:00:00 ~ 23:59:59 까지는 버튼 사용 금지*/
        if((resultDay == 3 && hour == 20 && min>=0 && min<=59))//말일, 23시 0분부터 59분까지 버튼 사용
            btn_complete.setEnabled(true);
        btn_complete.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                btn_reset.setEnabled(true);
            }
        });

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseRef = database.getReference("fourpeople");
        databaseRef.child("userAccount").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Button btn_reset = (Button)findViewById(R.id.btn_reset);
                btn_reset.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            String level = Long.toString(snapshot.child("level").getValue(Long.class));

                            if (level.equals("1")) {
                                String point = Long.toString(snapshot.child("point").getValue(Long.class));
                                Map<String, Object> pointUpdate = new HashMap<>();
                                pointUpdate.put("point", 0);
                                databaseRef.child("userAccount").child(snapshot.child("idToken").getValue(String.class)).updateChildren(pointUpdate);
                            }
                        }

                        Intent intent = getIntent();
                        finish();
                        startActivity(intent);
                    }

                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });


        databaseRef.child("userAccount").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                tableLayout.removeAllViews();
                tableLayout2.removeAllViews();
                tableLayout.addView(titleRow1);
                tableLayout2.addView(titleRow2);

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String id = snapshot.child("id").getValue(String.class);
                    String phoneNumber = snapshot.child("phoneNumber").getValue(String.class);
                    String level = Long.toString(snapshot.child("level").getValue(Long.class));

                    printAllRow(tableLayout, id, phoneNumber, level);

                }

                int row_cnt = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String id = snapshot.child("id").getValue(String.class);
                    String phoneNumber = snapshot.child("phoneNumber").getValue(String.class);
                    String level = Long.toString(snapshot.child("level").getValue(Long.class));

                    if(level.equals("1")){
                        String point = Long.toString(snapshot.child("point").getValue(Long.class));
                        int points = Integer.parseInt(point);
                        if(points >= 15) {//15로 변경필요
                            printPresentRow(tableLayout2, id, phoneNumber, points);
                            row_cnt += 1;
                        }
                    }
                }
                if(row_cnt == 0){
                    tableLayout2.addView(noDataComment);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

    }

    public void printAllRow(TableLayout tableLayout, String id, String phoneNumber, String level) {
        TableRow tableRow = new TableRow(this);
        tableRow.setLayoutParams(new TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT
        ));

        TextView textView1 = new TextView(this);
        textView1.setText(String.valueOf(id));
        textView1.setGravity(Gravity.CENTER);
        textView1.setTextSize(14);
        tableRow.addView(textView1);

        TextView textView2 = new TextView(this);
        textView2.setText(String.valueOf(phoneNumber));
        textView2.setGravity(Gravity.CENTER);
        textView2.setTextSize(14);
        tableRow.addView(textView2);

        TextView textView3 = new TextView(this);
        if(level.equals("0")) textView3.setText("관리자");
        else if(level.equals("1")) textView3.setText("일반회원");
        else if(level.equals("2")) textView3.setText("기업");
        else textView3.setText(String.valueOf(level));
        textView3.setGravity(Gravity.CENTER);
        textView3.setTextSize(14);
        tableRow.addView(textView3);

        tableLayout.addView(tableRow);
    }

    public void printPresentRow(TableLayout tableLayout, String id, String phoneNumber, int points) {
        TableRow tableRow = new TableRow(this);
        tableRow.setLayoutParams(new TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT
        ));

        TextView textView1 = new TextView(this);
        textView1.setText(String.valueOf(id));
        textView1.setGravity(Gravity.CENTER);
        textView1.setTextSize(14);
        tableRow.addView(textView1);

        TextView textView2 = new TextView(this);
        textView2.setText(String.valueOf(phoneNumber));
        textView2.setGravity(Gravity.CENTER);
        textView2.setTextSize(14);
        tableRow.addView(textView2);

        TextView textView3 = new TextView(this);
        if (points >= 30)
           textView3.setText("혜택 2");
        else if(points>=15)
            textView3.setText("혜택 1");
        else//삭제 필요
            textView3.setText(Integer.toString(points));
        textView3.setGravity(Gravity.CENTER);
        textView3.setTextSize(14);
        tableRow.addView(textView3);

        CheckBox checkbox = new CheckBox(this);
        checkbox.setGravity(Gravity.CENTER);
        tableRow.addView(checkbox);

        tableLayout.addView(tableRow);
    }

}
