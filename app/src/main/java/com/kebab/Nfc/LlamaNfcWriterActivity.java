package com.kebab.Nfc;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentFilter.MalformedMimeTypeException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.MifareUltralight;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.nfc.tech.NfcA;
import android.os.Bundle;
import com.kebab.AlertDialogEx.Builder;
import com.kebab.HelpersC;
import com.kebab.Llama.Logging;
import com.kebab.Llama.R;

public class LlamaNfcWriterActivity extends Activity {
    public static final String EXTRA_TAG_HEX_ID = "TagHexId";
    static final String LLAMA_NFC_URI = "llamaloc://llama.location.profiles/nfc";
    NfcAdapter mAdapter;
    PendingIntent pendingIntent;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mAdapter = NfcAdapter.getDefaultAdapter(this);
        this.pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(536870912), 0);
        setContentView(R.layout.nfc_formatter);
    }

    public void onPause() {
        super.onPause();
        this.mAdapter.disableForegroundDispatch(this);
    }

    public void onResume() {
        super.onResume();
        IntentFilter ndef = new IntentFilter("android.nfc.action.NDEF_DISCOVERED");
        try {
            ndef.addDataType("*/*");
            IntentFilter ndef2 = new IntentFilter("android.nfc.action.NDEF_DISCOVERED");
            try {
                ndef.addDataType("*/*");
                ndef.addDataScheme("http");
                IntentFilter[] intentFiltersArray = new IntentFilter[]{ndef, ndef2};
                String[][] techListsArray = new String[3][];
                techListsArray[0] = new String[]{MifareUltralight.class.getName(), Ndef.class.getName(), NfcA.class.getName()};
                techListsArray[1] = new String[]{NdefFormatable.class.getName()};
                techListsArray[2] = new String[]{MifareClassic.class.getName(), Ndef.class.getName(), NfcA.class.getName()};
                this.mAdapter.enableForegroundDispatch(this, this.pendingIntent, intentFiltersArray, techListsArray);
            } catch (MalformedMimeTypeException e) {
                throw new RuntimeException("fail", e);
            }
        } catch (MalformedMimeTypeException e2) {
            throw new RuntimeException("fail", e2);
        }
    }

    public void onNewIntent(Intent intent) {
        Tag tagFromIntent = (Tag) intent.getParcelableExtra("android.nfc.extra.TAG");
        if (tagFromIntent != null) {
            Ndef ndefTag = Ndef.get(tagFromIntent);
            NdefRecord record = NdefRecord.createUri(LLAMA_NFC_URI);
            NdefMessage ndefMessage = new NdefMessage(new NdefRecord[]{record});
            try {
                final Tag tag = ndefTag.getTag();
                ndefTag.connect();
                ndefTag.writeNdefMessage(ndefMessage);
                ndefTag.close();
                new Builder(this).setMessage(R.string.hrNfcFormatSuccess).setPositiveButton("Use this tag", new OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Intent result = new Intent();
                        result.putExtra(LlamaNfcWriterActivity.EXTRA_TAG_HEX_ID, HelpersC.toHexString(tag.getId()));
                        LlamaNfcWriterActivity.this.setResult(-1, result);
                        LlamaNfcWriterActivity.this.finish();
                    }
                }).setNegativeButton(R.string.hrOkeyDoke, null).show();
            } catch (Exception e) {
                Logging.Report(e, (Context) this);
                new Builder(this).setMessage(getString(R.string.hrFormatFailHelp, new Object[]{LLAMA_NFC_URI})).setNeutralButton("Copy Llama URL", new OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        ((ClipboardManager) LlamaNfcWriterActivity.this.getSystemService("clipboard")).setPrimaryClip(ClipData.newPlainText("Llama NFC URI", LlamaNfcWriterActivity.LLAMA_NFC_URI));
                    }
                }).setPositiveButton(R.string.hrOkeyDoke, null).show();
            }
        }
    }
}
