package com.example.ecobrandlyapp;


import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AdminActivity extends AppCompatActivity {

    private FirebaseAuth mFirebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        mFirebaseAuth = FirebaseAuth.getInstance();

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

        TableLayout tableLayout = (TableLayout)findViewById(R.id.tablelayout);
        TableLayout tableLayout2 = (TableLayout)findViewById(R.id.tablelayout2);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseRef = database.getReference("fourpeople");
        databaseRef.child("userAccount").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String id = snapshot.child("id").getValue(String.class);
                    String phoneNumber = snapshot.child("phoneNumber").getValue(String.class);
                    String level = Long.toString(snapshot.child("level").getValue(Long.class));

                    printAllRow(tableLayout, id, phoneNumber, level);

                }

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String id = snapshot.child("id").getValue(String.class);
                    String phoneNumber = snapshot.child("phoneNumber").getValue(String.class);
                    String level = Long.toString(snapshot.child("level").getValue(Long.class));

                    if(level.equals("1")){
                        String point = Long.toString(snapshot.child("point").getValue(Long.class));
                        int points = Integer.parseInt(point);
                        if(points >= 0)//15로 변경필요
                            printPresentRow(tableLayout2, id, phoneNumber, points);
                    }

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
        textView3.setText(String.valueOf(level));
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
