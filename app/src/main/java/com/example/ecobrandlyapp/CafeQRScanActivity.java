package com.example.ecobrandlyapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CafeQRScanActivity extends AppCompatActivity{

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
            }

        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }

    }


}
