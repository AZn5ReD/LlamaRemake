package com.kebab.Llama.EventActions;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.preference.PreferenceActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import com.kebab.AlertDialogEx.Builder;
import com.kebab.AppendableCharSequence;
import com.kebab.ClickablePreferenceEx;
import com.kebab.ClickablePreferenceEx.GotResultHandler;
import com.kebab.Helpers;
import com.kebab.IterableHelpers;
import com.kebab.Llama.Constants;
import com.kebab.Llama.Event;
import com.kebab.Llama.EventFragment;
import com.kebab.Llama.Instances;
import com.kebab.Llama.LlamaService;
import com.kebab.Llama.LlamaSettings;
import com.kebab.Llama.LlamaStorage;
import com.kebab.Llama.R;
import com.kebab.PreferenceEx;
import com.kebab.ResultRegisterableActivity;
import com.kebab.SeekBarDialogView;
import com.kebab.SeekBarDialogView.ValueFormatter;
import java.io.IOException;
import java.util.Arrays;

public class ChangeProfileAction2 extends EventAction<ChangeProfileAction2> {
    int _EnableProfileLockMinutes;
    String _ProfileName;

    public ChangeProfileAction2(String profileName, int enableProfileLock) {
        this._ProfileName = profileName;
        this._EnableProfileLockMinutes = enableProfileLock;
    }

    public ChangeProfileAction2(String profileName) {
        this(profileName, 0);
    }

    public ChangeProfileAction2(ChangeProfileAction action) {
        this._ProfileName = action._ProfileName;
    }

    public void PerformAction(LlamaService service, Activity activity, Event event, long currentTimeRoundedToNextMinute, int eventRunMode) {
        service.SetProfile(this._ProfileName, true, this._EnableProfileLockMinutes > 0 ? Integer.valueOf(this._EnableProfileLockMinutes) : null, false);
    }

    public boolean RenameProfile(String oldName, String newName) {
        if (!this._ProfileName.equals(oldName)) {
            return false;
        }
        this._ProfileName = newName;
        return true;
    }

    /* Access modifiers changed, original: protected */
    public int GetPartsConsumption() {
        return 2;
    }

    /* Access modifiers changed, original: protected */
    public String getId() {
        return EventFragment.CHANGE_PROFILE_ACTION2;
    }

    /* Access modifiers changed, original: protected */
    public void ToPsvInternal(StringBuilder sb) {
        sb.append(LlamaStorage.SimpleEscape(this._ProfileName));
        sb.append("|");
        sb.append(this._EnableProfileLockMinutes);
    }

    public static ChangeProfileAction2 CreateFrom(String[] parts, int currentPart) {
        return new ChangeProfileAction2(LlamaStorage.SimpleUnescape(parts[currentPart + 1]), Integer.parseInt(parts[currentPart + 2]));
    }

    public PreferenceEx<ChangeProfileAction2> CreatePreference(PreferenceActivity context) {
        final String[] profileNames = Instances.Service.GetProfileNames();
        Arrays.sort(profileNames);
        final PreferenceActivity preferenceActivity = context;
        return new ClickablePreferenceEx<ChangeProfileAction2>((ResultRegisterableActivity) context, context.getString(R.string.hrChangeProfile), this) {
            boolean hiddenLayoutVisible;

            /* Access modifiers changed, original: protected */
            public String GetHumanReadableValue(Context context, ChangeProfileAction2 value) {
                if (value._ProfileName == null) {
                    return "";
                }
                String lockProfilesFor;
                if (value._EnableProfileLockMinutes <= 0) {
                    lockProfilesFor = "";
                } else if (value._EnableProfileLockMinutes == Integer.MAX_VALUE) {
                    lockProfilesFor = ", " + context.getString(R.string.hrLockProfileForever);
                } else {
                    lockProfilesFor = ", " + String.format(context.getString(R.string.hrLockProfileFor1), new Object[]{Helpers.GetHoursMinutesSeconds(context, value._EnableProfileLockMinutes * 60)});
                }
                return value._ProfileName + lockProfilesFor;
            }

            /* Access modifiers changed, original: 0000 */
            public void updateToggleButton(LinearLayout togglableLayout, ImageView button, boolean newValue) {
                togglableLayout.setVisibility(this.hiddenLayoutVisible ? 0 : 8);
                button.setImageDrawable(preferenceActivity.getResources().getDrawable(this.hiddenLayoutVisible ? R.drawable.contract : R.drawable.expand));
            }

            /* Access modifiers changed, original: protected */
            public void OnPreferenceClicked(ResultRegisterableActivity host, ChangeProfileAction2 existingValue, GotResultHandler<ChangeProfileAction2> gotResultHandler) {
                int i;
                int i2;
                Integer selectedIndex = IterableHelpers.FindIndex(profileNames, existingValue._ProfileName);
                selectedIndex = Integer.valueOf(selectedIndex == null ? 0 : selectedIndex.intValue());
                View view = View.inflate(host.GetActivity(), R.layout.action_change_profile, null);
                Spinner list = (Spinner) view.findViewById(R.id.list);
                LinearLayout mainLayout = (LinearLayout) view.findViewById(R.id.mainLayout);
                LinearLayout togglableLayout = (LinearLayout) view.findViewById(R.id.togglableLayout);
                LinearLayout clickableLayout = (LinearLayout) view.findViewById(R.id.clickableLayout);
                final ImageView clickableImageButton = (ImageView) view.findViewById(R.id.clickableImageButton);
                final CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkBox);
                ArrayAdapter<String> adapter = new ArrayAdapter(host.GetActivity(), 17367048, profileNames);
                adapter.setDropDownViewResource(17367049);
                list.setAdapter(adapter);
                list.setSelection(selectedIndex.intValue());
                checkBox.setChecked(existingValue._EnableProfileLockMinutes > 0);
                this.hiddenLayoutVisible = existingValue._EnableProfileLockMinutes > 0;
                togglableLayout.setVisibility(this.hiddenLayoutVisible ? 0 : 8);
                if (existingValue._EnableProfileLockMinutes > 0) {
                    i = existingValue._EnableProfileLockMinutes;
                } else {
                    i = 30;
                }
                if (((Boolean) LlamaSettings.LongerProfileLock.GetValue(host.GetActivity())).booleanValue()) {
                    i2 = Constants.PROFILE_LOCK_MAX_MINUTES_LONG;
                } else {
                    i2 = 480;
                }
                final ResultRegisterableActivity resultRegisterableActivity = host;
                SeekBarDialogView seekBar = new SeekBarDialogView(i, 1, i2, getString(R.string.hrForeverEternity), null, new ValueFormatter() {
                    public String FormatValue(int value, boolean isTopMostValue, String topMostValue) {
                        return isTopMostValue ? topMostValue : Helpers.GetHoursMinutesSeconds(resultRegisterableActivity.GetActivity(), value * 60);
                    }

                    public int GetTextSize() {
                        return 15;
                    }
                });
                seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        if (fromUser) {
                            checkBox.setChecked(true);
                        }
                    }

                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }

                    public void onStopTrackingTouch(SeekBar seekBar) {
                    }
                });
                togglableLayout.addView(seekBar.createSeekBarDialogView(host.GetActivity()), new LayoutParams(-1, -2));
                LinearLayout linearLayout = togglableLayout;
                final LinearLayout finalLinearLayout = linearLayout;
                clickableLayout.setOnClickListener(new OnClickListener() {
                    public void onClick(View v) {
                        hiddenLayoutVisible = !hiddenLayoutVisible;
                        updateToggleButton(finalLinearLayout, clickableImageButton, hiddenLayoutVisible);
                    }
                });
                linearLayout = togglableLayout;
                final LinearLayout finalLinearLayout1 = linearLayout;
                clickableImageButton.setOnClickListener(new OnClickListener() {
                    public void onClick(View v) {
                        hiddenLayoutVisible = !hiddenLayoutVisible;
                        updateToggleButton(finalLinearLayout1, clickableImageButton, hiddenLayoutVisible);
                    }
                });
                final Spinner spinner = list;
                final CheckBox checkBox2 = checkBox;
                final SeekBarDialogView seekBarDialogView = seekBar;
                final GotResultHandler<ChangeProfileAction2> gotResultHandler2 = gotResultHandler;
                new Builder(host.GetActivity()).setTitle(R.string.hrChangeProfile).setView(view).setPositiveButton(R.string.hrOk, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        int selectedIndex = spinner.getSelectedItemPosition();
                        gotResultHandler2.HandleResult(new ChangeProfileAction2(selectedIndex >= 0 ? profileNames[selectedIndex] : null, checkBox2.isChecked() ? seekBarDialogView.GetResult() : 0));
                    }
                }).setNegativeButton(R.string.hrCancel, null).show();
            }
        };
    }

    public void AppendActionDescription(Context context, AppendableCharSequence sb) throws IOException {
        sb.append(String.format(context.getString(R.string.hrChangeProfileTo), new Object[]{this._ProfileName}));
        if (this._EnableProfileLockMinutes > 0) {
            sb.append((CharSequence) " ");
            sb.append(context.getString(R.string.hrAnd));
            sb.append((CharSequence) " ");
            if (this._EnableProfileLockMinutes == Integer.MAX_VALUE) {
                sb.append(context.getString(R.string.hrLockProfileForever));
                return;
            }
            sb.append(String.format(context.getString(R.string.hrLockProfileFor1), new Object[]{Helpers.GetHoursMinutesSeconds(context, this._EnableProfileLockMinutes * 60)}));
        }
    }

    public String GetIsValidError(Context context) {
        if (this._ProfileName == null || this._ProfileName.length() == 0) {
            return context.getString(R.string.hrChooseAProfile);
        }
        return null;
    }

    public boolean IsHarmful() {
        return false;
    }
}
