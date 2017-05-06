package com.ntust.smartrefrigerator;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.nfc.NfcAdapter;
import android.nfc.tech.IsoDep;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.MifareUltralight;
import android.nfc.tech.Ndef;
import android.nfc.tech.NfcA;
import android.nfc.tech.NfcB;
import android.nfc.tech.NfcF;
import android.nfc.tech.NfcV;
import android.os.Bundle;
import android.view.ActionProvider;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ntust.smartrefrigerator.listeners.OnNewTagListener;
import com.ntust.smartrefrigerator.Utils;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by petingo on 2017/5/6.
 */

public class PushFoodActivity extends Activity {
    // list of NFC technologies detected:

    private final String[][] nfcTechnologies = new String[][]{
            new String[]{
                    NfcA.class.getName(),
                    NfcB.class.getName(),
                    NfcF.class.getName(),
                    NfcV.class.getName(),
                    IsoDep.class.getName(),
                    MifareClassic.class.getName(),
                    MifareUltralight.class.getName(), Ndef.class.getName()
            }
    };

    // list of NFC listeners to notify on new NFC tag scanned:
    private List<OnNewTagListener> onNewTagListeners;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_push_food);

        //View
        final ImageView QRCodeView = (ImageView) findViewById(R.id.QRCodeView);
        final TextView tvHint = (TextView) findViewById(R.id.hint);
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
                String CardID = tag.getUuid();
                Calendar calendar = Calendar.getInstance();
                long timeStamp = calendar.getTimeInMillis();
                addFood(timeStamp, CardID);
                QRCodeView.setImageBitmap(Utils.QRCodeGenerate(String.valueOf(timeStamp) + CardID));

                //Other View
                tvHint.setText("讀取成功！");
                tvTime.setText(Utils.getDate(timeStamp));
                tvIDAndName.setText("B10533030 台科電資最帥兆哥");
                confirmButton.setVisibility(View.VISIBLE);
            }
        };
        registerOnNewTagListener(listener);

        //Confirm Button
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
        values.put("QRCode", String.valueOf(timeStamp) + CardID);
        db.insert("REF", null, values);
    }
    @Override
    protected void onNewIntent(final Intent intent) {
        if (intent.getAction().equals(NfcAdapter.ACTION_TAG_DISCOVERED)) {
            Tag tag = new Tag(Utils.byteArrayToHexString(intent.getByteArrayExtra(NfcAdapter.EXTRA_ID)));
            // notifying listeners:
            if (this.onNewTagListeners != null)
                for (OnNewTagListener listener : this.onNewTagListeners) listener.onNewTag(tag);
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        // creating pending intent:
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        // creating intent receiver for NFC events:
        IntentFilter filter = new IntentFilter();
        filter.addAction(NfcAdapter.ACTION_TAG_DISCOVERED);
        filter.addAction(NfcAdapter.ACTION_NDEF_DISCOVERED);
        filter.addAction(NfcAdapter.ACTION_TECH_DISCOVERED);
        // enabling foreground dispatch for getting intent from NFC event:
        NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        nfcAdapter.enableForegroundDispatch(this, pendingIntent, new IntentFilter[]{filter}, this.nfcTechnologies);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // disabling foreground dispatch:
        NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        nfcAdapter.disableForegroundDispatch(this);
    }

    public void registerOnNewTagListener(OnNewTagListener listener) {
        if (this.onNewTagListeners == null)
            this.onNewTagListeners = new LinkedList<OnNewTagListener>();
        this.onNewTagListeners.add(listener);
    }

    public boolean unregisterOnNewTagListener(OnNewTagListener listener) {
        if (this.onNewTagListeners != null) return this.onNewTagListeners.remove(listener);
        return false;
    }
}
