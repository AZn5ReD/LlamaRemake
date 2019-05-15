package com.kebab.Llama.EventActions;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.preference.PreferenceActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import com.kebab.AlertDialogEx.Builder;
import com.kebab.AppendableCharSequence;
import com.kebab.ClickablePreferenceEx;
import com.kebab.ClickablePreferenceEx.GotResultHandler;
import com.kebab.IterableHelpers;
import com.kebab.Llama.Event;
import com.kebab.Llama.EventFragment;
import com.kebab.Llama.LlamaService;
import com.kebab.Llama.LlamaStorage;
import com.kebab.Llama.R;
import com.kebab.PreferenceEx;
import com.kebab.ResultRegisterableActivity;
import java.io.IOException;
import java.util.Random;

public class SpeakAction extends EventAction<SpeakAction> {
    int _AudioStream;
    Random _Random = new Random();
    String _TextToSay;

    public SpeakAction(String notificationText, int audioStream) {
        this._TextToSay = notificationText;
        this._AudioStream = audioStream;
    }

    public void PerformAction(LlamaService service, Activity activity, Event event, long currentTimeRoundedToNextMinute, int eventRunMode) {
        service.Speak(this._TextToSay, this._AudioStream, event.Name);
    }

    public boolean RenameProfile(String oldName, String newName) {
        return false;
    }

    /* Access modifiers changed, original: protected */
    public String getId() {
        return EventFragment.SPEAK_ACTION;
    }

    /* Access modifiers changed, original: protected */
    public int GetPartsConsumption() {
        return 2;
    }

    /* Access modifiers changed, original: protected */
    public void ToPsvInternal(StringBuilder sb) {
        sb.append(LlamaStorage.SimpleEscape(this._TextToSay)).append("|").append(this._AudioStream);
    }

    public static SpeakAction CreateFrom(String[] parts, int currentPart) {
        return new SpeakAction(LlamaStorage.SimpleUnescape(parts[currentPart + 1]), Integer.parseInt(parts[currentPart + 2]));
    }

    public PreferenceEx<SpeakAction> CreatePreference(PreferenceActivity context) {
        final PreferenceActivity preferenceActivity = context;
        return new ClickablePreferenceEx<SpeakAction>((ResultRegisterableActivity) context, context.getString(R.string.hrActionSpeak), this) {
            String _DialogTextToSay;
            String[] streamNames = preferenceActivity.getResources().getStringArray(R.array.audioStreamNames);
            String[] streamValues = preferenceActivity.getResources().getStringArray(R.array.audioStreamValues);

            /* Access modifiers changed, original: protected */
            public String GetHumanReadableValue(Context context, SpeakAction value) {
                if (value == null) {
                    return "";
                }
                return value._TextToSay == null ? "" : value._TextToSay;
            }

            /* Access modifiers changed, original: protected */
            public void OnPreferenceClicked(ResultRegisterableActivity host, SpeakAction existingValue, final GotResultHandler<SpeakAction> gotResultHandler) {
                View view = View.inflate(host.GetActivity(), R.layout.speak_action, null);
                final Spinner streamPicker = (Spinner) view.findViewById(R.id.spinner);
                final EditText textView = (EditText) view.findViewById(R.id.text);
                textView.setText(existingValue._TextToSay);
                ArrayAdapter<String> adapter = new ArrayAdapter(preferenceActivity, 17367048, this.streamNames);
                adapter.setDropDownViewResource(17367049);
                streamPicker.setAdapter(adapter);
                Integer index = IterableHelpers.FindIndex(this.streamValues, String.valueOf(existingValue._AudioStream));
                if (index == null) {
                    index = Integer.valueOf(0);
                }
                streamPicker.setSelection(index.intValue());
                AlertDialog dialog = new Builder(host.GetActivity()).setPositiveButton(R.string.hrOk, new OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        gotResultHandler.HandleResult(new SpeakAction(textView.getText().toString(), Integer.parseInt(streamValues[streamPicker.getSelectedItemPosition()])));
                        dialog.dismiss();
                    }
                }).setNegativeButton(R.string.hrCancel, null).setView(view).create();
                dialog.setOwnerActivity(host.GetActivity());
                dialog.show();
            }
        };
    }

    public void AppendActionDescription(Context context, AppendableCharSequence sb) throws IOException {
        sb.append(context.getString(R.string.hrSay1, new Object[]{this._TextToSay}));
    }

    public String GetIsValidError(Context context) {
        if (this._TextToSay == null || this._TextToSay.length() == 0) {
            return context.getString(R.string.hrEnterSomeTextToSpeak);
        }
        return null;
    }

    public boolean IsHarmful() {
        return false;
    }
}
