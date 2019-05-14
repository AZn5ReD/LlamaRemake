package com.kebab.Llama.EventActions;

import android.app.Activity;
import android.content.Context;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import com.kebab.AppendableCharSequence;
import com.kebab.ArrayHelpers;
import com.kebab.DialogHandler;
import com.kebab.DialogPreference;
import com.kebab.Helpers;
import com.kebab.IterableHelpers;
import com.kebab.Llama.Event;
import com.kebab.Llama.EventFragment;
import com.kebab.Llama.LlamaService;
import com.kebab.Llama.R;
import com.kebab.OnGetValueEx;
import com.kebab.PreferenceEx;
import com.kebab.SeekBarDialogView;
import com.kebab.SeekBarDialogView.ValueFormatter;
import com.kebab.Tuple;
import java.io.IOException;

public class ChangeNotificationIconAction2 extends EventAction<ChangeNotificationIconAction2> {
    String _IconName;
    int _NotificationDots;
    int _NotificationIcon;

    public ChangeNotificationIconAction2(ChangeNotificationIconAction notificationIcon) {
        Tuple<Integer, Integer> legacy = ChangeNotificationIconAction.ConvertLegacy(notificationIcon);
        this._NotificationIcon = ((Integer) legacy.Item1).intValue();
        this._NotificationDots = ((Integer) legacy.Item2).intValue();
    }

    public ChangeNotificationIconAction2(int notificationIcon, int notificationIconDots) {
        this._NotificationIcon = notificationIcon;
        this._NotificationDots = notificationIconDots;
    }

    public boolean IsHarmful() {
        return false;
    }

    public void PerformAction(LlamaService service, Activity activity, Event event, long currentTimeRoundedToNextMinute, int eventRunMode) {
        service.SetNotificationIcon(Integer.valueOf(this._NotificationIcon), Integer.valueOf(this._NotificationDots));
    }

    public boolean RenameProfile(String oldName, String newName) {
        return false;
    }

    /* Access modifiers changed, original: protected */
    public int GetPartsConsumption() {
        return 2;
    }

    /* Access modifiers changed, original: protected */
    public String getId() {
        return EventFragment.CHANGE_NOTIFICATION_ICON_ACTION2;
    }

    /* Access modifiers changed, original: protected */
    public void ToPsvInternal(StringBuilder sb) {
        sb.append(this._NotificationIcon).append("|");
        sb.append(this._NotificationDots);
    }

    public static ChangeNotificationIconAction2 CreateFrom(String[] parts, int currentPart) {
        return new ChangeNotificationIconAction2(Integer.parseInt(parts[currentPart + 1]), Integer.parseInt(parts[currentPart + 2]));
    }

    public PreferenceEx<ChangeNotificationIconAction2> CreatePreference(final PreferenceActivity context) {
        final String[] names = context.getResources().getStringArray(R.array.notificationColourNames);
        final String[] values = context.getResources().getStringArray(R.array.notificationColourValues);
        return CreateDialogPreference(context, context.getString(R.string.hrChangeNotificationIcon), new DialogHandler<ChangeNotificationIconAction2>() {
            SeekBarDialogView _DotSeekBar;
            Spinner _IconColour;

            public void DialogHasFinished(View view) {
            }

            public boolean HideButtons() {
                return false;
            }

            public ChangeNotificationIconAction2 GetResultFromView() {
                int dotCount = this._DotSeekBar.GetResult();
                if (dotCount == Integer.MAX_VALUE) {
                    dotCount = -1;
                }
                return new ChangeNotificationIconAction2(Integer.parseInt(values[this._IconColour.getSelectedItemPosition()]), dotCount);
            }

            public ChangeNotificationIconAction2 fillValuesFromString(String value) {
                return ChangeNotificationIconAction2.CreateFrom(value.split("\\|", -1), -1);
            }

            public String getHumanReadableValue(ChangeNotificationIconAction2 value) {
                String string;
                value.ensureIconName(context);
                StringBuilder append = new StringBuilder().append(Helpers.CapitaliseFirstLetter(value._IconName)).append(", ");
                if (value._NotificationDots == -1) {
                    string = context.getString(R.string.hrDontChangeDots);
                } else if (value._NotificationDots == 1) {
                    string = String.format(context.getString(R.string.hr1Dot), new Object[]{Integer.valueOf(value._NotificationDots)});
                } else {
                    string = String.format(context.getString(R.string.hr1Dots), new Object[]{Integer.valueOf(value._NotificationDots)});
                }
                return append.append(string).toString();
            }

            public View getView(ChangeNotificationIconAction2 value, Context context, DialogPreference<?, ChangeNotificationIconAction2> dialogPreference) {
                int i;
                View v = ((LayoutInflater) context.getSystemService("layout_inflater")).inflate(R.layout.notify_icon_picker, null);
                this._IconColour = (Spinner) v.findViewById(R.id.spinner);
                TextView dotText = (TextView) v.findViewById(R.id.dotText);
                if (value._NotificationDots == -1) {
                    i = Integer.MAX_VALUE;
                } else {
                    i = value._NotificationDots;
                }
                this._DotSeekBar = new SeekBarDialogView(i, 0, 4, context.getString(R.string.hrDontChangeDots), (String) null, new ValueFormatter() {
                    public String FormatValue(int value, boolean isTopMostValue, String topMostValue) {
                        return isTopMostValue ? topMostValue : String.valueOf(value);
                    }

                    public int GetTextSize() {
                        return 20;
                    }
                });
                ViewGroup parent = (ViewGroup) dotText.getParent();
                parent.addView(this._DotSeekBar.createSeekBarDialogView(context), parent.indexOfChild(dotText) + 1);
                ArrayAdapter<String> adapter = new ArrayAdapter(context, 17367048, names);
                adapter.setDropDownViewResource(17367049);
                this._IconColour.setAdapter(adapter);
                Integer index = IterableHelpers.FindIndex(values, String.valueOf(value._NotificationIcon));
                if (index == null) {
                    index = Integer.valueOf(0);
                }
                this._IconColour.setSelection(index.intValue());
                return v;
            }

            public String serialiseToString(ChangeNotificationIconAction2 value) {
                return value.ToPsv();
            }

            public boolean RequiresScrollView() {
                return true;
            }
        }, this, new OnGetValueEx<ChangeNotificationIconAction2>() {
            public ChangeNotificationIconAction2 GetValue(Preference preference) {
                return (ChangeNotificationIconAction2) ((DialogPreference) preference).getValue();
            }
        });
    }

    public void AppendActionDescription(Context context, AppendableCharSequence sb) throws IOException {
        int resId = R.string.hr1Dot;
        ensureIconName(context);
        if (this._NotificationIcon >= 0) {
            ensureIconName(context);
            sb.append(String.format(context.getString(R.string.hrChangeNotificationIconTo1), new Object[]{this._IconName}));
            if (this._NotificationDots >= 0) {
                if (this._NotificationDots != 1) {
                    resId = R.string.hr1Dots;
                }
                sb.append((CharSequence) " ").append(String.format(context.getString(R.string.hrWith), new Object[0])).append(" ").append(String.format(context.getString(resId), new Object[]{Integer.valueOf(this._NotificationDots)}));
            }
        } else if (this._NotificationDots >= 0) {
            if (this._NotificationDots != 1) {
                resId = R.string.hr1Dots;
            }
            String numberOfDots = String.format(context.getString(resId), new Object[]{Integer.valueOf(this._NotificationDots)});
            sb.append(String.format(context.getString(R.string.hrChangeNotificationIconTo1), new Object[]{numberOfDots}));
        }
    }

    private void ensureIconName(Context context) {
        if (this._IconName == null) {
            String[] names = context.getResources().getStringArray(R.array.notificationColourNames);
            Integer index = ArrayHelpers.FindIndex(context.getResources().getStringArray(R.array.notificationColourValues), String.valueOf(this._NotificationIcon));
            if (index != null) {
                int iconIndex = index.intValue();
                if (iconIndex >= 0 && iconIndex < names.length) {
                    this._IconName = names[iconIndex].toLowerCase();
                    return;
                }
            }
            this._IconName = context.getString(R.string.hrUnknown);
        }
    }

    public String GetIsValidError(Context context) {
        if (this._NotificationDots == -1 && this._NotificationIcon == -1) {
            return context.getString(R.string.hrNotificationIconMustChangeTheIconOrNumberOfDots);
        }
        return null;
    }
}
