package com.ntust.smartrefrigerator;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.ntust.smartrefrigerator.listeners.OnNewTagListener;

import java.util.Calendar;

/**
 * Created by petingo on 2017/5/7.
 */

public class CheckFoodActivity extends ReadNFCCard {
    String cardID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_food);

        final OnNewTagListener listener = new OnNewTagListener() {
            @Override
            public void onNewTag(Tag tag) {
                cardID = tag.getUuid();
                ServerConnection.postNewComment(CheckFoodActivity.this, cardID, "search");
            }
        };
        registerOnNewTagListener(listener);

        Button confirm = (Button) findViewById(R.id.confirm);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckFoodActivity.this.finish();
            }
        });
    }
}
