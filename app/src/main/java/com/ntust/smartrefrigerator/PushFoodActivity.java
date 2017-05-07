package com.ntust.smartrefrigerator;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.ntust.smartrefrigerator.listeners.OnNewTagListener;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by petingo on 2017/5/6.
 */

public class PushFoodActivity extends ReadNFCCard {
    private long timeStamp;
    private String cardID;
    private String QRCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_push_food);

        //View
        final ImageView QRCodeView = (ImageView) findViewById(R.id.QRCodeView);
        final TextView tvHint = (TextView) findViewById(R.id.pophint);
        final TextView tvIDAndName = (TextView) findViewById(R.id.IDAndName);
        final TextView tvTime = (TextView) findViewById(R.id.time);
        final Button confirmButton = (Button) findViewById(R.id.confirmButton);
        confirmButton.setVisibility(View.GONE);

        //Database
        DBHelper myDBHelper = new DBHelper(this);
        final SQLiteDatabase db = myDBHelper.getWritableDatabase();
        @SuppressLint("Recycle") Cursor cs = db.rawQuery("Select * from REF", null);
        cs.moveToFirst();

        final OnNewTagListener listener = new OnNewTagListener() {
            @Override
            public void onNewTag(Tag tag) {
                cardID = tag.getUuid();
                Calendar calendar = Calendar.getInstance();
                timeStamp = calendar.getTimeInMillis();
                QRCode = String.valueOf(timeStamp) + " " + cardID;

                QRCodeView.setImageBitmap(Utils.QRCodeGenerate(QRCode));

                //Other View
                tvHint.setText("讀取成功！");
                tvTime.setText(Utils.getDate(timeStamp));
                //tvIDAndName.setText("B10533030 台科電資最帥兆哥");
                String IDAndName;
                if(cardID.equals("75FE1662")){
                    IDAndName = "B10322011 小拉基";
                } else if(cardID.equals("F511FB61")) {
                    IDAndName = "B10322022 小夥伴";
                } else {
                    IDAndName = "Axxxxxxxx OOO";
                }
                tvIDAndName.setText(IDAndName);
                confirmButton.setVisibility(View.VISIBLE);

            }
        };
        registerOnNewTagListener(listener);

        //Confirm Button
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //addFood(timeStamp, cardID);
                ServerConnection.postNewComment(PushFoodActivity.this, QRCode, "add");
                Toast.makeText(PushFoodActivity.this, "開始列印標籤...", Toast.LENGTH_SHORT).show();
                PushFoodActivity.this.finish();
            }
        });

    }

    private void addFood(long timeStamp, String CardID) {
        DBHelper myDBHelper = new DBHelper(this);
        SQLiteDatabase db = myDBHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("timeStamp", timeStamp);
        values.put("CardID", CardID);
        values.put("QRCode", QRCode);
        db.insert("REF", null, values);
    }

}
