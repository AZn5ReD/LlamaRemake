package com.kebab.Llama;

import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import com.kebab.Helpers;
import com.kebab.Llama.Content.LlamaToneContentProvider;
import com.kebab.LlamaToneRateLimits;
import com.kebab.SeekBarDialogView;
import com.kebab.SeekBarDialogView.ValueFormatter;
import com.kebab.Tuple;
import com.kebab.Tuple.CaseInsensitiveItem1StringSorter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map.Entry;

public class LlamaTonePickerActivity extends ListActivity {
    BaseAdapter _Adapter;
    ArrayList<Tuple<String, String>> _Data = new ArrayList();
    SeekBarDialogView _RateLimit;

    /* Access modifiers changed, original: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this._RateLimit = new SeekBarDialogView(0, 0, 600, null, null, new ValueFormatter() {
            public String FormatValue(int value, boolean isTopMostValue, String topMostValue) {
                if (value == 0) {
                    return LlamaTonePickerActivity.this.getString(R.string.hrNoRateLimiting);
                }
                return Helpers.GetHoursMinutesSeconds(LlamaTonePickerActivity.this, value);
            }

            public int GetTextSize() {
                return 16;
            }
        });
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(1);
        TextView message = new TextView(this);
        message.setText(R.string.hrLlamaToneRateLimitHint);
        layout.addView(message);
        layout.addView(this._RateLimit.createSeekBarDialogView(this));
        TextView message2 = new TextView(this);
        message2.setText(R.string.hrNowPickALlameTone);
        layout.addView(message2);
        getListView().addHeaderView(layout);
        for (Entry<String, String> kvp : LlamaService.GetAllLlamaToneNamesAndCurrentValues(getApplicationContext()).entrySet()) {
            String currentActualToneName;
            String tonePickerName = (String) kvp.getKey();
            String tonePickerUri = (String) kvp.getValue();
            if (tonePickerUri == null) {
                currentActualToneName = "";
            } else {
                Ringtone r = RingtoneManager.getRingtone(this, Uri.parse(tonePickerUri));
                if (r == null) {
                    currentActualToneName = "";
                } else {
                    currentActualToneName = r.getTitle(this);
                }
            }
            this._Data.add(new Tuple(tonePickerName, currentActualToneName));
        }
        Collections.sort(this._Data, new CaseInsensitiveItem1StringSorter());
        this._Adapter = new BaseAdapter() {
            public View getView(int position, View convertView, ViewGroup parent) {
                View v;
                if (convertView == null) {
                    v = View.inflate(LlamaTonePickerActivity.this, 17367053, null);
                } else {
                    v = convertView;
                }
                ((TextView) v.findViewById(16908308)).setText((CharSequence) ((Tuple) getItem(position)).Item1);
                ((TextView) v.findViewById(16908309)).setText(LlamaTonePickerActivity.this.getString(R.string.hrCurrentlySetTo1, new Object[]{map.Item2}));
                return v;
            }

            public int getCount() {
                return LlamaTonePickerActivity.this._Data.size();
            }

            public Object getItem(int position) {
                return LlamaTonePickerActivity.this._Data.get(position);
            }

            public long getItemId(int position) {
                return (long) position;
            }
        };
        setListAdapter(this._Adapter);
        if (this._Data.size() == 0) {
            Helpers.ShowSimpleDialogMessage(this, getString(R.string.hrYouHaventSetUpAnyLlamaTones), new OnClickListener() {
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    LlamaTonePickerActivity.this.finish();
                }
            });
        }
    }

    /* Access modifiers changed, original: protected */
    public void onListItemClick(ListView l, View v, int position, long id) {
        String toneName = ((Tuple) this._Data.get(position - 1)).Item1;
        int rateLimitSeconds = this._RateLimit.GetResult();
        int newRateLimitId = LlamaToneRateLimits.Instance(this).GetNewRateLimitId();
        Intent resultIntent = new Intent();
        resultIntent.putExtra("android.intent.extra.ringtone.PICKED_URI", LlamaToneContentProvider.CreateUri(this, toneName, newRateLimitId, rateLimitSeconds));
        setResult(-1, resultIntent);
        finish();
    }
}
