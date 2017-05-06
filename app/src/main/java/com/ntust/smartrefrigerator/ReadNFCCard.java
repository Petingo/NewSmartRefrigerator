package com.ntust.smartrefrigerator;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.tech.IsoDep;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.MifareUltralight;
import android.nfc.tech.Ndef;
import android.nfc.tech.NfcA;
import android.nfc.tech.NfcB;
import android.nfc.tech.NfcF;
import android.nfc.tech.NfcV;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.ntust.smartrefrigerator.listeners.OnNewTagListener;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by petingo on 2017/5/6.
 */

public class ReadNFCCard extends Activity {
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
