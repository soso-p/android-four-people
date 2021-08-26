package com.example.ecobrandlyapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import android.widget.ImageView;

public class CreateQrActivity extends AppCompatActivity {

    DatabaseReference mDatabaseRef;
    private ImageView img;
    private String text, num, phoneNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_qr);

        /*사업자번호 읽어오기*/
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        mDatabaseRef= FirebaseDatabase.getInstance().getReference("fourpeople");

        mDatabaseRef.child("userAccount").child(user.getUid()).child("businessReg").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                num = snapshot.getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //Log.e("MainActivity", String.valueOf(databaseError.toException())); // 에러문 출력
            }
        });

        mDatabaseRef.child("userAccount").child(user.getUid()).child("phoneNumber").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                phoneNum = snapshot.getValue(String.class); //핸드폰번호 받아오기
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //Log.e("MainActivity", String.valueOf(databaseError.toException())); // 에러문 출력
            }
        });

        text=num+"\n"+phoneNum;

        img= (ImageView)findViewById(R.id.qrcode);

        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            BitMatrix bitMatrix = multiFormatWriter.encode(text, BarcodeFormat.QR_CODE, 200, 200);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            img.setImageBitmap(bitmap);
        } catch (Exception e) {

        }


    }
}