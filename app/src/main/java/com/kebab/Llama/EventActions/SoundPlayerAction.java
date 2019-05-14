package com.kebab.Llama.EventActions;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.preference.PreferenceActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import com.kebab.AlertDialogEx.Builder;
import com.kebab.AppendableCharSequence;
import com.kebab.ClickablePreferenceEx;
import com.kebab.ClickablePreferenceEx.GotResultHandler;
import com.kebab.Helpers;
import com.kebab.IterableHelpers;
import com.kebab.Llama.Event;
import com.kebab.Llama.EventActions.RingtonePicker.RingtonePickerResultCallback;
import com.kebab.Llama.EventFragment;
import com.kebab.Llama.LlamaService;
import com.kebab.Llama.LlamaStorage;
import com.kebab.Llama.R;
import com.kebab.PreferenceEx;
import com.kebab.ResultRegisterableActivity;
import java.io.IOException;

public class SoundPlayerAction extends EventAction<SoundPlayerAction> {
    public final int STOP_SOUND_QUEUE = 666;
    int _StreamId;
    String _ToneName;
    String _ToneUri;

    public SoundPlayerAction(String toneUri, String toneName, int streamId) {
        this._StreamId = streamId;
        this._ToneUri = toneUri;
        this._ToneName = toneName;
    }

    public void PerformAction(LlamaService service, Activity activity, Event event, long currentTimeRoundedToNextMinute, int eventRunMode) {
        if (this._StreamId == 666) {
            service.StopNoise();
            return;
        }
        service.MakeNoise(this._ToneUri, this._ToneName, this._StreamId, event.Name);
        if (eventRunMode == 3) {
            Helpers.ShowTip((Context) service, service.getString(R.string.hrUseTheNotificationToStopPlaying));
        }
    }

    public boolean RenameProfile(String oldName, String newName) {
        return false;
    }

    /* Access modifiers changed, original: protected */
    public int GetPartsConsumption() {
        return 3;
    }

    /* Access modifiers changed, original: protected */
    public void ToPsvInternal(StringBuilder sb) {
        sb.append(LlamaStorage.SimpleEscape(this._ToneUri));
        sb.append("|");
        sb.append(LlamaStorage.SimpleEscape(this._ToneName));
        sb.append("|");
        sb.append(this._StreamId);
    }

    public static SoundPlayerAction CreateFrom(String[] parts, int currentPart) {
        return new SoundPlayerAction(LlamaStorage.SimpleUnescape(parts[currentPart + 1]), LlamaStorage.SimpleUnescape(parts[currentPart + 2]), Integer.parseInt(parts[currentPart + 3]));
    }

    /* Access modifiers changed, original: protected */
    public String getId() {
        return EventFragment.SOUND_PLAYER;
    }

    public PreferenceEx<SoundPlayerAction> CreatePreference(PreferenceActivity context) {
        final PreferenceActivity preferenceActivity = context;
        return new ClickablePreferenceEx<SoundPlayerAction>((ResultRegisterableActivity) context, context.getString(R.string.hrActionSoundPlayer), this) {
            String ringtoneTitle;
            String ringtoneUri;
            String[] streamNames = preferenceActivity.getResources().getStringArray(R.array.audioStreamNames);
            String[] streamValues = preferenceActivity.getResources().getStringArray(R.array.audioStreamValues);

            /* Access modifiers changed, original: protected */
            public String GetHumanReadableValue(Context context, SoundPlayerAction value) {
                StringBuilder sb = new StringBuilder();
                try {
                    value.AppendActionDescription(context, AppendableCharSequence.Wrap(sb));
                    Helpers.CapitaliseFirstLetter(sb);
                    return sb.toString();
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }

            /* Access modifiers changed, original: protected */
            public void OnPreferenceClicked(final ResultRegisterableActivity host, final SoundPlayerAction existingValue, final GotResultHandler<SoundPlayerAction> gotResultHandler) {
                this.ringtoneUri = existingValue._ToneUri;
                this.ringtoneTitle = existingValue._ToneName;
                View view = View.inflate(host.GetActivity(), R.layout.sound_player_action, null);
                Button ringtonePickerButton = (Button) view.findViewById(R.id.ringtoneButton);
                final Spinner streamPicker = (Spinner) view.findViewById(R.id.spinner);
                final TextView textView = (TextView) view.findViewById(R.id.text);
                String ringtoneTitleOrNone = this.ringtoneTitle == null ? preferenceActivity.getString(R.string.hrNoneSelected) : this.ringtoneTitle;
                textView.setText(preferenceActivity.getString(R.string.hrCurrentRingtoneColon1, new Object[]{ringtoneTitleOrNone}));
                ringtonePickerButton.setOnClickListener(new OnClickListener() {
                    public void onClick(View paramView) {
                        RingtonePicker.Show(host, existingValue._ToneUri, new RingtonePickerResultCallback() {
                            public void run(Uri value, String title, boolean isSilent) {
                                String ringtoneUriResult;
                                if (value != null) {
                                    ringtoneUriResult = value.toString();
                                } else {
                                    ringtoneUriResult = null;
                                }
                                if (isSilent) {
                                    new Builder(host.GetActivity()).setMessage(R.string.hrRingtonePickerPickedSilent).setPositiveButton(R.string.hrOkeyDoke, null).show();
                                    return;
                                }
                                AnonymousClass1.this.ringtoneUri = ringtoneUriResult;
                                AnonymousClass1.this.ringtoneTitle = title;
                                textView.setText(preferenceActivity.getString(R.string.hrCurrentRingtoneColon1, new Object[]{title}));
                            }
                        });
                    }
                });
                ArrayAdapter<String> adapter = new ArrayAdapter(preferenceActivity, 17367048, this.streamNames);
                adapter.setDropDownViewResource(17367049);
                streamPicker.setAdapter(adapter);
                Integer index = IterableHelpers.FindIndex(this.streamValues, String.valueOf(existingValue._StreamId));
                if (index == null) {
                    index = Integer.valueOf(0);
                }
                streamPicker.setSelection(index.intValue());
                AlertDialog dialog = new Builder(host.GetActivity()).setPositiveButton(R.string.hrOk, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        gotResultHandler.HandleResult(new SoundPlayerAction(AnonymousClass1.this.ringtoneUri, AnonymousClass1.this.ringtoneTitle, Integer.parseInt(AnonymousClass1.this.streamValues[streamPicker.getSelectedItemPosition()])));
                        dialog.dismiss();
                    }
                }).setNegativeButton(R.string.hrCancel, null).setView(view).create();
                dialog.setOwnerActivity(host.GetActivity());
                dialog.show();
            }
        };
    }

    public void AppendActionDescription(Context context, AppendableCharSequence sb) throws IOException {
        if (this._StreamId == 666) {
            sb.append(context.getString(R.string.hrStopQueuedSounds));
            return;
        }
        sb.append(context.getString(R.string.hrPlaySound1, new Object[]{this._ToneName}));
    }

    public String GetIsValidError(Context context) {
        if (this._ToneUri == null || this._ToneUri.length() == 0) {
            return context.getString(R.string.hrPleaseChooseASoundToPlay);
        }
        return null;
    }

    public boolean IsHarmful() {
        return false;
    }
}
