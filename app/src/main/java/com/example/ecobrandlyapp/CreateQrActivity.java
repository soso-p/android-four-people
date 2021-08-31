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
import android.widget.Toast;

public class CreateQrActivity extends AppCompatActivity {

    DatabaseReference mDatabaseRef;
    private ImageView img;
    private String uid, alias, text;
    private StringBuilder str;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_qr);

        str=new StringBuilder();
        /*사업자번호 읽어오기*/
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser(); //현 로그인한 유저
        mDatabaseRef= FirebaseDatabase.getInstance().getReference("fourpeople");

        img= (ImageView)findViewById(R.id.qrcode);
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();

        mDatabaseRef.child("userAccount").child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserAccount us = snapshot.getValue(UserAccount.class);//객체에 저장
                uid= us.getIdToken();
                alias=us.getAlising();

                try {
                    BitMatrix bitMatrix = multiFormatWriter.encode(uid+"\n"+alias, BarcodeFormat.QR_CODE, 200, 200);
                    BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                    Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
                    img.setImageBitmap(bitmap);
                } catch (Exception e) {

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //Log.e("MainActivity", String.valueOf(databaseError.toException())); // 에러문 출력
            }
        });



    }
}